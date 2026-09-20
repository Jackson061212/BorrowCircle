package com.example.borrowcircle.data

import androidx.compose.runtime.mutableStateListOf
import com.example.borrowcircle.data.model.CommunityRequest
import com.example.borrowcircle.data.model.ActivityKind
import com.example.borrowcircle.data.model.ActivityRole
import com.example.borrowcircle.data.model.ActivityStatus
import com.example.borrowcircle.data.model.DailyRate
import com.example.borrowcircle.data.model.FixedPeriod
import com.example.borrowcircle.data.model.FreeLoan
import com.example.borrowcircle.data.model.HelpOffer
import com.example.borrowcircle.data.model.ItemPhoto
import com.example.borrowcircle.data.model.ItemPhotoKind
import com.example.borrowcircle.data.model.ItemListing
import com.example.borrowcircle.data.model.ListingDraft
import com.example.borrowcircle.data.model.ListingPricing
import com.example.borrowcircle.data.model.LoanTransaction
import com.example.borrowcircle.data.model.MarketplaceActivity
import com.example.borrowcircle.data.model.MaximumBudget
import com.example.borrowcircle.data.model.MemberNotification
import com.example.borrowcircle.data.model.MemberProfile
import com.example.borrowcircle.data.model.MemberReview
import com.example.borrowcircle.data.model.MemberStatistics
import com.example.borrowcircle.data.model.PricingModel
import com.example.borrowcircle.data.model.PriceBasis
import com.example.borrowcircle.data.model.RequestDraft
import com.example.borrowcircle.data.model.WeeklyRate
import com.example.borrowcircle.data.model.acceptsOffer
import com.example.borrowcircle.data.model.displayLabel
import com.example.borrowcircle.data.model.maximumDays
import com.example.borrowcircle.data.model.minimumDays
import com.example.borrowcircle.data.model.totalMinorUnits
import com.example.borrowcircle.data.model.validationErrors

class DemoContentRepository {
    companion object {
        const val CurrentMemberId = "member-jackson"
    }

    private var nextListingId = 4
    private var nextRequestId = 4

    private val memberState = listOf(
        member(
            id = CurrentMemberId,
            name = "Jackson",
            memberSince = "September 2024",
            participation = "Active borrower and lender in the NYU Abu Dhabi circle",
            statistics = MemberStatistics(
                completedBorrows = 12,
                onTimeReturns = 11,
                completedLenderLoans = 9,
                listingAccuracyPercent = 94,
                onTimeHandoffs = 8,
                totalHandoffs = 9,
                activeListings = 1,
                openBorrowingActivities = 2,
                openLendingActivities = 2,
            ),
            rating = 4.7,
            reviewCount = 14,
            reviewAuthor = "Maya",
            reviewText = "Clear communication and a careful, on-time return.",
        ),
        member(
            id = "member-omar",
            name = "Omar",
            memberSince = "January 2023",
            participation = "Frequent tech lender and community helper",
            statistics = MemberStatistics(18, 17, 26, 97, 25, 26, 1, 1, 2),
            rating = 4.8,
            reviewCount = 23,
            reviewAuthor = "Priya",
            reviewText = "The projector matched the listing and pickup was easy to arrange.",
        ),
        member(
            id = "member-lina",
            name = "Lina",
            memberSince = "August 2023",
            participation = "Outdoor gear lender and residence volunteer",
            statistics = MemberStatistics(9, 8, 14, 93, 13, 14, 1, 1, 1),
            rating = 4.6,
            reviewCount = 11,
            reviewAuthor = "Jackson",
            reviewText = "Helpful handoff and the lantern set was accurately described.",
        ),
        member(
            id = "member-maya",
            name = "Maya",
            memberSince = "February 2022",
            participation = "Long-time creative-equipment contributor",
            statistics = MemberStatistics(21, 20, 17, 96, 16, 17, 0, 0, 1),
            rating = 4.9,
            reviewCount = 31,
            reviewAuthor = "Omar",
            reviewText = "Reliable borrower who returned everything clean and complete.",
        ),
        member(
            id = "member-priya",
            name = "Priya",
            memberSince = "May 2024",
            participation = "Regular borrower and peer-event organizer",
            statistics = MemberStatistics(7, 6, 5, 92, 5, 5, 0, 1, 0),
            rating = 4.5,
            reviewCount = 8,
            reviewAuthor = "Lina",
            reviewText = "Friendly coordination and good updates around the return time.",
        ),
        member(
            id = "member-nadia",
            name = "Nadia",
            memberSince = "October 2024",
            participation = "Creative-circle participant",
            statistics = MemberStatistics(5, 5, 3, 95, 3, 3, 0, 1, 0),
            rating = 4.8,
            reviewCount = 5,
            reviewAuthor = "Maya",
            reviewText = "Straightforward plans and a punctual return.",
        ),
        member(
            id = "member-yusuf",
            name = "Yusuf",
            memberSince = "March 2024",
            participation = "Campus maker-space participant",
            statistics = MemberStatistics(8, 7, 6, 90, 5, 6, 0, 1, 0),
            rating = 4.4,
            reviewCount = 7,
            reviewAuthor = "Omar",
            reviewText = "Kept me updated and returned the tools in good order.",
        ),
        member(
            id = "member-aisha",
            name = "Aisha",
            memberSince = "November 2023",
            participation = "Residence event coordinator",
            statistics = MemberStatistics(11, 10, 8, 94, 7, 8, 0, 1, 0),
            rating = 4.7,
            reviewCount = 12,
            reviewAuthor = "Priya",
            reviewText = "Thoughtful planning and dependable communication.",
        ),
    )

