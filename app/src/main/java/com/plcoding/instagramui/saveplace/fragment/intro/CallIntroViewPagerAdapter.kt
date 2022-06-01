package com.plcoding.instagramui.saveplace.fragment.intro


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.viewpager.widget.PagerAdapter
import com.plcoding.instagramui.saveplace.R
import com.plcoding.instagramui.saveplace.introActivity.ScreenItem

class CallIntroViewPagerAdapter(
    Switch: SwitchCompat?,
    mContext: Context,
    mListScreen: List<ScreenItem>
): PagerAdapter() {

    private var mContext: Context = mContext
    private var mListScreen: List<ScreenItem> = mListScreen
    var mode: Boolean = true
    private var switch = Switch
    private lateinit var mCurrentView:View

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val introScreen: View = inflater.inflate(R.layout.call_intro_screen, null)

        val imgSlide: ImageView = introScreen.findViewById(R.id.intro_img)
        val title = introScreen.findViewById<TextView>(R.id.intro_title)
        val description = introScreen.findViewById<TextView>(R.id.intro_description)
        title.text = mListScreen[position].Title
        description.text = mListScreen[position].Description
        imgSlide.setImageResource(mListScreen[position].ScreenImg)

        mode=switch!!.isChecked==true

        //判斷模式製造item
        if(mode) {
            title?.setTextColor(Color.parseColor("#000000"))
            description?.setTextColor(Color.parseColor("#000000"))
        }else{
            title?.setTextColor(Color.parseColor("#ffffff"))
            description?.setTextColor(Color.parseColor("#ffffff"))
        }

        container.addView(introScreen)
        return introScreen
    }

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun setPrimaryItem(container: View, position: Int, `object`: Any) {
        mCurrentView = `object` as View
    }

    fun getPrimaryItem(): View? {
        return mCurrentView
    }

}