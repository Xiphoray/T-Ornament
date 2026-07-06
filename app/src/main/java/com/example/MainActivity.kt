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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
    var isSettingsButtonVisible by remember { mutableStateOf(false) }
    var isSettingsPageOpen by remember { mutableStateOf(false) }
    // Macaron Background Color
    var backgroundColor by remember { mutableStateOf(Color(0xFFFDE2E4)) } 
    val chimeModel = remember { WindChimeModel() }

    LaunchedEffect(isSettingsButtonVisible) {
        if (isSettingsButtonVisible && !isSettingsPageOpen) {
            delay(3000)
            isSettingsButtonVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isSettingsButtonVisible = true
                        chimeModel.applyGust((Random.nextFloat() - 0.5f) * 15f)
                    }
                )
            }
    ) {
        // Only one ornament type currently: Wind Chime
        WindChimeScreen(chimeModel = chimeModel)

        AnimatedVisibility(
            visible = isSettingsButtonVisible && !isSettingsPageOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        ) {
            androidx.compose.material3.IconButton(
                onClick = { isSettingsPageOpen = true },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (isSettingsPageOpen) {
        SettingsPage(
            currentColor = backgroundColor,
            onColorSelected = { backgroundColor = it },
            onClose = {
                isSettingsPageOpen = false
                isSettingsButtonVisible = false
            }
        )
    }
}

@Composable
fun SettingsPage(
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onClose: () -> Unit
) {
    val macaronColors = listOf(
        Color(0xFFFDE2E4), // Pink
        Color(0xFFD3E4CD), // Mint
        Color(0xFFFEF6E4), // Yellow
        Color(0xFFE2D5F8), // Purple
        Color(0xFFBAE1FF)  // Blue
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) {
                onClose()
            }
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .clickable { /* prevent click through */ }
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Settings",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Ornament Type",
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Mock dropdown/selection for ornament type
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0F0F0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Wind Chime (风铃)",
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Text(
                    text = "Background Color",
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    macaronColors.forEach { color ->
                        val isSelected = color == currentColor
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                                .clickable { onColorSelected(color) }
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .background(Color.White.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                        }
                    }
                }

                androidx.compose.material3.Button(
                    onClick = onClose,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
                ) {
                    Text("Close", color = Color.White)
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
                colors = listOf(Color.Transparent, Color(0xFFD3E4CD).copy(alpha = 0.8f)), // Macaron Mint
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

                // Draw Inner Glow / Fill (Macaron Blue)
                drawPath(
                    path = domePath,
                    color = Color(0xFFBAE1FF).copy(alpha = 0.4f) 
                )

                // Geometric Accents inside dome (Macaron Green/Yellow)
                drawRect(
                    color = Color(0xFFBAFFC9).copy(alpha = 0.5f),
                    topLeft = Offset(-60f, 20f),
                    size = Size(80f, 60f)
                )
                drawRect(
                    color = Color(0xFFFEF6E4).copy(alpha = 0.6f),
                    topLeft = Offset(30f, 40f),
                    size = Size(50f, 50f)
                )

                // Draw Dome Stroke
                drawPath(
                    path = domePath,
                    color = Color(0xFFBAE1FF),
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
                        color = Color(0xFFD3E4CD), // Macaron Mint
                        start = Offset.Zero,
                        end = Offset(0f, stringLen),
                        strokeWidth = 3f
                    )

                    // Clapper bulb (Macaron Yellow)
                    drawCircle(
                        color = Color(0xFFFEF6E4),
                        radius = 10f,
                        center = Offset(0f, stringLen)
                    )

                    // The Tanzaku
                    val tanzakuWidth = 70f
                    val tanzakuHeight = 280f
                    val tTop = stringLen

                    // Macaron Pink Tanzaku
                    drawRect(
                        color = Color(0xFFFDE2E4).copy(alpha = 0.8f),
                        topLeft = Offset(-tanzakuWidth / 2, tTop),
                        size = Size(tanzakuWidth, tanzakuHeight)
                    )
                    drawRect(
                        color = Color(0xFFFDE2E4),
                        topLeft = Offset(-tanzakuWidth / 2, tTop),
                        size = Size(tanzakuWidth, tanzakuHeight),
                        style = Stroke(width = 3f)
                    )

                    // Minimalist Inscription/Texture (Darker Pink/Purple for contrast)
                    drawLine(
                        color = Color(0xFFE2D5F8).copy(alpha = 0.8f), // Macaron Purple
                        start = Offset(-12f, tTop + 40f),
                        end = Offset(-12f, tTop + 140f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color(0xFFE2D5F8).copy(alpha = 0.8f),
                        start = Offset(12f, tTop + 70f),
                        end = Offset(12f, tTop + 220f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color(0xFFE2D5F8).copy(alpha = 0.8f),
                        start = Offset(-2f, tTop + 180f),
                        end = Offset(-2f, tTop + 250f),
                        strokeWidth = 4f
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

        // Explicitly check for NaN and reset to 0 to prevent render thread freezing
        if (tanzakuAngle.isNaN()) tanzakuAngle = 0f
        if (bellAngle.isNaN()) bellAngle = 0f
        if (tanzakuVel.isNaN()) tanzakuVel = 0f
        if (bellVel.isNaN()) bellVel = 0f

        // Clamp angles to prevent excessive values from breaking the Canvas rendering
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

