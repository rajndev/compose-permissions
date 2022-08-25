package com.shivnasoft.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    Column {
        Button(onClick = {
            navController.navigate(NavScreens.PermissionsScreen.route)
        }) {
            Text(text = "Goto permissions screen")
        }

        Button(onClick = {
            navController.navigate(NavScreens.SecondPermissionScreen.route)
        }) {
            Text(text = "Goto second permissions screen")
        }
    }

}