    private val listingState = mutableStateListOf(
        ItemListing(
            id = "listing-1",
            title = "Portable projector",
            category = "Tech",
            description = "Compact projector with HDMI cable and carry case.",
            condition = "Very good",
            priceLabel = "Free",
            pricing = FreeLoan(minDays = 1, maxDays = 3),
            availability = "Weekday evenings",
            ownerId = "member-omar",
            ownerDisplayName = "Omar",
            photos = listOf(
                ItemPhoto(
                    id = "projector-front",
                    kind = ItemPhotoKind.ProjectorFront,
                    contentDescription = "Portable projector viewed from the front",
                    caption = "Projector and remote",
                ),
                ItemPhoto(
                    id = "projector-connections",
                    kind = ItemPhotoKind.ProjectorConnections,
                    contentDescription = "HDMI and power connections on the portable projector",
                    caption = "HDMI and power ports",
                ),
                ItemPhoto(
                    id = "projector-case",
                    kind = ItemPhotoKind.ProjectorCase,
                    contentDescription = "Portable projector packed in its carry case",
                    caption = "Carry case and included cable",
                ),
            ),
            accessories = listOf("HDMI cable", "Power adapter", "Remote", "Carry case"),
            pricingExplanation = "Free for verified circle members. No charge will be made by this prototype.",
            minimumLoanDays = 1,
            maximumLoanDays = 3,
            latestReturn = "Friday at 22:00",
            handoffArea = "Campus Center lobby",
            safetyNotes = "Let the projector cool before packing it into the case.",
        ),
        ItemListing(
            id = "listing-2",
            title = "Camping lantern set",
            category = "Outdoor",
            description = "Two rechargeable lanterns for trips and outdoor events.",
            condition = "Good",
            priceLabel = "AED 8 / day",
            pricing = DailyRate(amountMinor = 800, minDays = 1, maxDays = 5),
            availability = "Available this weekend",
            ownerId = "member-lina",
            ownerDisplayName = "Lina",
            photos = listOf(
                ItemPhoto(
                    id = "lantern-pair",
                    kind = ItemPhotoKind.LanternPair,
                    contentDescription = "Pair of rechargeable camping lanterns",
                    caption = "Two-lantern set",
                ),
                ItemPhoto(
                    id = "lantern-charging",
                    kind = ItemPhotoKind.LanternCharging,
                    contentDescription = "Camping lantern charging ports and cables",
                    caption = "Charging cables included",
                ),
            ),
            accessories = listOf("Two USB charging cables", "Storage pouch"),
            pricingExplanation = "AED 8 for each borrowing day. Prototype only; no payment is processed.",
            minimumLoanDays = 1,
            maximumLoanDays = 5,
            latestReturn = "Sunday at 21:00",
            handoffArea = "East Plaza",
            safetyNotes = "Keep the charging-port covers closed outdoors.",
        ),
        ItemListing(
            id = "listing-3",
            title = "Tripod and phone mount",
            category = "Creative",
            description = "Full-height tripod with a universal phone adapter.",
            condition = "Good",
            priceLabel = "Free",
            pricing = FreeLoan(minDays = 1, maxDays = 7),
            availability = "Available through October",
            ownerId = CurrentMemberId,
            ownerDisplayName = "Jackson",
            photos = listOf(
                ItemPhoto(
                    id = "tripod-extended",
                    kind = ItemPhotoKind.TripodExtended,
                    contentDescription = "Full-height tripod with phone mount extended",
                    caption = "Full-height setup",
                ),
                ItemPhoto(
                    id = "tripod-folded",
                    kind = ItemPhotoKind.TripodFolded,
                    contentDescription = "Tripod folded beside its universal phone adapter",
                    caption = "Folded tripod and phone adapter",
                ),
            ),
            accessories = listOf("Universal phone adapter", "Quick-release plate", "Carry sleeve"),
            pricingExplanation = "Free for verified circle members. No charge will be made by this prototype.",
            minimumLoanDays = 1,
            maximumLoanDays = 7,
            latestReturn = "31 October",
            handoffArea = "Arts Center reception",
            safetyNotes = "Tighten all leg locks before mounting a phone.",
        ),
    )

