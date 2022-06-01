package com.plcoding.instagramui.saveplace.mainActivity


import android.content.Context
import android.os.SystemClock.sleep
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.delay
import java.io.*
import java.net.*
import java.util.*

class clientSocket(IP:String ,Port:Int) {

    private var ip = IP
    private var port =Port
    private var sc:Socket? =null
    private var clientOutputStream: OutputStream? =null
    private var clientInputStream: InputStream? =null
    var isConnected =false
    private lateinit var  Data:String

    fun initConnect() {
        try{

            var addr =InetSocketAddress(ip, port)
            sc =Socket()
            sc!!.connect(addr,10000)

//            sc =Socket(ip,port)

            clientInputStream = sc!!.getInputStream()
            clientOutputStream = sc!!.getOutputStream()

            if(sc!=null && clientInputStream!=null && clientOutputStream!=null){

                isConnected=true
                Log.d("client","connect server successful")
            }
            else{
                Log.d("client","connect server failed,now retry...")
                initConnect()
            }

        }catch (e: SocketTimeoutException) {
            Log.d("client","timeout!! connect failed,now retry...")
            e.printStackTrace()
            initConnect()
        }
        catch(e:IOException){

            e.printStackTrace()
        }

    }

    fun checkDataVersion(version:String) {
        sendMessage("UPDATE|$version")
    }

    fun uploadDataVersion(version:String){
        sendMessage("DOWNLOAD|$version")
    }

    fun addNewStore(name:String,type:String,info:String,addr:String,ts:Int,te:Int){
        sendMessage("NEWST|$name|$type|$info|$addr|$ts|$te")
    }

    //send request of otp
    fun sendRequestOfOtp(){
        sendMessage("SHARE|0")
    }

    //check otp message to get id
    fun checkOTPToGetId(otp:String){
        sendMessage("VYOTP|$otp")
    }



    fun sendMessage(message:String){

        var msg =message
        try{
            if(isConnected){
                if(clientOutputStream!=null&&msg!=null){
                    msg =""" $msg""".trimIndent()

                    val msgToBytes =msg.toByteArray(Charsets.UTF_8)
                    clientOutputStream!!.write(msgToBytes)
                    clientOutputStream!!.flush()
                }
                else {
                    Log.d("client","The message to be sent is empty or have no connect")
                }
                Log.d("client","send message successful")
            }
            else {
                Log.d("client","no connect to send message")
            }
        }
        catch (e:IOException){
            Log.d("client","send message to client failed")
            e.printStackTrace()
        }

    }

    fun receiveMessage():String{


        try {
            if(isConnected){
                Data=""
                var readMsg =ByteArray(4)
                var msgLen =clientInputStream!!.read(readMsg)
                val dataSize = (readMsg[0].toInt() and 0xFF shl 24) or (readMsg[1].toInt() and 0xFF shl 16) or (readMsg[2].toInt() and 0xFF shl 8) or (readMsg[3].toInt() and 0xFF)
                Log.d("clientdata",dataSize.toString())
                var currentDataSize =0
                var data=ByteArray(0)

                while(currentDataSize!=dataSize){
                    //sleep(10)
                    Log.d("clientdata",currentDataSize.toString()+" "+dataSize)
                    readMsg = ByteArray(1024)
                    msgLen =clientInputStream!!.read(readMsg)
                    if(msgLen<0){
                        break
                    }
                    if(readMsg.size!=msgLen){
                        var str =ByteArray(msgLen)
                        System.arraycopy(readMsg,0,str,0,msgLen)
                        readMsg=str
                    }
                    var con = ByteArray(data.size+msgLen)
                    System.arraycopy(data,0,con,0,data.size)
                    System.arraycopy(readMsg,0,con,data.size,msgLen)
                    currentDataSize += msgLen
                    data = con


                    //Log.d("clientdata","!")
                }
                Log.d("clientdata",data.size.toString())
                Data  =String(data,0,data.size)

                //Log.d("data",Data)

                return Data
            }
            else {
                Log.d("client","no connect to receive message")

            }
        }
        catch(e:IOException){
            Log.i("client","receive message failed")
            e.printStackTrace()
        }

        return ""
    }

    fun closeConnect(){
        try {
            if(clientInputStream!=null){
                clientInputStream!!.close()
            }
            if(clientOutputStream!=null){
                clientOutputStream!!.close()
            }
            if(sc!=null){
                sc!!.close()
            }
        }
        catch (e:IOException){
            e.printStackTrace()
        }
        isConnected=false
        Log.d("client","close connect")
    }


}