package com.plcoding.instagramui.saveplace.mainActivity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.plcoding.instagramui.saveplace.R

class PermissionManager {

    private var phoneCallPermissionGranted :Boolean=false
    private var sendMessagePermissionGrated :Boolean =false
    private var locationPermissionGrated:Boolean=false

//    private var readPhoneStatePermissionGrated:Boolean=false

    private var readPhoneNumberPermissionGrated:Boolean=false

    private val permissionRequestList :MutableList<String> = ArrayList()
    private val REQUEST_MUTILPLE_CODE=1

    private val permissionList = listOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.ACCESS_FINE_LOCATION ,

//        Manifest.permission.READ_PHONE_STATE,

        Manifest.permission.READ_PHONE_NUMBERS
    )

    //setting permission granted

    fun setPhoneCallPermissionGranted(state:Boolean){
        phoneCallPermissionGranted=state
    }
    fun setSendMessagePermissionGrated(state:Boolean){
        sendMessagePermissionGrated=state
    }

    fun setLocationPermissionGrated(state:Boolean){
        locationPermissionGrated=state
    }
//    fun setReadPhoneStatePermissionGrated(state:Boolean){
//        readPhoneStatePermissionGrated=state
//    }

    fun setReadPhoneNumberPermissionGrated(state:Boolean){
        readPhoneNumberPermissionGrated=state
    }



    //get permission granted
    fun getPhoneCallPermissionGranted():Boolean{
        return phoneCallPermissionGranted
    }
    fun getSendMessagePermissionGrated():Boolean{
        return sendMessagePermissionGrated
    }

    fun getLocationPermissionGrated():Boolean{
        return locationPermissionGrated
    }
//    fun getReadPhoneStatePermissionGrated():Boolean{
//        return readPhoneStatePermissionGrated
//    }

    fun getReadPhoneNumberPermissionGrated():Boolean{
        return readPhoneNumberPermissionGrated
    }


    fun getREQUEST_MUTILPLE_CODE():Int{
        return REQUEST_MUTILPLE_CODE
    }


    private fun checkAllGrated():Boolean{

//        &&readPhoneStatePermissionGrated

        if(phoneCallPermissionGranted&&sendMessagePermissionGrated&&readPhoneNumberPermissionGrated&&locationPermissionGrated){

            return true
        }

        return false
    }

    //check permission after click Button
    fun checkPermissionAfterClickPhoneButton(act: Activity,ctx:Context):Boolean{
        Log.d("phone","all grated " +checkAllGrated().toString())
        if(!checkAllGrated()){

            if(!phoneCallPermissionGranted){
                requestPhoneCallPermission(act,ctx)
            }

            if(!sendMessagePermissionGrated){
                requestSendMessagePermission(act,ctx)
            }

            if(!locationPermissionGrated){
                requestLocationPermission(act,ctx)
            }


//            if(!readPhoneStatePermissionGrated){
//                requestReadPhoneStatePermission(act,ctx)
//            }
//


//            if(!readPhoneNumberPermissionGrated){
//                requestReadPhoneNumberPermission(act,ctx)
//            }
            return false
        }
        return true
    }

    //set state of all permission  when APP start
    fun checkAllPermission(ctx:Context){


        phoneCallPermissionGranted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE)==0
        sendMessagePermissionGrated = ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS)==0
        locationPermissionGrated = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)==0
