package com.plcoding.instagramui.saveplace.data.db.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "contact_items")
data class ContactItem(


    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "contact_item_name")
    var name: String,
    @ColumnInfo(name = "contact_item_phone_number")
    var phoneNumber: String


)


