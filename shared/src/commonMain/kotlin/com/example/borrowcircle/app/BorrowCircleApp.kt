package com.example.borrowcircle.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.borrowcircle.app.navigation.TopLevelDestination
import com.example.borrowcircle.app.theme.BorrowCircleTheme
import com.example.borrowcircle.data.DemoContentRepository
import com.example.borrowcircle.data.model.ActivityRole
import com.example.borrowcircle.ui.BorrowCircleScaffold
import com.example.borrowcircle.ui.HelpRequestDialog
import com.example.borrowcircle.ui.ItemDetailDialog
import com.example.borrowcircle.ui.MemberProfileDialog
import com.example.borrowcircle.ui.CreateChooserDialog
import com.example.borrowcircle.ui.CreateListingDialog
import com.example.borrowcircle.ui.CreateRequestDialog
import com.example.borrowcircle.ui.DestinationContent
import com.example.borrowcircle.ui.RequestDetailDialog
import com.example.borrowcircle.ui.BorrowCheckoutDialog
import com.example.borrowcircle.ui.OfferReviewDialog

@Composable
fun BorrowCircleApp() {
    var appState by remember { mutableStateOf(BorrowCircleAppState()) }
    var createdContentFocus by remember { mutableStateOf<CreatedContentFocus?>(null) }
    var selectedActivityRole by remember { mutableStateOf(ActivityRole.Borrowing) }
    var activityNotice by remember { mutableStateOf<String?>(null) }
    val repository = remember { DemoContentRepository() }

    BorrowCircleTheme {
        BorrowCircleScaffold(
            selectedDestination = appState.selectedDestination,
            onDestinationSelected = { destination ->
                appState = appState.select(destination)
            },
        ) { isWideLayout ->
            DestinationContent(
                destination = appState.selectedDestination,
                isWideLayout = isWideLayout,
                listings = repository.listings,
                requests = repository.requests,
                currentMember = repository.currentMember,
                members = repository.members,
                activities = repository.activities,
                selectedActivityRole = selectedActivityRole,
                activityNotice = activityNotice,
                createdContentFocus = createdContentFocus,
                onCreatedContentFocused = { createdContentFocus = null },
                onListingSelected = { listingId -> appState = appState.openItem(listingId) },
                onRequestSelected = { requestId -> appState = appState.openRequest(requestId) },
                onMemberSelected = { memberId -> appState = appState.openMember(memberId) },
                onActivityRoleSelected = { selectedActivityRole = it },
            )
        }

        when (val surface = appState.surface) {
            is AppSurface.ItemDetails -> ItemDetailDialog(
                listingId = surface.itemId,
                listing = repository.listingById(surface.itemId),
                onDismiss = { appState = appState.dismissSurface() },
                onOwnerSelected = { memberId -> appState = appState.openMember(memberId) },
                onRequestBorrow = { listingId -> appState = appState.beginBorrowCheckout(listingId) },
                canRequestBorrow = repository.listingById(surface.itemId)?.ownerId != DemoContentRepository.CurrentMemberId,
            )

            is AppSurface.RequestDetails -> {
                val request = repository.requestById(surface.requestId)
                RequestDetailDialog(
                    requestId = surface.requestId,
                    request = request,
                    linkedListings = request?.linkedListingIds.orEmpty().mapNotNull(repository::listingById),
                    onDismiss = { appState = appState.dismissSurface() },
                    onHelp = { requestId -> appState = appState.beginHelp(requestId) },
                    onRequesterSelected = { memberId -> appState = appState.openMember(memberId) },
                )
            }

            is AppSurface.MemberDetails -> MemberProfileDialog(
                memberId = surface.memberId,
                member = repository.memberById(surface.memberId),
                onDismiss = { appState = appState.dismissSurface() },
            )

            is AppSurface.HelpRequest -> HelpRequestDialog(
                request = repository.requestById(surface.requestId),
                currentUserListings = repository.currentUserListings(),
                onDismiss = { appState = appState.dismissHelp(surface.requestId) },
                onLinkListing = { listingId ->
                    appState = appState.reviewOffer(surface.requestId, listingId)
                },
                onCreateListing = { appState = appState.beginLinkedListing(surface.requestId) },
            )

            is AppSurface.BorrowCheckout -> {
                val listing = repository.listingById(surface.itemId)
                BorrowCheckoutDialog(
                    listing = listing,
                    lender = listing?.let { repository.memberById(it.ownerId) },
                    onDismiss = { appState = appState.dismissBorrowCheckout(surface.itemId) },
                    onConfirm = { durationDays ->
                        repository.submitBorrowRequest(surface.itemId, durationDays)
                        selectedActivityRole = ActivityRole.Borrowing
                        activityNotice = "Borrow request sent. It is now pending lender approval."
                        appState = appState.finishTransaction()
                    },
                )
            }

            is AppSurface.OfferReview -> {
                val request = repository.requestById(surface.requestId)
                OfferReviewDialog(
                    request = request,
                    listing = repository.listingById(surface.listingId),
                    requester = request?.let { repository.memberById(it.requesterId) },
                    onDismiss = { appState = appState.dismissOfferReview(surface.requestId) },
                    onConfirm = {
                        repository.submitHelpOffer(surface.requestId, surface.listingId)
                        selectedActivityRole = ActivityRole.Lending
                        activityNotice = "Offer sent. It is waiting for the requester."
                        appState = appState.finishTransaction()
                    },
                )
            }

            AppSurface.CreateChooser -> CreateChooserDialog(
                onDismiss = { appState = appState.dismissSurface() },
                onListItem = { appState = appState.beginCreate(CreateFlow.ListItem) },
                onPostRequest = { appState = appState.beginCreate(CreateFlow.PostRequest) },
            )

            is AppSurface.Create -> when (surface.flow) {
                CreateFlow.ListItem -> CreateListingDialog(
                    onDismiss = { appState = appState.dismissSurface() },
                    onSubmit = { draft ->
                        val listing = repository.createListing(draft)
                        val linkedRequestId = surface.linkedRequestId
                        if (linkedRequestId != null) {
                            appState = appState.reviewOffer(linkedRequestId, listing.id)
                        } else {
                            createdContentFocus = CreatedContentFocus(
                                destination = TopLevelDestination.Discover,
                                contentId = listing.id,
                            )
                            appState = appState.finishCreate(TopLevelDestination.Discover)
                        }
                    },
                )

                CreateFlow.PostRequest -> CreateRequestDialog(
                    onDismiss = { appState = appState.dismissSurface() },
                    onSubmit = { draft ->
                        val request = repository.createRequest(draft)
                        createdContentFocus = CreatedContentFocus(
                            destination = TopLevelDestination.Requests,
                            contentId = request.id,
                        )
                        appState = appState.finishCreate(TopLevelDestination.Requests)
                    },
                )
            }

            AppSurface.Closed -> Unit
        }
    }
}
