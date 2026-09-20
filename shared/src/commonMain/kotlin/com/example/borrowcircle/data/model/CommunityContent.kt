package com.example.borrowcircle.data.model

import kotlin.math.roundToInt

enum class PricingModel(val label: String) {
    Free("Free"),
    PerDay("Per day"),
    PerWeek("Per week"),
    FlatPeriod("Flat period"),
}

sealed interface ListingPricing

data class DailyRate(
    val amountMinor: Long,
    val minDays: Int,
    val maxDays: Int,
    val currency: String = "AED",
) : ListingPricing

data class WeeklyRate(
    val amountMinor: Long,
    val minWeeks: Int,
    val maxWeeks: Int,
    val currency: String = "AED",
) : ListingPricing

data class FixedPeriod(
    val totalAmountMinor: Long,
    val startDate: String,
    val returnDate: String,
    val billableDays: Int,
    val currency: String = "AED",
) : ListingPricing

data class FreeLoan(
    val minDays: Int,
    val maxDays: Int,
) : ListingPricing

val ListingPricing.minimumDays: Int
    get() = when (this) {
        is DailyRate -> minDays
        is WeeklyRate -> minWeeks * 7
        is FixedPeriod -> billableDays
        is FreeLoan -> minDays
    }

val ListingPricing.maximumDays: Int
    get() = when (this) {
        is DailyRate -> maxDays
        is WeeklyRate -> maxWeeks * 7
        is FixedPeriod -> billableDays
        is FreeLoan -> maxDays
    }

fun ListingPricing.totalMinorUnits(days: Int): Long {
    require(days in minimumDays..maximumDays) { "Borrowing duration is outside the listing limits." }
    return when (this) {
        is DailyRate -> amountMinor * days
        is WeeklyRate -> amountMinor * ((days + 6) / 7)
        is FixedPeriod -> totalAmountMinor
        is FreeLoan -> 0L
    }
}

fun ListingPricing.displayLabel(): String = when (this) {
    is DailyRate -> "${formatMoney(amountMinor, currency)} / day"
    is WeeklyRate -> "${formatMoney(amountMinor, currency)} / week"
    is FixedPeriod -> "${formatMoney(totalAmountMinor, currency)} total"
    is FreeLoan -> "Free"
}

fun formatMoney(amountMinor: Long, currency: String = "AED"): String {
    if (amountMinor == 0L) return "Free"
    val whole = amountMinor / 100
    val fraction = (amountMinor % 100).toString().padStart(2, '0')
    return if (fraction == "00") "$currency $whole" else "$currency $whole.$fraction"
}

enum class PriceBasis {
    PerDay,
    FixedPeriod,
}

sealed interface RequestPriceConstraint

data class ExactPrice(
    val amountMinor: Long,
    val basis: PriceBasis,
) : RequestPriceConstraint

data class PriceRange(
    val minAmountMinor: Long,
    val maxAmountMinor: Long,
    val basis: PriceBasis,
) : RequestPriceConstraint

data class MaximumBudget(
    val maxAmountMinor: Long,
    val basis: PriceBasis,
) : RequestPriceConstraint

fun RequestPriceConstraint?.acceptsOffer(totalMinor: Long, durationDays: Int): Boolean {
    if (totalMinor < 0 || durationDays < 1) return false
    val constraint = this ?: return true
    val factor = when (constraint) {
        is ExactPrice -> if (constraint.basis == PriceBasis.PerDay) durationDays else 1
        is PriceRange -> if (constraint.basis == PriceBasis.PerDay) durationDays else 1
        is MaximumBudget -> if (constraint.basis == PriceBasis.PerDay) durationDays else 1
    }
    return when (constraint) {
        is ExactPrice -> totalMinor == constraint.amountMinor * factor
        is PriceRange -> totalMinor in (constraint.minAmountMinor * factor)..(constraint.maxAmountMinor * factor)
        is MaximumBudget -> totalMinor <= constraint.maxAmountMinor * factor
    }
}

