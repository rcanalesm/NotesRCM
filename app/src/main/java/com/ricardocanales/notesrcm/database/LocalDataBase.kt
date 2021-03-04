package com.ricardocanales.notesrcm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ricardocanales.notesrcm.dao.NoteDataBaseDao
import com.ricardocanales.notesrcm.model.Notes

@Database(entities = [Notes::class], version = 1, exportSchema = false)
abstract class LocalDataBase : RoomDatabase() {
    abstract val noteDao: NoteDataBaseDao

    companion object{
        @Volatile
        private var dbInstance : LocalDataBase? = null

        fun getInstance(context: Context): LocalDataBase{
            synchronized(this){
                var instance = dbInstance

                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,LocalDataBase::class.java,"local_database")
                        .fallbackToDestructiveMigration()
                        .build()

                    dbInstance = instance
                }
                return instance
            }
        }
    }
}