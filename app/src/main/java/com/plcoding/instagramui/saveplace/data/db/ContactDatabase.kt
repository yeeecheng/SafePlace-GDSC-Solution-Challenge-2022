package com.plcoding.instagramui.saveplace.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem

@Database(
    entities = [ContactItem::class],
    version = 1
)

abstract class ContactDatabase: RoomDatabase() {

    abstract fun getContactDao(): ContactDao

    companion object {
        @Volatile
        private var instance: ContactDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{ instance = it }
        }


        private fun createDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, ContactDatabase::class.java, "contactDB.db").allowMainThreadQueries().build()
    }
}