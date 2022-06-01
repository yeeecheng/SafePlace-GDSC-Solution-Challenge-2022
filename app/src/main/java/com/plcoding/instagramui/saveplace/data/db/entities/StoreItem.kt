package com.plcoding.instagramui.saveplace.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "store_items")
data class StoreItem(

    @PrimaryKey
    @ColumnInfo(name="store_item_uid")
    var uid:Int?=null,
    @ColumnInfo(name="store_item_name")
    var name:String,
    @ColumnInfo(name="store_item_type")
    var type:String,
    @ColumnInfo(name="store_item_information")
    var information:String,
    @ColumnInfo(name="store_item_lng")
    var lng: Double,
    @ColumnInfo(name="store_item_lat")
    var lat:Double,
    @ColumnInfo(name="store_item_timeStart")
    var time_start:Int,
    @ColumnInfo(name="store_item_timeEnd")
    var time_end:Int
)
