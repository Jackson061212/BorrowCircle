package com.example.borrowcircle.app

import com.example.borrowcircle.app.navigation.TopLevelDestination

enum class CreateFlow {
    ListItem,
    PostRequest,
}

sealed interface AppSurface {
    data object Closed : AppSurface
    data object CreateChooser : AppSurface
    data class Create(val flow: CreateFlow, val linkedRequestId: String? = null) : AppSurface
    data class ItemDetails(val itemId: String) : AppSurface
    data class RequestDetails(val requestId: String) : AppSurface
    data class MemberDetails(val memberId: String) : AppSurface
    data class HelpRequest(val requestId: String) : AppSurface
    data class BorrowCheckout(val itemId: String) : AppSurface
    data class OfferReview(val requestId: String, val listingId: String) : AppSurface
}

data class CreatedContentFocus(
    val destination: TopLevelDestination,
    val contentId: String,
)

data class BorrowCircleAppState(
    val selectedDestination: TopLevelDestination = TopLevelDestination.Discover,
    val surface: AppSurface = AppSurface.Closed,
) {
    fun select(destination: TopLevelDestination): BorrowCircleAppState =
        if (destination == TopLevelDestination.Create) {
            copy(surface = AppSurface.CreateChooser)
        } else {
            copy(selectedDestination = destination, surface = AppSurface.Closed)
        }

    fun beginCreate(flow: CreateFlow): BorrowCircleAppState =
        copy(surface = AppSurface.Create(flow))

    fun dismissSurface(): BorrowCircleAppState = copy(surface = AppSurface.Closed)

    fun finishCreate(destination: TopLevelDestination): BorrowCircleAppState = copy(
        selectedDestination = destination,
        surface = AppSurface.Closed,
    )

    fun openItem(itemId: String): BorrowCircleAppState =
        copy(surface = AppSurface.ItemDetails(itemId))

    fun openRequest(requestId: String): BorrowCircleAppState =
        copy(surface = AppSurface.RequestDetails(requestId))

    fun openMember(memberId: String): BorrowCircleAppState =
        copy(surface = AppSurface.MemberDetails(memberId))

    fun beginHelp(requestId: String): BorrowCircleAppState =
        copy(surface = AppSurface.HelpRequest(requestId))

    fun beginBorrowCheckout(itemId: String): BorrowCircleAppState =
        copy(surface = AppSurface.BorrowCheckout(itemId))

    fun dismissBorrowCheckout(itemId: String): BorrowCircleAppState =
        copy(surface = AppSurface.ItemDetails(itemId))

    fun reviewOffer(requestId: String, listingId: String): BorrowCircleAppState =
        copy(surface = AppSurface.OfferReview(requestId, listingId))

    fun dismissOfferReview(requestId: String): BorrowCircleAppState =
        copy(surface = AppSurface.HelpRequest(requestId))

    fun finishTransaction(): BorrowCircleAppState = copy(
        selectedDestination = TopLevelDestination.Activity,
        surface = AppSurface.Closed,
    )

    fun dismissHelp(requestId: String): BorrowCircleAppState =
        copy(surface = AppSurface.RequestDetails(requestId))

    fun beginLinkedListing(requestId: String): BorrowCircleAppState =
        copy(surface = AppSurface.Create(CreateFlow.ListItem, linkedRequestId = requestId))

    fun finishLinkedListing(requestId: String): BorrowCircleAppState =
        copy(surface = AppSurface.RequestDetails(requestId))
}
