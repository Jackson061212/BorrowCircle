package com.example.borrowcircle.app

import com.example.borrowcircle.app.navigation.TopLevelDestination
import kotlin.test.Test
import kotlin.test.assertEquals

class BorrowCircleAppStateTest {
    @Test
    fun createOpensChooserWithoutReplacingSelectedDestination() {
        val requests = BorrowCircleAppState(selectedDestination = TopLevelDestination.Requests)

        val chooser = requests.select(TopLevelDestination.Create)

        assertEquals(TopLevelDestination.Requests, chooser.selectedDestination)
        assertEquals(AppSurface.CreateChooser, chooser.surface)
    }

    @Test
    fun onlyOneSurfaceCanBeOpenAtATime() {
        val item = BorrowCircleAppState().openItem("listing-1")
        val member = item.openMember("member-omar")
        val request = member.openRequest("request-2")

        assertEquals(AppSurface.ItemDetails("listing-1"), item.surface)
        assertEquals(AppSurface.MemberDetails("member-omar"), member.surface)
        assertEquals(AppSurface.RequestDetails("request-2"), request.surface)
        assertEquals(AppSurface.Closed, request.dismissSurface().surface)
    }

    @Test
    fun chooserEmitsBothCreationFlowsAndCanDismissSafely() {
        val chooser = BorrowCircleAppState().select(TopLevelDestination.Create)

        val listing = chooser.beginCreate(CreateFlow.ListItem)
        val request = chooser.beginCreate(CreateFlow.PostRequest)

        assertEquals(AppSurface.Create(CreateFlow.ListItem), listing.surface)
        assertEquals(AppSurface.Create(CreateFlow.PostRequest), request.surface)
        assertEquals(BorrowCircleAppState(), listing.dismissSurface())
    }

    @Test
    fun successfulCreationRoutesToRelevantFeed() {
        val listingComplete = BorrowCircleAppState(surface = AppSurface.Create(CreateFlow.ListItem))
            .finishCreate(TopLevelDestination.Discover)
        val requestComplete = BorrowCircleAppState(surface = AppSurface.Create(CreateFlow.PostRequest))
            .finishCreate(TopLevelDestination.Requests)

        assertEquals(TopLevelDestination.Discover, listingComplete.selectedDestination)
        assertEquals(TopLevelDestination.Requests, requestComplete.selectedDestination)
        assertEquals(AppSurface.Closed, listingComplete.surface)
        assertEquals(AppSurface.Closed, requestComplete.surface)
    }

    @Test
    fun helpAndLinkedCreationRetainTheStableRequestId() {
        val requestDetail = BorrowCircleAppState().openRequest("request-1")

        val helping = requestDetail.beginHelp("request-1")
        val creating = helping.beginLinkedListing("request-1")
        val finished = creating.finishLinkedListing("request-1")

        assertEquals(AppSurface.HelpRequest("request-1"), helping.surface)
        assertEquals(AppSurface.Create(CreateFlow.ListItem, "request-1"), creating.surface)
        assertEquals(AppSurface.RequestDetails("request-1"), finished.surface)
        assertEquals(AppSurface.RequestDetails("request-1"), helping.dismissHelp("request-1").surface)
    }

    @Test
    fun borrowCheckoutReturnsToItemOrFinishesInBorrowingActivity() {
        val checkout = BorrowCircleAppState().openItem("listing-2").beginBorrowCheckout("listing-2")

        assertEquals(AppSurface.BorrowCheckout("listing-2"), checkout.surface)
        assertEquals(AppSurface.ItemDetails("listing-2"), checkout.dismissBorrowCheckout("listing-2").surface)
        assertEquals(TopLevelDestination.Activity, checkout.finishTransaction().selectedDestination)
        assertEquals(AppSurface.Closed, checkout.finishTransaction().surface)
    }

    @Test
    fun linkedListingAdvancesFromHelpToOfferReview() {
        val review = BorrowCircleAppState()
            .beginHelp("request-1")
            .reviewOffer("request-1", "listing-3")

        assertEquals(AppSurface.OfferReview("request-1", "listing-3"), review.surface)
        assertEquals(AppSurface.HelpRequest("request-1"), review.dismissOfferReview("request-1").surface)
    }
}
