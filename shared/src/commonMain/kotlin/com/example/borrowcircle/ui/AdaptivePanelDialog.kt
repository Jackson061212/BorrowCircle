package com.example.borrowcircle.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

internal const val WidePanelBreakpoint = 840

@Composable
internal fun AdaptivePanelDialog(
    title: String,
    onDismiss: () -> Unit,
    dimAmount: Float = 0.08f,
    content: @Composable ColumnScope.(isWide: Boolean) -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    var hasOpened by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        hasOpened = true
    }
    LaunchedEffect(visible, hasOpened) {
        if (!visible && hasOpened) {
            delay(210)
            onDismiss()
        }
    }
    val requestDismiss = { visible = false }

    Dialog(
        onDismissRequest = requestDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWide = maxWidth >= WidePanelBreakpoint.dp
            val panelWidth = (maxWidth * 0.40f).coerceIn(400.dp, 520.dp)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (isWide) dimAmount else 0f))
                    .safeDrawingPadding(),
                contentAlignment = if (isWide) Alignment.CenterEnd else Alignment.Center,
            ) {
                if (isWide) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            animationSpec = tween(220),
                            initialOffsetX = { width -> width },
                        ) + fadeIn(tween(180)),
                        exit = slideOutHorizontally(
                            animationSpec = tween(200),
                            targetOffsetX = { width -> width },
                        ) + fadeOut(tween(160)),
                    ) {
                        PanelSurface(
                            title = title,
                            isWide = true,
                            panelWidth = panelWidth,
                            onDismiss = requestDismiss,
                            content = content,
                        )
                    }
                } else if (visible) {
                    PanelSurface(
                        title = title,
                        isWide = false,
                        panelWidth = panelWidth,
                        onDismiss = requestDismiss,
                        content = content,
                    )
                }
            }
        }
    }
}

@Composable
private fun PanelSurface(
    title: String,
    isWide: Boolean,
    panelWidth: androidx.compose.ui.unit.Dp,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.(isWide: Boolean) -> Unit,
) {
    Surface(
        modifier = if (isWide) {
            Modifier
                .width(panelWidth)
                .fillMaxHeight()
        } else {
            Modifier.fillMaxSize()
        },
        color = MaterialTheme.colorScheme.background,
        shape = if (isWide) RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp) else RectangleShape,
        shadowElevation = if (isWide) 12.dp else 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .height(64.dp)
                    .padding(horizontal = 10.dp),
            ) {
                if (isWide) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 10.dp),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .semantics { contentDescription = "Close $title" },
                    ) {
                        CloseIcon()
                    }
                } else {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterStart),
                    ) { Text("Back") }
                    Text(
                        text = title,
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.SemiBold,
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .semantics { contentDescription = "Close $title" },
                    ) {
                        CloseIcon()
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    content(isWide)
                }
            }
        }
    }
}

@Composable
private fun CloseIcon() {
    Text(
        text = "X",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
    )
}
