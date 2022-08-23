package com.shivnasoft.permissions

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
@Composable
fun Permission(
    showPermissionState: MutableState<Boolean>,
    permissions: List<String>,
    permissionNotGrantedContent: @Composable (MultiplePermissionsState) -> Unit,
    permissionPermanentlyDeniedContent: @Composable (MultiplePermissionsState) -> Unit,
    permissionsGrantedContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dataStore = PreferencesDataStoreHelper(context = context, permissions)
    val scope = rememberCoroutineScope()

    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = permissions) { it ->
            showPermissionState.value = false
            it.keys.forEach { perm ->
                if (it[perm] == false) {
                    scope.launch {
                        dataStore.setDataStoreValue(
                            perm,
                            true
                        )
                    }
                } else if (it[perm] == true) {
                    scope.launch {
                        dataStore.setDataStoreValue(
                            perm,
                            false
                        )
                    }
                }
            }
        }

    val isPermissionsDenied = dataStore.permissionsStatusList.values.filter {
        it.collectAsState(initial = false).value == true
    }

    if (showPermissionState.value) {
        when {
            multiplePermissionsState.allPermissionsGranted ->
                permissionsGrantedContent()

            multiplePermissionsState.shouldShowRationale ->
                permissionNotGrantedContent(multiplePermissionsState)

            !multiplePermissionsState.shouldShowRationale && isPermissionsDenied.isNotEmpty() ->
                permissionPermanentlyDeniedContent(multiplePermissionsState)

            else -> {
                SideEffect {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
            }
        }
    }
}