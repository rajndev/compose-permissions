package com.shivnasoft.permissions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesDataStoreHelper(private val context: Context, private val permissions: List<String>) {

    companion object{
        val Context.permissionsDataStore: DataStore<Preferences> by preferencesDataStore(name = "permissions_datastore")
    }

    val permissionsStatusList: MutableMap<String, Flow<Boolean?>> = mutableMapOf()

    init {
        permissions.forEach { permission ->
            val permissionName = permission.split(".")[2]
            val status = context.permissionsDataStore.data
                .map { preferences ->
                    preferences[booleanPreferencesKey(permissionName)]
                }
            permissionsStatusList[permissionName] = status
        }
    }

    suspend fun setDataStoreValue(permission: String, value: Boolean) {
        val permissionName = permission.split(".")[2]
        context.permissionsDataStore.edit { preferences ->
            preferences[booleanPreferencesKey(permissionName)] = value
        }
    }
}