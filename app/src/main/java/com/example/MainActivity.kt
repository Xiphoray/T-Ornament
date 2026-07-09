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
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

enum class OrnamentType(val displayName: String) {
    WIND_CHIME("Wind Chime (风铃)"),
    TERU_TERU_BOZU("Teru Teru Bozu (晴天娃娃)")
}

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
    var currentOrnament by remember { mutableStateOf(OrnamentType.WIND_CHIME) }
    // Use dark theme by default, as requested
    var backgroundColor by remember { mutableStateOf(Color.Black) } 
    val windChimeModel = remember { WindChimeModel() }
    val teruTeruBozuModel = remember { TeruTeruBozuModel() }

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
                    }
                )
            }
    ) {
        when (currentOrnament) {
            OrnamentType.WIND_CHIME -> WindChimeScreen(model = windChimeModel)
            OrnamentType.TERU_TERU_BOZU -> TeruTeruBozuScreen(model = teruTeruBozuModel)
        }

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
            currentOrnament = currentOrnament,
            onOrnamentSelected = { currentOrnament = it },
            currentColor = backgroundColor,
            onColorSelected = { backgroundColor = it },
            onClose = {
                isSettingsPageOpen = false
                isSettingsButtonVisible = false
            }
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    currentOrnament: OrnamentType,
    onOrnamentSelected: (OrnamentType) -> Unit,
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onClose: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val bgColors = listOf(
        Color.Black,
        Color(0xFF121212), // Dark Gray
        Color(0xFF001524), // Midnight Blue
        Color(0xFF1B2A1E), // Dark Green
        Color(0xFF2E1A1A)  // Dark Red
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) {
                onClose()
            }
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth()
                .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) { /* prevent click through */ }
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Customization",
                    color = Color(0xFF1A1C1E),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Ornament Type",
                    color = Color(0xFF42474E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )
                
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        readOnly = true,
                        value = currentOrnament.displayName,
                        onValueChange = { },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = {
                            androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0061A4),
                            unfocusedBorderColor = Color(0xFFC3C7CF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        OrnamentType.entries.forEach { type ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = { 
                                    onOrnamentSelected(type)
                                    expanded = false 
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Background Theme",
                    color = Color(0xFF42474E),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    bgColors.forEach { color ->
                        val isSelected = color == currentColor
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF0061A4) else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { onColorSelected(color) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                androidx.compose.material3.Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4))
                ) {
                    Text("Apply & Close", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun WindChimeScreen(model: WindChimeModel) {
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
                model.update(simDt)
            }
        }
    }

    val domeRadius = 140f
    val domeHeight = 80f // height of the straight vertical part
    val domePath = remember {
        androidx.compose.ui.graphics.Path().apply {
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
    }

    val supportBrush = remember {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0xFFD3E4CD).copy(alpha = 0.8f)) // Macaron Mint
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentMs = timeMs // Read to trigger redraw
        val pivotX = size.width / 2f
        val pivotY = 0f // The rotation anchor is at the fading end (top of the screen)
        val bellYOffset = size.height * 0.35f // How far down the string goes before the bell
        
        withTransform({
            translate(pivotX, pivotY)
            rotate(Math.toDegrees(model.bellAngle.toDouble()).toFloat(), Offset.Zero)
        }) {
                // Support string extends from the top of the screen down to the bell
                drawLine(
                    brush = supportBrush,
                    start = Offset.Zero,
                    end = Offset(0f, bellYOffset),
                    strokeWidth = 3f
                )

                // Translate down to the center of the bell's dome arc
                withTransform({
                    translate(0f, bellYOffset + domeRadius)
                }) {
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
                        rotate(Math.toDegrees((model.tanzakuAngle - model.bellAngle).toDouble()).toFloat(), Offset.Zero)
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
}

@Composable
fun TeruTeruBozuScreen(model: TeruTeruBozuModel) {
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
                model.update(simDt)
            }
        }
    }

    val supportBrush = remember {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.8f))
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val currentMs = timeMs // Read to trigger redraw
        val pivotX = size.width / 2f
        val pivotY = 0f // The rotation anchor is at the top of the screen
        val headYOffset = size.height * 0.35f // How far down the string goes before the head
        val headRadius = 65f
        
        withTransform({
            translate(pivotX, pivotY)
            // Use bellAngle for the main string sway
            rotate(Math.toDegrees(model.bodyAngle.toDouble()).toFloat(), Offset.Zero)
        }) {
            // Support string
            drawLine(
                brush = supportBrush,
                start = Offset.Zero,
                end = Offset(0f, headYOffset),
                strokeWidth = 3f
            )

            // Translate to neck/head
            withTransform({
                translate(0f, headYOffset)
            }) {
                // Draw skirt (driven by tanzaku angle but pivoted from neck)
                withTransform({
                    // Scale down the tanzaku sway for the skirt so it looks connected
                    rotate(Math.toDegrees((model.skirtAngle - model.bodyAngle).toDouble()).toFloat() * 0.4f, Offset.Zero)
                }) {
                    // Skirt
                    val skirtPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(-25f, 0f) // Neck width left
                        lineTo(25f, 0f)  // Neck width right
                        quadraticBezierTo(110f, 160f, 140f, 220f) // Flare right
                        
                        // wavy bottom
                        quadraticBezierTo(0f, 260f, -140f, 220f)
                        
                        quadraticBezierTo(-110f, 160f, -25f, 0f) // Flare left
                    }
                    
                    drawPath(
                        path = skirtPath,
                        color = Color(0xFFFEF6E4) // Macaron Yellow/Cream
                    )
                    
                    // Skirt folds
                    val foldPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(-15f, 20f)
                        quadraticBezierTo(-30f, 100f, -60f, 225f)
                        
                        moveTo(15f, 20f)
                        quadraticBezierTo(30f, 100f, 60f, 225f)
                        
                        moveTo(0f, 25f)
                        lineTo(0f, 235f)
                    }
                    drawPath(
                        path = foldPath,
                        color = Color.Black.copy(alpha = 0.05f),
                        style = Stroke(width = 3f)
                    )
                }

                // Draw Head (Macaron White)
                drawCircle(
                    color = Color.White,
                    radius = headRadius,
                    center = Offset(0f, -headRadius/2)
                )
                
                // Draw Ribbon around neck (Macaron Pink)
                drawRect(
                    color = Color(0xFFFDE2E4), // Macaron Pink
                    topLeft = Offset(-30f, -8f),
                    size = Size(60f, 16f),
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
                
                // Draw Ribbon tails (bow)
                val bowPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(-15f, 0f)
                    lineTo(-45f, 30f)
                    lineTo(-25f, 40f)
                    close()
                    
                    moveTo(15f, 0f)
                    lineTo(45f, 30f)
                    lineTo(25f, 40f)
                    close()
                }
                drawPath(
                    path = bowPath,
                    color = Color(0xFFFDE2E4)
                )
                
                // Draw Face (Cute!)
                // Left Eye
                drawCircle(
                    color = Color(0xFF555555),
                    radius = 5f,
                    center = Offset(-20f, -headRadius/2 + 5f)
                )
                // Right Eye
                drawCircle(
                    color = Color(0xFF555555),
                    radius = 5f,
                    center = Offset(20f, -headRadius/2 + 5f)
                )
                // Smile
                val smilePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(-10f, -headRadius/2 + 20f)
                    quadraticBezierTo(0f, -headRadius/2 + 30f, 10f, -headRadius/2 + 20f)
                }
                drawPath(
                    path = smilePath,
                    color = Color(0xFF555555),
                    style = Stroke(width = 3f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
                
                // Blush (Macaron Pink)
                drawCircle(
                    color = Color(0xFFFDE2E4),
                    radius = 7f,
                    center = Offset(-30f, -headRadius/2 + 12f)
                )
                drawCircle(
                    color = Color(0xFFFDE2E4),
                    radius = 7f,
                    center = Offset(30f, -headRadius/2 + 12f)
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
        
        // Increase the frequency of target changes for more randomness
        if (Random.nextFloat() < dt * 0.4f) {
            // Increase maximum wind gust force by 1.5x (from 2f to 3f)
            gustTarget = (Random.nextFloat() * 2f - 1f) * 3f 
        }
        
        // Lower interpolation factor (0.3f to 0.2f) makes the transition even smoother
        gust += (gustTarget - gust) * dt * 0.2f
        
        // Base wind oscillation increased by 1.5x (0.6f -> 0.9f, 0.2f -> 0.3f)
        val base = sin(time * 0.5f) * 0.9f + sin(time * 1.2f) * 1f 
        
        return base + gust
    }
}

abstract class OrnamentModel {
    protected val windGen = WindGenerator()
    abstract fun update(dt: Float)
    abstract fun applyGust(force: Float)
}

class WindChimeModel : OrnamentModel() {
    var bellAngle = 0f
    var bellVel = 0f
    val bellLength = 400f

    var tanzakuAngle = 0f
    var tanzakuVel = 0f
    val tanzakuLength = 300f

    override fun update(dt: Float) {
        val wind = windGen.getForce(dt)

        // Increased gravity to prevent inverted bell
        val g = 8000f

        // Tanzaku is light, catches more wind directly, sways more
        val tanzakuAccel = -(g / tanzakuLength) * sin(tanzakuAngle) + wind * 4.0f
        tanzakuVel += tanzakuAccel * dt
        tanzakuVel *= (1f - 0.5f * dt).coerceAtLeast(0f) // Reduced damping for freer movement
        
        // Bell is heavy, catches less wind
        // Pull force from tanzaku swinging inside it - reduced for looser binding
        val pullForce = (tanzakuAngle - bellAngle) * 5f
        val bellAccel = -(g / bellLength) * sin(bellAngle) + wind * 1f + pullForce
        bellVel += bellAccel * dt
        
        // Simple, constant damping to avoid sudden velocity drops that look like jitter
        bellVel *= (1f - 1.5f * dt).coerceAtLeast(0f)
        
        tanzakuAngle += tanzakuVel * dt
        bellAngle += bellVel * dt

        // Explicitly check for non-finite values and reset to 0 to prevent render thread freezing
        if (!tanzakuAngle.isFinite()) tanzakuAngle = 0f
        if (!bellAngle.isFinite()) bellAngle = 0f
        if (!tanzakuVel.isFinite()) tanzakuVel = 0f
        if (!bellVel.isFinite()) bellVel = 0f

        // Soften the clamping to prevent excessive values without hard stops
        tanzakuAngle = tanzakuAngle.coerceIn(-2.5f, 2.5f)
        bellAngle = bellAngle.coerceIn(-1.5f, 1.5f)

        // Constrain tanzaku angle relative to the bell, to simulate the clapper hitting the glass
        // Smoothly dampen velocity when it hits the limit rather than sharp bouncing, which causes jitter
        val maxRelativeAngle = Math.toRadians(45.0).toFloat()
        if (tanzakuAngle > bellAngle + maxRelativeAngle) {
            tanzakuAngle = bellAngle + maxRelativeAngle
            tanzakuVel = bellVel + (tanzakuVel - bellVel) * 0.5f // Soften impact, no negative bounce
        } else if (tanzakuAngle < bellAngle - maxRelativeAngle) {
            tanzakuAngle = bellAngle - maxRelativeAngle
            tanzakuVel = bellVel + (tanzakuVel - bellVel) * 0.5f // Soften impact, no negative bounce
        }
    }

    override fun applyGust(force: Float) {
        bellVel += force * 0.1f
        tanzakuVel += force
    }
}

class TeruTeruBozuModel : OrnamentModel() {
    var bodyAngle = 0f
    var bodyVel = 0f
    val bodyLength = 350f
    
    var skirtAngle = 0f
    var skirtVel = 0f
    val skirtLength = 200f

    override fun update(dt: Float) {
        val wind = windGen.getForce(dt)
        val g = 8000f

        val skirtAccel = -(g / skirtLength) * sin(skirtAngle) + wind * 4.0f
        skirtVel += skirtAccel * dt
        skirtVel *= (1f - 0.5f * dt).coerceAtLeast(0f)
        
        val pullForce = (skirtAngle - bodyAngle) * 5f
        val bodyAccel = -(g / bodyLength) * sin(bodyAngle) + wind * 1f + pullForce
        bodyVel += bodyAccel * dt
        
        bodyVel *= (1f - 1.5f * dt).coerceAtLeast(0f)
        
        skirtAngle += skirtVel * dt
        bodyAngle += bodyVel * dt

        if (!skirtAngle.isFinite()) skirtAngle = 0f
        if (!bodyAngle.isFinite()) bodyAngle = 0f
        if (!skirtVel.isFinite()) skirtVel = 0f
        if (!bodyVel.isFinite()) bodyVel = 0f

        skirtAngle = skirtAngle.coerceIn(-2.5f, 2.5f)
        bodyAngle = bodyAngle.coerceIn(-1.5f, 1.5f)

        val maxRelativeAngle = Math.toRadians(45.0).toFloat()
        if (skirtAngle > bodyAngle + maxRelativeAngle) {
            skirtAngle = bodyAngle + maxRelativeAngle
            skirtVel = bodyVel + (skirtVel - bodyVel) * 0.5f
        } else if (skirtAngle < bodyAngle - maxRelativeAngle) {
            skirtAngle = bodyAngle - maxRelativeAngle
            skirtVel = bodyVel + (skirtVel - bodyVel) * 0.5f
        }
    }

    override fun applyGust(force: Float) {
        bodyVel += force * 0.1f
        skirtVel += force
    }
}

