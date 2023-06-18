package com.vinayismd.contactify

sealed class Screens(val route: String) {
    object MainScreen : Screens("main_screen")
    object ContactScreen : Screens("contact_screen")
    object InfoScreen : Screens("info_screen")

    fun withArgs(vararg  args: String): String {
        return buildString {
            append(route)
            args.forEach { args->
                append("/$args")
            }
        }
    }
}