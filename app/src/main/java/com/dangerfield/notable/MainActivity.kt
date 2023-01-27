package com.dangerfield.notable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dangerfield.core.navigation.Screen
import com.dangerfield.core.notesapi.NoteRepository
import com.dangerfield.editnote.EditNoteRoute
import com.dangerfield.editnote.EditNoteViewModel
import com.dangerfield.editnote.NEW_NOTE_ID
import com.dangerfield.notable.theme.NotableTheme
import com.dangerfield.noteslist.NoteListRoute
import com.dangerfield.noteslist.NotesListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notesRepository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotableTheme {
                Surface(modifier = Modifier.background(MaterialTheme.colors.background)) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.NoteList.route) {

                        composable(Screen.NoteList.route) {

                            val viewModel: NotesListViewModel = hiltViewModel()
                            NoteListRoute(
                                viewModel = viewModel,
                                onNoteSelected = {
                                    navController.navigate(
                                        Screen.EditNote.route + "?noteId=${it.id}&?noteColor=${it.color}"
                                    )
                                },
                                onAddNoteSelected = {
                                    navController.navigate(Screen.EditNote.route)
                                }
                            )
                        }

                        composable(
                            Screen.EditNote.route + "?noteId={noteId}&?noteColor={noteColor}",
                            arguments = listOf(
                                navArgument("noteId") {
                                    type = NavType.StringType
                                    defaultValue = NEW_NOTE_ID
                                },
                                navArgument("noteColor") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) {
                            val viewModel: EditNoteViewModel = hiltViewModel()
                            EditNoteRoute(
                                viewModel = viewModel,
                                onDone = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
