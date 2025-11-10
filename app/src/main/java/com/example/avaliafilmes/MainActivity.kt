package com.example.avaliafilmes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.avaliafilmes.data.database.AppDatabase
import com.example.avaliafilmes.data.repository.MovieReviewRepository
import com.example.avaliafilmes.data.repository.UserRepository
import com.example.avaliafilmes.data.service.OMDBService
import com.example.avaliafilmes.ui.navigation.Screen
import com.example.avaliafilmes.ui.screen.*
import com.example.avaliafilmes.ui.theme.AvaliaFilmesTheme
import com.example.avaliafilmes.ui.viewmodel.AuthViewModel
import com.example.avaliafilmes.ui.viewmodel.MovieViewModel
import com.example.avaliafilmes.ui.viewmodel.ReviewViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inicializar repositórios
        val database = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(database.userDao())
        val reviewRepository = MovieReviewRepository(database.movieReviewDao())
        val omdbService = OMDBService()
        
        setContent {
            AvaliaFilmesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AvaliaFilmesApp(
                        userRepository = userRepository,
                        reviewRepository = reviewRepository,
                        omdbService = omdbService
                    )
                }
            }
        }
    }
}

@Composable
fun AvaliaFilmesApp(
    userRepository: UserRepository,
    reviewRepository: MovieReviewRepository,
    omdbService: OMDBService
) {
    val navController = rememberNavController()
    
    // ViewModels
    val authViewModel: AuthViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(userRepository) as T
            }
        }
    )
    
    val movieViewModel: MovieViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(omdbService) as T
            }
        }
    )
    
    val reviewViewModel: ReviewViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(reviewRepository) as T
            }
        }
    )
    
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                authViewModel = authViewModel,
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToMyReviews = {
                    navController.navigate(Screen.MyReviews.route)
                },
                onNavigateToAllReviews = {
                    navController.navigate(Screen.AllReviews.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                movieViewModel = movieViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieClick = { imdbId ->
                    navController.navigate(Screen.MovieDetails.createRoute(imdbId))
                }
            )
        }
        
        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(navArgument("imdbId") { type = NavType.StringType })
        ) { backStackEntry ->
            val imdbId = backStackEntry.arguments?.getString("imdbId") ?: return@composable
            MovieDetailsScreen(
                imdbId = imdbId,
                movieViewModel = movieViewModel,
                reviewViewModel = reviewViewModel,
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.MyReviews.route) {
            MyReviewsScreen(
                reviewViewModel = reviewViewModel,
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieClick = { imdbId ->
                    navController.navigate(Screen.MovieDetails.createRoute(imdbId))
                }
            )
        }
        
        composable(Screen.AllReviews.route) {
            AllReviewsScreen(
                reviewViewModel = reviewViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieClick = { imdbId ->
                    navController.navigate(Screen.MovieDetails.createRoute(imdbId))
                }
            )
        }
    }
}
