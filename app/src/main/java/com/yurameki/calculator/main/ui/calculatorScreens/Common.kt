package com.yurameki.calculator.main.ui.calculatorScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yurameki.calculator.ui.theme.LocalCalculatorColors

@Composable
fun DisplayField(
    displayValue: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = displayValue,
            fontSize = 30.sp,
            color = colors.digitTextColor.copy(alpha = 0.4f),
            maxLines = 1
        )
    }
}

@Composable
fun Divider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}

@Composable
fun ActionBar(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            ActionButton(
                label = "🕒",
            )
        }
        ActionButton(label = "⌫️", onClick = onDelete)
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                indication = LocalIndication.current,
                interactionSource = MutableInteractionSource()
            )
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            color = Color.Gray.copy(alpha = 0.6f)
        )
    }
}