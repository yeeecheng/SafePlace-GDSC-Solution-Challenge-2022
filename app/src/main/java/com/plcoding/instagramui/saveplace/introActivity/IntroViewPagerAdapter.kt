package com.plcoding.instagramui.saveplace.introActivity


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.plcoding.instagramui.saveplace.R

class IntroViewPagerAdapter(mContext: Context, mListScreen: List<ScreenItem>): PagerAdapter() {

    private var mContext: Context = mContext
    private var mListScreen: List<ScreenItem> = mListScreen

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val introScreen: View = inflater.inflate(R.layout.intro_screen, null)

        val imgSlide: ImageView = introScreen.findViewById(R.id.intro_img)
        val title = introScreen.findViewById<TextView>(R.id.intro_title)
        val description = introScreen.findViewById<TextView>(R.id.intro_description)
        title.text = mListScreen[position].Title
        description.text = mListScreen[position].Description
        imgSlide.setImageResource(mListScreen[position].ScreenImg)

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
}

