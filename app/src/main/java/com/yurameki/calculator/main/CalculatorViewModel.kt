package com.yurameki.calculator.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.yurameki.calculator.R

class CalculatorViewModel : ViewModel() {
    var currentValue by mutableDoubleStateOf(0.0)
    var currentString by mutableStateOf("")
}

val buttons = listOf(
    "", "", "", "<-",
    "C", "( )", "%", "/",
    "7", "8", "9", "x",
    "4", "5", "6", "-",
    "1", "2", "3", "+",
    "+/-", "0", ",", "="
)

@Preview(showBackground = true, backgroundColor = android.graphics.Color.BLACK.toLong())
@Composable
fun CalculatorViewP() {
    CalculatorView()
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun CalculatorButton(buttonText: String) {
    val numberPadButtonColor = ButtonDefaults.buttonColors(
        containerColor = colorResource(R.color.numberPadButtonColor),
        contentColor = Color.White
    )

    val otherButtonColor = ButtonDefaults.buttonColors(
        containerColor = colorResource(R.color.otherButtonColor),
        contentColor = Color.White
    )

    val operationButtonColor = ButtonDefaults.buttonColors(
        containerColor = colorResource(R.color.operationButtonColor),
        contentColor = Color.Black
    )

    val equalsButtonColor = ButtonDefaults.buttonColors(
        containerColor = colorResource(R.color.equalsButtonColor),
        contentColor = Color.White
    )

    val index = buttons.toList().indexOf(buttonText)
    var colors: ButtonColors
    if (index in 4..6)  {
        colors = otherButtonColor
    }
    else if (index in (3..19 step 4)) {
        colors = operationButtonColor
    }
    else if (index == 23) {
        colors = equalsButtonColor
    }
    else {
        colors = numberPadButtonColor
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenHeightDp.dp

    val itemSize = remember(screenWidth) {
        ((screenWidth - 120.dp) / 8)
    }

    Box(modifier = Modifier.padding(2.dp).height(itemSize)) {
        Button(
            onClick = {},
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            colors = colors
        ) {
            Text(text = buttonText,
                fontSize = (itemSize.value / 3).sp)
        }
    }
}

@Composable
fun CalculatorView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.then(Modifier.fillMaxSize())) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenHeightDp.dp

            val itemSize = remember(screenWidth) {
                (screenWidth) / 10
            }

            Text(text = "test",
                style = TextStyle(
                    fontSize = (itemSize.value / 3).sp,
                    textAlign = TextAlign.End,
                    color = Color.White
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.08f).padding(PaddingValues(10.dp, 5.dp))
            )

            Spacer(Modifier.weight(0.08f))

            Text(text = "test",
                style = TextStyle(
                    fontSize = (itemSize.value / 2).sp,
                    textAlign = TextAlign.End,
                    color = Color.White
                ),
                modifier = Modifier.weight(0.09f).padding(PaddingValues(10.dp, 5.dp)),
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                Modifier.weight(0.75f),
                userScrollEnabled = false
            ) {
                items(buttons) {
                    if (it != "") {
                        CalculatorButton(it)
                    }
                }
            }
        }
    }
}