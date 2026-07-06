package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MaterialTheme {
                OrnamentApp()
            }
        }
    }
}

@Composable
fun OrnamentApp() {
    var isSettingsVisible by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(Color.Black) }
    val chimeModel = remember { WindChimeModel() }

    LaunchedEffect(isSettingsVisible) {
        if (isSettingsVisible) {
            delay(5000)
            isSettingsVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isSettingsVisible = true
                        chimeModel.applyGust((Random.nextFloat() - 0.5f) * 15f)
                    }
                )
            }
    ) {
        WindChimeScreen(chimeModel = chimeModel)

        AnimatedVisibility(
            visible = isSettingsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            SettingsPanel(
                currentColor = backgroundColor,
                onColorSelected = { backgroundColor = it }
            )
        }
    }
}

@Composable
fun SettingsPanel(
    currentColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val colors = listOf(
        Color.Black,
        Color(0xFF121212), // Dark Gray
        Color(0xFF001524), // Midnight Blue
        Color(0xFF1B2A1E), // Dark Green
        Color(0xFF2E1A1A)  // Dark Red
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "T-Ornament",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Current: Wind Chime",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                colors.forEach { color ->
                    val isSelected = color == currentColor
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(36.dp)
                            .background(
                                color = color,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(color) }
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.White.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WindChimeScreen(chimeModel: WindChimeModel) {
    var timeMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        var lastTime = withFrameMillis { it }
        while (true) {
            timeMs = withFrameMillis { it }
            val rawDt = (timeMs - lastTime) / 1000f
            lastTime = timeMs
            if (rawDt > 0 && rawDt < 0.1f) {
                // Cap the simulation step to 1/30th of a second to ensure stability
                val simDt = rawDt.coerceAtMost(0.033f)
                chimeModel.update(simDt)
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentMs = timeMs // Read to trigger redraw
        val pivotX = size.width / 2
        val pivotY = size.height * 0.35f // Center vertically higher up

        val domeRadius = 140f
        
        // Support string extends from the top of the screen down to the pivot
        drawLine(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.5f)),
                startY = 0f,
                endY = pivotY
            ),
            start = Offset(pivotX, 0f),
            end = Offset(pivotX, pivotY),
            strokeWidth = 3f
        )

        // Rotate the entire assembly including the support string
        withTransform({
            translate(pivotX, pivotY)
            rotate(Math.toDegrees(chimeModel.bellAngle.toDouble()).toFloat(), Offset.Zero)
        }) {
                val domeHeight = 80f // height of the straight vertical part

                // Dome Path
                val domePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(-domeRadius, domeHeight)
                    lineTo(-domeRadius, 0f)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(-domeRadius, -domeRadius, domeRadius, domeRadius),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = 180f,
                        forceMoveTo = false
                    )
                    lineTo(domeRadius, domeHeight)
                }

                // Draw Inner Glow / Fill
                drawPath(
                    path = domePath,
                    color = Color.White.copy(alpha = 0.1f)
                )

                // Geometric Accents inside dome (Mountain Style)
                drawRect(
                    color = Color.White.copy(alpha = 0.15f),
                    topLeft = Offset(-60f, 20f),
                    size = Size(80f, 60f)
                )
                drawRect(
                    color = Color.White.copy(alpha = 0.08f),
                    topLeft = Offset(30f, 40f),
                    size = Size(50f, 50f)
                )

                // Draw Dome Stroke
                drawPath(
                    path = domePath,
                    color = Color.White.copy(alpha = 0.2f),
                    style = Stroke(width = 4f)
                )

                // Draw Tanzaku (Paper Strip)
                withTransform({
                    // Tanzaku pivots from inside the dome, around the center of the arc
                    // Subtract bellAngle so tanzakuAngle is relative to world vertical
                    rotate(Math.toDegrees((chimeModel.tanzakuAngle - chimeModel.bellAngle).toDouble()).toFloat(), Offset.Zero)
                }) {
                    // Internal Clapper String
                    val stringLen = 70f
                    drawLine(
                        color = Color.White.copy(alpha = 0.6f),
                        start = Offset.Zero,
                        end = Offset(0f, stringLen),
                        strokeWidth = 2f
                    )

                    // Clapper bulb
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 8f,
                        center = Offset(0f, stringLen)
                    )

                    // The Tanzaku
                    val tanzakuWidth = 70f
                    val tanzakuHeight = 280f
                    val tTop = stringLen

                    drawRect(
                        color = Color.White.copy(alpha = 0.15f),
                        topLeft = Offset(-tanzakuWidth / 2, tTop),
                        size = Size(tanzakuWidth, tanzakuHeight)
                    )
                    drawRect(
                        color = Color.White.copy(alpha = 0.1f),
                        topLeft = Offset(-tanzakuWidth / 2, tTop),
                        size = Size(tanzakuWidth, tanzakuHeight),
                        style = Stroke(width = 2f)
                    )

                    // Minimalist Inscription/Texture
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(-12f, tTop + 40f),
                        end = Offset(-12f, tTop + 140f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(12f, tTop + 70f),
                        end = Offset(12f, tTop + 220f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(-2f, tTop + 180f),
                        end = Offset(-2f, tTop + 250f),
                        strokeWidth = 3f
                    )
                }
        }
    }
}

