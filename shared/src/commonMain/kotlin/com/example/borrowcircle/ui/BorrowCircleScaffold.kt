package com.example.borrowcircle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.borrowcircle.app.navigation.TopLevelDestination
import com.example.borrowcircle.app.navigation.topLevelDestinations
import com.example.borrowcircle.app.theme.BorrowCircleColors

private val WideLayoutBreakpoint = 840.dp

@Composable
fun BorrowCircleScaffold(
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    content: @Composable (isWideLayout: Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            val isWideLayout = maxWidth >= WideLayoutBreakpoint

            if (isWideLayout) {
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail(
                        selectedDestination = selectedDestination,
                        onDestinationSelected = onDestinationSelected,
                    )
                    VerticalDivider(color = BorrowCircleColors.Border)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 1180.dp),
                        ) {
                            content(true)
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        content(false)
                    }
                    HorizontalDivider(color = BorrowCircleColors.Border)
                    BottomNavigation(
                        selectedDestination = selectedDestination,
                        onDestinationSelected = onDestinationSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationRail(
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(224.dp)
            .fillMaxHeight()
            .background(BorrowCircleColors.Surface)
            .padding(horizontal = 16.dp, vertical = 28.dp),
    ) {
        BorrowCircleWordmark(modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(modifier = Modifier.height(34.dp))
        topLevelDestinations.forEach { destination ->
            NavigationRailItem(
                destination = destination,
                selected = destination == selectedDestination,
                onClick = { onDestinationSelected(destination) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun NavigationRailItem(
    destination: TopLevelDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val isCreate = destination == TopLevelDestination.Create
    val background = when {
        selected -> BorrowCircleColors.AccentSoft
        else -> Color.Transparent
    }
    val contentColor = if (selected) BorrowCircleColors.Accent else BorrowCircleColors.TextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(background, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .semantics {
                role = if (isCreate) Role.Button else Role.Tab
                if (!isCreate) this.selected = selected
                contentDescription = "${destination.label} destination"
            }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(28.dp)
                    .background(BorrowCircleColors.Accent, RoundedCornerShape(3.dp)),
            )
            Spacer(modifier = Modifier.width(11.dp))
        }
        Box(
            modifier = Modifier
                .size(if (isCreate) 40.dp else 24.dp)
                .background(
                    color = if (isCreate) BorrowCircleColors.Accent else Color.Transparent,
                    shape = RoundedCornerShape(if (isCreate) 10.dp else 0.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            ShellIcon(
                destination = destination,
                color = if (isCreate) Color.White else contentColor,
                size = if (isCreate) 23.dp else 24.dp,
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = destination.label,
            color = contentColor,
            fontSize = 15.sp,
            fontWeight = if (selected || isCreate) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun BottomNavigation(
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(BorrowCircleColors.Surface)
            .padding(horizontal = 4.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        topLevelDestinations.forEach { destination ->
            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                destination = destination,
                selected = destination == selectedDestination,
                onClick = { onDestinationSelected(destination) },
            )
        }
    }
}

@Composable
private fun BottomNavigationItem(
    modifier: Modifier,
    destination: TopLevelDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val isCreate = destination == TopLevelDestination.Create
    val contentColor = when {
        isCreate -> Color.White
        selected -> BorrowCircleColors.Accent
        else -> BorrowCircleColors.TextSecondary
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .semantics {
                role = if (isCreate) Role.Button else Role.Tab
                if (!isCreate) this.selected = selected
                contentDescription = "${destination.label} destination"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(if (isCreate) 40.dp else 34.dp)
                .background(
                    color = if (isCreate) BorrowCircleColors.Accent else Color.Transparent,
                    shape = RoundedCornerShape(if (isCreate) 10.dp else 8.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            ShellIcon(
                destination = destination,
                color = contentColor,
                size = if (isCreate) 23.dp else 22.dp,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = destination.label,
            color = if (selected) BorrowCircleColors.Accent else BorrowCircleColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(3.dp)
                .background(
                    color = if (selected) BorrowCircleColors.Accent else Color.Transparent,
                    shape = RoundedCornerShape(3.dp),
                ),
        )
    }
}

@Composable
fun BorrowCircleWordmark(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            text = "Borrow",
            color = BorrowCircleColors.TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.6).sp,
        )
        Text(
            text = "Circle",
            color = BorrowCircleColors.Accent,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.6).sp,
        )
    }
}

@Composable
private fun ShellIcon(
    destination: TopLevelDestination,
    color: Color,
    size: Dp = 24.dp,
) {
    Canvas(
        modifier = Modifier
            .size(size)
            .semantics { contentDescription = destination.label },
    ) {
        val strokeWidth = 1.9.dp.toPx()
        val stroke = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val scale = this.size.minDimension / 24f
        fun p(value: Float) = value * scale

        when (destination) {
            TopLevelDestination.Discover -> {
                drawCircle(
                    color = color,
                    radius = p(6.8f),
                    center = Offset(p(10f), p(10f)),
                    style = stroke,
                )
                drawLine(
                    color = color,
                    start = Offset(p(15.2f), p(15.2f)),
                    end = Offset(p(21f), p(21f)),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }

            TopLevelDestination.Requests -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(p(4f), p(2.5f)),
                    size = Size(p(16f), p(19f)),
                    cornerRadius = CornerRadius(p(2f), p(2f)),
                    style = stroke,
                )
                listOf(8f, 12f, 16f).forEach { y ->
                    drawLine(
                        color = color,
                        start = Offset(p(8f), p(y)),
                        end = Offset(p(16f), p(y)),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                    )
                }
            }

            TopLevelDestination.Create -> {
                drawLine(
                    color = color,
                    start = Offset(p(12f), p(5f)),
                    end = Offset(p(12f), p(19f)),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(p(5f), p(12f)),
                    end = Offset(p(19f), p(12f)),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }

            TopLevelDestination.Activity -> {
                drawCircle(
                    color = color,
                    radius = p(9f),
                    center = Offset(p(12f), p(12f)),
                    style = stroke,
                )
                drawLine(
                    color = color,
                    start = Offset(p(12f), p(7f)),
                    end = Offset(p(12f), p(12f)),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(p(12f), p(12f)),
                    end = Offset(p(16f), p(14f)),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }

            TopLevelDestination.Profile -> {
                drawCircle(
                    color = color,
                    radius = p(3.5f),
                    center = Offset(p(12f), p(7f)),
                    style = stroke,
                )
                val path = Path().apply {
                    moveTo(p(5f), p(21f))
                    cubicTo(p(5.5f), p(15.5f), p(8f), p(13.5f), p(12f), p(13.5f))
                    cubicTo(p(16f), p(13.5f), p(18.5f), p(15.5f), p(19f), p(21f))
                }
                drawPath(path = path, color = color, style = stroke)
            }
        }
    }
}