enum class ItemPhotoKind {
    ProjectorFront,
    ProjectorConnections,
    ProjectorCase,
    LanternPair,
    LanternCharging,
    TripodExtended,
    TripodFolded,
    SewingReference,
    GameNightReference,
    SessionPreview,
}

data class ItemPhoto(
    val id: String,
    val kind: ItemPhotoKind,
    val contentDescription: String,
    val caption: String,
)

data class ListingDraft(
    val title: String,
    val category: String,
    val description: String,
    val condition: String,
    val accessories: String,
    val pricingModel: PricingModel,
    val price: String,
    val minimumLoanDays: String,
    val maximumLoanDays: String,
    val availability: String,
    val latestReturn: String,
    val replacementValue: String,
    val handoffLocation: String,
    val safetyNotes: String,
    val hasSessionPhoto: Boolean,
)

data class RequestDraft(
    val title: String,
    val category: String,
    val description: String,
    val neededFrom: String,
    val neededUntil: String,
    val circle: String,
    val maximumBudget: String,
    val pickupFlexibility: String,
    val hasReferencePreview: Boolean = false,
)

data class ItemListing(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val condition: String,
    val priceLabel: String,
    val pricing: ListingPricing,
    val availability: String,
    val ownerId: String,
    val ownerDisplayName: String,
    val photos: List<ItemPhoto> = emptyList(),
    val ownerCircle: String = "NYU Abu Dhabi",
    val accessories: List<String> = emptyList(),
    val pricingExplanation: String = "See the price shown for the selected borrowing period.",
    val minimumLoanDays: Int? = null,
    val maximumLoanDays: Int? = null,
    val latestReturn: String? = null,
    val handoffArea: String? = null,
    val safetyNotes: String? = null,
    val addedThisSession: Boolean = false,
)

data class CommunityRequest(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val neededWindow: String,
    val requesterId: String,
    val requesterDisplayName: String,
    val neededFrom: String = neededWindow.substringBefore(" – "),
    val neededUntil: String = neededWindow.substringAfter(" – ", missingDelimiterValue = neededWindow),
    val requesterCircle: String = "NYU Abu Dhabi",
    val maximumBudgetLabel: String? = null,
    val priceConstraint: RequestPriceConstraint? = null,
    val requestedDays: Int = 1,
    val pickupFlexibility: String = "Flexible pickup on campus",
    val referencePhoto: ItemPhoto? = null,
    val linkedListingIds: List<String> = emptyList(),
    val freshness: String = "Posted recently",
    val status: String = "Open",
    val addedThisSession: Boolean = false,
)

data class MemberReview(
    val id: String,
    val reviewerDisplayName: String,
    val rating: Int,
    val text: String,
)

data class MemberStatistics(
    val completedBorrows: Int,
    val onTimeReturns: Int,
    val completedLenderLoans: Int,
    val listingAccuracyPercent: Int,
    val onTimeHandoffs: Int,
    val totalHandoffs: Int,
    val activeListings: Int,
    val openBorrowingActivities: Int,
    val openLendingActivities: Int,
) {
    val borrowerReliability: Int
        get() = percentage(onTimeReturns, completedBorrows)

    val handoffReliability: Int
        get() = percentage(onTimeHandoffs, totalHandoffs)

    val lenderIntegrity: Int
        get() = ((listingAccuracyPercent + handoffReliability) / 2f).roundToInt()

    private fun percentage(part: Int, total: Int): Int =
        if (total == 0) 0 else (part * 100f / total).roundToInt()
}

data class MemberProfile(
    val id: String,
    val displayName: String,
    val circle: String = "NYU Abu Dhabi",
    val verified: Boolean = true,
    val memberSince: String,
    val participationSummary: String,
    val statistics: MemberStatistics,
    val borrowerRating: Double,
    val borrowerReviewCount: Int,
    val publicReviews: List<MemberReview>,
) {
    val initial: String get() = displayName.firstOrNull()?.uppercase() ?: "?"
}

