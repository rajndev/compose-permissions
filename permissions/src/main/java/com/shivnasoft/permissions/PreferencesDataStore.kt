package com.shivnasoft.permissions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PreferencesDataStore(private val context: Context) {
    // to make sure there's only one instance
    companion object {
        private val Context.permissionsDataStore: DataStore<Preferences> by preferencesDataStore("permissions_data_store")
        val CAMERA_PERMISSION = booleanPreferencesKey("camera_permission")
        val READ_EXTERNAL_STORAGE_PERMISSION = booleanPreferencesKey("read_external_storage_permission")
    }

    suspend fun saveCameraPermissionDeniedStatus(isDenied: Boolean) {
        context.permissionsDataStore.edit { preferences ->
            preferences[CAMERA_PERMISSION] = isDenied
        }
    }

    suspend fun saveReadExternalStoragePermissionDeniedStatus(isDenied: Boolean) {
        context.permissionsDataStore.edit { preferences ->
            preferences[READ_EXTERNAL_STORAGE_PERMISSION] = isDenied
        }
    }

    //get the saved email
    val cameraPermissionDeniedStatus: Flow<Boolean?> = context.permissionsDataStore.data
        .map { preferences ->
            preferences[CAMERA_PERMISSION] ?: false
        }

    val readExternalStoragePermissionDeniedStatus: Flow<Boolean?> = context.permissionsDataStore.data
        .map { preferences ->
            preferences[READ_EXTERNAL_STORAGE_PERMISSION] ?: false
        }
}