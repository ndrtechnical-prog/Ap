package com.example.data.repository

import com.example.data.local.MovieDao
import com.example.data.local.MovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository(private val movieDao: MovieDao) {

    val allMovies: Flow<List<MovieEntity>> = movieDao.getAllMovies()
    val downloadedMovies: Flow<List<MovieEntity>> = movieDao.getDownloadedMovies()
    val watchlistMovies: Flow<List<MovieEntity>> = movieDao.getWatchlistMovies()
    val favoriteMovies: Flow<List<MovieEntity>> = movieDao.getFavoriteMovies()

    fun searchMovies(query: String): Flow<List<MovieEntity>> {
        return if (query.isBlank()) {
            movieDao.getAllMovies()
        } else {
            movieDao.searchMovies(query.trim())
        }
    }

    fun getMoviesByCategory(category: String): Flow<List<MovieEntity>> {
        return if (category == "all") {
            movieDao.getAllMovies()
        } else {
            movieDao.getMoviesByCategory(category)
        }
    }

    suspend fun toggleDownload(movie: MovieEntity) {
        val willBeDownloaded = !movie.isDownloaded
        val sizeMb = if (willBeDownloaded) (340 + (movie.title.length * 17) % 450) else 0
        val date = if (willBeDownloaded) System.currentTimeMillis() else 0L
        movieDao.setDownloaded(movie.id, willBeDownloaded, sizeMb, date)
    }

    suspend fun toggleWatchlist(movie: MovieEntity) {
        movieDao.setWatchlist(movie.id, !movie.isInWatchlist)
    }

    suspend fun toggleFavorite(movie: MovieEntity) {
        movieDao.setFavorite(movie.id, !movie.isFavorite)
    }

    suspend fun clearAllDownloads() {
        movieDao.clearAllDownloads()
    }

    suspend fun prepopulateIfEmpty() {
        val count = movieDao.getMovieCount()
        if (count == 0) {
            val seedMovies = getInitialMovloCatalog()
            movieDao.insertAll(seedMovies)
        }
    }

    private fun getInitialMovloCatalog(): List<MovieEntity> {
        return listOf(
            MovieEntity(
                id = "movlo_welcome",
                title = "Welcome",
                year = 2007,
                rating = 8.8,
                duration = "2h 26m",
                quality = "1080p HD",
                genre = "Indian Cinema",
                genres = "Indian Cinema, Comedy, Action, Bollywood",
                description = "Bollywood comedy blockbuster. Two criminal don brothers seek a respectable groom for their sister, leading to a cascade of unforgettable comic misunderstandings.",
                posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=dqDjZgwt9SQ",
                category = "indian_cinema",
                isDownloaded = true,
                downloadSizeMb = 512,
                downloadDate = System.currentTimeMillis() - 86400000L,
                isFavorite = true,
                isInWatchlist = true
            ),
            MovieEntity(
                id = "movlo_dune_3",
                title = "Dune: Part Three (Messiah)",
                year = 2026,
                rating = 9.4,
                duration = "2h 46m",
                quality = "4K UHD",
                genre = "Sci-Fi",
                genres = "Sci-Fi, Adventure, Drama",
                description = "Paul Atreides faces the devastating aftermath of his galactic holy war as ancient powers conspire against the Golden Throne across the cosmos.",
                posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=Way9Dexny3w",
                category = "upcoming",
                isDownloaded = true,
                downloadSizeMb = 640,
                downloadDate = System.currentTimeMillis() - 43200000L,
                isFavorite = true,
                isInWatchlist = true
            ),
            MovieEntity(
                id = "movlo_dhol",
                title = "Dhol",
                year = 2007,
                rating = 8.7,
                duration = "2h 21m",
                quality = "1080p HD",
                genre = "Indian Cinema",
                genres = "Indian Cinema, Comedy, Crime, South Cinema",
                description = "Four unemployed childhood friends looking for a shortcut to wealth try to woo a rich neighborhood girl, only to get entangled with a ruthless underworld kingpin.",
                posterUrl = "https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=uQrTvCdEYi8",
                category = "indian_cinema",
                isDownloaded = true,
                downloadSizeMb = 480,
                downloadDate = System.currentTimeMillis() - 172800000L,
                isFavorite = false,
                isInWatchlist = false
            ),
            MovieEntity(
                id = "movlo_oppenheimer",
                title = "Oppenheimer",
                year = 2023,
                rating = 8.9,
                duration = "3h 00m",
                quality = "4K UHD",
                genre = "Drama",
                genres = "Biography, Drama, History",
                description = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during the Manhattan Project.",
                posterUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=uYPbbksJxIg",
                category = "trending",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = false,
                isInWatchlist = true
            ),
            MovieEntity(
                id = "movlo_dhoom",
                title = "Dhoom",
                year = 2004,
                rating = 8.5,
                duration = "2h 09m",
                quality = "1080p HD",
                genre = "Indian Cinema",
                genres = "Indian Cinema, Action, Thriller, Bollywood",
                description = "Two daring high-tech superbike thieves carry out high-profile robberies across Mumbai while a dedicated ACP teams up with a mechanic to catch them.",
                posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=uQrTvCdEYi8",
                category = "indian_cinema",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = true,
                isInWatchlist = false
            ),
            MovieEntity(
                id = "movlo_interstellar",
                title = "Interstellar",
                year = 2014,
                rating = 8.9,
                duration = "2h 49m",
                quality = "4K UHD",
                genre = "Sci-Fi",
                genres = "Sci-Fi, Adventure, Drama",
                description = "When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft, along with a team of researchers, to find a new planet for humans.",
                posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=zSWdZVtXT7E",
                category = "trending",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = true,
                isInWatchlist = false
            ),
            MovieEntity(
                id = "movlo_avengers_secret_wars",
                title = "Avengers: Secret Wars",
                year = 2027,
                rating = 9.3,
                duration = "2h 55m",
                quality = "4K UHD",
                genre = "Action",
                genres = "Action, Sci-Fi, Adventure",
                description = "The multiversal saga reaches its epic climax as timelines collide on Battleworld, requiring every surviving superhero across dimensions to unite.",
                posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=mq8L_f_c6R4",
                category = "upcoming",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = false,
                isInWatchlist = true
            ),
            MovieEntity(
                id = "movlo_spider_man_beyond",
                title = "Spider-Man: Beyond the Spider-Verse",
                year = 2026,
                rating = 9.5,
                duration = "2h 20m",
                quality = "4K UHD",
                genre = "Animation",
                genres = "Animation, Action, Adventure, Sci-Fi",
                description = "Miles Morales must find his way back home across dimensions to prevent a cataclysmic fate while evading the Multiverse Spider-Society.",
                posterUrl = "https://images.unsplash.com/photo-1513151233558-d860c5398176?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1513151233558-d860c5398176?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=cqGjhVJWtEg",
                category = "upcoming",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = true,
                isInWatchlist = true
            ),
            MovieEntity(
                id = "movlo_chup_chup_ke",
                title = "Chup Chup Ke",
                year = 2006,
                rating = 8.6,
                duration = "2h 44m",
                quality = "1080p HD",
                genre = "Indian Cinema",
                genres = "Indian Cinema, Comedy, Drama, Bollywood",
                description = "A debt-ridden young man attempts suicide by drowning, but is rescued by fishermen who mistake him for mute, resulting in hilarious mayhem.",
                posterUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=SG0bvpT101c",
                category = "indian_cinema",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = false,
                isInWatchlist = false
            ),
            MovieEntity(
                id = "movlo_the_batman_2",
                title = "The Batman: Part II",
                year = 2026,
                rating = 8.8,
                duration = "2h 56m",
                quality = "4K UHD",
                genre = "Action",
                genres = "Action, Crime, Mystery, Drama",
                description = "Bruce Wayne navigates a flooded Gotham City's deep underbelly as new villains arise to claim power from the shadows.",
                posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=mqqft2x_Aa4",
                category = "trending",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = false,
                isInWatchlist = true
            ),
            MovieEntity(
                id = "movlo_rrr",
                title = "RRR",
                year = 2022,
                rating = 8.8,
                duration = "3h 02m",
                quality = "4K UHD",
                genre = "Indian Cinema",
                genres = "Indian Cinema, Action, Drama, South Cinema",
                description = "A fearless revolutionary and an officer in the British force develop a profound friendship before their true identities and conflicting missions collide.",
                posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=NgBoMJy386M",
                category = "indian_cinema",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = true,
                isInWatchlist = false
            ),
            MovieEntity(
                id = "movlo_kgf_2",
                title = "K.G.F: Chapter 2",
                year = 2022,
                rating = 8.4,
                duration = "2h 48m",
                quality = "1080p HD",
                genre = "Indian Cinema",
                genres = "Indian Cinema, Action, Crime, South Cinema",
                description = "Rocky, now the undisputed king of Kolar Gold Fields, must defend his supremacy against brutal rival Adheera and government forces.",
                posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=JKa05nyUmuQ",
                category = "indian_cinema",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = false,
                isInWatchlist = false
            ),
            MovieEntity(
                id = "movlo_stree_2",
                title = "Stree 2",
                year = 2024,
                rating = 8.3,
                duration = "2h 27m",
                quality = "1080p HD",
                genre = "Comedy",
                genres = "Comedy, Horror, Indian Cinema, Bollywood",
                description = "After the events of Stree, the town of Chanderi is plagued by a new headless ghost named Sarkata who abducts women seeking modernization.",
                posterUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=800&auto=format&fit=crop&q=80",
                backdropUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=1200&auto=format&fit=crop&q=80",
                trailerUrl = "https://www.youtube.com/watch?v=qpn7aY8cIuY",
                category = "trending",
                isDownloaded = false,
                downloadSizeMb = 0,
                downloadDate = 0L,
                isFavorite = false,
                isInWatchlist = false
            )
        )
    }
}