    private val requestState = mutableStateListOf(
        CommunityRequest(
            id = "request-1",
            title = "Need a sewing kit",
            category = "Creative",
            description = "For a quick costume repair before Friday's rehearsal.",
            neededWindow = "Thursday evening – Friday afternoon",
            requesterId = "member-nadia",
            requesterDisplayName = "Nadia",
            neededFrom = "Thursday evening",
            neededUntil = "Friday afternoon",
            maximumBudgetLabel = "Up to AED 20",
            priceConstraint = MaximumBudget(maxAmountMinor = 2_000, basis = PriceBasis.FixedPeriod),
            pickupFlexibility = "Can meet anywhere on the main campus after 17:00.",
            referencePhoto = ItemPhoto(
                id = "sewing-reference",
                kind = ItemPhotoKind.SewingReference,
                contentDescription = "Reference image showing a compact sewing kit",
                caption = "A compact kit like this would work",
            ),
            freshness = "Posted 2 hours ago",
        ),
        CommunityRequest(
            id = "request-2",
            title = "Looking for a power drill",
            category = "Tools",
            description = "A short campus installation; drill bits would help too.",
            neededWindow = "Saturday, 10:00–14:00",
            requesterId = "member-yusuf",
            requesterDisplayName = "Yusuf",
            neededFrom = "Saturday at 10:00",
            neededUntil = "Saturday at 14:00",
            maximumBudgetLabel = "Up to AED 35",
            priceConstraint = MaximumBudget(maxAmountMinor = 3_500, basis = PriceBasis.FixedPeriod),
            pickupFlexibility = "Pickup Friday evening or Saturday morning near Campus Center.",
            freshness = "Posted yesterday",
        ),
        CommunityRequest(
            id = "request-3",
            title = "Board game for residence night",
            category = "Games",
            description = "Something suitable for six to eight players.",
            neededWindow = "Sunday evening",
            requesterId = "member-aisha",
            requesterDisplayName = "Aisha",
            neededFrom = "Sunday at 18:00",
            neededUntil = "Sunday at 23:00",
            pickupFlexibility = "Residence-hall pickup is preferred.",
            priceConstraint = MaximumBudget(maxAmountMinor = 0, basis = PriceBasis.FixedPeriod),
            referencePhoto = ItemPhoto(
                id = "game-night-reference",
                kind = ItemPhotoKind.GameNightReference,
                contentDescription = "Reference image of a tabletop game night",
                caption = "Looking for a group game for 6–8 people",
            ),
            freshness = "Posted 2 days ago",
        ),
    )

