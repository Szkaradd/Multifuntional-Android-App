package com.szkarad.szkaradapp.common

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.szkarad.szkaradapp.MainActivity
import com.szkarad.szkaradapp.common.utils.Utils.Companion.addSpacesBeforeCapitals
import com.szkarad.szkaradapp.ui.theme.AppTheme
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

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun CommonTopBar() {
            val context = LocalContext.current

            TopAppBar(
                title = { Text(context.javaClass.simpleName.addSpacesBeforeCapitals()) },
                navigationIcon = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }) {
                        Icon(Icons.Filled.Home, contentDescription = "Home")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun WelcomeTextPreview() {
        SzkaradAppTheme(AppTheme.Light) {
            WelcomeText("Hello Dear User", Color.Black)
        }
    }
}


