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
    permissionPermanentlyDeniedContent: @Composable () -> Unit,
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

  /*
    val cameraPermissionDeniedStatus =
        dataStore.cameraPermissionDeniedStatus.collectAsState(initial = false)
    val readExtStoragePermissionDeniedStatus =
        dataStore.readExternalStoragePermissionDeniedStatus.collectAsState(initial = false)
*/
    //val isPermissionsDenied = cameraPermissionDeniedStatus.value == true || readExtStoragePermissionDeniedStatus.value == true

/*
    val isAllDenied = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    ).all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
    }
*/

/*    Log.i("@@@ allPermissionsGranted", multiplePermissionsState.allPermissionsGranted.toString())
    Log.i("@@@ isAllDinied", isAllDenied.toString())
    Log.i("@@@ isPermissionsDenied", isPermissionsDenied.toString())
    Log.i("@@@ showrationale?", multiplePermissionsState.shouldShowRationale.toString())*/

    if (showPermissionState.value) {
        when {
            multiplePermissionsState.allPermissionsGranted ->
                permissionsGrantedContent()

            multiplePermissionsState.shouldShowRationale ->
                permissionNotGrantedContent(multiplePermissionsState)

            !multiplePermissionsState.shouldShowRationale && isPermissionsDenied.isNotEmpty() ->
                permissionPermanentlyDeniedContent()

            else -> {
                SideEffect {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
            }
        }
    }
}

/*

internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}
*/
