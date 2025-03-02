package com.fitting.lenzdelivery.screens.component_holders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.EaseOutQuint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeToButton(
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
    onSwipeComplete: () -> Unit
) {
    val goldAccent = Color(0xFFD4AF37)
    val cream = Color(0xFFF5F5F0)
    val darkNavy = Color(0xFF1A2A3A)

    val coroutineScope = rememberCoroutineScope()
    var thumbSize by remember { mutableStateOf(0) }
    var containerWidth by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }
    var isActivated by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(true) }

    val shouldAnimate = remember { mutableStateOf(false) }
    val isDragging = remember { mutableStateOf(false) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(
            durationMillis = if (isActivated) 400 else 250,
            easing = if (isActivated) EaseOutQuart else EaseOutBack
        ),
        label = "swipeAnimation",
        finishedListener = { shouldAnimate.value = false }
    )

    val maxOffset = remember(containerWidth, thumbSize) {
        (containerWidth - thumbSize).toFloat().coerceAtLeast(0f)
    }

    val progress = if (maxOffset > 0) {
        ((if (shouldAnimate.value) animatedOffsetX else offsetX) / maxOffset).coerceIn(0f, 1f)
    } else {
        0f
    }

    val thumbRotation by animateFloatAsState(
        targetValue = if (isDragging.value) 2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "thumbRotation"
    )

    val backgroundBrush = Brush.horizontalGradient(
        colors = listOf(
            if (isActivated) goldAccent else cream,
            if (isActivated) goldAccent.copy(alpha = 0.8f) else cream
        ),
        startX = 0f,
        endX = (containerWidth * progress * 1.5f).coerceAtLeast(0f)
    )

    val checkmarkScale by animateFloatAsState(
        targetValue = if (isActivated) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseOutBack
        ),
        label = "checkmarkScale"
    )

    val effectiveOffsetX = if (shouldAnimate.value) animatedOffsetX else offsetX

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(animationSpec = tween(400, easing = EaseOutQuint)) +
                slideOutHorizontally(
                    animationSpec = tween(500, easing = EaseOutQuint)
                ) { it }
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(2.dp))
                .background(backgroundBrush)
                .border(
                    width = 1.dp,
                    color = goldAccent.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(2.dp)
                )
                .onSizeChanged { containerWidth = it.width }
        ) {
            Text(
                text = if (isActivated) "Assigned" else "Swipe to Assign",
                color = if (isActivated) cream else darkNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        alpha = if (isActivated) 1f else 1f - (progress * 0.7f)
                    }
            )

            Box(
                modifier = Modifier
                    .offset { IntOffset(effectiveOffsetX.roundToInt(), 0) }
                    .size(height)
                    .padding(4.dp)
                    .shadow(
                        elevation = if (isDragging.value) 6.dp else 2.dp,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .clip(RoundedCornerShape(2.dp))
                    .background(goldAccent)
                    .graphicsLayer {
                        rotationZ = thumbRotation
                        scaleX = if (isDragging.value) 1.05f else 1f
                        scaleY = if (isDragging.value) 1.05f else 1f
                    }
                    .onSizeChanged { thumbSize = it.width }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                shouldAnimate.value = false
                                isDragging.value = true
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (!isActivated) {
                                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxOffset)
                                }
                            },
                            onDragEnd = {
                                isDragging.value = false
                                if (offsetX >= maxOffset * 0.6f && !isActivated) {
                                    shouldAnimate.value = true
                                    isActivated = true
                                    offsetX = maxOffset

                                    coroutineScope.launch {
                                        onSwipeComplete()
                                        delay(700)
                                        isVisible = false
                                    }
                                } else {
                                    shouldAnimate.value = true
                                    offsetX = 0f
                                }
                            }
                        )
                    }
            ) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = if (isActivated) cream else darkNavy,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer {
                                alpha = 1f - checkmarkScale
                            }
                    )

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = cream,
                        modifier = Modifier
                            .size(22.dp)
                            .graphicsLayer {
                                alpha = checkmarkScale
                                scaleX = checkmarkScale
                                scaleY = checkmarkScale
                            }
                    )
                }
            }
        }
    }
}