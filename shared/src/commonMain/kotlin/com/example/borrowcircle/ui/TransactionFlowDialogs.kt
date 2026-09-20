package com.example.borrowcircle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.borrowcircle.data.model.CommunityRequest
import com.example.borrowcircle.data.model.DailyRate
import com.example.borrowcircle.data.model.ExactPrice
import com.example.borrowcircle.data.model.FixedPeriod
import com.example.borrowcircle.data.model.FreeLoan
import com.example.borrowcircle.data.model.ItemListing
import com.example.borrowcircle.data.model.ListingPricing
import com.example.borrowcircle.data.model.MaximumBudget
import com.example.borrowcircle.data.model.MemberProfile
import com.example.borrowcircle.data.model.PriceBasis
import com.example.borrowcircle.data.model.PriceRange
import com.example.borrowcircle.data.model.WeeklyRate
import com.example.borrowcircle.data.model.formatMoney
import com.example.borrowcircle.data.model.maximumDays
import com.example.borrowcircle.data.model.minimumDays
import com.example.borrowcircle.data.model.totalMinorUnits
import com.example.borrowcircle.data.model.acceptsOffer

@Composable
fun BorrowCheckoutDialog(
    listing: ItemListing?,
    lender: MemberProfile?,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    AdaptivePanelDialog(title = "Review borrow request", onDismiss = onDismiss, dimAmount = 0.12f) {
        if (listing == null) {
            MissingTransactionContent("This listing is no longer available.")
            return@AdaptivePanelDialog
        }
        val pricing = listing.pricing
        var durationDays by remember(listing.id) { mutableIntStateOf(pricing.minimumDays) }
        val totalMinor = pricing.totalMinorUnits(durationDays)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            listing.photos.firstOrNull()?.let { photo ->
                SeededItemPhoto(
                    photo = photo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                )
            }
            Text(listing.title, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
            ReceiptSection("Lender") {
                Text(lender?.displayName ?: listing.ownerDisplayName, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Verified ${lender?.circle ?: listing.ownerCircle} member",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                )
            }
            ReceiptSection("Borrowing period") {
                ReceiptLine("Pickup / start", checkoutStartLabel(pricing))
                ReceiptLine("Return", checkoutReturnLabel(pricing, durationDays))
                if (pricing !is FixedPeriod) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedButton(
                            onClick = { durationDays -= 1 },
                            enabled = durationDays > pricing.minimumDays,
                        ) { Text("−") }
                        Text(
                            text = "$durationDays ${if (durationDays == 1) "day" else "days"}",
                            fontWeight = FontWeight.SemiBold,
                        )
                        OutlinedButton(
                            onClick = { durationDays += 1 },
                            enabled = durationDays < pricing.maximumDays,
                        ) { Text("+") }
                    }
                } else {
                    ReceiptLine("Duration", "$durationDays ${if (durationDays == 1) "day" else "days"}")
                }
            }
            ReceiptSection("Item reminder") {
                Text("Condition: ${listing.condition}")
                if (listing.accessories.isNotEmpty()) {
                    Text("Included: ${listing.accessories.joinToString()}")
                }
                listing.safetyNotes?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            ReceiptSection("Price summary") {
                ReceiptLine("Pricing policy", pricing.displayPolicy())
                ReceiptLine("Borrowing period", "$durationDays ${if (durationDays == 1) "day" else "days"}")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Total", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = formatMoney(totalMinor),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            PrototypeDisclaimer()
            Button(
                onClick = { onConfirm(durationDays) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Send borrow request", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun OfferReviewDialog(
    request: CommunityRequest?,
    listing: ItemListing?,
    requester: MemberProfile?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AdaptivePanelDialog(title = "Review your offer", onDismiss = onDismiss, dimAmount = 0.12f) {
        if (request == null || listing == null) {
            MissingTransactionContent("The request or listing is no longer available.")
            return@AdaptivePanelDialog
        }
        val durationDays = request.requestedDays.coerceIn(
            listing.pricing.minimumDays,
            listing.pricing.maximumDays,
        )
        val totalMinor = listing.pricing.totalMinorUnits(durationDays)
        val isValid = request.priceConstraint.acceptsOffer(totalMinor, durationDays)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(request.title, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
            ReceiptSection("Request") {
                ReceiptLine("Requested by", requester?.displayName ?: request.requesterDisplayName)
                ReceiptLine("Category", request.category)
                Text(request.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            ReceiptSection("Dates and handoff") {
                ReceiptLine("Needed from", request.neededFrom)
                ReceiptLine("Needed until", request.neededUntil)
                ReceiptLine("Duration", "$durationDays ${if (durationDays == 1) "day" else "days"}")
                Text(request.pickupFlexibility, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            ReceiptSection("Linked listing") {
                listing.photos.firstOrNull()?.let { photo ->
                    SeededItemPhoto(
                        photo = photo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                }
                Text(listing.title, fontWeight = FontWeight.SemiBold)
                Text("Condition: ${listing.condition}")
                listing.handoffArea?.let { ReceiptLine("Suggested handoff", it) }
            }
            ReceiptSection("Offer summary") {
                ReceiptLine("Request limit", request.priceConstraintLabel())
                ReceiptLine("Listing price", listing.pricing.displayPolicy())
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Offer total", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = formatMoney(totalMinor),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (!isValid) {
                    Text(
                        text = "This listing price is outside the requester’s allowed budget.",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Text(
                text = "The request stays open until ${request.requesterDisplayName} accepts an offer.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
            PrototypeDisclaimer()
            Button(
                onClick = onConfirm,
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Send offer", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ReceiptSection(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun ReceiptLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(
            text = value,
            modifier = Modifier.padding(start = 18.dp),
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PrototypeDisclaimer() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(9.dp),
    ) {
        Text(
            text = "Prototype checkout — no real payment will be processed.",
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun MissingTransactionContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun checkoutStartLabel(pricing: ListingPricing): String =
    if (pricing is FixedPeriod) pricing.startDate else "Today"

private fun checkoutReturnLabel(pricing: ListingPricing, durationDays: Int): String = when (pricing) {
    is FixedPeriod -> pricing.returnDate
    else -> if (durationDays == 1) "Tomorrow" else "In $durationDays days"
}

private fun com.example.borrowcircle.data.model.ListingPricing.displayPolicy(): String = when (this) {
    is DailyRate -> "${formatMoney(amountMinor, currency)} × each billable day"
    is WeeklyRate -> "${formatMoney(amountMinor, currency)} × each started week"
    is FixedPeriod -> "${formatMoney(totalAmountMinor, currency)} for the fixed period"
    is FreeLoan -> "Free loan"
}

private fun CommunityRequest.priceConstraintLabel(): String = when (val constraint = priceConstraint) {
    is ExactPrice -> "Exact ${formatMoney(constraint.amountMinor)}${constraint.basis.suffix()}"
    is PriceRange -> "${formatMoney(constraint.minAmountMinor)}–${formatMoney(constraint.maxAmountMinor)}${constraint.basis.suffix()}"
    is MaximumBudget -> "Up to ${formatMoney(constraint.maxAmountMinor)}${constraint.basis.suffix()}"
    null -> maximumBudgetLabel ?: "No price limit specified"
}

private fun PriceBasis.suffix(): String = if (this == PriceBasis.PerDay) " / day" else " total"
