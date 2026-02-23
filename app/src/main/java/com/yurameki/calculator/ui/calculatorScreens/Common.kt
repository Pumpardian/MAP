package com.yurameki.calculator.calculatorScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yurameki.calculator.domain.models.CalculatorHistoryItem
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
    onHistoryClick: () -> Unit,
    onThemeToggle: () -> Unit,
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
                onClick = onHistoryClick
            )
            ActionButton(
                label = "💡",
                onClick = onThemeToggle
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
    onClick: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .combinedClickable(
                onClick = onClick,
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

@Composable
fun HistoryList(
    history: List<CalculatorHistoryItem>,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false
) {
    val colors = LocalCalculatorColors.current
    LazyColumn(modifier = modifier) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Clear History",
                    fontSize = 16.sp,
                    color = colors.symbolTextColor,
                    modifier = Modifier.clickable { onClearHistory() }
                )
            }
        }
        items(history) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                border = if (showBorder) BorderStroke(1.dp, colors.buttonGray) else null,
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = item.expression,
                        fontSize = 20.sp,
                        color = colors.digitTextColor
                    )
                    Text(
                        text = "= ${item.result}",
                        fontSize = 20.sp,
                        color = colors.symbolTextColor
                    )
                }
            }
        }
    }
}