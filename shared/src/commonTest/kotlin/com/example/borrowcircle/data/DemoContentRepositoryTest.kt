package com.example.borrowcircle.data

import com.example.borrowcircle.data.model.ListingDraft
import com.example.borrowcircle.data.model.ActivityRole
import com.example.borrowcircle.data.model.ActivityStatus
import com.example.borrowcircle.data.model.DailyRate
import com.example.borrowcircle.data.model.FixedPeriod
import com.example.borrowcircle.data.model.FreeLoan
import com.example.borrowcircle.data.model.ExactPrice
import com.example.borrowcircle.data.model.MaximumBudget
import com.example.borrowcircle.data.model.PriceBasis
import com.example.borrowcircle.data.model.PriceRange
import com.example.borrowcircle.data.model.PricingModel
import com.example.borrowcircle.data.model.RequestDraft
import com.example.borrowcircle.data.model.validationErrors
import com.example.borrowcircle.data.model.totalMinorUnits
import com.example.borrowcircle.data.model.acceptsOffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DemoContentRepositoryTest {
    @Test
    fun successfulListingCreationIsImmediatelyVisible() {
        val repository = DemoContentRepository()
        val initialCount = repository.listings.size

        val listing = repository.createListing(validListingDraft())

        assertEquals(initialCount + 1, repository.listings.size)
        assertEquals(listing, repository.listings.first())
        assertEquals("Shared toolkit", listing.title)
        assertEquals(DemoContentRepository.CurrentMemberId, listing.ownerId)
        assertEquals("Jackson", listing.ownerDisplayName)
        assertTrue(listing.addedThisSession)
    }

    @Test
    fun successfulRequestCreationIsImmediatelyVisible() {
        val repository = DemoContentRepository()
        val initialCount = repository.requests.size

        val request = repository.createRequest(validRequestDraft())

        assertEquals(initialCount + 1, repository.requests.size)
        assertEquals(request, repository.requests.first())
        assertEquals("Need a toolkit", request.title)
        assertEquals(DemoContentRepository.CurrentMemberId, request.requesterId)
        assertEquals("Jackson", request.requesterDisplayName)
        assertTrue(request.addedThisSession)
    }

    @Test
    fun invalidDraftsAreRejectedWithoutMutation() {
        val repository = DemoContentRepository()
        val initialListingCount = repository.listings.size
        val invalid = validListingDraft().copy(title = "", hasSessionPhoto = false)

        assertTrue(invalid.validationErrors().containsKey("title"))
        assertTrue(invalid.validationErrors().containsKey("photo"))
        assertFailsWith<IllegalArgumentException> { repository.createListing(invalid) }
        assertEquals(initialListingCount, repository.listings.size)
    }

    @Test
    fun seededListingsResolveByStableIdAndIncludeNavigablePhotos() {
        val repository = DemoContentRepository()

        val projector = repository.listingById("listing-1")

        assertEquals("Portable projector", projector?.title)
        assertEquals(3, projector?.photos?.size)
        assertTrue(repository.listings.all { it.photos.isNotEmpty() })
        assertTrue(repository.listings.all { repository.memberById(it.ownerId) != null })
        assertNull(repository.listingById("missing-listing"))
    }

    @Test
    fun seededRequestsResolveByStableIdAndInvalidIdsStayMissing() {
        val repository = DemoContentRepository()

        assertEquals("Need a sewing kit", repository.requestById("request-1")?.title)
        assertNull(repository.requestById("missing-request"))
    }

    @Test
    fun linkingAnExistingListingCreatesAnOfferWithoutMatchingTheRequest() {
        val repository = DemoContentRepository()

        val updated = repository.linkListingToRequest(
            requestId = "request-1",
            listingId = "listing-3",
        )

        assertEquals(listOf("listing-3"), updated.linkedListingIds)
        assertEquals("Open", updated.status)
        assertEquals(updated, repository.requestById("request-1"))
    }

    @Test
    fun currentMemberIsJacksonWithEvidenceBasedNonPerfectScores() {
        val repository = DemoContentRepository()
        val jackson = repository.currentMember

        assertEquals("Jackson", jackson.displayName)
        assertEquals("J", jackson.initial)
        assertTrue(jackson.verified)
        assertEquals(92, jackson.statistics.borrowerReliability)
        assertEquals(92, jackson.statistics.lenderIntegrity)
        assertEquals(11, jackson.statistics.onTimeReturns)
        assertEquals(12, jackson.statistics.completedBorrows)
        assertTrue(repository.currentUserListings().all { it.ownerId == jackson.id })
        assertTrue(repository.currentUserListings().isNotEmpty())
    }

    @Test
    fun severalMemberProfilesResolveByStableId() {
        val repository = DemoContentRepository()

        assertEquals("Maya", repository.memberById("member-maya")?.displayName)
        assertEquals("Omar", repository.memberById("member-omar")?.displayName)
        assertEquals("Priya", repository.memberById("member-priya")?.displayName)
        assertEquals("Lina", repository.memberById("member-lina")?.displayName)
        assertTrue(repository.members.all { it.publicReviews.isNotEmpty() })
        assertNull(repository.memberById("missing-member"))
    }

    @Test
    fun exactPricingUsesMinorUnitsAndConsistentBillableDays() {
        assertEquals(800L, DailyRate(800, 1, 5).totalMinorUnits(1))
        assertEquals(2_400L, DailyRate(800, 1, 5).totalMinorUnits(3))
        assertEquals(0L, FreeLoan(1, 3).totalMinorUnits(3))
        assertEquals(
            4_000L,
            FixedPeriod(4_000, "20 Sep", "22 Sep", 2).totalMinorUnits(2),
        )
        assertFailsWith<IllegalArgumentException> { DailyRate(800, 1, 5).totalMinorUnits(0) }
        assertFailsWith<IllegalArgumentException> { DailyRate(800, 1, 5).totalMinorUnits(6) }
        assertFailsWith<IllegalArgumentException> {
            FixedPeriod(4_000, "20 Sep", "22 Sep", 2).totalMinorUnits(1)
        }
    }

    @Test
    fun requestPriceConstraintsValidateCalculatedTotals() {
        assertTrue(ExactPrice(800, PriceBasis.PerDay).acceptsOffer(2_400, durationDays = 3))
        assertTrue(PriceRange(500, 1_000, PriceBasis.PerDay).acceptsOffer(2_400, durationDays = 3))
        assertTrue(MaximumBudget(2_000, PriceBasis.FixedPeriod).acceptsOffer(0, durationDays = 1))
        assertTrue(!MaximumBudget(2_000, PriceBasis.FixedPeriod).acceptsOffer(2_001, durationDays = 1))
    }

    @Test
    fun borrowConfirmationCreatesOneTwoSidedPendingTransaction() {
        val repository = DemoContentRepository()

        val first = repository.submitBorrowRequest("listing-2", durationDays = 3)
        val duplicate = repository.submitBorrowRequest("listing-2", durationDays = 3)

        assertEquals(first, duplicate)
        assertEquals(1, repository.loanTransactions.size)
        assertEquals(2_400L, first.totalMinor)
        val borrower = repository.activitiesFor(DemoContentRepository.CurrentMemberId, ActivityRole.Borrowing)
        val lender = repository.activitiesFor("member-lina", ActivityRole.Lending)
        assertEquals(1, borrower.size)
        assertEquals(1, lender.size)
        assertEquals(first.id, borrower.single().transactionId)
        assertEquals(first.id, lender.single().transactionId)
        assertEquals(ActivityStatus.PendingApproval, borrower.single().status)
        assertEquals("AED 24", borrower.single().totalLabel)
        assertEquals(1, repository.notificationsFor("member-lina").size)
    }

    @Test
    fun helpOfferConfirmationCreatesOneTwoSidedWaitingActivity() {
        val repository = DemoContentRepository()

        val first = repository.submitHelpOffer("request-1", "listing-3")
        val duplicate = repository.submitHelpOffer("request-1", "listing-3")

        assertEquals(first, duplicate)
        assertEquals(1, repository.offers.size)
        assertEquals("Open", repository.requestById("request-1")?.status)
        assertEquals(listOf("listing-3"), repository.requestById("request-1")?.linkedListingIds)
        val lender = repository.activitiesFor(DemoContentRepository.CurrentMemberId, ActivityRole.Lending)
        val requester = repository.activitiesFor("member-nadia", ActivityRole.Borrowing)
        assertEquals(1, lender.size)
        assertEquals(1, requester.size)
        assertEquals(first.id, lender.single().transactionId)
        assertEquals(first.id, requester.single().transactionId)
        assertEquals(ActivityStatus.WaitingForRequester, lender.single().status)
        assertEquals(1, repository.notificationsFor("member-nadia").size)
    }

    private fun validListingDraft() = ListingDraft(
        title = "Shared toolkit",
        category = "Tools",
        description = "Basic hand tools for small campus repairs.",
        condition = "Good",
        accessories = "Hammer, screwdrivers",
        pricingModel = PricingModel.Free,
        price = "",
        minimumLoanDays = "1",
        maximumLoanDays = "7",
        availability = "Weekends",
        latestReturn = "End of month",
        replacementValue = "75",
        handoffLocation = "Campus Center lobby",
        safetyNotes = "Return tools to the case.",
        hasSessionPhoto = true,
    )

    private fun validRequestDraft() = RequestDraft(
        title = "Need a toolkit",
        category = "Tools",
        description = "For a small repair in the club room.",
        neededFrom = "Saturday morning",
        neededUntil = "Saturday evening",
        circle = "NYU Abu Dhabi",
        maximumBudget = "20",
        pickupFlexibility = "Flexible campus pickup",
    )
}
