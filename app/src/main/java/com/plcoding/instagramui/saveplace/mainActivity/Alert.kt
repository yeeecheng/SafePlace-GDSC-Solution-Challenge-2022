package com.plcoding.instagramui.saveplace.mainActivity

import android.app.Activity
import android.media.MediaPlayer
import com.plcoding.instagramui.saveplace.R

class Alert(private val activity: Activity) {

    private var mediaPlay: MediaPlayer = MediaPlayer.create(activity, R.raw.alert_media)

    fun startMedia(){
        mediaPlay.start()
        mediaPlay.isLooping = true
    }



    fun stopMedia(){
        mediaPlay.stop()
        mediaPlay.reset()
        mediaPlay = MediaPlayer.create(activity, R.raw.alert_media)

    }

    fun isMediaPlaying():Boolean{
        return mediaPlay.isPlaying
    }

}