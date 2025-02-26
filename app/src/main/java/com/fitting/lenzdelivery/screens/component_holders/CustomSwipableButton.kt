package com.fitting.lenzdelivery.screens.component_holders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeToButton(
    modifier: Modifier = Modifier, height: Dp = 56.dp, onSwipeComplete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var thumbSize by remember { mutableStateOf(0) }
    var containerWidth by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    var isActivated by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }

    // Only animate the offset when returning to start or snapping to end
    val shouldAnimate = remember { mutableStateOf(false) }
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX,
        animationSpec = tween(durationMillis = 300),
        label = "swipeAnimation",
        // Only animate when returning to start or completing the swipe
        finishedListener = { shouldAnimate.value = false })

    val maxOffset = remember(containerWidth, thumbSize) {
        (containerWidth - thumbSize).toFloat()
    }

    // Use the position based on whether we're animating or directly following finger
    val effectiveOffsetX = if (shouldAnimate.value) animatedOffsetX else offsetX

    AnimatedVisibility(visible = isVisible,
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { it }) {
        Box(modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (isActivated) Color.Green else Color(0xFF03A9F4))
            .onSizeChanged { containerWidth = it.width }) {
            Text(
                text = "Swipe to Assign",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 16.dp)
            )

            Box(modifier = Modifier
                .offset { IntOffset(effectiveOffsetX.roundToInt(), 0) }
                .size(height)
                .padding(2.dp)
                .shadow(4.dp, RoundedCornerShape(percent = 50))
                .clip(RoundedCornerShape(percent = 50))
                .background(Color.White)
                .onSizeChanged { thumbSize = it.width }
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = {
                        // Turn off animation when dragging starts
                        shouldAnimate.value = false
                    }, onDrag = { change, dragAmount ->
                        change.consume()
                        if (!isActivated) {
                            // Direct thumb movement with finger - no animation during drag
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxOffset)
                        }
                    }, onDragEnd = {
                        if (offsetX >= maxOffset * 0.8f && !isActivated) {
                            // Success - snap to end with animation
                            shouldAnimate.value = true
                            isActivated = true
                            offsetX = maxOffset

                            coroutineScope.launch {
                                onSwipeComplete()
                                delay(300) // Wait for the animation to complete
                                isVisible = false // Trigger the disappearing animation
                            }
                        } else {
                            // Failed - return to start with animation
                            shouldAnimate.value = true
                            offsetX = 0f
                        }
                    })
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFF03A9F4),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp)
                )
            }
        }
    }
}