package com.szkarad.szkaradapp.common

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.szkarad.szkaradapp.AppPreferences
import com.szkarad.szkaradapp.ui.theme.AppTheme
import com.szkarad.szkaradapp.ui.theme.SzkaradAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val dataStore = AppPreferences(context)
            val theme = dataStore.getTheme.collectAsState(initial = AppTheme.Default)

            SzkaradAppTheme(appTheme = theme.value!!) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Column {
                        CommonComposables.CommonTopBar()
                        Settings(scope, dataStore, theme.value!!)
                    }
                }
            }
        }
    }
}

@Composable
fun Settings(
    scope: CoroutineScope,
    dataStore: AppPreferences,
    theme: AppTheme,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Dark Theme",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = theme == AppTheme.Dark,
                onCheckedChange = { isChecked ->
                    val newTheme = if (isChecked) AppTheme.Dark else AppTheme.Light
                    scope.launch {
                        dataStore.saveTheme(newTheme)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Authors:",
            style = MaterialTheme.typography.headlineSmall
        )
        LazyColumn {
            items(listOf("Mikołaj Szkaradek")) { author ->
                Text(author)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "App Version: $versionName",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
