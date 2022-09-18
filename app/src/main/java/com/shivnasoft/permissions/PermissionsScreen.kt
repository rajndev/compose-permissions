package com.shivnasoft.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionsScreen() {
    val isCameraPermission = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {

            }
        }
    )

    @Composable
    fun LaunchCamera() {
        SideEffect {
            val uri = Uri.EMPTY
            cameraLauncher.launch(uri)
            isCameraPermission.value = false
        }
    }

    Column {
        Button(onClick = {
            isCameraPermission.value = true
        }) {
            Text("Use Camera")
        }

        Permission(
            showPermissionState = isCameraPermission,
            permissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CALENDAR
            ),
            permissionNotGrantedContent = { permissionsState, textToShow ->
                Rationale(
                    textToShow = textToShow,
                    onDismissRequest = { isCameraPermission.value = false },
                    onRequestPermission = {
                        permissionsState.launchMultiplePermissionRequest()
                    })
            },
           permissionPermanentlyDeniedContent = {textToShow ->
               PermissionNotAvailableAlertDialog(
                   onDismissRequest = { isCameraPermission.value = false },
                   onOpenSettingsClick = { isCameraPermission.value = false },
                   textToShow = textToShow
               )
           }
        ) {
            LaunchCamera()
            it?.value = false
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
