package com.shivnasoft.permissions

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
@Composable
fun Permission(
    showPermissionState: MutableState<Boolean>,
    permissions: List<String>,
    permissionNotGrantedContent: @Composable (MultiplePermissionsState, String) -> Unit,
    permissionPermanentlyDeniedContent: @Composable (String) -> Unit,
    permissionsGrantedContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val dataStore = PreferencesDataStoreHelper(context = context, permissions)
    val scope = rememberCoroutineScope()

    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissions = permissions) { permissionsList ->
            showPermissionState.value = false
            permissionsList.keys.forEach { permission ->
                scope.launch {
                    if (permissionsList[permission] == false) {
                        dataStore.setDataStoreValue(
                            permission,
                            true
                        )
                    }

                    if (permissionsList[permission] == true) {
                        dataStore.setDataStoreValue(
                            permission,
                            false
                        )
                    }
                }
            }
        }

        val permissionsDeniedList = dataStore.permissionsStatusList.values.filter {
            it.collectAsState(initial = null).value == true
        }

        val permissionsNotRequestedList = dataStore.permissionsStatusList.filter {
            it.value.collectAsState(initial = null).value == null
        }

    Log.i("@@@ Denied List", permissionsDeniedList.size.toString())
    Log.i("@@@ Not Requeted List", permissionsNotRequestedList.size.toString())
    Log.i("@@@ Not Requeted List", multiplePermissionsState.shouldShowRationale.toString())

    if (showPermissionState.value) {
        when {
            multiplePermissionsState.allPermissionsGranted ->
                permissionsGrantedContent()

            multiplePermissionsState.shouldShowRationale -> {
                val textToShow = getTextToShowGivenPermissions(
                    multiplePermissionsState.revokedPermissions,
                    multiplePermissionsState.shouldShowRationale,
                )
                permissionNotGrantedContent(multiplePermissionsState, textToShow)
            }

            permissionsDeniedList.isNotEmpty() && permissionsNotRequestedList.isEmpty()-> {
                val textToShow = getTextToShowGivenPermissions(
                    multiplePermissionsState.revokedPermissions,
                    multiplePermissionsState.shouldShowRationale,
                )
                permissionPermanentlyDeniedContent(textToShow)
            }

            else -> {
                SideEffect {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun getTextToShowGivenPermissions(
    permissions: List<PermissionState>,
    shouldShowRationale: Boolean,
): String {
    val revokedPermissionsSize = permissions.size
    if (revokedPermissionsSize == 0) return ""

    val textToShow = StringBuilder().apply {
        append("The ")
    }

    for (i in permissions.indices) {
        textToShow.append(permissions[i].permission.split(".")[2])
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                textToShow.append(", and ")
            }
            i == revokedPermissionsSize - 1 -> {
                textToShow.append(" ")
            }
            else -> {
                textToShow.append(", ")
            }
        }
    }
    textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    textToShow.append(
        if (shouldShowRationale) {
            " required for this feature to work properly. Please grant ${if (revokedPermissionsSize == 1) "it." else "all of them."}"
        } else {
            " permanently denied. This feature will not work without ${if (revokedPermissionsSize == 1) "it" else "them"}. Please go to settings, and grant the required permissions."
        }
    )
    return textToShow.toString()
}