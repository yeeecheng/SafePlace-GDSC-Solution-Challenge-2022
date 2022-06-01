package com.plcoding.instagramui.saveplace.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.plcoding.instagramui.saveplace.data.db.entities.StoreItem


@Dao
interface StoreDao {



    @Query("DELETE FROM store_items WHERE store_item_uid = :uid")
    fun deleteByUid(uid:Int)

    //新增 or 更新
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item:StoreItem)

    @Query("SELECT * FROM store_items ")
    fun getAll():List<StoreItem>

    @Query("SELECT * FROM store_items where store_item_uid = :uid")
    fun findByUid(uid:Int):List<StoreItem>

    @Query("SELECT * FROM store_items where store_item_type = :Type")
    fun findByType(Type:String):List<StoreItem>

    @Query("SELECT * FROM store_items where store_item_information = :Info")
    fun findByInfo(Info:String):List<StoreItem>

    @Query("SELECT * FROM store_items where store_item_lng = :lng AND store_item_lat =:lat")
    fun findByLngLat(lng:Double,lat:Double):List<StoreItem>

    @Query("SELECT * FROM store_items where ((store_item_timeStart > store_item_timeEnd) AND ((store_item_timeStart <= :Time OR :Time <=store_item_timeEnd)))OR" +
            "((store_item_timeStart < store_item_timeEnd) AND ((store_item_timeStart <= :Time AND :Time <=store_item_timeEnd)))")
    fun findByTime(Time:Int):List<StoreItem>



}