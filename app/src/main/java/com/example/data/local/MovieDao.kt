package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY rating DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isDownloaded = 1 ORDER BY downloadDate DESC")
    fun getDownloadedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isInWatchlist = 1 ORDER BY title ASC")
    fun getWatchlistMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY rating DESC")
    fun getMoviesByCategory(category: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun getMovieById(id: String): Flow<MovieEntity?>

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' OR genre LIKE '%' || :query || '%' OR genres LIKE '%' || :query || '%'")
    fun searchMovies(query: String): Flow<List<MovieEntity>>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("UPDATE movies SET isDownloaded = :isDownloaded, downloadSizeMb = :sizeMb, downloadDate = :date WHERE id = :id")
    suspend fun setDownloaded(id: String, isDownloaded: Boolean, sizeMb: Int, date: Long)

    @Query("UPDATE movies SET isInWatchlist = :inWatchlist WHERE id = :id")
    suspend fun setWatchlist(id: String, inWatchlist: Boolean)

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE movies SET isDownloaded = 0, downloadSizeMb = 0, downloadDate = 0")
    suspend fun clearAllDownloads()
}