    private val loanTransactionState = mutableStateListOf<LoanTransaction>()
    private val offerState = mutableStateListOf<HelpOffer>()
    private val activityState = mutableStateListOf<MarketplaceActivity>()
    private val notificationState = mutableStateListOf<MemberNotification>()

    val listings: List<ItemListing> get() = listingState
    val requests: List<CommunityRequest> get() = requestState
    val members: List<MemberProfile> get() = memberState
    val loanTransactions: List<LoanTransaction> get() = loanTransactionState
    val offers: List<HelpOffer> get() = offerState
    val activities: List<MarketplaceActivity> get() = activityState
    val notifications: List<MemberNotification> get() = notificationState
    val currentMember: MemberProfile get() = memberById(CurrentMemberId)!!

    fun listingById(id: String): ItemListing? = listingState.firstOrNull { it.id == id }

    fun requestById(id: String): CommunityRequest? = requestState.firstOrNull { it.id == id }

    fun memberById(id: String): MemberProfile? = memberState.firstOrNull { it.id == id }

    fun currentUserListings(): List<ItemListing> = listingState.filter { it.ownerId == CurrentMemberId }

    fun activitiesFor(memberId: String, role: ActivityRole): List<MarketplaceActivity> =
        activityState.filter { it.memberId == memberId && it.role == role }

    fun notificationsFor(memberId: String): List<MemberNotification> =
        notificationState.filter { it.memberId == memberId }

    fun submitBorrowRequest(listingId: String, durationDays: Int): LoanTransaction {
        val listing = requireNotNull(listingById(listingId)) { "Listing must exist before it can be borrowed." }
        require(listing.ownerId != CurrentMemberId) { "Members cannot borrow their own listing." }
        val transactionId = "borrow-$listingId-$CurrentMemberId"
        loanTransactionState.firstOrNull { it.id == transactionId }?.let { return it }

        val totalMinor = listing.pricing.totalMinorUnits(durationDays)
        val transaction = LoanTransaction(
            id = transactionId,
            listingId = listingId,
            borrowerId = CurrentMemberId,
            lenderId = listing.ownerId,
            durationDays = durationDays,
            totalMinor = totalMinor,
        )
        loanTransactionState += transaction
        val period = "$durationDays ${if (durationDays == 1) "day" else "days"} · starts today"
        activityState += MarketplaceActivity(
            id = "$transactionId-borrowing",
            transactionId = transactionId,
            memberId = CurrentMemberId,
            counterpartyId = listing.ownerId,
            role = ActivityRole.Borrowing,
            kind = ActivityKind.BorrowRequest,
            title = listing.title,
            status = ActivityStatus.PendingApproval,
            periodLabel = period,
            totalMinor = totalMinor,
        )
        activityState += MarketplaceActivity(
            id = "$transactionId-lending",
            transactionId = transactionId,
            memberId = listing.ownerId,
            counterpartyId = CurrentMemberId,
            role = ActivityRole.Lending,
            kind = ActivityKind.BorrowRequest,
            title = listing.title,
            status = ActivityStatus.PendingApproval,
            periodLabel = period,
            totalMinor = totalMinor,
        )
        notificationState += MemberNotification(
            id = "$transactionId-notification",
            memberId = listing.ownerId,
            message = "Jackson requested to borrow ${listing.title}.",
        )
        return transaction
    }

