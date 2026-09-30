package com.salaam.compass

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

private val Gold = Color(0xFFE8C547)
private val QiblaGreen = Color(0xFF2ECC71)

/**
 * Compass rose that rotates so N points to true north, with the Kaaba marker on the ring
 * and a needle pointing at the Qibla. [heading] is continuous, so animation never spins the long way.
 */
@Composable
fun CompassDial(heading: Double, qibla: Double, reliable: Boolean, aligned: Boolean, modifier: Modifier = Modifier) {
    val animatedHeading by animateFloatAsState(
        targetValue = heading.toFloat(),
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "heading",
    )
    val textPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER; typeface = Typeface.DEFAULT_BOLD }
    }
    val needleColor = when {
        !reliable -> Color.Gray
        aligned -> QiblaGreen
        else -> Gold
    }

    Canvas(modifier) {
        val c = center
        val r = size.minDimension / 2f * 0.92f

        rotate(-animatedHeading, c) {
            drawCircle(Color(0xFF0E4D33), r, c)
            drawCircle(Color.White.copy(alpha = 0.8f), r, c, style = Stroke(r * 0.015f))
            for (deg in 0 until 360 step 5) {
                val major = deg % 30 == 0
                val len = if (major) r * 0.09f else r * 0.04f
                rotate(deg.toFloat(), c) {
                    drawLine(
                        Color.White.copy(alpha = if (major) 0.9f else 0.45f),
                        Offset(c.x, c.y - r), Offset(c.x, c.y - r + len),
                        strokeWidth = if (major) r * 0.012f else r * 0.006f,
                    )
                }
            }
            textPaint.textSize = r * 0.13f
            listOf("N" to 0f, "E" to 90f, "S" to 180f, "W" to 270f).forEach { (label, deg) ->
                rotate(deg, c) {
                    textPaint.color = (if (label == "N") Color(0xFFFF5252) else Color.White).toArgb()
                    drawContext.canvas.nativeCanvas.drawText(label, c.x, c.y - r * 0.72f, textPaint)
                }
            }
            // Kaaba marker on the ring at the Qibla bearing.
            rotate(qibla.toFloat(), c) {
                val s = r * 0.13f
                val topLeft = Offset(c.x - s / 2, c.y - r * 0.98f)
                drawRect(Color(0xFF111111), topLeft, Size(s, s))
                drawRect(Gold, Offset(topLeft.x, topLeft.y + s * 0.2f), Size(s, s * 0.14f))
            }
        }

        // Qibla needle: points toward the Kaaba relative to where the phone is facing.
        rotate(qibla.toFloat() - animatedHeading, c) {
            val needle = Path().apply {
                moveTo(c.x, c.y - r * 0.7f)
                lineTo(c.x + r * 0.09f, c.y)
                lineTo(c.x - r * 0.09f, c.y)
                close()
            }
            drawPath(needle, needleColor)
            val tail = Path().apply {
                moveTo(c.x, c.y + r * 0.4f)
                lineTo(c.x + r * 0.09f, c.y)
                lineTo(c.x - r * 0.09f, c.y)
                close()
            }
            drawPath(tail, Color.White.copy(alpha = 0.5f))
        }
        drawCircle(Color.White, r * 0.05f, c)

        // Fixed marker: the direction the top of the phone is facing.
        val marker = Path().apply {
            moveTo(c.x, c.y - r * 1.07f)
            lineTo(c.x + r * 0.05f, c.y - r * 0.99f)
            lineTo(c.x - r * 0.05f, c.y - r * 0.99f)
            close()
        }
        drawPath(marker, Color.White)
    }
}
