package com.szkarad.szkaradapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.szkarad.szkaradapp.AppPreferences

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = Color.DarkGray,
    tertiary = Pink80,
    onSecondary = Color.LightGray

)

private val LightColorScheme = lightColorScheme(
    primary = Kolorek,
    secondary = Purple40,
    tertiary = LightKolorek,
    onSecondary = Color.LightGray,
    onTertiary = Purple20,
    onPrimary = Color.White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

enum class AppTheme {
    Light, Dark, Default
}

enum class IconSize {
    BigIcons, MediumIcons, Default
}

@Composable
fun SzkaradAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dataStore = AppPreferences(context)
    val appTheme = dataStore.getTheme.collectAsState(initial = AppTheme.Default)
    val iconSize = dataStore.getIconSize.collectAsState(initial = IconSize.MediumIcons)

    val colorScheme = when (appTheme.value!!) {
        AppTheme.Light -> {
            LightColorScheme
        }
        AppTheme.Dark -> {
            DarkColorScheme
        }
        else -> {
            if (darkTheme) {
                DarkColorScheme
            } else {
                LightColorScheme
            }
        }
    }

    val typography = when (iconSize.value!!) {
        IconSize.BigIcons -> { LargeTypography }
        else -> { MediumTypography }
    }


    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}