package com.plcoding.instagramui.saveplace.fragment.intro

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.introActivity.ScreenItem


class IntroFragment: Fragment() {
    private lateinit var screenPager: ViewPager
    private lateinit var callIntroViewPagerAdapter: CallIntroViewPagerAdapter
    private lateinit var tab: TabLayout
    private lateinit var btnNext: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("OnCreate")
        return inflater.inflate(R.layout.intro_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("OnCreateed")

        super.onViewCreated(view, savedInstanceState)
        btnNext = view.findViewById(R.id.btn_next)
        tab = view.findViewById(R.id.tab)


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
                resources.getString( R.string.Siren_title),
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
                resources.getString( R.string.Night_mode_or_Light_mode_title),
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
        var Switch =activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)
        screenPager = view.findViewById(R.id.vp_intro)
        callIntroViewPagerAdapter = CallIntroViewPagerAdapter(Switch,requireContext(), mList)
        screenPager.adapter = callIntroViewPagerAdapter




        //tab with viewpager
        tab.setupWithViewPager(screenPager)

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

                callIntroViewPagerAdapter.mode = activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)?.isChecked == true
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

                callIntroViewPagerAdapter.mode = activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)?.isChecked == true
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

                callIntroViewPagerAdapter.mode = activity?.findViewById<NavigationView>(R.id.nav_view)?.menu?.findItem(R.id.nav_light_mode)?.actionView?.findViewById<SwitchCompat>(R.id.switch_light)?.isChecked == true
            }

        })



        var position :Int



        btnNext.setOnClickListener {


            position = (screenPager.currentItem+1)
            if (position < mList.size) {
                screenPager.currentItem = position

            }
            else if (position == mList.size ) {
                position = 0
                screenPager.currentItem = position

            }



        }





    }


}