enum class ActivityRole(val label: String) {
    Borrowing("Borrowing"),
    Lending("Lending"),
}

enum class ActivityKind {
    BorrowRequest,
    HelpOffer,
}

enum class ActivityStatus(val label: String) {
    PendingApproval("Pending approval"),
    WaitingForRequester("Waiting for requester"),
}

data class MarketplaceActivity(
    val id: String,
    val transactionId: String,
    val memberId: String,
    val counterpartyId: String,
    val role: ActivityRole,
    val kind: ActivityKind,
    val title: String,
    val status: ActivityStatus,
    val periodLabel: String,
    val totalMinor: Long,
    val currency: String = "AED",
) {
    val totalLabel: String get() = formatMoney(totalMinor, currency)
}

data class LoanTransaction(
    val id: String,
    val listingId: String,
    val borrowerId: String,
    val lenderId: String,
    val durationDays: Int,
    val totalMinor: Long,
    val status: ActivityStatus = ActivityStatus.PendingApproval,
)

enum class OfferStatus(val label: String) {
    WaitingForRequester("Waiting for requester"),
}

data class HelpOffer(
    val id: String,
    val requestId: String,
    val listingId: String,
    val lenderId: String,
    val requesterId: String,
    val totalMinor: Long,
    val status: OfferStatus = OfferStatus.WaitingForRequester,
)

data class MemberNotification(
    val id: String,
    val memberId: String,
    val message: String,
)

fun ListingDraft.validationErrors(): Map<String, String> = buildMap {
    if (title.isBlank()) put("title", "Add a title.")
    if (category.isBlank()) put("category", "Add a category.")
    if (description.isBlank()) put("description", "Describe the item.")
    if (!hasSessionPhoto) put("photo", "Add at least one photo preview.")
    if (pricingModel != PricingModel.Free && price.toDoubleOrNull()?.let { it > 0.0 } != true) {
        put("price", "Enter a price greater than zero.")
    }
    val minimumDays = minimumLoanDays.toIntOrNull()
    val maximumDays = maximumLoanDays.toIntOrNull()
    if (minimumDays == null || minimumDays < 1) put("minimumLoanDays", "Use at least one day.")
    if (maximumDays == null || maximumDays < 1) put("maximumLoanDays", "Use at least one day.")
    if (minimumDays != null && maximumDays != null && maximumDays < minimumDays) {
        put("maximumLoanDays", "Maximum must be at least the minimum.")
    }
    if (availability.isBlank()) put("availability", "Describe when it is available.")
    if (latestReturn.isBlank()) put("latestReturn", "Add the latest return date.")
    if (replacementValue.toDoubleOrNull()?.let { it >= 0.0 } != true) {
        put("replacementValue", "Enter an approximate value.")
    }
    if (handoffLocation.isBlank()) put("handoffLocation", "Add a public handoff location.")
}

fun RequestDraft.validationErrors(): Map<String, String> = buildMap {
    if (title.isBlank()) put("title", "Add a title.")
    if (category.isBlank()) put("category", "Add a category.")
    if (description.isBlank()) put("description", "Describe what you need.")
    if (neededFrom.isBlank()) put("neededFrom", "Add a start date or time.")
    if (neededUntil.isBlank()) put("neededUntil", "Add an end date or time.")
    if (circle.isBlank()) put("circle", "Choose a circle.")
    if (pickupFlexibility.isBlank()) put("pickupFlexibility", "Describe pickup flexibility.")
    if (maximumBudget.isNotBlank() && maximumBudget.toDoubleOrNull()?.let { it >= 0.0 } != true) {
        put("maximumBudget", "Enter a valid budget or leave it blank.")
    }
}