    fun submitHelpOffer(requestId: String, listingId: String): HelpOffer {
        val request = requireNotNull(requestById(requestId)) { "Request must exist before an offer can be sent." }
        val listing = requireNotNull(listingById(listingId)) { "Listing must exist before it can be offered." }
        require(listing.ownerId == CurrentMemberId) { "Only the listing owner can offer an item." }
        require(request.requesterId != CurrentMemberId) { "Members cannot offer against their own request." }
        val offerId = "offer-$requestId-$listingId-$CurrentMemberId"
        offerState.firstOrNull { it.id == offerId }?.let { return it }

        val requestedDays = request.requestedDays.coerceIn(
            listing.pricing.minimumDays,
            listing.pricing.maximumDays,
        )
        val totalMinor = listing.pricing.totalMinorUnits(requestedDays)
        require(request.priceConstraint.acceptsOffer(totalMinor, requestedDays)) {
            "The listing price is outside the requester's allowed budget."
        }
        val offer = HelpOffer(
            id = offerId,
            requestId = requestId,
            listingId = listingId,
            lenderId = CurrentMemberId,
            requesterId = request.requesterId,
            totalMinor = totalMinor,
        )
        offerState += offer
        linkListingToRequest(requestId = requestId, listingId = listingId)
        val period = "${request.neededFrom} – ${request.neededUntil}"
        activityState += MarketplaceActivity(
            id = "$offerId-lending",
            transactionId = offerId,
            memberId = CurrentMemberId,
            counterpartyId = request.requesterId,
            role = ActivityRole.Lending,
            kind = ActivityKind.HelpOffer,
            title = "Offer: ${listing.title}",
            status = ActivityStatus.WaitingForRequester,
            periodLabel = period,
            totalMinor = totalMinor,
        )
        activityState += MarketplaceActivity(
            id = "$offerId-borrowing",
            transactionId = offerId,
            memberId = request.requesterId,
            counterpartyId = CurrentMemberId,
            role = ActivityRole.Borrowing,
            kind = ActivityKind.HelpOffer,
            title = "Offer: ${listing.title}",
            status = ActivityStatus.WaitingForRequester,
            periodLabel = period,
            totalMinor = totalMinor,
        )
        notificationState += MemberNotification(
            id = "$offerId-notification",
            memberId = request.requesterId,
            message = "Jackson offered ${listing.title} for ${request.title}.",
        )
        return offer
    }

    fun linkListingToRequest(requestId: String, listingId: String): CommunityRequest {
        require(listingById(listingId) != null) { "Listing must exist before it can be linked." }
        val requestIndex = requestState.indexOfFirst { it.id == requestId }
        require(requestIndex >= 0) { "Request must exist before a listing can be linked." }
        val request = requestState[requestIndex]
        if (listingId in request.linkedListingIds) return request
        return request.copy(linkedListingIds = request.linkedListingIds + listingId).also {
            requestState[requestIndex] = it
        }
    }

    fun createListing(draft: ListingDraft): ItemListing {
        require(draft.validationErrors().isEmpty()) { "Listing draft must be valid before submission." }
        val listingId = "listing-${nextListingId++}"
        val pricing = draft.toListingPricing()
        val listing = ItemListing(
            id = listingId,
            title = draft.title.trim(),
            category = draft.category.trim(),
            description = draft.description.trim(),
            condition = draft.condition.trim(),
            priceLabel = pricing.displayLabel(),
            pricing = pricing,
            availability = draft.availability.trim(),
            ownerId = CurrentMemberId,
            ownerDisplayName = currentMember.displayName,
            photos = listOf(
                ItemPhoto(
                    id = "$listingId-session-photo",
                    kind = ItemPhotoKind.SessionPreview,
                    contentDescription = "Session preview for ${draft.title.trim()}",
                    caption = "Primary session photo",
                ),
            ),
            accessories = draft.accessories.split(',').mapNotNull { value ->
                value.trim().takeIf { it.isNotEmpty() }
            },
            pricingExplanation = when (draft.pricingModel) {
                PricingModel.Free -> "Free for verified circle members. No charge will be made by this prototype."
                PricingModel.PerDay -> "AED ${draft.price.trim()} for each borrowing day. Prototype only; no payment is processed."
                PricingModel.PerWeek -> "AED ${draft.price.trim()} for each borrowing week. Prototype only; no payment is processed."
                PricingModel.FlatPeriod -> "AED ${draft.price.trim()} for the agreed period. Prototype only; no payment is processed."
            },
            minimumLoanDays = draft.minimumLoanDays.toIntOrNull(),
            maximumLoanDays = draft.maximumLoanDays.toIntOrNull(),
            latestReturn = draft.latestReturn.trim(),
            handoffArea = draft.handoffLocation.trim(),
            safetyNotes = draft.safetyNotes.trim().ifEmpty { null },
            addedThisSession = true,
        )
        listingState.add(0, listing)
        return listing
    }

