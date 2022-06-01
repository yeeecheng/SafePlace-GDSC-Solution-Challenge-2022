package com.plcoding.instagramui.saveplace.mainActivity

import android.content.Context
import java.io.*

class FileOperation {

    fun readFile(context : Context, filename:String):String{

        var data =""
        val path: File = context.filesDir
        val content = ByteArray(1024)
        File(path, filename).createNewFile()
        val reader = FileInputStream(File(path, filename))
        val contentLen =reader.read(content)
        if(contentLen==-1){
            return ""
        }
        val newContent=ByteArray(contentLen)
        System.arraycopy(content,0,newContent,0,contentLen)

        data =String(newContent,0,contentLen)
        return data
    }

    fun writeFile(context: Context, data:String, filename:String){
        val path: File = context.filesDir

        val writer = FileOutputStream(File(path, filename))
        writer.write(data.toByteArray())
        writer.close()
    }



}