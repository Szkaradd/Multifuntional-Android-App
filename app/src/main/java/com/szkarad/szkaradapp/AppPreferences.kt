package com.szkarad.szkaradapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.szkarad.szkaradapp.ui.theme.AppTheme
import com.szkarad.szkaradapp.ui.theme.IconSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AppPreferences")
        val KEY_THEME = stringPreferencesKey("theme")
        val ICON_SIZE = stringPreferencesKey("icons")
    }

    val getTheme: Flow<AppTheme?> = context.dataStore.data
        .map { preferences ->
            when(preferences[KEY_THEME]) {
                AppTheme.Light.name -> AppTheme.Light
                AppTheme.Dark.name -> AppTheme.Dark
                else -> AppTheme.Default
            }
        }

    val getIconSize: Flow<IconSize?> = context.dataStore.data
        .map {preferences ->
            when(preferences[ICON_SIZE]) {
                IconSize.BigIcons.name -> IconSize.BigIcons
                IconSize.MediumIcons.name -> IconSize.MediumIcons
                else -> IconSize.MediumIcons
            }

        }

    suspend fun saveTheme(appTheme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = appTheme.name
        }
    }

    suspend fun saveIconSize(iconSize: IconSize) {
        context.dataStore.edit { preferences ->
            preferences[ICON_SIZE] = iconSize.name
        }
    }
}
