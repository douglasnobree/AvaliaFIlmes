package com.example.avaliafilmes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.avaliafilmes.data.dao.MovieReviewDao
import com.example.avaliafilmes.data.dao.UserDao
import com.example.avaliafilmes.data.model.MovieReview
import com.example.avaliafilmes.data.model.User

@Database(
    entities = [User::class, MovieReview::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun movieReviewDao(): MovieReviewDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "avalia_filmes_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