class WindGenerator {
    private var time = 0f
    private var gust = 0f
    private var gustTarget = 0f

    fun getForce(dt: Float): Float {
        time += dt
        if (Random.nextFloat() < dt * 0.3f) {
            gustTarget = (Random.nextFloat() * 2f - 1f) * 6f // Reduced wind gust force
        }
        gust += (gustTarget - gust) * dt * 1.0f
        val base = sin(time * 0.9f) * 1.5f + sin(time * 2.3f) * 0.5f // Reduced base wind force
        return base + gust
    }
}

class WindChimeModel {
    val windGen = WindGenerator()

    var bellAngle = 0f
    var bellVel = 0f
    val bellLength = 400f

    var tanzakuAngle = 0f
    var tanzakuVel = 0f
    val tanzakuLength = 300f

    fun update(dt: Float) {
        val wind = windGen.getForce(dt)

        val g = 3000f

        // Tanzaku is light, catches more wind directly
        val tanzakuAccel = -(g / tanzakuLength) * sin(tanzakuAngle) + wind * 2.0f
        tanzakuVel += tanzakuAccel * dt
        tanzakuVel *= (1f - 1.2f * dt).coerceAtLeast(0f)
        
        // Bell is heavy, catches less wind
        // Reduced pull force significantly to prevent explicit Euler explosion at high dt
        val pullForce = (tanzakuAngle - bellAngle) * 50f
        val bellAccel = -(g / bellLength) * sin(bellAngle) + wind * 0.2f + pullForce
        bellVel += bellAccel * dt
        bellVel *= (1f - 1.8f * dt).coerceAtLeast(0f)
        
        tanzakuAngle += tanzakuVel * dt
        bellAngle += bellVel * dt

        // Clamp angles to prevent NaN/Infinity from breaking the Canvas rendering
        tanzakuAngle = tanzakuAngle.coerceIn(-3f, 3f)
        bellAngle = bellAngle.coerceIn(-3f, 3f)

        // Constrain tanzaku angle relative to the bell, to simulate the clapper hitting the glass
        val maxRelativeAngle = Math.toRadians(20.0).toFloat()
        if (tanzakuAngle > bellAngle + maxRelativeAngle) {
            tanzakuAngle = bellAngle + maxRelativeAngle
            val impact = tanzakuVel - bellVel // Relative velocity
            if (impact > 0) {
                tanzakuVel -= impact * 0.6f // Bounce off
                bellVel += impact * 0.2f // Transfer momentum to bell
            }
        } else if (tanzakuAngle < bellAngle - maxRelativeAngle) {
            tanzakuAngle = bellAngle - maxRelativeAngle
            val impact = tanzakuVel - bellVel // Relative velocity
            if (impact < 0) {
                tanzakuVel -= impact * 0.6f // Bounce off
                bellVel += impact * 0.2f // Transfer momentum to bell
            }
        }
    }

    fun applyGust(force: Float) {
        bellVel += force * 0.1f
        tanzakuVel += force
    }
}

