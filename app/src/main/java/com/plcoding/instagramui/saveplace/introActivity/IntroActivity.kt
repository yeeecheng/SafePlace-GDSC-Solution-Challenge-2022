package com.plcoding.instagramui.saveplace.introActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.data.contactViewModel.ContactViewModel
import com.plcoding.instagramui.saveplace.data.contactViewModel.ContactViewModelFactor
import com.plcoding.instagramui.saveplace.data.db.ContactDatabase
import com.plcoding.instagramui.saveplace.data.db.entities.ContactItem
import com.plcoding.instagramui.saveplace.data.repository.ContactRepository
import com.plcoding.instagramui.saveplace.mainActivity.MainActivity
import com.plcoding.instagramui.saveplace.mainActivity.clientSocket


class IntroActivity: AppCompatActivity() {
    private lateinit var screenPager: ViewPager
    private lateinit var introViewPagerAdapter: IntroViewPagerAdapter
    private lateinit var tab: TabLayout
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button
    private lateinit var tvSkip: TextView
    private lateinit var etUserName: EditText
    private lateinit var userNameIntro :TextView
    private lateinit var tvUserTitle: TextView
    private lateinit var database : ContactDatabase
    private lateinit var repository: ContactRepository
    private lateinit var  factor: ContactViewModelFactor
    private lateinit var viewModel: ContactViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("build","!onCreate")
        database = ContactDatabase(this)
        repository = ContactRepository(database)
        factor = ContactViewModelFactor(repository)


        viewModel = ViewModelProviders.of(this, factor).get(ContactViewModel::class.java)
        //make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);




        setContentView(R.layout.activity_intro)

        //hide the actionbar
        supportActionBar!!.hide()

        //init
        tvUserTitle = findViewById(R.id.tv_user_title)
        etUserName = findViewById(R.id.et_user_name)
        userNameIntro =findViewById(R.id.user_name_intro)
        tvSkip = findViewById(R.id.tv_skip)
        btnGetStarted = findViewById(R.id.btn_get_started)
        btnNext = findViewById(R.id.btn_next)
        tab = findViewById(R.id.tab)


        //fill list screen
        val mList: MutableList<ScreenItem> = ArrayList()
        mList.add(
            ScreenItem(
                "SafePlace",
                resources.getString(R.string.safeplace1),
                R.drawable.icon
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.Navigation_title),
                resources.getString(R.string.Navigation_descri1),
                R.drawable.start1
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.Emergency_Call_title),
                resources.getString(R.string.Emergency_Call_descri1),
                R.drawable.start2
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.Siren_title),
                resources.getString(R.string.Siren_descri),
                R.drawable.start3
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.Next_Or_Back_title),
                resources.getString(R.string.Next_Or_Back_descri),
                R.drawable.start4
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.Emergency_Contact_title),
                resources.getString(R.string.Emergency_Contact_descri),
                R.drawable.emergency_contact
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.Hot_Point_title),
                resources.getString(R.string.Hot_Point_descri1),
                R.drawable.hot_pot
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.Night_mode_or_Light_mode_title),
                resources.getString(R.string.Night_mode_or_Light_mode_descri),
                R.drawable.light
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.add_a_new_store),
                resources.getString(R.string.add_a_new_store_descri),
                R.drawable.add_new_store
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.report_wrong_store),
                resources.getString(R.string.report_wrong_store_descri),
                R.drawable.start5
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.update_data_from_server),
                resources.getString(R.string.update_data_from_server_descri),
                R.drawable.start6
            )
        )
        mList.add(
            ScreenItem(
                resources.getString(R.string.show_people_in_denger),
                resources.getString(R.string.show_people_in_denger_descri),
                R.drawable.start7
            )
        )

        //setup view pager
        screenPager = findViewById(R.id.vp_intro)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager.adapter = introViewPagerAdapter


        //tab with viewpager
        tab.setupWithViewPager(screenPager)

        //user info


        //btn_next page
        var position :Int

        btnNext.setOnClickListener {

            position = (screenPager.currentItem+1)
            if (position < mList.size) {

                screenPager.currentItem = position

            }
            else if (position == mList.size ) {
                tvSkip.visibility=View.GONE
                loadLastScreen()
            }

            //mode

        }
        btnGetStarted.setOnClickListener {


            val name = etUserName.text.toString()

            if(name.isEmpty() ){
                Toast.makeText(this, R.string.enter_all_info, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val item = ContactItem(0,name,"")
            viewModel.upsert(item)

//            val mainActivity = Intent(applicationContext, MainActivity::class.java)
//            startActivity(mainActivity)
            savePrefData()
            finish()

        }
        tvSkip.setOnClickListener {
         
            loadLastScreen()
            tvSkip.visibility=View.GONE
        }


    }


    private fun savePrefData(){
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.commit()
    }

    private fun loadLastScreen(){
        tvUserTitle.visibility = View.VISIBLE
        etUserName.visibility = View.VISIBLE
        userNameIntro.visibility=View.VISIBLE
        btnGetStarted.visibility = View.VISIBLE
        btnNext.visibility = View.INVISIBLE
        tab.visibility = View.INVISIBLE
        screenPager.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("build","!onDestroy")


    }

}