    fun createRequest(draft: RequestDraft): CommunityRequest {
        require(draft.validationErrors().isEmpty()) { "Request draft must be valid before submission." }
        val requestId = "request-${nextRequestId++}"
        val request = CommunityRequest(
            id = requestId,
            title = draft.title.trim(),
            category = draft.category.trim(),
            description = draft.description.trim(),
            neededWindow = "${draft.neededFrom.trim()} – ${draft.neededUntil.trim()}",
            requesterId = CurrentMemberId,
            requesterDisplayName = currentMember.displayName,
            neededFrom = draft.neededFrom.trim(),
            neededUntil = draft.neededUntil.trim(),
            maximumBudgetLabel = draft.maximumBudget.trim().takeIf { it.isNotEmpty() }?.let { "Up to AED $it" },
            priceConstraint = draft.maximumBudget.trim().takeIf { it.isNotEmpty() }?.let {
                MaximumBudget(maxAmountMinor = it.toMinorUnits(), basis = PriceBasis.FixedPeriod)
            },
            pickupFlexibility = draft.pickupFlexibility.trim(),
            referencePhoto = if (draft.hasReferencePreview) {
                ItemPhoto(
                    id = "$requestId-reference",
                    kind = ItemPhotoKind.SessionPreview,
                    contentDescription = "Reference preview for ${draft.title.trim()}",
                    caption = "Session reference preview",
                )
            } else {
                null
            },
            freshness = "Just posted",
            addedThisSession = true,
        )
        requestState.add(0, request)
        return request
    }

    private fun member(
        id: String,
        name: String,
        memberSince: String,
        participation: String,
        statistics: MemberStatistics,
        rating: Double,
        reviewCount: Int,
        reviewAuthor: String,
        reviewText: String,
    ) = MemberProfile(
        id = id,
        displayName = name,
        memberSince = memberSince,
        participationSummary = participation,
        statistics = statistics,
        borrowerRating = rating,
        borrowerReviewCount = reviewCount,
        publicReviews = listOf(
            MemberReview(
                id = "$id-review-1",
                reviewerDisplayName = reviewAuthor,
                rating = rating.toInt().coerceIn(1, 5),
                text = reviewText,
            ),
        ),
    )

    private fun ListingDraft.toListingPricing(): ListingPricing {
        val minDays = minimumLoanDays.toInt()
        val maxDays = maximumLoanDays.toInt()
        val amountMinor = price.toMinorUnits()
        return when (pricingModel) {
            PricingModel.Free -> FreeLoan(minDays = minDays, maxDays = maxDays)
            PricingModel.PerDay -> DailyRate(amountMinor = amountMinor, minDays = minDays, maxDays = maxDays)
            PricingModel.PerWeek -> WeeklyRate(
                amountMinor = amountMinor,
                minWeeks = ((minDays + 6) / 7).coerceAtLeast(1),
                maxWeeks = ((maxDays + 6) / 7).coerceAtLeast(1),
            )
            PricingModel.FlatPeriod -> FixedPeriod(
                totalAmountMinor = amountMinor,
                startDate = availability.trim(),
                returnDate = latestReturn.trim(),
                billableDays = maxDays,
            )
        }
    }

    private fun String.toMinorUnits(): Long {
        if (isBlank()) return 0L
        val normalized = trim()
        val whole = normalized.substringBefore('.').toLong()
        val fraction = normalized.substringAfter('.', "").take(2).padEnd(2, '0').toLongOrNull() ?: 0L
        return whole * 100 + fraction
    }
}
