package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Custom modifier that draws a realistic soft skeuomorphic dual shadow (raised depth).
 * Top-Left: Light highlight (white)
 * Bottom-Right: Dark shadow (soft grey)
 */
fun Modifier.skeuomorphicShadow(
    cornerRadius: Dp = 24.dp,
    offset: Dp = 4.dp,
    blur: Dp = 6.dp,
    isPressed: Boolean = false,
    darkShadowColor: Color? = null,
    lightShadowColor: Color? = null
): Modifier = this.composed {
    val isDark = isSystemInDarkTheme()
    val darkColor = darkShadowColor ?: if (isDark) MacOSDarkShadowDark else MacOSLightShadowDark
    val lightColor = lightShadowColor ?: if (isDark) MacOSDarkShadowLight else MacOSLightShadowLight

    val currentOffset = if (isPressed) offset / 3 else offset
    val currentBlur = if (isPressed) blur / 2 else blur

    this.drawBehind {
        drawIntoCanvas { canvas ->
            // 1. Draw bottom-right dark shadow
            val darkPaint = Paint().asFrameworkPaint().apply {
                color = Color.Transparent.toArgb()
                setShadowLayer(
                    currentBlur.toPx(),
                    currentOffset.toPx(),
                    currentOffset.toPx(),
                    darkColor.toArgb()
                )
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                darkPaint
            )

            // 2. Draw top-left light highlight
            val lightPaint = Paint().asFrameworkPaint().apply {
                color = Color.Transparent.toArgb()
                setShadowLayer(
                    currentBlur.toPx(),
                    -currentOffset.toPx(),
                    -currentOffset.toPx(),
                    lightColor.toArgb()
                )
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                lightPaint
            )
        }
    }
}

/**
 * Custom modifier that draws a realistic inset (sunken/hollow) shadow inside a component.
 */
fun Modifier.skeuomorphicInset(
    cornerRadius: Dp = 12.dp,
    depth: Dp = 2.dp,
    insetShadowColor: Color? = null,
    highlightColor: Color? = null
): Modifier = this.composed {
    val isDark = isSystemInDarkTheme()
    val shadowColor = insetShadowColor ?: if (isDark) Color(0x80000000) else Color(0x2B000000)
    val lightColor = highlightColor ?: if (isDark) Color(0x1AFFFFFF) else Color(0x80FFFFFF)

    this.drawBehind {
        drawIntoCanvas { canvas ->
            // Subtle top-left inset shadow
            val paintTopLeft = Paint().asFrameworkPaint().apply {
                color = Color.Transparent.toArgb()
                setShadowLayer(
                    depth.toPx() * 2,
                    depth.toPx(),
                    depth.toPx(),
                    shadowColor.toArgb()
                )
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                paintTopLeft
            )

            // Subtle bottom-right offset highlight
            val paintBottomRight = Paint().asFrameworkPaint().apply {
                color = Color.Transparent.toArgb()
                setShadowLayer(
                    depth.toPx(),
                    -depth.toPx(),
                    -depth.toPx(),
                    lightColor.toArgb()
                )
            }
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                paintBottomRight
            )
        }
    }
}

@Composable
fun SkeuomorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 5.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .skeuomorphicShadow(cornerRadius = cornerRadius, offset = elevation)
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .padding(1.dp), // Tiny border to emulate high precision beveling
        contentAlignment = Alignment.Center
    ) {
        // Soft gradient overlays to look like metallic or acrylic lighting
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isSystemInDarkTheme()) 0.03f else 0.4f),
                            Color.Transparent
                        )
                    )
                ),
            content = content
        )
    }
}

@Composable
fun SkeuomorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedElevation by animateDpAsState(targetValue = if (isPressed) elevation / 3 else elevation, label = "elevation")
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1.0f, label = "scale")

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
            .skeuomorphicShadow(cornerRadius = cornerRadius, offset = animatedElevation, isPressed = isPressed)
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    }
                )
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
fun SkeuomorphicInsetField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val fieldBg = if (isSystemInDarkTheme()) MacOSDarkSidebar else MacOSLightSidebar

    Box(
        modifier = modifier
            .height(48.dp)
            .skeuomorphicInset(cornerRadius = cornerRadius, depth = 1.5.dp)
            .background(fieldBg, RoundedCornerShape(cornerRadius))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

/**
 * Classic macOS traffic-light buttons on a beautiful Title Bar
 */
@Composable
fun MacOSTitleBar(
    title: String,
    modifier: Modifier = Modifier,
    onCloseClick: (() -> Unit)? = null,
    onMinimizeClick: (() -> Unit)? = null,
    onMaximizeClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                Brush.verticalGradient(
                    colors = if (isSystemInDarkTheme()) {
                        listOf(Color(0xFF2E2E2E), Color(0xFF1E1E1E))
                    } else {
                        listOf(Color(0xFFFCFCFC), Color(0xFFECEFF1))
                    }
                )
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // macOS Traffic Lights
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Red (Close)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF5F56))
                    .clickable(enabled = onCloseClick != null) { onCloseClick?.invoke() }
            )
            // Yellow (Minimize)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFFBD2E))
                    .clickable(enabled = onMinimizeClick != null) { onMinimizeClick?.invoke() }
            )
            // Green (Maximize)
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF27C93F))
                    .clickable(enabled = onMaximizeClick != null) { onMaximizeClick?.invoke() }
            )
        }

        // Title
        Text(
            text = title,
            fontSize = 14.sp,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        // Spacer to balance
        Spacer(modifier = Modifier.width(52.dp))
    }
}

/**
 * 3D Embossed Container for high-fidelity macOS/skeuomorphic 3D Emoji Icons
 */
@Composable
fun Apple3DIcon(
    emoji: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    bgGradient: List<Color>? = null
) {
    val defaultGradient = if (isSystemInDarkTheme()) {
        listOf(Color(0xFF3A3A3C), Color(0xFF1C1C1E))
    } else {
        listOf(Color(0xFFFFFFFF), Color(0xFFE5E5EA))
    }

    Box(
        modifier = modifier
            .size(size)
            .skeuomorphicShadow(cornerRadius = size / 3, offset = 3.dp, blur = 5.dp)
            .background(
                Brush.linearGradient(colors = bgGradient ?: defaultGradient),
                RoundedCornerShape(size / 3)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = (size.value * 0.5f).sp
        )
    }
}
