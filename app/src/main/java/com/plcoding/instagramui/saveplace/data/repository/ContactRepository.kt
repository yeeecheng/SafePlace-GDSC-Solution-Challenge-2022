package com.plcoding.instagramui.saveplace.data.repository

import com.plcoding.instagramui.saveplace.data.db.ContactDatabase
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem

class ContactRepository(
    private val db: ContactDatabase
) {
    suspend fun upsert(item: ContactItem) = db.getContactDao().upsert(item)

    suspend fun delete(item: ContactItem) = db.getContactDao().delete(item)

    suspend fun update(item: ContactItem) = db.getContactDao().update(item)

    fun getAllContactItems() = db.getContactDao().getAllContactItems()

    fun getIdByName (name:String):Int = db.getContactDao().getIdByName(name)

    fun getContactPhoneNumber() = db.getContactDao().getContactPhoneNumber()

    fun getItemNameById(id :Int) = db.getContactDao().getItemNameById(id)

    fun getItemPhoneNumberById(id:Int)= db.getContactDao().getItemPhoneNumberById(id)
}