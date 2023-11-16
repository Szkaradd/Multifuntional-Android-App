package com.szkarad.szkaradapp.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.szkarad.szkaradapp.ui.theme.SzkaradAppTheme

class CommonComposables {
    companion object {
        @Composable
        fun WelcomeText(text: String, color: Color) {
            val shadow = Shadow(
                color = Color.Black,
                offset = Offset(4f, 4f),
                blurRadius = 4f
            )
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = shadow,
                    color = color,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WelcomeTextPreview() {
        SzkaradAppTheme {
            WelcomeText("Hello Dear User", Color.Black)
        }
    }
}


