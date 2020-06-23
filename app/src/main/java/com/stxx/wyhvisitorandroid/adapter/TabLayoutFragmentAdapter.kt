package com.stxx.wyhvisitorandroid.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * description:
 * Created by liNan on  2020/2/7 20:39
 */
class TabLayoutFragmentAdapter constructor(
    fm: FragmentManager,
    private val fragmentList: ArrayList<Fragment>,
    private val fragmentTitle: ArrayList<String>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
   /*     container.removeView(`object` as View)
        com.orhanobut.logger.Logger.i("destory")*/
    }

}