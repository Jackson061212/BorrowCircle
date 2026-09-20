package com.example.borrowcircle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.borrowcircle.data.model.MemberProfile

@Composable
fun MemberProfileDialog(
    memberId: String,
    member: MemberProfile?,
    onDismiss: () -> Unit,
) {
    AdaptivePanelDialog(
        title = "Member profile",
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (member == null) {
                Text("Member not found", fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The profile $memberId is no longer available in this session.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                MemberProfileOverview(member = member)
            }
        }
    }
}

@Composable
internal fun MemberProfileOverview(
    member: MemberProfile,
    showHeading: Boolean = false,
) {
    val stats = member.statistics
    var showBorrowerBreakdown by remember(member.id) { mutableStateOf(false) }
    var showLenderBreakdown by remember(member.id) { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (showHeading) {
            Text("Profile", fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Your verified community identity and account summary.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 17.sp,
                lineHeight = 24.sp,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = member.initial,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(member.displayName, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                if (member.verified) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(7.dp),
                    ) {
                        Text(
                            text = "✓ Verified member",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Member since ${member.memberSince}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }
        }

        Text(
            text = member.participationSummary,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp,
        )

        ScoreCard(
            title = "Borrower Reliability",
            score = stats.borrowerReliability,
            evidence = "${stats.onTimeReturns} on-time returns from ${stats.completedBorrows} completed borrows",
            expanded = showBorrowerBreakdown,
            expandedText = "Calculated from finalized returns only. Ratings and written reviews are shown separately below.",
            onToggle = { showBorrowerBreakdown = !showBorrowerBreakdown },
        )
        ScoreCard(
            title = "Lender Integrity",
            score = stats.lenderIntegrity,
            evidence = "${stats.completedLenderLoans} completed loans · ${stats.listingAccuracyPercent}% listing accuracy",
            expanded = showLenderBreakdown,
            expandedText = "Combines listing accuracy with ${stats.onTimeHandoffs}/${stats.totalHandoffs} on-time handoffs (${stats.handoffReliability}%).",
            onToggle = { showLenderBreakdown = !showLenderBreakdown },
        )

        ProfileSection("Participation") {
            StatisticRow("Completed borrows", stats.completedBorrows.toString())
            StatisticRow("On-time returns", stats.onTimeReturns.toString())
            StatisticRow("Completed lender loans", stats.completedLenderLoans.toString())
            StatisticRow("Active listings", stats.activeListings.toString())
            StatisticRow("Open borrowing activity", stats.openBorrowingActivities.toString())
            StatisticRow("Open lending activity", stats.openLendingActivities.toString())
        }

        ProfileSection("Borrower reviews") {
            Text(
                text = "★ ${member.borrowerRating} from ${member.borrowerReviewCount} reviews",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Star ratings reflect member reviews; they are separate from the reliability score.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
            member.publicReviews.forEach { review ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(9.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "${review.rating} / 5 · ${review.reviewerDisplayName}",
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(review.text, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
internal fun MemberLink(
    member: MemberProfile,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Button
                contentDescription = "Open ${member.displayName} member profile"
            }
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(member.initial, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(member.displayName, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Lender Integrity ${member.statistics.lenderIntegrity} · ${member.borrowerRating} ★",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }
            Text("View", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ScoreCard(
    title: String,
    score: Int,
    evidence: String,
    expanded: Boolean,
    expandedText: String,
    onToggle: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text("$score / 100", color = ScoreGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(evidence, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, lineHeight = 19.sp)
            TextButton(onClick = onToggle) {
                Text(if (expanded) "Hide breakdown" else "View breakdown")
            }
            if (expanded) {
                Text(expandedText, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, lineHeight = 19.sp)
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
private fun StatisticRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private val ScoreGreen = Color(0xFF167548)
