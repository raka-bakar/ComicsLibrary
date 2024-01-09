package com.catalin.comicslibrary

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.catalin.comicslibrary.ui.theme.ComicsLibraryTheme
import com.catalin.view.CharacterDetailScreen
import com.catalin.view.CharactersBottomNav
import com.catalin.view.CollectionScreen
import com.catalin.view.LibraryScreen
import com.catalin.viewmodel.CollectionDbViewModel
import com.catalin.viewmodel.LibraryApiViewModel
import dagger.hilt.android.AndroidEntryPoint

sealed class Destination(val route: String) {
    object Library : Destination("library")
    object Collection : Destination("collection")
    object CharacterDetail : Destination("character/{characterId}") {
        fun createRoute(characterId: Int?) = "character/$characterId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<LibraryApiViewModel>()
    private val dbViewModel by viewModels<CollectionDbViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComicsLibraryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    CharactersScaffold(navController = navController, viewModel, dbViewModel = dbViewModel)
                }
            }
        }
    }
}

@Composable
fun CharactersScaffold(
    navController: NavHostController,
    viewModel: LibraryApiViewModel,
    dbViewModel: CollectionDbViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    Scaffold(scaffoldState = scaffoldState,
        bottomBar = { CharactersBottomNav(navController = navController) }) { paddingValues ->
        NavHost(navController = navController, startDestination = Destination.Library.route) {
            composable(Destination.Library.route) {
                val result by viewModel.result.collectAsState()
                val text = viewModel.queryText.collectAsState()
                val networkAvailable = viewModel.networkAvailable.observe()
                LibraryScreen(
                    navController = navController,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )
            }
            composable(Destination.Collection.route) {
                CollectionScreen(dbViewModel, navController)
            }
            composable(Destination.CharacterDetail.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("characterId")?.toIntOrNull()
                if (id == null) {
                    Toast.makeText(context, "Character Id is required", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.retrieveSingleCharacter(id)
                    CharacterDetailScreen(
                        viewmodel = viewModel,
                        paddingValues = paddingValues,
                        navController = navController,
                        dbViewModel = dbViewModel
                    )
                }
            }
        }
    }
}





