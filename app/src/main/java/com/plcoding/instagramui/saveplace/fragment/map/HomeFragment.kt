package com.plcoding.instagramui.saveplace.fragment.map


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.PolyUtil
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.plcoding.instagramui.saveplace.BuildConfig.MAPS_API_KEY
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.data.db.StoreDatabase
import com.plcoding.instagramui.saveplace.data.db.entities.StoreItem
import com.plcoding.instagramui.saveplace.mainActivity.*
import com.plcoding.instagramui.saveplace.mainActivity.AESCrypt.toHex
import org.json.JSONObject
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class HomeFragment(PM: PermissionManager) : Fragment(), GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,OnMapsSdkInitializedCallback
    , OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowLongClickListener{
    private lateinit var mMap: GoogleMap
    private var directionJson = ""
    private var placeData : MutableList<Pair<LatLng,Pair<String,String> > > = mutableListOf()
    private var markers : MutableList<Pair<Marker?,Pair<String,LatLng> > > = mutableListOf()
    private var markVisible = false
    private var heatMapLatLngs: MutableList<LatLng> = mutableListOf()
    private var trackData: MutableList<Pair<String, String>> = mutableListOf()
    private var permissionManager=PM
    private var hasShowPermissionAlert =false
    lateinit var currentLocation: Location
    private var nowDirect = 0
    private var tracknumber = 0
    private var tmpnumber = 0
    private var senderlocation: LatLng? = null
    private var placeShow = mutableMapOf("convenienceStore" to true , "gasStation" to true , "policeStation" to true)
    private lateinit var  polyline1 : Polyline
    private lateinit var provider: HeatmapTileProvider
    private lateinit var overlay: TileOverlay
    private val googleMapHandler = Handler(Looper.getMainLooper())
    private val directionHandler = Handler(Looper.getMainLooper())
    private val enableLocationHandler = Handler(Looper.getMainLooper())
    private val locationHandler = Handler(Looper.getMainLooper())
    private val trackHandler = Handler(Looper.getMainLooper())
    private val updataetrackHandler = Handler(Looper.getMainLooper())
    private val showtrackHandler = Handler(Looper.getMainLooper())
    private val reporterrorHandler = Handler(Looper.getMainLooper())
    private val sendercameraHandler = Handler(Looper.getMainLooper())
    private val addnewmarkerHandler = Handler(Looper.getMainLooper())
    private val loafmarkerloopHandler = Handler(Looper.getMainLooper())
    private val WaitReportRecvloopHandler = Handler(Looper.getMainLooper())
    //    private lateinit var binding: ActivityM

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var  curLat:String
    lateinit var  curLng:String
    lateinit var  directionText:String
    lateinit var  navigationTime:String
    lateinit var  navigationDistance:String

    private lateinit var db: StoreDatabase

    private lateinit var  mapFragment :SupportMapFragment



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //initialize map fragment
        MapsInitializer.initialize(requireActivity().applicationContext, MapsInitializer.Renderer.LATEST, this)
        //initialize view
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        Log.d("build","home oncreateview")


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        loadFile()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val lightModeSwitch = activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)

        var options = GoogleMapOptions()
            .mapId("fc18cda0b78b9e40")

        if(lightModeSwitch?.isChecked == true){
            options = GoogleMapOptions()
                .mapId("6f9152b98d311d9e")

        }




        mapFragment = SupportMapFragment.newInstance(options)

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.google_map,mapFragment)
            ?.commit()


        mapFragment?.getMapAsync(this)



        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {


        println("onMapReady")



        //google map 變數
        mMap = googleMap

        // 設定定位和原點的click event
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowLongClickListener(this)
        mMap.setInfoWindowAdapter(MyInfoWindowAdapter(requireContext()))
        //enableMyLocation()



        enableLocationHandler.post( object : Runnable {
            override fun run() {

                if(enableMyLocation()==false)
                    enableLocationHandler.postDelayed(this,1000)
                else
                    Log.d("Location","OK")

            }

        })

        //熱點圖
        if(heatMapLatLngs.isNotEmpty()){

            val colors = intArrayOf(
                Color.rgb(0, 128, 255),
                Color.rgb(0, 255, 255),
                Color.rgb(255, 255, 255)
            )
            val startPoints = floatArrayOf(0.01f ,0.03f ,1f)
            val gradient = Gradient(colors, startPoints,10000)

            provider = HeatmapTileProvider.Builder()
                .gradient(gradient)
                .radius(50)
                .data(heatMapLatLngs)
                .build()

            overlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))!!

            Log.d("heatmap","!")
        }


        googleMapHandler.post( object : Runnable {
            override fun run() {

                getLastKnownLocation()
                googleMapHandler.postDelayed(this, 10000)

            }

        })



        locationHandler.post( object : Runnable {
            override fun run() {

                if(updateCurrentLocation()==false) {
                    locationHandler.postDelayed(this, 1000)
                }
                else {
                    activity?.findViewById<CoordinatorLayout>(R.id.main_container)?.visibility=View.VISIBLE
                    activity?.findViewById<NavigationView>(R.id.nav_view)?.visibility=View.VISIBLE
                    activity?.findViewById<ProgressBar>(R.id.loader)?.visibility=View.GONE
                    loadMarker()
                }
            }

        })


        trackHandler.post( object : Runnable {
            override fun run() {

                if(this@HomeFragment.currentLocationIsInitialized()) {

                    savetrack()
                    trackHandler.postDelayed(this, 30000)
                }
                else
                    trackHandler.postDelayed(this, 3000)



            }

        })

        //updateCurrentLocation()



    }

    @SuppressLint("MissingPermission")
    fun enableMyLocation():Boolean {
        if (!::mMap.isInitialized) return true
        // [START maps_check_location_permission]
        if (permissionManager.getLocationPermissionGrated()) {
            mMap.isMyLocationEnabled = true
            hasShowPermissionAlert=false
            return true
        } else {
//            Log.d("res","@### "+hasShowPermissionAlert.toString())
            if(!hasShowPermissionAlert){
                permissionManager.requestLocationPermission(activity as MainActivity,context as MainActivity)
                hasShowPermissionAlert=true
            }
            return  false
        }
        // [END maps_check_location_permission]
    }



    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17F))
        return false
    }


    override fun onMyLocationClick(location: Location) {
        Toast.makeText(getActivity(), "Current location:\n$location", Toast.LENGTH_LONG).show()
    }


    //選定的marker不顯示infowindow，代表沒有click
    override fun onMarkerClick (marker : Marker): Boolean {

        return marker.zIndex==-1.0f
    }


    //長按出現回報視窗
    override fun onInfoWindowLongClick(marker : Marker) {

        if(marker.zIndex == 0.0f) {

            var uid = marker.snippet?.split("?")?.get(0)



            Log.d("uid", uid.toString())

            //REPORT|Store_id|Reason
            var result:String=""

            AlertDialog.Builder(requireContext())
                .setTitle(R.string.Report_title)
                .setMessage(getString(R.string.Report_msg1) + " "+marker.title +" "+ getString(R.string.Report_msg2))
                .setNegativeButton(R.string.Report_button1) { _, _ ->


                    //Log.d("track", "error: $ask")
                    Thread {
                        var ask = "REPORT|$uid|WRONG DATA"
                        var client = clientSocket("35.206.214.161", 3333)
                        client.initConnect()

                        Log.d("track", "error: $ask")

                        client.sendMessage(ask)
                        result = client.receiveMessage()
                        Log.d("track", "result: $result")
                        client.closeConnect()


                    }.start()
                }
                .setPositiveButton(R.string.Report_button2) { _, _ ->


                    Thread {
                        var ask = "REPORT|$uid|NOT EXIST"
                        var client = clientSocket("35.206.214.161", 3333)
                        client.initConnect()

                        Log.d("track", "error: $ask")

                        client.sendMessage(ask)
                        result = client.receiveMessage()
                        Log.d("track", "result: $result")
                        client.closeConnect()


                    }.start()

                }.show()

            WaitReportRecvloopHandler.post( object : Runnable {
                override fun run() {

                    if(result=="")
                        WaitReportRecvloopHandler.postDelayed(this, 1000)
                    else if (result != "Success")
                        Toast.makeText(getActivity(), "Report fail.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(getActivity(), "Report success.", Toast.LENGTH_SHORT).show()


                }

            })



        }
    }


    //infowindow
    class MyInfoWindowAdapter(context: Context ) : GoogleMap.InfoWindowAdapter {

        var mWindow: View = (context as Activity).layoutInflater.inflate(R.layout.infowindow, null)

        private fun render(marker: Marker, view: View) {

            val tvTitle = view.findViewById<TextView>(R.id.title)
            val tvSnippet = view.findViewById<TextView>(R.id.snippet)

            if(marker.snippet?.split("?")?.get(0) == "time"){
                tvTitle.text = marker.snippet?.split("?")?.get(1)
                tvSnippet.text = null
            }
            else{
                tvTitle.text = marker.title
                tvSnippet.text = marker.snippet?.split("?")?.get(1)
            }

        }

        override fun getInfoContents(marker: Marker): View {
            render(marker, mWindow)
            return mWindow
        }

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

    }




    override fun onResume() {
        super.onResume()
        Log.d("build","onResume")
    }





    //新版rander
    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }


    //選定的marker
    private var mark: Marker? = null
    //HTTP request
    private fun getGoogleDirection(latlng : LatLng){
        val lat = latlng.latitude.toString()
        val lng = latlng.longitude.toString()
        var lan = Locale.getDefault().language
        curLat = currentLocation.latitude.toString()
        curLng = currentLocation.longitude.toString()

        mark?.remove()

        mark = mMap.addMarker(
            MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.check))
                .anchor(0.53f,1.6f)
                .zIndex(-1.0f)
        )


        if(lan=="zh")
            lan+="-TW"

        //多執行續處理HTTP request
        Executors.newSingleThreadExecutor().execute {

            //var url : String = "https://maps.googleapis.com/maps/api/directions/json?origin=$curLat,$curLng&destination=$lat,$lng&mode=walking&key=${MAPS_API_KEY}"
            var url : String = "https://maps.googleapis.com/maps/api/directions/json?origin=$curLat,$curLng&destination=$lat,$lng&language=${lan}&mode=walking&key=${MAPS_API_KEY}"
            directionJson = URL(url).readText()

        }
        //等待HTTP request跑完


        directionHandler.post( object : Runnable {
            override fun run() {

                if(directionJson=="") {
                    Log.d("wait","!")
                    directionHandler.postDelayed(this, 500)
                }
                else{

                    //解析JSON檔案




                    val jsonObject = JSONObject(directionJson)
                    if(jsonObject.getString("status")=="ZERO_RESULTS")
                        return

                    Log.d("Location",directionJson)
                    val point = jsonObject.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")

                    Log.d("Url", point)

                    directionText = jsonObject.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONArray("steps")
                        .getJSONObject(0)
                        .getString("html_instructions")

                    Log.d("Url", directionText)

                    navigationDistance = jsonObject.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONObject("distance")
                        .getString("text")

                    navigationTime = jsonObject.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONObject("duration")
                        .getString("text")

                    (activity as MainActivity).printInstruct(
                        directionText,
                        navigationTime,
                        navigationDistance
                    )
                    //解碼point
                    val decode = PolyUtil.decode(point)

                    if (this@HomeFragment::polyline1.isInitialized)
                        polyline1.remove()
                    //畫出路線
                    polyline1 = mMap.addPolyline(
                        PolylineOptions()
                            .clickable(true)
                            .color(-0x7e387c)
                            .width(30F)
                            .addAll(decode)
                    )



                    Log.d("Url", decode.toString())

                    directionJson = ""

                }
            }

        })


    }



    //得到目前座標 並 更新鏡頭
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    currentLocation=location

                    //Log.d("location", "${currentLocation.longitude} ${currentLocation.latitude}")

                    //Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
                    //Toast.makeText(activity,"!", Toast.LENGTH_LONG).show()
                    //updateCurrentLocation()
                    calculateDistance()

                    if(placeData.isNotEmpty())
                    getGoogleDirection(placeData[nowDirect].first)
                    //loadMarker()


                }

            }

    }



    //更新鏡頭到目前位置
    private fun updateCurrentLocation():Boolean{

        if(this::currentLocation.isInitialized) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0F))
            mMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
            );

            return true;
        }
        else
            return false;
    }


    private fun savetrack(){

        if(tracknumber==10){
            tracknumber=0
        }

        val rightNow = Calendar.getInstance()
        val tracktime = String.format("%02d:%02d:%02d", (rightNow.get(Calendar.HOUR_OF_DAY)+8)%24, (rightNow.get(Calendar.MINUTE)), (rightNow.get(Calendar.SECOND)))
        val tracklatlng = "${currentLocation.latitude},${currentLocation.longitude}"


        if(trackData.getOrNull(tracknumber) == null)
            trackData.add(Pair(tracklatlng, tracktime))
        else
            trackData.set(tracknumber, Pair(tracklatlng, tracktime))

        Log.d("track","$tracknumber:${trackData[tracknumber].first}, ${trackData[tracknumber].second}")

        tracknumber++

    }

    fun updataetrack(key: String, iv: String, id: String){


        //SHARE|id|position|time_with_timezone
        //SHARE|0|regj+,gk148t_%$(r64|00:00:00+8

        lateinit var aes: AESCrypt

        Thread{

            lateinit var  client : clientSocket
            aes = AESCrypt

            var trackDatatmp: MutableList<Pair<String, String>> = mutableListOf()
            for(i in trackData)
            {
                trackDatatmp.add(i)
            }

            Log.d("track","${trackDatatmp.size}")


            for(i in trackDatatmp){


                client = clientSocket("35.206.214.161",3333)
                client.initConnect()

                var data= aes.encrypt(key, iv, i.first)
                var stand ="SHARE|$id|$data|${i.second}+8"
                Log.d("track","$stand")

                client.sendMessage(stand)
                Log.d("track", "${i.second}:${client.receiveMessage()}")


                client.closeConnect()
            }



        }.start()


        lateinit var  client : clientSocket
        var starttime = Calendar.getInstance().get(Calendar.MINUTE)
        var endtime = (starttime+30)%60
        updataetrackHandler.post( object : Runnable {
            override fun run() {

                Thread{

                    client = clientSocket("35.206.214.161",3333)
                    client.initConnect()

                    val rightNow = Calendar.getInstance()
                    val tracktime = String.format("%02d:%02d:%02d", (rightNow.get(Calendar.HOUR_OF_DAY)+8)%24, (rightNow.get(Calendar.MINUTE)), (rightNow.get(Calendar.SECOND)))


                    val tracklatlng: String = "${currentLocation.latitude},${currentLocation.longitude}"

                    var data: String? = aes.encrypt(key, iv, tracklatlng)
                    var stand ="SHARE|$id|$data|${tracktime}+8"
                    Log.d("track","now:$stand")


                    client.sendMessage(stand)
                    Log.d("track", "${tracktime}:${client.receiveMessage()}")


                    client.closeConnect()

                }.start()

                var tmpnow = Calendar.getInstance().get(Calendar.MINUTE)
                Log.d("track","$tmpnow:$starttime-$endtime")

                if(starttime>endtime){

                    if(tmpnow<starttime && tmpnow>endtime){
                        Log.d("track","stopupdate")

                    }else
                        updataetrackHandler.postDelayed(this,30000)

                }else{

                    if(tmpnow<starttime || tmpnow>endtime){
                        Log.d("track","stopupdate")

                    }else
                        updataetrackHandler.postDelayed(this,30000)

                }
            }

        })




    }


    fun showtrack(key: String, iv: String, id: String){

        lateinit var  client : clientSocket
        lateinit var aes: AESCrypt
        aes = AESCrypt

        //GETLOC|id|time_with_timezon
        //GETLOC|2561|16:51:31+8

        var updateidx = 0
        var trackmark: MutableList<Marker> = mutableListOf()
        var trackMarkerData: MutableList<Pair<String, String>> = mutableListOf()
        var starttime = Calendar.getInstance().get(Calendar.MINUTE)
        var endtime = (starttime+30)%60

        val rightNow = Calendar.getInstance()
        var tracktime:String= String.format(
            "%02d:%02d:%02d",
            (rightNow.get(Calendar.HOUR_OF_DAY) -1+8) % 24,
            (rightNow.get(Calendar.MINUTE)),
            (rightNow.get(Calendar.SECOND))
        )

        showtrackHandler.post( object : Runnable {
            override fun run() {

                Thread{

                    Log.d("track", "waitconnect")
                    client = clientSocket("35.206.214.161",3333)
                    client.initConnect()

                    var stand = "GETLOC|$id|${tracktime}+8"
                    Log.d("track", stand)
                    client.sendMessage(stand)


                    var tmptrack = client.receiveMessage()
                    Log.d("track",tmptrack)
                    client.closeConnect()

                    if(tmptrack.contains('\n')){
                        var recitrack = tmptrack.split('\n')


                        //Log.d("track", "test:${recitrack.size}")
                        for (i in 0..(recitrack.size-2)) {
                            //regj+,gk148t_%$(r64|16:53:00+8
                            Log.d("track","$i:${recitrack[i]}")
                            if(recitrack[i]!=null){

                                var ori:String = aes.decrypt(key, iv, recitrack[i].split(' ')[0]).toString()
                                tracktime = recitrack[i].split(' ')[1]
                                Log.d("track", "$i:decrypt->${ori} ${tracktime}")

                                trackMarkerData.add(Pair(ori, tracktime))
                                senderlocation = LatLng(ori.split(',')[0].toDouble(), ori.split(',')[1].toDouble())

                                tracktime = tracktime.split('+')[0]
                            }

                        }
                    }

                }.start()

                Log.d("track", "no:${trackMarkerData.size}")

                if(trackMarkerData.isNotEmpty() && trackMarkerData.size > updateidx){

                    Log.d("track", "hi:${trackMarkerData.size}")

                    for(i in updateidx..(trackMarkerData.size-1)){

                        var tmpmark: Marker? = mMap.addMarker(
                            MarkerOptions()
                                .position(
                                    LatLng(
                                        trackMarkerData[i].first.split(',')[0].toDouble(),
                                        trackMarkerData[i].first.split(',')[1].toDouble()
                                    )
                                )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle))
                                .snippet("time?${trackMarkerData[i].second.split('+')[0]}")
                                .zIndex(1.0f)
                        )

                        if (tmpmark != null) {
                            trackmark.add(tmpmark)
                        }

                        Log.d("track", tmpmark.toString())
                    }

                    updateidx = trackMarkerData.size
                }

                var tmpnow = Calendar.getInstance().get(Calendar.MINUTE)
                Log.d("track","$tmpnow:$starttime-$endtime")

                if(starttime>endtime && tmpnow<starttime && tmpnow>endtime){

                    Log.d("track","remove")
                    for(item in trackmark){
                        item.remove()
                    }

                }else if(starttime<endtime && (tmpnow<starttime || tmpnow>endtime)){

                    Log.d("track","remove")
                    for(item in trackmark){
                        item.remove()
                    }

                }else if(trackMarkerData.isEmpty())
                    showtrackHandler.postDelayed(this,2000)
                else
                    showtrackHandler.postDelayed(this,30000)
            }

        })

        Log.d("track","bye")

    }


    private fun calculateDistance(){
        placeData.sortBy { Math.abs(currentLocation.latitude-it.first.latitude)*101+Math.abs(currentLocation.longitude-it.first.longitude)*111  }

        /*
        for(i in 0..4) {
            mMap.addMarker(
                MarkerOptions()
                    .position(placeData[i].first)
            )
        }

         */

    }

    override fun onCameraIdle() {
        // [START_EXCLUDE silent]
        if(!markVisible&&mMap.cameraPosition.zoom>=15){
            markers.forEach(){

                if(placeShow[it.second.first]==true)
                    it.first?.setVisible(true)

            }
            markVisible=true;
        }
        else if(markVisible && mMap.cameraPosition.zoom<15){

            markers.forEach(){

                it.first?.setVisible(false)


            }
            markVisible=false;
        }


        Log.d("Camera", "onCameraIdle")
    }

    fun sendercamera(){

        if(senderlocation != null){
            mMap.moveCamera(CameraUpdateFactory.zoomTo(17.0F))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(senderlocation!!))
        }



    }


    private fun loadMarker(){
        Log.d("countcount","count!")

        loadmarkerloop()


    }

    private fun loadmarkerloop(){

        loafmarkerloopHandler.post( object : Runnable{
            override fun run() {

                if(activity==null){
                    return
                }
                Log.d("run","runrunrun")
                for(i in markers
                    .size..markers.size+200)
                    if(i>=placeData.size){
                        Log.d("Load",markers.size.toString())

                        return
                    }
                    else{

                        var other = placeData[i].second.second.split("!")[1]
                        var type = placeData[i].second.second.split("!")[0]
                        var positionLatLng = placeData[i].first
                        var plcaeName = placeData[i].second.first

                        when(type) {

                            "便利商店" ->
                                markers.add(Pair(mMap.addMarker(
                                    MarkerOptions()
                                        .position(positionLatLng)
                                        .title(plcaeName)
                                        .snippet(other)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.convenience_store))),
                                    Pair("convenienceStore",positionLatLng))


                                )




                            "警察局"->
                                markers.add(Pair(mMap.addMarker(
                                    MarkerOptions()
                                        .position(positionLatLng)
                                        .title(plcaeName)
                                        .snippet(other)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.police_station))),
                                    Pair("policeStation",positionLatLng))

                                )



                            "加油站"->
                                markers.add(Pair(mMap.addMarker(
                                    MarkerOptions()
                                        .position(positionLatLng)
                                        .title(plcaeName)
                                        .snippet(other)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.gas_station))),
                                    Pair("gasStation",positionLatLng))

                                )

                            "消防局"->
                                markers.add(Pair(mMap.addMarker(
                                    MarkerOptions()
                                        .position(positionLatLng)
                                        .title(plcaeName)
                                        .snippet(other)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.garage))),
                                    Pair("fireDepartment",positionLatLng))

                                )

                            "醫院"->
                                markers.add(Pair(mMap.addMarker(
                                    MarkerOptions()
                                        .position(positionLatLng)
                                        .title(plcaeName)
                                        .snippet(other)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital))),
                                    Pair("hospital",positionLatLng))

                                )

                            "餐廳"->
                                markers.add(Pair(mMap.addMarker(
                                    MarkerOptions()
                                        .position(positionLatLng)
                                        .title(plcaeName)
                                        .snippet(other)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant))),
                                    Pair("restaurant",positionLatLng))

                                )

                            "其他"->
                                markers.add(Pair(mMap.addMarker(
                                    MarkerOptions()
                                        .position(positionLatLng)
                                        .title(plcaeName)
                                        .snippet(other)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.open_sign))),
                                    Pair("other",positionLatLng))

                                )

                        }

                        if(mMap.cameraPosition.zoom>=15)
                            markers[markers.size-1].first?.setVisible(true)
                        else
                            markers[markers.size-1].first?.setVisible(false)


                    }


                loafmarkerloopHandler.postDelayed(this, 1000)
            }

        }   )
    }

    fun addnewmarker(addData: ArrayList<String>){

        addnewmarkerHandler.post( object : Runnable {
            override fun run() {

                Log.d("track","loop")

                var new_data : MutableList<Pair<LatLng,Pair<String,String> > > = mutableListOf()

                if(placeData.size == markers.size){


                    for(item in addData){


                        val placeName = item.split('|')[1]

                        //營業時間改成string改中英文
                        val other = item.split('|')[2]+'!'+item.split('|')[0]+"?營業時間"+item.split('|')[6]+":00~"+item.split('|')[7]+":00\n"+item.split('|')[3]
                        val placePosition = LatLng(item.split('|')[5].toDouble(),item.split('|')[4].toDouble())

                        heatMapLatLngs.add(placePosition)

                        new_data.add(Pair(placePosition,Pair(placeName,other)))
                    }

                    new_data.sortBy { Math.abs(currentLocation.latitude-it.first.latitude)*101+Math.abs(currentLocation.longitude-it.first.longitude)*111  }

                    new_data.forEach{
                        placeData.add(it)
                    }

                    if(this@HomeFragment::provider.isInitialized)
                    provider.setData(heatMapLatLngs)
                    else
                    {
                        val colors = intArrayOf(
                            Color.rgb(0, 128, 255),
                            Color.rgb(0, 255, 255),
                            Color.rgb(255, 255, 255)
                        )
                        val startPoints = floatArrayOf(0.01f ,0.03f ,1f)
                        val gradient = Gradient(colors, startPoints,10000)
                        provider=HeatmapTileProvider.Builder()
                            .gradient(gradient)
                            .radius(50)
                            .data(heatMapLatLngs)
                            .build()

                        overlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))!!
                    }





                    overlay?.clearTileCache()
                    loadmarkerloop()
                    Log.d("track","loopend")



                    Log.d("track","markerend")
                }
                else
                    addnewmarkerHandler.postDelayed(this, 1000)
            }
        }   )


    }

    private fun loadFile(){

        //Log.d("file", LocalTime.now().toString())

        val rightNow = Calendar.getInstance()
        val Hour24: Int =(rightNow.get(Calendar.HOUR_OF_DAY)+8)%24
        val Minute24: Int =rightNow.get(Calendar.MINUTE)
        val currenttime = 100*Hour24 + Minute24;
        Log.d("file",currenttime.toString())

        //從sql拿時間內的資料
        db =StoreDatabase(context as MainActivity)
        var data = db.getStoreDao().findByTime(Hour24)
        for (item in data) {

            val placeName = item.name

            //營業時間改成string改中英文
            val other = item.type+"!"+item.uid+"?營業時間"+item.time_start+":00~"+item.time_end+":00\n"+item.information
            val lat = item.lat
            val lng = item.lng
            val placePosition = LatLng(lat,lng)

            heatMapLatLngs.add(placePosition)
            placeData.add(Pair(placePosition,Pair(placeName,other)))

            //val text = item.time_start.toString()+"~"+item.time_end.toString()
            //Log.d("file",item.name+other)
        }

        Log.d("file",placeData.joinToString())

        /*
        loadCsv("gas_station_Lat")
        loadCsv("police_station_Lat")
        loadCsv("convenience_store_Lat")

         */

    }

    fun bottom_change(type:String , isChecked : Boolean){
        placeShow[type]=isChecked
        markers.forEach(){

            if(it.second.first==type&&mMap.cameraPosition.zoom>=15)
                it.first?.setVisible(isChecked)

        }

    }

    fun nextDirect (){
        nowDirect++;
        if(nowDirect==5){
            nowDirect=0
        }
        getLastKnownLocation()
        moveDirectionView()
    }

    fun backDirect () {
        nowDirect--
        if(nowDirect<0){
            nowDirect=4
        }
        getLastKnownLocation()
        moveDirectionView()
    }


    private fun moveDirectionView(){
        val south = Math.min(placeData[nowDirect].first.latitude, currentLocation.latitude)-0.001
        val north = Math.max(placeData[nowDirect].first.latitude, currentLocation.latitude)+0.001
        val west = Math.min(placeData[nowDirect].first.longitude, currentLocation.longitude)
        val east = Math.max(placeData[nowDirect].first.longitude, currentLocation.longitude)
        val australiaBounds = LatLngBounds(
            LatLng(south, west),
            LatLng(north, east))

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 200))
    }



    override fun onStop() {
        super.onStop()
        println("map stop")
    }

    override fun onPause() {
        super.onPause()
        println("map pause")


    }
    override fun onDestroy() {
        super.onDestroy()
        println("map destroy")


        placeData.clear()
        markers.clear()
        heatMapLatLngs.clear()
        mMap.clear()
        googleMapHandler.removeCallbacksAndMessages(null)
        directionHandler.removeCallbacksAndMessages(null)
        enableLocationHandler.removeCallbacksAndMessages(null)
        locationHandler.removeCallbacksAndMessages(null)
        trackHandler.removeCallbacksAndMessages(null)
        updataetrackHandler.removeCallbacksAndMessages(null)
        showtrackHandler.removeCallbacksAndMessages(null)
        reporterrorHandler.removeCallbacksAndMessages(null)
        sendercameraHandler.removeCallbacksAndMessages(null)
        addnewmarkerHandler.removeCallbacksAndMessages(null)
        loafmarkerloopHandler.removeCallbacksAndMessages(null)



//        Runtime.getRuntime().gc();
//        System.gc();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("build","home onDestroyView")

    }

    fun showFragment(){
        activity?.supportFragmentManager?.beginTransaction()?.show(mapFragment)?.commit()
    }

    fun hideFragment(){
        activity?.supportFragmentManager?.beginTransaction()?.hide(mapFragment)?.commit()
    }

    fun currentLocationIsInitialized(): Boolean{
        return ::currentLocation.isInitialized
    }
}