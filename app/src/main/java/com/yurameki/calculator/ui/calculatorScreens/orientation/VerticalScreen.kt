package com.yurameki.calculator.ui.calculatorScreens.orientation

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yurameki.calculator.api.rememberFlashlightHandler
import com.yurameki.calculator.api.rememberVibrationHandler
import com.yurameki.calculator.app.rememberNotificationsCenter
import com.yurameki.calculator.ui.calculatorScreens.ActionBar
import com.yurameki.calculator.ui.calculatorScreens.DisplayField
import com.yurameki.calculator.ui.calculatorScreens.Divider
import com.yurameki.calculator.ui.calculatorScreens.HistoryList
import com.yurameki.calculator.domain.viewmodels.CalculatorViewModel
import com.yurameki.calculator.ui.theme.LocalCalculatorColors

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VerticalScreen(viewModel: CalculatorViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalCalculatorColors.current
    val flashlightHandler = rememberFlashlightHandler()
    val notificationsCenter = rememberNotificationsCenter()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.calcBackground)
            .padding(20.dp)
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.weight(1f))

            VerticalDisplayField(
                displayValue = uiState.primaryValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.16f)
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

            DisplayField(
                displayValue = uiState.secondaryValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.05f)
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

            ActionBar(
                onDelete = { viewModel.onButtonClicked("⌫️") },
                onHistoryClick = { viewModel.toggleHistory() },
                onThemeToggle = { viewModel.toggleTheme() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.08f)
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )

            Spacer(modifier = Modifier.height(screenHeight * 0.01f))

            if (uiState.isHistoryVisible) {
                HistoryList(
                    history = uiState.history,
                    onClearHistory = { viewModel.clearHistory() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenWidth * 101 / 80)
                )
            }
            else {
                CalculatorPad(
                    onButtonClick = { text ->
                        viewModel.onButtonClicked(text, flashlightHandler, notificationsCenter)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenWidth * 101 / 80)
                )
            }
        }
    }
}

@Composable
fun VerticalDisplayField(
    displayValue: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(text = displayValue, selection = TextRange(displayValue.length))
        )
    }
    LaunchedEffect(displayValue) {
        if (textFieldValue.text != displayValue) {
            textFieldValue = textFieldValue.copy(
                text = displayValue,
                selection = TextRange(displayValue.length)
            )
        }
    }
    val textSize = when {
        displayValue.length > 18 -> 30.sp
        displayValue.length > 12 -> 40.sp
        else -> 48.sp
    }
    Box(
        modifier = modifier.verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.BottomEnd
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { },
            textStyle = TextStyle(
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                color = colors.digitTextColor,
                textAlign = TextAlign.End,
                lineHeight = 56.sp
            ),
            cursorBrush = SolidColor(Color.Transparent),
            readOnly = true,
            modifier = Modifier
                .fillMaxSize()
                .focusable(false),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    if (textFieldValue.text.isEmpty()) {
                        Text(
                            text = "",
                            fontSize = textSize,
                            fontWeight = FontWeight.Normal,
                            color = colors.digitTextColor.copy(alpha = 0.4f),
                            textAlign = TextAlign.End,
                            lineHeight = 56.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun CalculatorPad(
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = listOf(
        "C", "( )", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "−",
        "1", "2", "3", "+",
        "+/-", "0", ",", "="
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(buttons.size) { index ->
            val btnText = buttons[index]
            CalculatorButton(
                text = btnText,
                onClick = { onButtonClick(btnText) },
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val colors = LocalCalculatorColors.current
    val vibrationHandler = rememberVibrationHandler()

    val backgroundColor: Color = when (text) {
        "C", "%", "( )" -> colors.upperButtonsBackground
        "=" -> colors.equalsButtonBackground
        "÷", "×", "−", "+"-> colors.symbolBackground
        "+/-", "," -> colors.buttonGray
        else -> colors.buttonGray
    }
    val contentColor: Color = when (text) {
        "C", "%", "( )" -> colors.upperButtonsText
        "=" -> colors.equalsButtonText
        "÷", "×", "−", "+" -> colors.symbolTextColor
        "+/-", "," -> colors.digitTextColor
        else -> if (text.all { it.isDigit() }) colors.digitTextColor else Color.White
    }
    val fontSize = when (text) {
        "( )" -> 28.sp
        in listOf("%", "÷", "×", "−", "+", "=") -> 42.sp
        else -> 36.sp
    }
    val buttonBackgroundColor by animateColorAsState(targetValue = backgroundColor)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 50)
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(
                onClick = {
                    onClick()
                    vibrationHandler.vibrate()
                },
                interactionSource = interactionSource,
                indication = null
            )
            .semantics { contentDescription = "Button $text" }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(buttonBackgroundColor)
                .clickable(
                    onClick = {
                        onClick()
                        vibrationHandler.vibrate()
                    },
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                )
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Normal,
                color = contentColor
            )
        }
    }
}