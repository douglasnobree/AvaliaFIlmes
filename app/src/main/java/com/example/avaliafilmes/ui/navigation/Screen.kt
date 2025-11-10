package com.example.avaliafilmes.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Search : Screen("search")
    object MovieDetails : Screen("movie_details/{imdbId}") {
        fun createRoute(imdbId: String) = "movie_details/$imdbId"
    }
    object MyReviews : Screen("my_reviews")
    object AllReviews : Screen("all_reviews")
}
