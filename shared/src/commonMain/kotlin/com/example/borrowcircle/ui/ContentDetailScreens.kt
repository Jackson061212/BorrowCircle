package com.example.borrowcircle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.borrowcircle.data.model.CommunityRequest
import com.example.borrowcircle.data.model.ItemListing
import com.example.borrowcircle.data.model.ItemPhoto
import com.example.borrowcircle.data.model.ItemPhotoKind

@Composable
fun ItemDetailDialog(
    listingId: String,
    listing: ItemListing?,
    onDismiss: () -> Unit,
    onOwnerSelected: (String) -> Unit,
    onRequestBorrow: (String) -> Unit,
    canRequestBorrow: Boolean = true,
) {
    if (listing == null) {
        MissingContentDetail(
            title = "Listing not found",
            message = "The listing $listingId is no longer available in this session.",
            onDismiss = onDismiss,
        )
        return
    }

    DetailFrame(title = "Item details", onDismiss = onDismiss) {
        item(key = "item-gallery") {
            ItemPhotoGallery(photos = listing.photos, title = listing.title)
        }
        item(key = "item-heading") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = listing.title,
                    fontSize = 30.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${listing.category} · ${listing.condition}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    PriceLabel(listing.priceLabel)
                }
                Text(
                    text = listing.availability,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
        }
        item(key = "item-description") {
            DetailSection(title = "About this item") {
                Text(
                    text = listing.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )
            }
        }
        item(key = "item-owner") {
            DetailSection(title = "Owner") {
                MemberSummary(
                    name = listing.ownerDisplayName,
                    circle = listing.ownerCircle,
                    onClick = { onOwnerSelected(listing.ownerId) },
                )
            }
        }
        item(key = "item-borrowing") {
            DetailSection(title = "Borrowing details") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailFact("Price policy", listing.pricingExplanation)
                    val duration = when {
                        listing.minimumLoanDays != null && listing.maximumLoanDays != null ->
                            "${listing.minimumLoanDays}–${listing.maximumLoanDays} days"
                        listing.maximumLoanDays != null -> "Up to ${listing.maximumLoanDays} days"
                        else -> null
                    }
                    duration?.let { DetailFact("Loan duration", it) }
                    listing.latestReturn?.let { DetailFact("Latest permitted return", it) }
                    listing.handoffArea?.let { DetailFact("Handoff area", it) }
                }
            }
        }
        if (listing.accessories.isNotEmpty()) {
            item(key = "item-accessories") {
                DetailSection(title = "Included accessories") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listing.accessories.forEach { accessory ->
                            Text("• $accessory", fontSize = 15.sp)
                        }
                    }
                }
            }
        }
        listing.safetyNotes?.let { notes ->
            item(key = "item-safety") {
                DetailSection(title = "Usage and safety") {
                    Text(
                        text = notes,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp,
                    )
                }
            }
        }
        item(key = "item-action") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { onRequestBorrow(listing.id) },
                    enabled = canRequestBorrow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = if (canRequestBorrow) "Request to borrow" else "This is your listing",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Text(
                    text = if (canRequestBorrow) {
                        "Review the borrowing period and total before sending your request. Prototype only—no payment will be collected."
                    } else {
                        "Your own listing cannot be requested from this account."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
fun RequestDetailDialog(
    requestId: String,
    request: CommunityRequest?,
    linkedListings: List<ItemListing>,
    onDismiss: () -> Unit,
    onHelp: (String) -> Unit,
    onRequesterSelected: (String) -> Unit,
) {
    if (request == null) {
        MissingContentDetail(
            title = "Request not found",
            message = "The request $requestId is no longer available in this session.",
            onDismiss = onDismiss,
        )
        return
    }

    DetailFrame(title = "Request details", onDismiss = onDismiss) {
        item(key = "request-heading") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = request.title,
                            fontSize = 30.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = request.category,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    StatusPill(request.status)
                }
                Text(
                    text = request.freshness,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }
        }
        item(key = "request-member") {
            DetailSection(title = "Requested by") {
                MemberSummary(
                    name = request.requesterDisplayName,
                    circle = request.requesterCircle,
                    onClick = { onRequesterSelected(request.requesterId) },
                )
            }
        }
        request.referencePhoto?.let { photo ->
            item(key = "request-reference") {
                DetailSection(title = "Reference photo") {
                    SeededItemPhoto(
                        photo = photo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                }
            }
        }
        item(key = "request-description") {
            DetailSection(title = "What is needed") {
                Text(
                    text = request.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )
            }
        }
        item(key = "request-timing") {
            DetailSection(title = "Timing and pickup") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailFact("Needed from", request.neededFrom)
                    DetailFact("Needed until", request.neededUntil)
                    request.maximumBudgetLabel?.let { DetailFact("Maximum budget", it) }
                    DetailFact("Pickup flexibility", request.pickupFlexibility)
                }
            }
        }
        item(key = "request-links") {
            DetailSection(title = "Linked offers") {
                if (linkedListings.isEmpty()) {
                    Text(
                        text = "No listings have been offered yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        linkedListings.forEach { listing ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(listing.title, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = "${listing.priceLabel} · offered by ${listing.ownerDisplayName}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (request.status == "Open" || request.status == "Matched") {
            item(key = "request-action") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onHelp(request.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text("I can help", fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        text = "Offering an item does not mark the request as matched. The requester must accept an offer first.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun HelpRequestDialog(
    request: CommunityRequest?,
    currentUserListings: List<ItemListing>,
    onDismiss: () -> Unit,
    onLinkListing: (String) -> Unit,
    onCreateListing: () -> Unit,
) {
    AdaptivePanelDialog(title = "I can help", onDismiss = onDismiss, dimAmount = 0.12f) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = request?.let { "Offer a public listing for “${it.title}”." }
                    ?: "This request is no longer available.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
            )
            if (request != null) {
                Text("Link one of your eligible listings", fontWeight = FontWeight.SemiBold)
                if (currentUserListings.isEmpty()) {
                    Text(
                        text = "You do not have an eligible listing yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    currentUserListings.forEach { listing ->
                        OutlinedButton(
                            onClick = { onLinkListing(listing.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text("Link ${listing.title}")
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Button(
                    onClick = onCreateListing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("Create a new public listing")
                }
            }
        }
    }
}

@Composable
private fun DetailFrame(
    title: String,
    onDismiss: () -> Unit,
    content: LazyListScope.() -> Unit,
) {
    AdaptivePanelDialog(title = title, onDismiss = onDismiss) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            content = content,
        )
    }
}

@Composable
private fun MissingContentDetail(
    title: String,
    message: String,
    onDismiss: () -> Unit,
) {
    DetailFrame(title = title, onDismiss = onDismiss) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Button(onClick = onDismiss) { Text("Return to the feed") }
            }
        }
    }
}

@Composable
private fun ItemPhotoGallery(photos: List<ItemPhoto>, title: String) {
    if (photos.isEmpty()) {
        EmptyImageFallback(title)
        return
    }

    var selectedIndex by remember(photos) { mutableIntStateOf(0) }
    val selected = photos[selectedIndex.coerceIn(photos.indices)]
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SeededItemPhoto(
            photo = selected,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        )
        if (photos.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { selectedIndex = if (selectedIndex == 0) photos.lastIndex else selectedIndex - 1 },
                ) {
                    Text("Previous")
                }
                Text(
                    text = "${selectedIndex + 1} of ${photos.size}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
                OutlinedButton(
                    onClick = { selectedIndex = if (selectedIndex == photos.lastIndex) 0 else selectedIndex + 1 },
                ) {
                    Text("Next")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                photos.forEachIndexed { index, photo ->
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .border(
                                width = if (index == selectedIndex) 3.dp else 1.dp,
                                color = if (index == selectedIndex) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                                shape = RoundedCornerShape(8.dp),
                            )
                            .padding(3.dp)
                            .semantics {
                                role = Role.Button
                                contentDescription = "Show photo ${index + 1}: ${photo.caption}"
                            }
                            .clickable { selectedIndex = index },
                    ) {
                        SeededItemPhoto(photo = photo, modifier = Modifier.fillMaxSize(), showCaption = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyImageFallback(title: String) {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .semantics { contentDescription = "No photos available for $title" },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(52.dp)) {
                val stroke = 2.dp.toPx()
                drawRoundRect(
                    color = accent,
                    cornerRadius = CornerRadius(7.dp.toPx()),
                    style = Stroke(stroke),
                )
                drawCircle(
                    color = accent,
                    radius = 5.dp.toPx(),
                    center = Offset(size.width * 0.72f, size.height * 0.28f),
                    style = Stroke(stroke),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("No item photos yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
internal fun SeededItemPhoto(
    photo: ItemPhoto,
    modifier: Modifier = Modifier,
    showCaption: Boolean = true,
) {
    val palette = photoPalette(photo.kind)
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(listOf(palette.first, palette.second)),
                shape = RoundedCornerShape(10.dp),
            )
            .semantics { contentDescription = photo.contentDescription },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ink = Color.White.copy(alpha = 0.92f)
            val softInk = Color.White.copy(alpha = 0.48f)
            val stroke = Stroke(width = size.minDimension * 0.025f, cap = StrokeCap.Round)
            when (photo.kind) {
                ItemPhotoKind.ProjectorFront,
                ItemPhotoKind.ProjectorConnections,
                ItemPhotoKind.ProjectorCase,
                -> {
                    drawRoundRect(
                        color = softInk,
                        topLeft = Offset(size.width * 0.22f, size.height * 0.26f),
                        size = Size(size.width * 0.56f, size.height * 0.43f),
                        cornerRadius = CornerRadius(size.minDimension * 0.06f),
                    )
                    drawCircle(
                        color = ink,
                        radius = size.minDimension * 0.12f,
                        center = Offset(size.width * 0.60f, size.height * 0.47f),
                        style = stroke,
                    )
                    drawCircle(
                        color = ink,
                        radius = size.minDimension * 0.045f,
                        center = Offset(size.width * 0.60f, size.height * 0.47f),
                    )
                    drawLine(
                        color = ink,
                        start = Offset(size.width * 0.30f, size.height * 0.38f),
                        end = Offset(size.width * 0.43f, size.height * 0.38f),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round,
                    )
                }
                ItemPhotoKind.LanternPair,
                ItemPhotoKind.LanternCharging,
                -> {
                    listOf(0.38f, 0.62f).forEach { x ->
                        drawRoundRect(
                            color = softInk,
                            topLeft = Offset(size.width * (x - 0.10f), size.height * 0.28f),
                            size = Size(size.width * 0.20f, size.height * 0.43f),
                            cornerRadius = CornerRadius(size.minDimension * 0.05f),
                        )
                        drawCircle(
                            color = ink,
                            radius = size.minDimension * 0.065f,
                            center = Offset(size.width * x, size.height * 0.49f),
                            style = stroke,
                        )
                    }
                }
                ItemPhotoKind.TripodExtended,
                ItemPhotoKind.TripodFolded,
                -> {
                    val center = Offset(size.width * 0.5f, size.height * 0.36f)
                    drawRoundRect(
                        color = softInk,
                        topLeft = Offset(size.width * 0.39f, size.height * 0.23f),
                        size = Size(size.width * 0.22f, size.height * 0.16f),
                        cornerRadius = CornerRadius(size.minDimension * 0.03f),
                    )
                    drawLine(ink, center, Offset(size.width * 0.32f, size.height * 0.78f), stroke.width, StrokeCap.Round)
                    drawLine(ink, center, Offset(size.width * 0.68f, size.height * 0.78f), stroke.width, StrokeCap.Round)
                    drawLine(ink, center, Offset(size.width * 0.50f, size.height * 0.78f), stroke.width, StrokeCap.Round)
                }
                ItemPhotoKind.SewingReference -> {
                    drawRoundRect(
                        color = softInk,
                        topLeft = Offset(size.width * 0.22f, size.height * 0.25f),
                        size = Size(size.width * 0.56f, size.height * 0.48f),
                        cornerRadius = CornerRadius(size.minDimension * 0.06f),
                    )
                    drawCircle(ink, size.minDimension * 0.08f, Offset(size.width * 0.40f, size.height * 0.48f), style = stroke)
                    drawCircle(ink, size.minDimension * 0.08f, Offset(size.width * 0.60f, size.height * 0.48f), style = stroke)
                    drawLine(ink, Offset(size.width * 0.40f, size.height * 0.48f), Offset(size.width * 0.60f, size.height * 0.63f), stroke.width)
                }
                ItemPhotoKind.GameNightReference -> {
                    drawCircle(softInk, size.minDimension * 0.26f, Offset(size.width * 0.5f, size.height * 0.5f))
                    listOf(0.0f, 0.25f, 0.5f, 0.75f).forEach { turn ->
                        val x = size.width * (0.35f + (turn % 0.5f))
                        val y = size.height * (0.35f + turn * 0.4f)
                        drawCircle(ink, size.minDimension * 0.035f, Offset(x, y))
                    }
                }
                ItemPhotoKind.SessionPreview -> {
                    drawRoundRect(
                        color = softInk,
                        topLeft = Offset(size.width * 0.25f, size.height * 0.24f),
                        size = Size(size.width * 0.50f, size.height * 0.50f),
                        cornerRadius = CornerRadius(size.minDimension * 0.06f),
                        style = stroke,
                    )
                    drawCircle(ink, size.minDimension * 0.06f, Offset(size.width * 0.63f, size.height * 0.39f))
                    drawLine(ink, Offset(size.width * 0.31f, size.height * 0.66f), Offset(size.width * 0.46f, size.height * 0.48f), stroke.width)
                    drawLine(ink, Offset(size.width * 0.46f, size.height * 0.48f), Offset(size.width * 0.69f, size.height * 0.66f), stroke.width)
                }
            }
        }
        if (showCaption) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                color = Color.Black.copy(alpha = 0.58f),
                contentColor = Color.White,
                shape = RoundedCornerShape(7.dp),
            ) {
                Text(
                    text = photo.caption,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun photoPalette(kind: ItemPhotoKind): Pair<Color, Color> = when (kind) {
    ItemPhotoKind.ProjectorFront -> Color(0xFF32526B) to Color(0xFF7AA6A2)
    ItemPhotoKind.ProjectorConnections -> Color(0xFF243743) to Color(0xFF5C7D88)
    ItemPhotoKind.ProjectorCase -> Color(0xFF4A4E56) to Color(0xFF8B968C)
    ItemPhotoKind.LanternPair -> Color(0xFF4F652F) to Color(0xFFD49B47)
    ItemPhotoKind.LanternCharging -> Color(0xFF3B5D50) to Color(0xFF85A95E)
    ItemPhotoKind.TripodExtended -> Color(0xFF32445B) to Color(0xFF8E7896)
    ItemPhotoKind.TripodFolded -> Color(0xFF464A57) to Color(0xFF6D879B)
    ItemPhotoKind.SewingReference -> Color(0xFF8F5162) to Color(0xFFD6A27A)
    ItemPhotoKind.GameNightReference -> Color(0xFF43528D) to Color(0xFFAD705E)
    ItemPhotoKind.SessionPreview -> Color(0xFF0B6B61) to Color(0xFF6CA89F)
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun DetailFact(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(text = value, fontSize = 15.sp, lineHeight = 21.sp)
    }
}

@Composable
private fun MemberSummary(name: String, circle: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .semantics {
                            role = Role.Button
                            contentDescription = "Open $name member profile"
                        }
                        .clickable(onClick = onClick)
                        .padding(vertical = 4.dp)
                } else {
                    Modifier
                },
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(name, fontWeight = FontWeight.SemiBold)
            Text(
                text = "Verified $circle member",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
            )
            if (onClick != null) {
                Text(
                    text = "View member profile",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun PriceLabel(text: String) {
    Surface(
        color = Color(0xFFDDF3E4),
        contentColor = Color(0xFF0B5B2A),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun StatusPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
