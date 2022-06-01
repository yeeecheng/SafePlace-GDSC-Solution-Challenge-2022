package com.plcoding.instagramui.saveplace.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.plcoding.instagramui.saveplace.data.db.entities.StoreItem

@Database(
    entities = [StoreItem::class],
    version = 1
)



abstract  class StoreDatabase:RoomDatabase() {

    abstract fun getStoreDao(): StoreDao

    companion object {
        @Volatile
        private var instance: StoreDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{ instance = it }
        }


        private fun createDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, StoreDatabase::class.java, "storeDB.db").allowMainThreadQueries().build()
    }
}