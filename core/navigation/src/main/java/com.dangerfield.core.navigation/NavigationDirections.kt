package com.dangerfield.core.navigation

import androidx.navigation.NamedNavArgument

// https://medium.com/google-developer-experts/modular-navigation-with-jetpack-compose-fda9f6b2bef7

object NavigationDirections {
    val notesList = object : NavigationCommand {
        override val arguments: List<NamedNavArgument> = emptyList()
        override val destination = "notesList"
    }

    val editNote = object : NavigationCommand {
        override val arguments: List<NamedNavArgument> = emptyList()
        override val destination = "editNote"
    }
}
