package com.shivnasoft.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionsScreen(navController: NavHostController) {
    val isCameraPermission = remember { mutableStateOf(false) }

    Column {
        Button(onClick = {
            isCameraPermission.value = true
        }) {
            Text("Click me!")
        }

        Permission(
            showPermissionState = isCameraPermission,
            permissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            permissionNotGrantedContent = { permissionsState ->
                Rationale(
                    textToShow = getTextToShowGivenPermissions(
                        permissionsState.revokedPermissions,
                        permissionsState.shouldShowRationale
                    ),
                    onDismissRequest = { isCameraPermission.value = false },
                    onRequestPermission = {
                        permissionsState.launchMultiplePermissionRequest()
                    })
            },
           permissionPermanentlyDeniedContent = { permissionsState ->
               PermissionNotAvailableAlertDialog(
                   onDismissRequest = { isCameraPermission.value = false },
                   onOpenSettingsClick = { isCameraPermission.value = false },
                   textToShow = getTextToShowGivenPermissions(
                       permissionsState.revokedPermissions,
                       permissionsState.shouldShowRationale
                   )
               )
           }
        ) {
            Text("Gotit!")
        }
    }
}

@Composable
private fun Rationale(
    textToShow: String,
    onDismissRequest: (Boolean) -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = { onDismissRequest(false) },
        title = {
            Text(text = "Permissions Denied")
        },
        text = {
            Text(textToShow)
        },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text("Open")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest(false) }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PermissionNotAvailableAlertDialog(
    onDismissRequest: (Boolean) -> Unit,
    onOpenSettingsClick: () -> Unit,
    textToShow: String,
) {
    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = { onDismissRequest(false) },
        confirmButton = {
            TextButton(onClick = { onOpenSettingsClick() })
            { Text(text = "Open Settings") }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest(false) })
            { Text(text = "Cancel") }
        },
        title = { Text(text = "Permanently Denied") },
        text = { Text(text = textToShow) }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
private fun getTextToShowGivenPermissions(
    permissions: List<PermissionState>,
    shouldShowRationale: Boolean
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
            " important. Please grant ${ if(revokedPermissionsSize == 1) "it" else "all of them" } for the app to function properly."
        } else {
            " permanently denied. The app cannot function without ${ if(revokedPermissionsSize == 1) "it" else "them" }. Please go to settings and grant them."
        }
    )
    return textToShow.toString()
}
