package com.shivnasoft.permissions

sealed class NavScreens(val route: String) {
    object MainScreen : NavScreens(route = "main_screen")
    object PermissionsScreen : NavScreens(route ="permissions_screen")
}
