package com.plcoding.instagramui.saveplace.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ContactItem)

    @Delete
    suspend fun delete(item: ContactItem)

    @Update
    suspend fun update(item: ContactItem)

    @Query("SELECT * FROM contact_items WHERE id  > 0")
    fun getAllContactItems(): LiveData<List<ContactItem>>

    @Query ("SELECT id FROM contact_items WHERE contact_item_name = :name AND id>0")
    fun getIdByName (name: String) : Int

    @Query("SELECT contact_item_phone_number FROM contact_items  WHERE id > 0")
    fun getContactPhoneNumber():List<String>

    @Query ("SELECT contact_item_name FROM contact_items where id = :id   ")
    fun getItemNameById (id:Int) : String

    @Query ("SELECT contact_item_phone_number FROM contact_items where id =:id ")
    fun getItemPhoneNumberById(id:Int):String


}