package com.dangerfield.core.navigation

sealed class Screen(val route: String) {
    object NoteList : Screen("note_list")
    object EditNote : Screen("edit_note")
}
