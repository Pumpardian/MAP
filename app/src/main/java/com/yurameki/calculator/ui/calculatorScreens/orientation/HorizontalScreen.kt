package com.yurameki.calculator.calculatorScreens.orientation

import androidx.compose.runtime.Composable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yurameki.calculator.api.rememberFlashlightHandler
import com.yurameki.calculator.api.rememberVibrationHandler
import com.yurameki.calculator.app.rememberNotificationsCenter
import com.yurameki.calculator.calculatorScreens.ActionBar
import com.yurameki.calculator.calculatorScreens.DisplayField
import com.yurameki.calculator.calculatorScreens.Divider
import com.yurameki.calculator.calculatorScreens.HistoryList
import com.yurameki.calculator.domain.viewmodels.CalculatorViewModel
import com.yurameki.calculator.ui.theme.LocalCalculatorColors

@Composable
fun HorizontalScreen(viewModel: CalculatorViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = LocalCalculatorColors.current
    val flashlightHandler = rememberFlashlightHandler()
    val notificationsCenter = rememberNotificationsCenter()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.calcBackground)
            .padding(1.dp)
    ) {
        val gridFraction = 0.6f
        val gridHeight = this@BoxWithConstraints.maxHeight * gridFraction
        val numRows = 5
        val numCols = 7

        val buttonHeight = (gridHeight / numRows) * 0.9f
        val buttonWidth = this@BoxWithConstraints.maxWidth / numCols
        val buttonSize = if (buttonHeight < buttonWidth) buttonHeight else buttonWidth

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(this@BoxWithConstraints.maxHeight * 0.35f),
                horizontalAlignment = Alignment.End
            ) {
                HorizontalDisplayField(
                    displayValue = uiState.primaryValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(this@BoxWithConstraints.maxHeight * 0.2f)
                )
                Spacer(modifier = Modifier.height(2.dp))

                DisplayField(
                    displayValue = uiState.secondaryValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(this@BoxWithConstraints.maxHeight * 0.08f)
                )
                Spacer(modifier = Modifier.height(2.dp))

                ActionBar(
                    onDelete = { viewModel.onButtonClicked("⌫️") },
                    onHistoryClick = { viewModel.toggleHistory() },
                    onThemeToggle = { viewModel.toggleTheme() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(this@BoxWithConstraints.maxHeight * 0.1f)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            if (uiState.isHistoryVisible) {
                HistoryList(
                    history = uiState.history,
                    onClearHistory = { viewModel.clearHistory() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeight)
                )
            } else {
                EngineeringCalculatorPad(
                    onButtonClick = { text ->
                        viewModel.onButtonClicked(text, flashlightHandler, notificationsCenter)
                    },
                    buttonSize = buttonSize
                )
            }
        }
    }
}

@Composable
fun HorizontalDisplayField(
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
                lineHeight = 40.sp
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
                            lineHeight = 40.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun EngineeringCalculatorPad(
    onButtonClick: (String) -> Unit,
    buttonSize: Dp
) {
    val buttons = listOf(
        "!", "∛", "√", "C", "( )", "%", "÷",
        "sin", "cos", "tan", "7", "8", "9", "×",
        "ln", "log", "1/x", "4", "5", "6", "−",
        "e^x", "x²", "x^y", "1", "2", "3", "+",
        "|x|", "π", "e", "+/-", "0", ",", "="
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(50.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(buttons) { btnText ->
            HorizontalCalculatorButton(
                text = btnText,
                onClick = { onButtonClick(btnText) },
                modifier = Modifier.size(buttonSize)
            )
        }
    }
}

@Composable
fun HorizontalCalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val colors = LocalCalculatorColors.current
    val vibrationHandler = rememberVibrationHandler()

    val fontSize = 30.sp

    val backgroundColor: Color = when (text) {
        "C", "%", "( )" -> colors.upperButtonsBackground
        "=" -> colors.equalsButtonBackground
        "÷", "×", "−", "+" -> colors.symbolBackground
        "+/-", "," -> colors.buttonGray
        else -> colors.buttonGray
    }
    val contentColor: Color = when (text) {
        "C", "%", "( )" -> colors.upperButtonsText
        "=" -> colors.equalsButtonText
        "÷", "×", "−", "+" -> colors.symbolTextColor
        "+/-", "," -> colors.digitTextColor
        else -> colors.digitTextColor
    }

    val buttonBackgroundColor by animateColorAsState(targetValue = backgroundColor)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .clip(CircleShape)
            .background(buttonBackgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = {
                    onClick()
                    vibrationHandler.vibrate()
                }
            )
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Normal,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}