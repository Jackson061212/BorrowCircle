package com.example.borrowcircle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.borrowcircle.app.navigation.TopLevelDestination
import com.example.borrowcircle.app.CreatedContentFocus
import com.example.borrowcircle.app.theme.BorrowCircleColors
import com.example.borrowcircle.data.model.CommunityRequest
import com.example.borrowcircle.data.model.ActivityKind
import com.example.borrowcircle.data.model.ActivityRole
import com.example.borrowcircle.data.model.ItemListing
import com.example.borrowcircle.data.model.MarketplaceActivity
import com.example.borrowcircle.data.model.MemberProfile

@Composable
fun DestinationContent(
    destination: TopLevelDestination,
    isWideLayout: Boolean,
    listings: List<ItemListing>,
    requests: List<CommunityRequest>,
    currentMember: MemberProfile,
    members: List<MemberProfile>,
    activities: List<MarketplaceActivity>,
    selectedActivityRole: ActivityRole,
    activityNotice: String?,
    createdContentFocus: CreatedContentFocus?,
    onCreatedContentFocused: () -> Unit,
    onListingSelected: (String) -> Unit,
    onRequestSelected: (String) -> Unit,
    onMemberSelected: (String) -> Unit,
    onActivityRoleSelected: (ActivityRole) -> Unit,
) {
    val discoverListState = rememberLazyListState()
    val requestsListState = rememberLazyListState()
    val activityListState = rememberLazyListState()
    val profileListState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(createdContentFocus?.contentId, destination, isWideLayout) {
        if (createdContentFocus?.destination == destination) {
            val targetIndex = if (isWideLayout) 1 else 2
            when (destination) {
                TopLevelDestination.Discover -> discoverListState.scrollToItem(targetIndex)
                TopLevelDestination.Requests -> requestsListState.scrollToItem(targetIndex)
                else -> Unit
            }
            onCreatedContentFocused()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (destination) {
            TopLevelDestination.Discover -> DiscoverScreen(
                isWideLayout = isWideLayout,
                listings = listings,
                requests = requests,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                listState = discoverListState,
                onListingSelected = onListingSelected,
                onRequestSelected = onRequestSelected,
                onMemberSelected = onMemberSelected,
            )

            TopLevelDestination.Requests -> RequestsScreen(
                isWideLayout = isWideLayout,
                requests = requests,
                listState = requestsListState,
                onRequestSelected = onRequestSelected,
            )

            TopLevelDestination.Activity -> ActivityScreen(
                isWideLayout = isWideLayout,
                listState = activityListState,
                activities = activities,
                currentMemberId = currentMember.id,
                members = members,
                selectedRole = selectedActivityRole,
                activityNotice = activityNotice,
                onRoleSelected = onActivityRoleSelected,
            )

            TopLevelDestination.Profile -> ProfileScreen(
                isWideLayout = isWideLayout,
                listState = profileListState,
                currentMember = currentMember,
                members = members,
                onMemberSelected = onMemberSelected,
            )

            TopLevelDestination.Create -> Unit
        }

        if (isWideLayout) {
            NotificationMenu(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 36.dp),
            )
        }
    }
}

