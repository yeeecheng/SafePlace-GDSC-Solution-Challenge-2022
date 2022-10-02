package com.plcoding.instagramui.saveplace.mainActivity



import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.R.string.convenience_store_on
import com.plcoding.instagramui.saveplace.data.contactViewModel.ContactViewModel
import com.plcoding.instagramui.saveplace.data.contactViewModel.ContactViewModelFactor
import com.plcoding.instagramui.saveplace.data.db.ContactDatabase
import com.plcoding.instagramui.saveplace.data.db.StoreDatabase
import com.plcoding.instagramui.saveplace.data.db.entities.StoreItem
import com.plcoding.instagramui.saveplace.data.repository.ContactRepository
import com.plcoding.instagramui.saveplace.fragment.addNewStore.AddNewStoreFragment
import com.plcoding.instagramui.saveplace.fragment.contactList.CallContactItemAdapter
import com.plcoding.instagramui.saveplace.fragment.contactList.ContactFragment
import com.plcoding.instagramui.saveplace.fragment.intro.IntroFragment
import com.plcoding.instagramui.saveplace.fragment.map.HomeFragment
import com.plcoding.instagramui.saveplace.introActivity.IntroActivity
import com.plcoding.instagramui.saveplace.mainActivity.AESCrypt.toHex
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), SensorEventListener  {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout


    private lateinit var fragment_home: HomeFragment
    private var fragment_contact: ContactFragment = ContactFragment()
    private var fragment_intro: IntroFragment = IntroFragment()
    private var fragment_add_new_store: AddNewStoreFragment = AddNewStoreFragment()

    private val GPSHandler = Handler(Looper.getMainLooper())

    private var userPhoneNumber =""
    private var phoneNumberList =listOf<String>()
    private var phoneNumber =""
    private var message:String = "help me! "
    private var userName:String ="user"
    private var hasClickedButton=false
    private var ip="114.33.145.3"
    private var port = 1234

    private lateinit var alertMedia: Alert
    private lateinit var permissionManager : PermissionManager


    private lateinit var database :ContactDatabase
    private lateinit var repository:ContactRepository
    private lateinit var  factor: ContactViewModelFactor
    private lateinit var viewModel: ContactViewModel
    private lateinit var db: StoreDatabase

    private lateinit var audioManager:AudioManager
    private lateinit var bottomSheet:View
    lateinit var fabPhone:FloatingActionButton
    lateinit var fabNavigation:FloatingActionButton
    private lateinit var fabSiren:FloatingActionButton
    private lateinit var fabNext:FloatingActionButton
    private lateinit var fabBack:FloatingActionButton
    private lateinit var fabVictim:FloatingActionButton
    private lateinit var phoneCallMenu:View

    private  var hasOpenMenu=false
    private  var direction:String =""



    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var currentAzimuth = 0f

    private var list_of_otp: ArrayList<String> = ArrayList()
    //connect with server
    private lateinit var myKey: String
    private lateinit var myInitVector: String
    private lateinit var myId: String
    private lateinit var victimKey: String
    private lateinit var victimInitVector: String
    private lateinit var victimId: String

    private lateinit var nowFragment:Fragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("build","onCreate")

        val aes = AESCrypt
        myKey = aes.generateKey()
        Log.d("client", myKey)
        myInitVector = aes.generateIv()
        Log.d("client",myInitVector)


        //setting data base
        database = ContactDatabase(this)
        repository = ContactRepository(database)
        factor = ContactViewModelFactor(repository)
        viewModel = ViewModelProviders.of(this, factor).get(ContactViewModel::class.java)

        db = StoreDatabase(this)

        //define gps class
        var gpsCheck= CheckBackgroundSetting()

        //define file operation
        var fo=FileOperation()


        //GPS thread
        Thread{
            GPSHandler.post(object:Runnable{

                override fun run() {
                    gpsCheck.checkGPS(this@MainActivity)
                    GPSHandler.postDelayed(this, 3000)
                }
            })
        }.start()





        //client Thread


        Thread{
            var currentVersion:String
            var newVersion:String
            var data=""
            currentVersion = fo.readFile(this,"dataVersion.txt")
            Log.d("version",currentVersion)
            //when first open APP ,it will download new data directly
            if(currentVersion==""){

                currentVersion="v0"

                newVersion =getNewVersion(currentVersion)
                Log.d("dd",newVersion.toString())
                data = uploadData(currentVersion)

                var addNum =data.split("\n"," ")
                while(addNum.size==1){

                    data = uploadData(currentVersion)
                    addNum =data.split("\n"," ")
                }

                storeData(data)
                fo.writeFile(this,newVersion,"dataVersion.txt")

            }
            //otherwise
            else {

                newVersion =getNewVersion(currentVersion)
                Log.d("dd",newVersion.toString())

                val mHandler = Handler(Looper.getMainLooper())
                mHandler.postDelayed( {
                    if(newVersion!=currentVersion){
                        AlertDialog.Builder(this)
                            .setTitle(R.string.new_data)
                            .setMessage(R.string.new_data_content)
                            .setPositiveButton(R.string.confirm){ _, _ ->

                                Thread{

                                    data = uploadData(currentVersion)
                                    storeData(data)
                                    fo.writeFile(this,newVersion,"dataVersion.txt")

                                }.start()

                            } .setNegativeButton(R.string.cancel,null)
                            .show()
                    }
                },5000)

            }


        }.start()




        //define siren class
        alertMedia= Alert(this)

        //define permission manager
        permissionManager = PermissionManager()

        //init check
        permissionManager.checkAllPermission(this)
        fragment_home= HomeFragment(permissionManager)
        //check has gone to intro activity
        if(!restrorePreData()){
            permissionManager.getPermission(this)
            val introActivity = Intent(applicationContext, IntroActivity::class.java)
            startActivity(introActivity)
        }


        //define audio manager
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //define compass manager
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager



        //bottom sheet
        bottomSheet= findViewById(R.id.bottom_sheet)
        val mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        fabNavigation= findViewById(R.id.fab_Navigation)
        fabPhone  = findViewById(R.id.fab_Phone)
        fabSiren = findViewById(R.id.fab_Siren)
        fabVictim = findViewById(R.id.fab_victim)
        fabNext= findViewById(R.id.fab_next)
        fabBack = findViewById(R.id.fab_back)
        phoneCallMenu= findViewById(R.id.phone_call_menu)


        //get User's Name and PhoneNumber
        getUserName()
        getUserPhoneNumber()



        //set init button's state
        val navView : NavigationView = findViewById(R.id.nav_view)

        navView.menu.findItem(R.id.nav_home).isChecked=true
        navView.menu.findItem(R.id.nav_help).isChecked=false
        navView.menu.findItem(R.id.nav_contact).isChecked=false

        val convenienceStoreSwitch = navView.menu.findItem(R.id.nav_convenience_store).actionView.findViewById<SwitchCompat>(
            R.id.switch_convenience_store
        )
        val gasStationSwitch = navView.menu.findItem(R.id.nav_gas_station).actionView.findViewById<SwitchCompat>(
            R.id.switch_gas_station
        )
        val policeStationSwitch = navView.menu.findItem(R.id.nav_police_station).actionView.findViewById<SwitchCompat>(
            R.id.switch_police_station
        )
        val fireDepartmentSwitch = navView.menu.findItem(R.id.nav_fire_department).actionView.findViewById<SwitchCompat>(
            R.id.switch_fire_department
        )
        val hospitalSwitch = navView.menu.findItem(R.id.nav_hospital).actionView.findViewById<SwitchCompat>(
            R.id.switch_hospital
        )
        val restaurantSwitch = navView.menu.findItem(R.id.nav_restaurant).actionView.findViewById<SwitchCompat>(
            R.id.switch_restaurant
        )
        val otherSwitch = navView.menu.findItem(R.id.nav_other).actionView.findViewById<SwitchCompat>(
            R.id.switch_other
        )
        val lightModeSwitch = navView.menu.findItem(R.id.nav_light_mode).actionView.findViewById<SwitchCompat>(
            R.id.switch_light
        )

        //go to home fragment

        replaceFragment(fragment_contact,"")
        replaceFragment(fragment_intro,"")
        replaceFragment(fragment_add_new_store,"")
        replaceFragment(fragment_home,"")
        nowFragment = fragment_home


        //setting last used color model
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            changeTheme("DAY")
        }else{
            changeTheme("NIGHT")
        }


        //home navigation listener
        fabNavigation.setOnClickListener {
            if (fabNavigation.isSelected) {

                fabNavigation.rotation = 180.0F
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            } else if (!fabNavigation.isSelected) {

                fabNavigation.rotation = 360.0F
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
            fabNavigation.isSelected = !fabNavigation.isSelected
        }

        //behavior change
        mBottomSheetBehavior.setBottomSheetCallback(  object :BottomSheetBehavior.BottomSheetCallback(){

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){

                    fabNavigation.rotation=180.0F
                    fabNavigation.isSelected=true

                }
                else {

                    fabNavigation.rotation=360.0F
                    fabNavigation.isSelected=false

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        } )

        //victim listener
        fabVictim.setOnClickListener{
            fragment_home.sendercamera()
        }


        //next listener
        fabNext.setOnClickListener {

            fragment_home.nextDirect()

        }

        // back listener
        fabBack.setOnClickListener {

            fragment_home.backDirect()

        }


        //set local storage for nav bar

        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()

        val isConvenienceStoreOn: Boolean = appSettingPrefs.getBoolean("ConvenienceStore", true)
        convenienceStoreSwitch.isChecked = isConvenienceStoreOn
        fragment_home.bottom_change("convenienceStore",convenienceStoreSwitch.isChecked)

        val isGasStationOn: Boolean = appSettingPrefs.getBoolean("GasStation", true)
        gasStationSwitch.isChecked = isGasStationOn
        fragment_home.bottom_change("gasStation",gasStationSwitch.isChecked)

        val isPoliceStationOn: Boolean = appSettingPrefs.getBoolean("PoliceStation", true)
        policeStationSwitch.isChecked = isPoliceStationOn
        fragment_home.bottom_change("policeStation",policeStationSwitch.isChecked)

        val isFireDepartmentOn: Boolean = appSettingPrefs.getBoolean("FireDepartment", true)
        fireDepartmentSwitch.isChecked = isFireDepartmentOn
        fragment_home.bottom_change("fireDepartment",fireDepartmentSwitch.isChecked)

        val isHospitalOn: Boolean = appSettingPrefs.getBoolean("Hospital", true)
        hospitalSwitch.isChecked = isHospitalOn
        fragment_home.bottom_change("hospital",hospitalSwitch.isChecked)

        val isRestaurantOn: Boolean = appSettingPrefs.getBoolean("Restaurant", true)
        restaurantSwitch.isChecked = isRestaurantOn
        fragment_home.bottom_change("restaurant",restaurantSwitch.isChecked)

        val isOtherOn: Boolean = appSettingPrefs.getBoolean("Other", true)
        otherSwitch.isChecked = isOtherOn
        fragment_home.bottom_change("other",otherSwitch.isChecked)


        lightModeSwitch.isChecked = isNightModeOn


        //navigation bar
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        //menu switch

        navView.setNavigationItemSelectedListener {

            when(it.itemId){
                R.id.nav_home ->{
                    title = "Safe Place"
                    nowFragment = fragment_home
                    replaceFragment(fragment_home,"")

                    navView.menu.findItem(R.id.nav_contact).isChecked=false
                    navView.menu.findItem(R.id.nav_home).isChecked=true
                    navView.menu.findItem(R.id.nav_help).isChecked=false
                    navView.menu.findItem(R.id.nav_add_a_store).isChecked=false
                    drawerLayout.closeDrawers()

                    naviButtonVisibility(true)
                    fabBack.visibility = View.VISIBLE

                }
                R.id.nav_add_a_store->{
                    title = "Add a Store"
                    nowFragment = fragment_add_new_store
                    replaceFragment(fragment_add_new_store,"")

                    navView.menu.findItem(R.id.nav_add_a_store).isChecked=true
                    navView.menu.findItem(R.id.nav_contact).isChecked=false
                    navView.menu.findItem(R.id.nav_home).isChecked=false
                    navView.menu.findItem(R.id.nav_help).isChecked=false
                    drawerLayout.closeDrawers()

                    naviButtonVisibility(false)
                    phoneCallMenu.visibility = View.GONE

                }
                R.id.nav_contact -> {
                    title = "Emergency Contact"
                    nowFragment = fragment_contact
                    replaceFragment(fragment_contact,"")

                    navView.menu.findItem(R.id.nav_contact).isChecked=true
                    navView.menu.findItem(R.id.nav_home).isChecked=false
                    navView.menu.findItem(R.id.nav_help).isChecked=false
                    navView.menu.findItem(R.id.nav_add_a_store).isChecked=false
                    drawerLayout.closeDrawers()

                    naviButtonVisibility(false)
                    phoneCallMenu.visibility = View.GONE


                }
                R.id.nav_help ->{
                    title = "Help"
                    nowFragment = fragment_intro
                    replaceFragment(fragment_intro,"")

                    navView.menu.findItem(R.id.nav_contact).isChecked=false
                    navView.menu.findItem(R.id.nav_help).isChecked=true
                    navView.menu.findItem(R.id.nav_home).isChecked=false
                    navView.menu.findItem(R.id.nav_add_a_store).isChecked=false

                    drawerLayout.closeDrawers()

                    naviButtonVisibility(false)
                    phoneCallMenu.visibility = View.GONE

                }
                R.id.nav_convenience_store -> {

                    convenienceStoreSwitch.isChecked = !convenienceStoreSwitch.isChecked

                }
                R.id.nav_gas_station -> {

                    gasStationSwitch.isChecked = !gasStationSwitch.isChecked

                }
                R.id.nav_police_station -> {

                    policeStationSwitch.isChecked = !policeStationSwitch.isChecked

                }
                R.id.nav_fire_department -> {

                    fireDepartmentSwitch.isChecked = !fireDepartmentSwitch.isChecked

                }
                R.id.nav_hospital -> {

                    hospitalSwitch.isChecked = !hospitalSwitch.isChecked

                }
                R.id.nav_restaurant -> {

                    restaurantSwitch.isChecked = !restaurantSwitch.isChecked

                }
                R.id.nav_other -> {

                    otherSwitch.isChecked = !otherSwitch.isChecked

                }
                R.id.nav_light_mode -> {

                    lightModeSwitch.isChecked = !lightModeSwitch.isChecked

                }

            }
            true
        }


        //menu switch fun

        //convenience store switch listener
        convenienceStoreSwitch.setOnCheckedChangeListener { _, _ ->

            fragment_home.bottom_change("convenienceStore", convenienceStoreSwitch.isChecked)

            if (convenienceStoreSwitch.isChecked) {

                Toast.makeText(applicationContext, convenience_store_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("ConvenienceStore", true)
                sharedPrefsEdit.apply()

            } else {

                Toast.makeText(applicationContext, R.string.convenience_store_off, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("ConvenienceStore", false)
                sharedPrefsEdit.apply()

            }
        }

        // gas station switch listener
        gasStationSwitch.setOnCheckedChangeListener { _, _ ->

            fragment_home.bottom_change("gasStation", gasStationSwitch.isChecked)

            if (gasStationSwitch.isChecked) {
                Toast.makeText(applicationContext, R.string.gas_station_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("GasStation", true)
                sharedPrefsEdit.apply()

            } else {

                Toast.makeText(applicationContext, R.string.gas_statio_off, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("GasStation", false)
                sharedPrefsEdit.apply()

            }
        }

        //police station switch listener
        policeStationSwitch.setOnCheckedChangeListener { _, _ ->

            fragment_home.bottom_change("policeStation", policeStationSwitch.isChecked)

            if (policeStationSwitch.isChecked) {

                Toast.makeText(applicationContext, R.string.police_station_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("PoliceStation", true)
                sharedPrefsEdit.apply()
            } else {

                Toast.makeText(applicationContext, R.string.police_station_off, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("PoliceStation", false)
                sharedPrefsEdit.apply()
            }

        }

        //fire department switch listener
        fireDepartmentSwitch.setOnCheckedChangeListener { _, _ ->

            fragment_home.bottom_change("fireDepartment", fireDepartmentSwitch.isChecked)

            if (fireDepartmentSwitch.isChecked) {

                Toast.makeText(applicationContext, R.string.fire_department_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("FireDepartment", true)
                sharedPrefsEdit.apply()
            } else {

                Toast.makeText(applicationContext, R.string.fire_department_off, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("FireDepartment", false)
                sharedPrefsEdit.apply()
            }

        }

        //hospital switch listener
        hospitalSwitch.setOnCheckedChangeListener { _, _ ->

            fragment_home.bottom_change("hospital", hospitalSwitch.isChecked)

            if (hospitalSwitch.isChecked) {

                Toast.makeText(applicationContext, R.string.hospital_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("Hospital", true)
                sharedPrefsEdit.apply()
            } else {

                Toast.makeText(applicationContext, R.string.hospital_off, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("Hospital", false)
                sharedPrefsEdit.apply()
            }

        }

        //restaurant switch listener
        restaurantSwitch.setOnCheckedChangeListener { _, _ ->

            fragment_home.bottom_change("restaurant", restaurantSwitch.isChecked)

            if (restaurantSwitch.isChecked) {

                Toast.makeText(applicationContext, R.string.restaurant_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("Restaurant", true)
                sharedPrefsEdit.apply()
            } else {

                Toast.makeText(applicationContext, R.string.restaurant_off, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("Restaurant", false)
                sharedPrefsEdit.apply()
            }

        }

        //other switch listener
        otherSwitch.setOnCheckedChangeListener { _, _ ->

            fragment_home.bottom_change("other", otherSwitch.isChecked)

            if (otherSwitch.isChecked) {

                Toast.makeText(applicationContext, R.string.other_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("Other", true)
                sharedPrefsEdit.apply()
            } else {

                Toast.makeText(applicationContext, R.string.other_off, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("Other", false)
                sharedPrefsEdit.apply()
            }

        }

        //light mode switch listener
        lightModeSwitch.setOnCheckedChangeListener { _, _ ->


            if (lightModeSwitch.isChecked) {
                changeTheme("DAY")

                replaceFragment(fragment_home,"remove")
                fragment_home = HomeFragment(permissionManager)
                replaceFragment(fragment_home,"")
                replaceFragment(nowFragment,"")

                Toast.makeText(applicationContext, R.string.click_light_mode_on, Toast.LENGTH_SHORT).show()

                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()


            } else {
                changeTheme("NIGHT")

                replaceFragment(fragment_home,"remove")
                fragment_home = HomeFragment(permissionManager)
                replaceFragment(fragment_home,"")
                replaceFragment(nowFragment,"")

                Toast.makeText(applicationContext, R.string.click_light_mode_off, Toast.LENGTH_SHORT).show()
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()
            }

        }

        //sensor

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME)

    }

    override fun onStart() {
        super.onStart()
        Log.d("build","onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.d("build", "onResume")


        val uri = intent.data
        Log.d("url",uri.toString())

        if(uri != null){
            fabVictim.visibility = View.VISIBLE;
            //var otpKeyIv = uri.path?.substring(1).toString()
            var listOtpKeyIv : ArrayList<String> = ArrayList(uri.toString().split('?'))

            listOtpKeyIv[0]=listOtpKeyIv[0].split('/')[3]


//            var listOtpKeyIv: List<String> = otpKeyIv.split("?")
            Log.d("client","list_otp_key_iv: "+listOtpKeyIv)

            Thread {
                while(true) {

                    if (fragment_home.currentLocationIsInitialized()) {

                        var client = clientSocket(ip, port)
                        client.initConnect()
                        client.checkOTPToGetId(listOtpKeyIv[0])
                        victimId = client.receiveMessage()
                        Log.d("message",list_of_otp.toString())
                        Log.d("client", "id: " + victimId)
                        client.closeConnect()
                        if(victimId=="Error"||victimId==null){
                            val mHandler = Handler(Looper.getMainLooper())
                            mHandler.post {
                                fabVictim.visibility = View.GONE;
                                Toast.makeText(this, R.string.url_is_expired, Toast.LENGTH_SHORT).show()
                            }
                            break
                        }

                        victimKey = getDecrptKey(userPhoneNumber, listOtpKeyIv[1])
                        Log.d("client", "key: " + victimKey)

                        victimInitVector = getDecrptIv(userPhoneNumber, listOtpKeyIv[2])
                        Log.d("client", "iv: " + victimInitVector)

                        fragment_home.showtrack(victimKey, victimInitVector, victimId)
                        break
                    }
                }
            }.start()

        }


        fabPhone.setOnClickListener {


            phoneCallMenu.visibility = View.GONE
            hasClickedButton=true
            if(checkHasContact()&&!hasOpenMenu&&permissionManager.checkPermissionAfterClickPhoneButton(this,this)){
                Log.d("phone","can calling ")

                fabPhone.isSelected=true
                phoneNumber=phoneNumberList[0]


                Thread {
                    getIdAndOTP()

                    var pass_key_iv =
                        passKeyAndIvToEmergency(phoneNumber, myKey, myInitVector)
                    onClickSOSButton(list_of_otp[1], pass_key_iv)
                }.start()

            }else{
                val mHandler = Handler(Looper.getMainLooper())
                mHandler.post {
                    Toast.makeText(this, R.string.SOS_fail, Toast.LENGTH_SHORT).show()
                }

            }

            fabPhone.isSelected=false
            hasOpenMenu=false


        }

        fabPhone.setOnLongClickListener {

            val lightModeSwitch =findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)

            val adapter = CallContactItemAdapter(lightModeSwitch,listOf(), viewModel)
            val rvCallContactItems: RecyclerView = findViewById(R.id.rv_call_contactItems)

            phoneCallMenu.visibility = View.VISIBLE
            fabPhone.isSelected=true
            hasOpenMenu = true


            rvCallContactItems.layoutManager = LinearLayoutManager(this)
            rvCallContactItems.adapter = adapter



            if(checkHasContact()&&permissionManager.checkPermissionAfterClickPhoneButton(this,this)){
                adapter.setOnItemClickListener(object : CallContactItemAdapter.onItemClickListener {

                    override fun onItemClick(PhoneNumber: String) {
                        Log.d("phone",PhoneNumber)
                        phoneNumber=PhoneNumber

                        Thread {

                            getIdAndOTP()

                            var pass_key_iv =
                                passKeyAndIvToEmergency(phoneNumber, myKey, myInitVector)

                            onClickSOSButton(
                                list_of_otp[phoneNumberList.indexOf(phoneNumber) + 1],
                                pass_key_iv
                            )
                        }.start()

                        hasOpenMenu=false
                        fabPhone.isSelected=false


                    }

                })

                viewModel.getAllContactItems().observe(this, androidx.lifecycle.Observer{
                    adapter.items = it
                    adapter.notifyDataSetChanged()//刷新資料內容

                })
            }else{
                val mHandler = Handler(Looper.getMainLooper())
                mHandler.post {
                    Toast.makeText(this, R.string.SOS_fail, Toast.LENGTH_SHORT).show()
                }
                fabPhone.isSelected=false

            }

            true

        }
        phoneCallMenu.visibility = View.GONE


        //siren event
        val mediaClick : FloatingActionButton = findViewById(R.id.fab_Siren)

        mediaClick.setOnClickListener{

            if(!alertMedia.isMediaPlaying()) {

                AlertDialog.Builder(this)
                    .setMessage(R.string.diologSiren)
                    .setPositiveButton(R.string.confirm){ _, _ ->

                        val maxVal = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,maxVal,AudioManager.FLAG_PLAY_SOUND)
                        mediaClick.isSelected=true
                        alertMedia.startMedia()

                    }
                    .setNegativeButton(R.string.cancel){ _, _->}.show()

            }
            else{

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,4,AudioManager.FLAG_PLAY_SOUND)
                mediaClick.isSelected=false
                alertMedia.stopMedia()

            }
        }
    }

    //store upload data and insert to the database
    private fun storeData(data:String){

        var addData:ArrayList<String> = ArrayList()
        var delData:ArrayList<String> = ArrayList()
        var modData:ArrayList<String> = ArrayList()

        var dataList =data.split("\n")

        //Log.d("dataList",dataList.toString())

        var item:StoreItem
        var dataContent:List<String>
        var nowTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var index=1
        var addNum = dataList[0].split(" ")[1].toInt()
        while(index <= addNum){
            addData.add(dataList[index])
            dataContent = dataList[index].split("|")
            //Log.d("dddd",dataContent.toString())
            item =StoreItem(dataContent[0].toInt(),dataContent[1],dataContent[2],dataContent[3],dataContent[4].toDouble(),dataContent[5].toDouble(),dataContent[6].toInt(),dataContent[7].toInt())
            var st = dataContent[6].toInt()
            var et = dataContent[7].toInt()
            if(st<=et){
                if(nowTime in st..et){
                    addData.add(dataList[index])
                }
            }
            else {
                if(nowTime>=st||nowTime<=et){
                    addData.add(dataList[index])
                }
            }
            db.getStoreDao().insert(item)
            index++
        }

        fragment_home.addnewmarker(addData)
        var delNum = dataList[index].split(" ")[1].toInt()+index

        index++

        while(index <= delNum){
            delData.add(dataList[index])
            db.getStoreDao().deleteByUid(dataList[index].toInt())
            index++
        }

        var modNum = dataList[index].split(" ")[1].toInt()+index
        index++

        while(index <= modNum){
            modData.add(dataList[index])
            dataContent = dataList[index].split("|")
            item =StoreItem(dataContent[0].toInt(),dataContent[1],dataContent[2],dataContent[3],dataContent[4].toDouble(),dataContent[5].toDouble(),dataContent[6].toInt(),dataContent[7].toInt())
            db.getStoreDao().insert(item)
            index++
        }




    }

    //check store data version
    private fun getNewVersion(version:String):String{
        var client = clientSocket(ip,port)
        client.initConnect()
        client.checkDataVersion(version)
        var data = client.receiveMessage()
        client.closeConnect()
        var str =data.split(" ")

        //check return is correct
        if(str.size==1){
            return getNewVersion(version)
        }
        return str[0]
    }

    //upload new data from server
    private fun uploadData(version: String):String{
        var client = clientSocket(ip,port)
        client.initConnect()
        client.uploadDataVersion(version)
        var data = client.receiveMessage()
        client.closeConnect()
        return data
    }

    private fun restrorePreData(): Boolean {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpened", false)
    }


    //get id and otp from server
    private fun getIdAndOTP() {

        var startTime: Long = 0
        var endTime: Long = System.nanoTime()

        var time_gap: Double = ((endTime - startTime) / 1000000000).toDouble()

        if (time_gap >= 1800)
            list_of_otp.clear()



        if (fragment_home.currentLocationIsInitialized()&&list_of_otp.isEmpty())
        {
            var client = clientSocket(ip, port)
            client.initConnect()
            client.sendRequestOfOtp()
            list_of_otp = ArrayList(client.receiveMessage().split(" "))
            client.closeConnect()
            startTime = System.nanoTime()

            Log.d("client", "list_of_otp: " + list_of_otp)

            var pass_key_iv =
                passKeyAndIvToEmergency(phoneNumber, myKey, myInitVector)
            Log.d("client", "pass_key_iv: " + pass_key_iv)

            client.initConnect()
            myId = list_of_otp[0]
            Log.d("client", "myid: " + myId)
            client.closeConnect()

            fragment_home.updataetrack(myKey, myInitVector, myId)

            hasClickedButton = false
        }

    }

    //set button
    private fun naviButtonVisibility(show:Boolean){

        when(show){
            true->{
                fabNavigation.visibility = View.VISIBLE
                fabPhone.visibility = View.VISIBLE
                fabSiren.visibility = View.VISIBLE
                bottomSheet.visibility = View.VISIBLE
                fabNext.visibility = View.VISIBLE
                fabBack.visibility = View.VISIBLE
            }
            false->{
                fabNavigation.visibility = View.GONE
                fabPhone.visibility = View.GONE
                fabSiren.visibility = View.GONE
                bottomSheet.visibility = View.GONE
                fabNext.visibility = View.GONE
                fabBack.visibility = View.GONE
            }
        }
    }

    //get location , phone call and  send message
    private fun getUserPhoneNumber(){
        val mTelManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        userPhoneNumber = mTelManager.line1Number

        userPhoneNumber="0"+userPhoneNumber.substring(4)
        Log.d("phone",userPhoneNumber)

    }


    private fun onClickSOSButton(otp :String, pass_key_iv:String) {
        Log.d("sender message","你可以藉由點開這個網址，打開Safe Place看到他及時的位置\n" +" https://sunny1928.github.io/safeplace/deeplink/index.html?id=$otp?$pass_key_iv")
        Log.d("otp",otp+" "+pass_key_iv)
        getCurrentLocation(otp, pass_key_iv)
        sendMessage(phoneNumber, message)
        startCall()
    }

    //request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("per","main request")
        when(requestCode){

            permissionManager.getREQUEST_MUTILPLE_CODE()->{

                if(grantResults.isNotEmpty() ) {

                    val arraySize=grantResults.size-1

                    for(index in 0..arraySize) {

                        when(permissions[index]){

                            Manifest.permission.CALL_PHONE->{
                                if (grantResults[index] == PackageManager.PERMISSION_GRANTED && !permissionManager.getPhoneCallPermissionGranted()) {

                                    permissionManager.setPhoneCallPermissionGranted(true)
                                    //Toast.makeText(this, R.string.phone_call_permission, Toast.LENGTH_SHORT).show()

                                } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {


                                    //Toast.makeText(this, R.string.phone_call_permission_off, Toast.LENGTH_SHORT).show()
                                    AlertDialog.Builder(this)
                                        .setTitle(R.string.phone_call_permission_title)
                                        .setMessage(R.string.phone_call_permission_msg)
                                        .setPositiveButton(R.string.confirm) { _, _ -> openPermissionSettings() }
                                        .setNegativeButton(R.string.cancel) { _, _ -> permissionManager.requestPhoneCallPermission(this,this) }.show()


                                }
                            }

                            Manifest.permission.SEND_SMS->{
                                if (grantResults[index] == PackageManager.PERMISSION_GRANTED && !permissionManager.getSendMessagePermissionGrated()) {

                                    permissionManager.setSendMessagePermissionGrated(true)
                                    //Toast.makeText(this, R.string.send_message_permission, Toast.LENGTH_SHORT).show()

                                } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                                    //Toast.makeText(this, R.string.send_message_permission_off, Toast.LENGTH_SHORT).show()
                                    AlertDialog.Builder(this)
                                        .setTitle(R.string.send_message_permission_title)
                                        .setMessage(R.string.send_message_permission_msg)
                                        .setPositiveButton(R.string.confirm) { _, _ -> openPermissionSettings() }
                                        .setNegativeButton(R.string.cancel) { _, _ -> permissionManager.requestSendMessagePermission(this,this) }.show()

                                }
                            }
//
                            Manifest.permission.ACCESS_FINE_LOCATION->{
                                if (grantResults[index] == PackageManager.PERMISSION_GRANTED && !permissionManager.getLocationPermissionGrated()) {
                                    permissionManager.setLocationPermissionGrated(true)
                                    //Toast.makeText(this, R.string.location_permission, Toast.LENGTH_SHORT).show()

                                } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {

                                    //Toast.makeText(this, R.string.location_permission_off, Toast.LENGTH_SHORT).show()
                                    AlertDialog.Builder(this)
                                        .setTitle(R.string.location_permission_title)
                                        .setMessage(R.string.location_permission_msg)
                                        .setPositiveButton(R.string.confirm) { _, _ -> openPermissionSettings() }
                                        .setNegativeButton(R.string.cancel) { _, _ -> permissionManager.requestLocationPermission(this,this) }.show()

                                }
                            }

//
//                            Manifest.permission.READ_PHONE_STATE->{
//                                if (grantResults[index] == PackageManager.PERMISSION_GRANTED && !permissionManager.getReadPhoneStatePermissionGrated()) {
//
//                                    permissionManager.setReadPhoneStatePermissionGrated(true)
//                                    //Toast.makeText(this, R.string.read_phone_state_permission, Toast.LENGTH_SHORT).show()
//
//                                } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
//
//                                    //Toast.makeText(this, R.string.read_phone_state_permission_off, Toast.LENGTH_SHORT).show()
//                                    AlertDialog.Builder(this)
//                                        .setTitle("開啟狀態權限")
//                                        .setMessage("此APP，狀態權限已被關閉，需開啟才能使用")
//                                        .setPositiveButton("確定") { _, _ -> openPermissionSettings() }
//                                        .setNegativeButton("取消") { _, _ -> permissionManager.requestReadPhoneStatePermission(this,this) }.show()
//
//
//                                }
//                            }


                            Manifest.permission.READ_PHONE_NUMBERS->{
                                if (grantResults[index] == PackageManager.PERMISSION_GRANTED && !permissionManager.getReadPhoneNumberPermissionGrated()) {

                                    permissionManager.setReadPhoneNumberPermissionGrated(true)
                                    Toast.makeText(this, R.string.read_phone_number_permission, Toast.LENGTH_SHORT).show()

                                } else if (grantResults[index] == PackageManager.PERMISSION_DENIED) {

                                    //Toast.makeText(this, R.string.read_phone_state_permission_off, Toast.LENGTH_SHORT).show()
                                    AlertDialog.Builder(this)
                                        .setTitle(R.string.read_phone_number_permission_title)
                                        .setMessage(R.string.read_phone_number_permission_msg)
                                        .setPositiveButton(R.string.confirm) { _, _ -> openPermissionSettings() }
                                        .setNegativeButton(R.string.cancel) { _, _ -> permissionManager.requestReadPhoneNumberPermission(this,this) }.show()


                                }
                            }
                        }
                    }
//
                    var allGrantResult =true
                    for(i in grantResults.indices){
                        if(grantResults[i]!=0){
                            allGrantResult=false
                            break
                        }

                    }
                    if (hasClickedButton && allGrantResult) {

                        startCall()

                    }

                }

            }
        }
    }


    //go to setting
    private fun openPermissionSettings() {

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    //phoneCall
    private fun startCall() {

        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")

        startActivity(callIntent)

    }

    //Send msg
    private fun sendMessage(phoneNumber:String, message :String){
       val smsManager = SmsManager.getDefault()
        val messagelist: ArrayList<String> = smsManager.divideMessage(message)
        smsManager.sendMultipartTextMessage(phoneNumber,null,messagelist,null,null)

    }

    //get location
    private fun getCurrentLocation(otp: String, pass_key_iv: String)  {

        //now getting address from latitude and longitude
        val geocoder = Geocoder(this@MainActivity, Locale.getDefault())

        //Log.d("location",fragment_home.currentLocation.latitude.toString()+" "+fragment_home.currentLocation.longitude)

        val addresses = geocoder.getFromLocation(fragment_home.currentLocation.latitude,fragment_home.currentLocation.longitude,1)
        val address:String = addresses[0].getAddressLine(0)


        message = if(Locale.getDefault().language.toString()=="zh"){
            "你的朋友${userName}現在有危險!他的位置在: ${address}，請盡快聯繫或尋找他!你可以藉由點開這個網址，打開Safe Place看到他及時的位置, https://sunny1928.github.io/safeplace/deeplink/index.html?id=$otp?$pass_key_iv"
        } else{
            "Your friend $userName in danger! he's at $address .You can click this url to open Safe Place to see his instant location  https://sunny1928.github.io/safeplace/deeplink/index.html?id=$otp?$pass_key_iv"
        }
        Log.d("sender", message)

    }




    //send key inivector to emergency // phone_16byte: **********000000
    private fun passKeyAndIvToEmergency(phone: String, key: String, initVector: String): String{ //key inivector
        val aes = AESCrypt
        var phone_16byte = (phone + "000000").toByteArray().toHex()
        return aes.encrypt(phone_16byte, phone_16byte, key).toString()+"?"+aes.encrypt(phone_16byte, phone_16byte, initVector).toString()
    }


    //get decrpt_key, decrpt_iv
    private fun getDecrptKey(phone: String, encrptKey: String): String{
        val aes = AESCrypt
        var phone_16byte = (phone + "000000").toByteArray().toHex()
        return aes.decrypt(phone_16byte, phone_16byte, encrptKey).toString()
    }

    private fun getDecrptIv(phone: String, encrptIv: String): String{
        val aes = AESCrypt
        var phone_16byte = (phone + "000000").toByteArray().toHex()
        return aes.decrypt(phone_16byte, phone_16byte, encrptIv).toString()
    }


    //get the user name
    private fun getUserName(){
        userName=viewModel.getItemNameById(0)
    }


    //check contact has exist
    private fun checkHasContact():Boolean{

        phoneNumberList = viewModel.getContactPhoneNumber()

        return if(phoneNumberList.isNotEmpty()){
            true
        } else {
            Toast.makeText(this, R.string.donot_set_contact,Toast.LENGTH_SHORT).show()
            phoneCallMenu.visibility = View.GONE
            false
        }
    }


    //show information of navigation
    fun printInstruct(msg:String, time:String, distance:String){
        Log.d("navi","!")
        val delimiter1 ="<b>"
        val delimiter2="</b>"
        val splitEnd = msg.split(delimiter1,delimiter2)
        var naviMsg=""

        splitEnd.forEach(){
            naviMsg+=it
        }

        Log.d("navi","$naviMsg $time $distance")


        //msg.
        direction=splitEnd[1]


        findViewById<TextView>(R.id.tvInstruct).text = naviMsg
        findViewById<TextView>(R.id.tvTime).text = resources.getString(R.string.time)+": "+time
        findViewById<TextView>(R.id.tvDistance).text = resources.getString(R.string.distance)+": "+distance
    }


    //change the fragment
    private fun replaceFragment(fragment: Fragment,instruct :String) {

        supportFragmentManager.beginTransaction().apply {
            when(instruct){
                "remove"->{
                    remove(fragment)
                }

                "" ->{
                    if (!fragment.isAdded) {

                        add(R.id.frameLayout, fragment)
                    }

                    when (fragment) {
                        fragment_home -> {

                            show(fragment_home)
                            hide(fragment_add_new_store)
                            hide(fragment_contact)
                            hide(fragment_intro)
                            fragment_home.showFragment()

                        }
                        fragment_contact -> {

                            hide(fragment_add_new_store)
                            hide(fragment_home)
                            hide(fragment_intro)
                            fragment_home.hideFragment()
                            show(fragment_contact)
                        }
                        fragment_intro -> {

                            hide(fragment_home)
                            hide(fragment_contact)
                            hide(fragment_add_new_store)
                            fragment_home.hideFragment()
                            show(fragment_intro)
                        }
                        fragment_add_new_store->{

                            hide(fragment_home)
                            hide(fragment_contact)
                            hide(fragment_intro)
                            fragment_home.hideFragment()
                            show(fragment_add_new_store)
                        }
                    }
                }
            }




        }.commit()

    }



    //menu toggle
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){

            return true
        }

        return super.onOptionsItemSelected(item)

    }


    //nothing
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    //listen ths sensor change
    override fun onSensorChanged(event: SensorEvent) {
        val alpha = 0.97f
        synchronized(this) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
            }
            val R = FloatArray(9)
            val L = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, L, mGravity, mGeomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360
                currentAzimuth = azimuth
                //Log.d("HHHH", azimuth.toString())
                //Log.d("HHHH", currentAzimuth.toString())


                var angle = 0;
                if(direction!=""){

                    when(direction){
                        "北","north" -> angle=0
                        "東北","northeast"-> angle=45
                        "東","east"-> angle=90
                        "東南","southeast"-> angle=135
                        "南","south"-> angle=180
                        "西南","southwest"-> angle=225
                        "西","west"-> angle=270
                        "西北","northwest"-> angle=315
                    }
                    Log.d("navi", "$angle　$direction")
                    var angle_dif:Float
                    angle_dif = if((currentAzimuth-angle)<0){
                        currentAzimuth+360-angle
                    } else{
                        currentAzimuth-angle
                    }

                    val instruct :ImageView=findViewById(com.plcoding.instagramui.saveplace.R.id.ivInstruct)
                    instruct.rotation= (angle-currentAzimuth)


                }

            }
        }
    }


    //setting phone back button func
    override fun onBackPressed() {
        //super.onBackPressed()

        replaceFragment(fragment_home,"")
        title = "Safe Place"
        naviButtonVisibility(true)
    }

    //change light or dark model
    private fun changeTheme(theme: String){



        val colorStateListIcon = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        val colorStateListText = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
        val hot_point: MenuItem =  findViewById<NavigationView>(R.id.nav_view).menu.findItem(R.id.hot_point)
        val setting: MenuItem =  findViewById<NavigationView>(R.id.nav_view).menu.findItem(R.id.setting)
        val sh = SpannableString(hot_point.title)
        val ss = SpannableString(setting.title)

        when(theme){
            "NIGHT"->{

                supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.grey1)))
                findViewById<DrawerLayout>(R.id.drawerLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
                findViewById<LinearLayout>(R.id.phone_call_menu).setBackgroundResource(R.drawable.shape_call_contact_item_night)
                findViewById<NavigationView>(R.id.nav_view).setBackgroundColor(ContextCompat.getColor(this, R.color.grey2))
                findViewById<NavigationView>(R.id.nav_view).itemTextColor = colorStateListIcon
                findViewById<NavigationView>(R.id.nav_view).itemIconTintList = colorStateListIcon

                sh.setSpan(TextAppearanceSpan(this, R.style.TextAppearanceNight), 0, sh.length, 0)
                hot_point.title = sh
                ss.setSpan(TextAppearanceSpan(this, R.style.TextAppearanceNight), 0, ss.length, 0)
                setting.title = ss


                findViewById<EditText>(R.id.et_store_address)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                findViewById<EditText>(R.id.et_store_information)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                findViewById<EditText>(R.id.et_store_name)?.setTextColor(ContextCompat.getColor(this, R.color.white))


                //contact fragment

                if(findViewById<RecyclerView>(R.id.rv_ContactItems)!=null){
                    for(i in 0 until findViewById<RecyclerView>(R.id.rv_ContactItems).childCount){

                        findViewById<RecyclerView>(R.id.rv_ContactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Name)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                        findViewById<RecyclerView>(R.id.rv_ContactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Phone)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                        findViewById<RecyclerView>(R.id.rv_ContactItems).getChildAt(i).findViewById<ImageView>(R.id.iv_Delete)?.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    }
                }
                if(findViewById<RecyclerView>(R.id.rv_call_contactItems)!=null){
                    for(i in 0 until findViewById<RecyclerView>(R.id.rv_call_contactItems).childCount){

                        findViewById<RecyclerView>(R.id.rv_call_contactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Name)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                        findViewById<RecyclerView>(R.id.rv_call_contactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Phone)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                        findViewById<RecyclerView>(R.id.rv_call_contactItems).getChildAt(i).findViewById<ImageView>(R.id.iv_Delete)?.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    }
                }

                if(findViewById<ViewPager>(R.id.vp_intro)!=null){

                    for(i in 0 until findViewById<ViewPager>(R.id.vp_intro).childCount){
                        findViewById<ViewPager>(R.id.vp_intro).getChildAt(i).findViewById<TextView>(R.id.intro_title)?.setTextColor(ContextCompat.getColor(this, R.color.white))
                        findViewById<ViewPager>(R.id.vp_intro).getChildAt(i).findViewById<TextView>(R.id.intro_description)?.setTextColor(ContextCompat.getColor(this, R.color.white))

                    }
                }

            }
            "DAY"->{

                supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.topic)))
                findViewById<DrawerLayout>(R.id.drawerLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                findViewById<LinearLayout>(R.id.phone_call_menu).setBackgroundResource(R.drawable.shape_call_contact_item_day)
                findViewById<NavigationView>(R.id.nav_view).setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                findViewById<NavigationView>(R.id.nav_view).itemTextColor = colorStateListText
                findViewById<NavigationView>(R.id.nav_view).itemIconTintList = colorStateListText
                sh.setSpan(TextAppearanceSpan(this, R.style.TextAppearanceDay), 0, sh.length, 0)
                hot_point.title = sh
                ss.setSpan(TextAppearanceSpan(this, R.style.TextAppearanceDay), 0, ss.length, 0)
                setting.title = ss

                findViewById<EditText>(R.id.et_store_address)?.setTextColor(ContextCompat.getColor(this, R.color.black))
                findViewById<EditText>(R.id.et_store_information)?.setTextColor(ContextCompat.getColor(this, R.color.black))
                findViewById<EditText>(R.id.et_store_name)?.setTextColor(ContextCompat.getColor(this, R.color.black))


                //contact fragment

                if(findViewById<RecyclerView>(R.id.rv_ContactItems)!=null){
                    for(i in 0 until findViewById<RecyclerView>(R.id.rv_ContactItems).childCount){
                        findViewById<RecyclerView>(R.id.rv_ContactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Name)?.setTextColor(ContextCompat.getColor(this, R.color.black))
                        findViewById<RecyclerView>(R.id.rv_ContactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Phone)?.setTextColor(ContextCompat.getColor(this, R.color.black))
                        findViewById<RecyclerView>(R.id.rv_ContactItems).getChildAt(i).findViewById<ImageView>(R.id.iv_Delete)?.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    }
                }

                if(findViewById<RecyclerView>(R.id.rv_call_contactItems)!=null){
                    for(i in 0 until findViewById<RecyclerView>(R.id.rv_call_contactItems).childCount){
                        findViewById<RecyclerView>(R.id.rv_call_contactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Name)?.setTextColor(ContextCompat.getColor(this, R.color.black))
                        findViewById<RecyclerView>(R.id.rv_call_contactItems).getChildAt(i).findViewById<TextView>(R.id.tv_Phone)?.setTextColor(ContextCompat.getColor(this, R.color.black))
                        findViewById<RecyclerView>(R.id.rv_call_contactItems).getChildAt(i).findViewById<ImageView>(R.id.iv_Delete)?.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    }
                }


                if(findViewById<ViewPager>(R.id.vp_intro)!=null){

                    for(i in 0 until findViewById<ViewPager>(R.id.vp_intro).childCount){
                        findViewById<ViewPager>(R.id.vp_intro).getChildAt(i).findViewById<TextView>(R.id.intro_title)?.setTextColor(ContextCompat.getColor(this, R.color.black))
                        findViewById<ViewPager>(R.id.vp_intro).getChildAt(i).findViewById<TextView>(R.id.intro_description)?.setTextColor(ContextCompat.getColor(this, R.color.black))

                    }
                }




            }
        }
    }

    override fun onPause() {
        super.onPause()
        println("onPause")

    }

    override fun onStop() {
        super.onStop()
        Log.d("build","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("build","onDestroy")
        GPSHandler.removeCallbacksAndMessages(null)


    }



}







