package com.example.avaliafilmes.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.avaliafilmes.data.model.MovieReview
import com.example.avaliafilmes.ui.viewmodel.AuthViewModel
import com.example.avaliafilmes.ui.viewmodel.MovieDetailsState
import com.example.avaliafilmes.ui.viewmodel.MovieViewModel
import com.example.avaliafilmes.ui.viewmodel.ReviewState
import com.example.avaliafilmes.ui.viewmodel.ReviewViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    imdbId: String,
    movieViewModel: MovieViewModel,
    reviewViewModel: ReviewViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val movieDetailsState by movieViewModel.movieDetailsState.collectAsState()
    val reviewState by reviewViewModel.reviewState.collectAsState()
    val movieReviews by reviewViewModel.movieReviews.collectAsState()
    val averageRating by reviewViewModel.averageRating.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    var showReviewDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(imdbId) {
        movieViewModel.getMovieDetails(imdbId)
        reviewViewModel.loadMovieReviews(imdbId)
    }
    
    LaunchedEffect(reviewState) {
        if (reviewState is ReviewState.Success) {
            showReviewDialog = false
            reviewViewModel.resetReviewState()
            reviewViewModel.loadMovieReviews(imdbId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Filme", color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val state = movieDetailsState) {
            is MovieDetailsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MovieDetailsState.Success -> {
                val movie = state.movie
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Poster e informações básicas
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AsyncImage(
                                model = if (movie.Poster != "N/A") movie.Poster else null,
                                contentDescription = movie.Title,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = movie.Title,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Ano: ${movie.Year}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Gênero: ${movie.Genre}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Diretor: ${movie.Director}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "IMDb: ${movie.imdbRating}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                if (movieReviews.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = String.format("%.1f/5.0 (${movieReviews.size} avaliações)", averageRating),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Sinopse
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Sinopse",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = movie.Plot,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Botão de avaliar
                    item {
                        Button(
                            onClick = { showReviewDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Avaliar Filme")
                        }
                    }
                    
                    // Avaliações
                    item {
                        Text(
                            text = "Avaliações (${movieReviews.size})",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    items(movieReviews, key = { it.id }) { review ->
                        ReviewCard(review)
                    }
                }
                
                if (showReviewDialog && currentUser != null) {
                    ReviewDialog(
                        movieTitle = movie.Title,
                        moviePoster = movie.Poster,
                        movieYear = movie.Year,
                        imdbId = movie.imdbID,
                        userId = currentUser!!.id,
                        reviewViewModel = reviewViewModel,
                        reviewState = reviewState,
                        onDismiss = { 
                            showReviewDialog = false
                            reviewViewModel.resetReviewState()
                        }
                    )
                }
            }
            is MovieDetailsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ReviewCard(review: MovieReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = String.format("%.1f", review.rating),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            if (review.comment.isNotBlank()) {
                Text(
                    text = review.comment,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(review.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReviewDialog(
    movieTitle: String,
    moviePoster: String,
    movieYear: String,
    imdbId: String,
    userId: Long,
    reviewViewModel: ReviewViewModel,
    reviewState: ReviewState,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Avaliar: $movieTitle",
                color = MaterialTheme.colorScheme.onSurface
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Sua nota:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = (index + 1).toFloat() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Text(
                        text = String.format("%.1f", rating),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentário (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4,
                    enabled = reviewState !is ReviewState.Loading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                
                if (reviewState is ReviewState.Error) {
                    Text(
                        text = reviewState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        reviewViewModel.addReview(
                            userId = userId,
                            imdbId = imdbId,
                            movieTitle = movieTitle,
                            moviePoster = moviePoster,
                            movieYear = movieYear,
                            rating = rating,
                            comment = comment
                        )
                    }
                },
                enabled = rating > 0 && reviewState !is ReviewState.Loading
            ) {
                if (reviewState is ReviewState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Avaliar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