//        readPhoneStatePermissionGrated = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE)==0
        readPhoneNumberPermissionGrated = ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_NUMBERS)==0

    }


    //get permission
    fun getPermission(act:Activity){


        var count=0

        if(phoneCallPermissionGranted){

            permissionRequestList.add("")

        }
        else {

            permissionRequestList.add(Manifest.permission.CALL_PHONE)
            count++

        }

        if(sendMessagePermissionGrated){

            permissionRequestList.add("")

        }
        else {

            permissionRequestList.add(Manifest.permission.SEND_SMS)
            count++

        }

        if(locationPermissionGrated){

            permissionRequestList.add("")

        }
        else {

            permissionRequestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
            count++

        }


//        if(readPhoneStatePermissionGrated){
//
//            permissionRequestList.add("")
//
//        }
//        else {
//
//            permissionRequestList.add(Manifest.permission.READ_PHONE_STATE)
//            count++
//
//        }
//



        if(readPhoneNumberPermissionGrated){

            permissionRequestList.add("")

        }
        else {

            permissionRequestList.add(Manifest.permission.READ_PHONE_NUMBERS)
            count++

        }


        if(permissionRequestList.isNotEmpty()&&count!=0){
            ActivityCompat.requestPermissions(act,permissionList.toTypedArray() ,REQUEST_MUTILPLE_CODE)
        }

    }

    //phone call permission request
    fun requestPhoneCallPermission(act:Activity,ctx: Context){
        if(ActivityCompat.shouldShowRequestPermissionRationale(act,Manifest.permission.CALL_PHONE)){//因為第一次拒絕，所以這次要跑這個，第三
            AlertDialog.Builder(ctx)
                .setMessage(R.string.phone_call_permission_title)
                .setPositiveButton(R.string.confirm){_, _ -> ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_MUTILPLE_CODE)}
                .setNegativeButton(R.string.cancel){_,_->requestPhoneCallPermission(act,ctx)}.show()
        }
        else {
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_MUTILPLE_CODE)
        }
    }

    //send message permission request
    fun requestSendMessagePermission(act:Activity,ctx: Context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.SEND_SMS)) {//因為第一次拒絕，所以這次要跑這個，第三
            AlertDialog.Builder(ctx)
                .setMessage(R.string.send_message_permission_title)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.SEND_SMS),REQUEST_MUTILPLE_CODE)
                }
                .setNegativeButton(R.string.cancel) { _, _ -> requestSendMessagePermission(act,ctx) }.show()
        } else {
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.SEND_SMS),REQUEST_MUTILPLE_CODE)

        }
    }

    //location permission request
    fun requestLocationPermission(act:Activity,ctx: Context){
        if(ActivityCompat.shouldShowRequestPermissionRationale(act,Manifest.permission.ACCESS_FINE_LOCATION)){//因為第一次拒絕，所以這次要跑這個，第三
            AlertDialog.Builder(ctx)
                .setMessage(R.string.location_permission_title)
                .setPositiveButton(R.string.confirm){_, _ -> ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_MUTILPLE_CODE)}
                .setNegativeButton(R.string.cancel){_,_->requestLocationPermission(act,ctx)}.show()
        }
        else {
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_MUTILPLE_CODE)

        }
    }



    //read phone call permission request
//    fun requestReadPhoneStatePermission(act:Activity,ctx: Context) {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.READ_PHONE_STATE)) {//因為第一次拒絕，所以這次要跑這個，第三
//
//            AlertDialog.Builder(ctx)
//                .setMessage("此應用程式，需狀態權限才能正常使用")
//                .setPositiveButton(R.string.confirm) { _, _ ->
//                    ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.READ_PHONE_STATE),REQUEST_MUTILPLE_CODE)
//                }
//                .setNegativeButton(R.string.cancel) { _, _ -> requestReadPhoneStatePermission(act,ctx) }.show()
//        } else {
//
//            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.READ_PHONE_STATE),REQUEST_MUTILPLE_CODE)
//
//        }
//    }


    //read phone number permission request
    fun requestReadPhoneNumberPermission(act:Activity,ctx: Context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.READ_PHONE_NUMBERS)) {//因為第一次拒絕，所以這次要跑這個，第三

            AlertDialog.Builder(ctx)
                .setMessage(R.string.read_phone_number_permission_title)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.READ_PHONE_NUMBERS),REQUEST_MUTILPLE_CODE)
                }
                .setNegativeButton(R.string.cancel) { _, _ -> requestReadPhoneNumberPermission(act,ctx) }.show()
        } else {

            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.READ_PHONE_NUMBERS),REQUEST_MUTILPLE_CODE)

        }
    }


}