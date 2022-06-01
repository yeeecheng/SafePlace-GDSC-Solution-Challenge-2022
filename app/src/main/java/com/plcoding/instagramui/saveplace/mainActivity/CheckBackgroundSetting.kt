package com.plcoding.instagramui.saveplace.mainActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.plcoding.instagramui.saveplace.R

class CheckBackgroundSetting {

    private var hasShowGpsAlert=false

    fun checkGPS(ctx:Context){
        val state =getGpsStatus(ctx)
        if(hasShowGpsAlert &&state){
            hasShowGpsAlert=false;
        }
        else if(!state&&!hasShowGpsAlert){
            hasShowGpsAlert=true
            AlertDialog.Builder(ctx)
                .setMessage(R.string.location_not_open)
                .setPositiveButton(R.string.setting){ _, _ ->
                    goToOpenGps(ctx)
                }.show()
        }
    }

    //confirm Internet
    fun checkInternet(act: Activity, ctx: Context){
        val state = haveInternet(ctx)
        if(!state){

            AlertDialog.Builder(ctx)
                .setMessage("網路沒開啟將無法運行!!!")
                .setPositiveButton("OK!!"){ _, _ ->
                    act.finish();
                }.show()

        }
    }


    private fun haveInternet(ctx: Context): Boolean {

        val connManager = ctx.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connManager.activeNetworkInfo

        return if(info==null||!info.isConnected){
            false
        } else {
            info.isAvailable
        }

    }


    private fun getGpsStatus(ctx: Context): Boolean {
        //从系统服务中获取定位管理器
        val lm = ctx.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun goToOpenGps(ctx: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        ctx.startActivity(intent)

    }


}