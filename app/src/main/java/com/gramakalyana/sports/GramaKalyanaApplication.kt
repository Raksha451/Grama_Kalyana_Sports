package com.gramakalyana.sports

import android.app.Application
import androidx.room.Room
import com.gramakalyana.sports.data.local.AppDatabase

class GramaKalyanaApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "grama_kalyana_db"
        ).fallbackToDestructiveMigration()
         .build()
        
        instance = this
    }

    companion object {
        lateinit var instance: GramaKalyanaApplication
            private set
    }
}