@Composable
private fun DiscoverScreen(
    isWideLayout: Boolean,
    listings: List<ItemListing>,
    requests: List<CommunityRequest>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onListingSelected: (String) -> Unit,
    onRequestSelected: (String) -> Unit,
    onMemberSelected: (String) -> Unit,
) {
    val filteredListings = listings.filter { listing ->
        val matchesQuery = searchQuery.isBlank() ||
            listing.title.contains(searchQuery.trim(), ignoreCase = true) ||
            listing.description.contains(searchQuery.trim(), ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || listing.category == selectedCategory
        matchesQuery && matchesCategory
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = pagePadding(isWideLayout),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        mobileHeader(isWideLayout)
        item(key = "discover-intro") {
            Column {
                Text(
                    text = "Good afternoon, Jackson",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = if (isWideLayout) 34.sp else 30.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.7).sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                CircleIdentity()
                Spacer(modifier = Modifier.height(28.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search items",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                        )
                    },
                    singleLine = true,
                    leadingIcon = { SearchIcon() },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("All", "Tech", "Outdoor", "Creative").forEach { category ->
                        CategoryChip(
                            label = category,
                            selected = selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle("Available in your circle")
            }
        }

        if (filteredListings.isEmpty()) {
            item(key = "listings-empty") {
                EmptySurface(
                    title = emptyTitle(searchQuery, selectedCategory),
                    message = "Try another search or publish a listing from Create.",
                )
            }
        } else {
            items(filteredListings, key = { it.id }) { listing ->
                ListingCard(
                    listing = listing,
                    onClick = { onListingSelected(listing.id) },
                    onOwnerClick = { onMemberSelected(listing.ownerId) },
                )
            }
        }

        item(key = "community-needs-heading") {
            Column(modifier = Modifier.padding(top = 18.dp)) {
                SectionTitle("Community needs")
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Recent open requests from verified members.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
        }
        if (requests.isEmpty()) {
            item(key = "requests-empty") {
                EmptySurface(
                    title = "No open requests yet",
                    message = "Your circle's unmet needs will stay visible here.",
                    compact = true,
                )
            }
        } else {
            items(requests.take(2), key = { "discover-${it.id}" }) { request ->
                RequestCard(
                    request = request,
                    compact = true,
                    onClick = { onRequestSelected(request.id) },
                )
            }
        }
        item(key = "discover-bottom-space") { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
private fun RequestsScreen(
    isWideLayout: Boolean,
    requests: List<CommunityRequest>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onRequestSelected: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = pagePadding(isWideLayout),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        mobileHeader(isWideLayout)
        item(key = "requests-intro") {
            DestinationHeading(
                title = "Community requests",
                description = "See what verified members need and offer an item when you can help.",
            )
        }
        if (requests.isEmpty()) {
            item(key = "requests-empty") {
                EmptySurface(
                    title = "No open requests yet",
                    message = "New requests from NYU Abu Dhabi members will appear here.",
                )
            }
        } else {
            items(requests, key = { it.id }) { request ->
                RequestCard(request = request, onClick = { onRequestSelected(request.id) })
            }
        }
        item(key = "requests-bottom-space") { Spacer(modifier = Modifier.height(18.dp)) }
    }
}

@Composable
private fun ActivityScreen(
    isWideLayout: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    activities: List<MarketplaceActivity>,
    currentMemberId: String,
    members: List<MemberProfile>,
    selectedRole: ActivityRole,
    activityNotice: String?,
    onRoleSelected: (ActivityRole) -> Unit,
) {
    val visibleActivities = activities.filter { it.memberId == currentMemberId && it.role == selectedRole }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = pagePadding(isWideLayout),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        mobileHeader(isWideLayout)
        item(key = "activity-intro") {
            DestinationHeading(
                title = "Activity",
                description = "Follow borrowing and lending from request through return.",
            )
        }
        activityNotice?.let { notice ->
            item(key = "activity-notice") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = notice,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        item(key = "activity-tabs") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActivityRole.entries.forEach { role ->
                    CategoryChip(
                        label = role.label,
                        selected = selectedRole == role,
                        onClick = { onRoleSelected(role) },
                    )
                }
            }
        }
        if (visibleActivities.isEmpty()) {
            item(key = "activity-empty-${selectedRole.name}") {
                EmptySurface(
                    title = "No ${selectedRole.label.lowercase()} activity",
                    message = "Confirmed requests and offers will stay visible here for this session.",
                )
            }
        } else {
            items(visibleActivities, key = { it.id }) { activity ->
                ActivityCard(
                    activity = activity,
                    counterparty = members.firstOrNull { it.id == activity.counterpartyId },
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: MarketplaceActivity, counterparty: MemberProfile?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(activity.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = if (activity.kind == ActivityKind.BorrowRequest) "Borrow request" else "Help offer",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                StatusBadge(activity.status.label)
            }
            Text(
                text = activity.periodLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = when (activity.role) {
                    ActivityRole.Borrowing -> "With ${counterparty?.displayName ?: "community lender"}"
                    ActivityRole.Lending -> "For ${counterparty?.displayName ?: "community borrower"}"
                },
                fontWeight = FontWeight.Medium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Prototype total", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                Text(
                    text = activity.totalLabel,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = when (activity.kind) {
                    ActivityKind.BorrowRequest -> "Next: wait for the lender to approve."
                    ActivityKind.HelpOffer -> "Next: wait for the requester to accept."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    isWideLayout: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    currentMember: MemberProfile,
    members: List<MemberProfile>,
    onMemberSelected: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = pagePadding(isWideLayout),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        mobileHeader(isWideLayout)
        item(key = "profile") {
            MemberProfileOverview(member = currentMember, showHeading = true)
        }
        item(key = "profile-community-heading") {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                SectionTitle("People in your circle")
                Text(
                    text = "Open a member profile to see score evidence and public reviews.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
        }
        items(members.filter { it.id != currentMember.id }, key = { "member-${it.id}" }) { member ->
            MemberLink(member = member, onClick = { onMemberSelected(member.id) })
        }
    }
}

private fun pagePadding(isWideLayout: Boolean): PaddingValues = PaddingValues(
    start = if (isWideLayout) 36.dp else 20.dp,
    top = if (isWideLayout) 32.dp else 20.dp,
    end = if (isWideLayout) 96.dp else 20.dp,
    bottom = 32.dp,
)

private fun LazyListScope.mobileHeader(isWideLayout: Boolean) {
    if (!isWideLayout) {
        item(key = "mobile-header") {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BorrowCircleWordmark()
                    NotificationMenu()
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun DestinationHeading(title: String, description: String) {
    Column {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.6).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            modifier = Modifier.widthIn(max = 620.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 17.sp,
            lineHeight = 25.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

private fun emptyTitle(searchQuery: String, category: String): String = when {
    searchQuery.isNotBlank() -> "No matches for “${searchQuery.trim()}”"
    category != "All" -> "No $category items yet"
    else -> "Your shared shelf is ready"
}

@Composable
private fun ListingCard(
    listing: ItemListing,
    onClick: () -> Unit,
    onOwnerClick: () -> Unit,
) {
    val hoverInteraction = remember { MutableInteractionSource() }
    val isHovered by hoverInteraction.collectIsHoveredAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(hoverInteraction)
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics {
                role = Role.Button
                contentDescription = "Open ${listing.title} details"
            }
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = if (isHovered) 3.dp else 0.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(listing.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${listing.category} · ${listing.condition}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                if (listing.addedThisSession) StatusBadge("Just added")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = listing.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ListingPriceChip(listing.priceLabel)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = listing.availability,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            TextButton(
                onClick = onOwnerClick,
                modifier = Modifier.semantics {
                    contentDescription = "Open ${listing.ownerDisplayName} member profile"
                },
                contentPadding = PaddingValues(horizontal = 0.dp),
            ) {
                Text("Lender · ${listing.ownerDisplayName}", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun RequestCard(
    request: CommunityRequest,
    compact: Boolean = false,
    onClick: () -> Unit,
) {
    val hoverInteraction = remember { MutableInteractionSource() }
    val isHovered by hoverInteraction.collectIsHoveredAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(hoverInteraction)
            .pointerHoverIcon(PointerIcon.Hand)
            .semantics {
                role = Role.Button
                contentDescription = "Open ${request.title} details"
            }
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = if (isHovered) 3.dp else 0.dp,
    ) {
        Column(modifier = Modifier.padding(if (compact) 18.dp else 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = request.category,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                StatusBadge(if (request.addedThisSession) "Just posted" else request.status)
            }
            Spacer(modifier = Modifier.height(9.dp))
            Text(
                text = request.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 22.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${request.neededWindow} · ${request.requesterDisplayName}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ListingPriceChip(text: String) {
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
private fun StatusBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CircleIdentity() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "N",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "NYU Abu Dhabi",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(44.dp)
            .semantics {
                role = Role.Tab
                contentDescription = "$label category"
            }
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(10.dp),
    ) {
        Box(modifier = Modifier.padding(horizontal = 18.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.25).sp,
    )
}

@Composable
private fun EmptySurface(title: String, message: String, compact: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(if (compact) 22.dp else 26.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            EmptyStateIcon()
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun NotificationMenu(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .clickable { expanded = true }
                .semantics { contentDescription = "Notifications" },
            contentAlignment = Alignment.Center,
        ) {
            BellIcon()
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("No new notifications") },
                onClick = { expanded = false },
            )
        }
    }
}

@Composable
private fun SearchIcon() {
    Canvas(
        modifier = Modifier
            .size(22.dp)
            .semantics { contentDescription = "Search" },
    ) {
        val stroke = 1.8.dp.toPx()
        drawCircle(
            color = BorrowCircleColors.TextSecondary,
            radius = size.minDimension * 0.28f,
            center = Offset(size.width * 0.43f, size.height * 0.43f),
            style = Stroke(width = stroke),
        )
        drawLine(
            color = BorrowCircleColors.TextSecondary,
            start = Offset(size.width * 0.64f, size.height * 0.64f),
            end = Offset(size.width * 0.86f, size.height * 0.86f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun EmptyStateIcon() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        drawCircle(
            color = BorrowCircleColors.Accent,
            radius = size.minDimension * 0.34f,
            center = Offset(size.width * 0.5f, size.height * 0.5f),
            style = Stroke(width = strokeWidth),
        )
        drawCircle(
            color = BorrowCircleColors.Accent,
            radius = strokeWidth * 0.55f,
            center = Offset(size.width * 0.5f, size.height * 0.5f),
        )
    }
}

@Composable
private fun BellIcon() {
    Canvas(
        modifier = Modifier
            .size(22.dp)
            .semantics { contentDescription = "Notifications" },
    ) {
        val color = BorrowCircleColors.TextPrimary
        val stroke = 1.8.dp.toPx()
        val path = Path().apply {
            moveTo(size.width * 0.24f, size.height * 0.70f)
            cubicTo(
                size.width * 0.31f,
                size.height * 0.60f,
                size.width * 0.31f,
                size.height * 0.44f,
                size.width * 0.31f,
                size.height * 0.36f,
            )
            cubicTo(
                size.width * 0.31f,
                size.height * 0.15f,
                size.width * 0.69f,
                size.height * 0.15f,
                size.width * 0.69f,
                size.height * 0.36f,
            )
            cubicTo(
                size.width * 0.69f,
                size.height * 0.44f,
                size.width * 0.69f,
                size.height * 0.60f,
                size.width * 0.76f,
                size.height * 0.70f,
            )
            close()
        }
        drawPath(path = path, color = color, style = Stroke(width = stroke, join = StrokeJoin.Round))
        drawLine(
            color = color,
            start = Offset(size.width * 0.42f, size.height * 0.82f),
            end = Offset(size.width * 0.58f, size.height * 0.82f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}
