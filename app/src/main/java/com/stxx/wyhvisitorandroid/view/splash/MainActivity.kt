package com.stxx.wyhvisitorandroid.view.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.gyf.immersionbar.ImmersionBar
import com.stxx.wyhvisitorandroid.R
import com.stxx.wyhvisitorandroid.view.home.HomeFragment
import com.stxx.wyhvisitorandroid.view.mine.MineFragment
import com.stxx.wyhvisitorandroid.view.scenic.ScenicMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private val homeFragment1: HomeFragment by lazy { HomeFragment() }
    private val homeFragment2: ScenicMapFragment by lazy { ScenicMapFragment() }
    private val homeFragment3: MineFragment by lazy { MineFragment() }


    private lateinit var mFragments: List<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigation()
        /*bottomBar点击事件*/
        bottom_navigation_bar.setTabSelectedListener(object :
            BottomNavigationBar.SimpleOnTabSelectedListener() {
            override fun onTabSelected(position: Int) {
                switchFragment(position)
            }
        })

        mFragments = getFragments
        switchFragment()


    }

    /**
     * 初始化bottomNavigation
     */
    private fun initBottomNavigation() {
        bottom_navigation_bar.addItem(
            BottomNavigationItem(
                R.mipmap.ic_bottom_bar_home,
                R.string.bottom_navigation_view_home
            )
        ).addItem(
            BottomNavigationItem(
                R.mipmap.ic_bottom_bar_scenic,
                R.string.bottom_navigation_view_scenic
            )
        ).addItem(
            BottomNavigationItem(
                R.mipmap.ic_bottom_bar_mine,
                R.string.bottom_navigation_view_mine
            )
        ).setMode(BottomNavigationBar.MODE_FIXED)
            .setBarBackgroundColor(android.R.color.background_light)
            .setActiveColor(R.color.colorPrimaryDark)
            .setFirstSelectedPosition(0)
            .initialise()

    }

    private var mLastFgIndex: Int = 0
    /**
     * 切换fragment
     *
     * @param position 要显示的fragment的下标
     */
    private fun switchFragment(position: Int = 0) {
        if (position >= mFragments.size) {
            return
        }
        val ft = supportFragmentManager.beginTransaction()
        val targetFg = mFragments[position]
        val lastFg = mFragments[mLastFgIndex]
        mLastFgIndex = position
        ft.hide(lastFg)
        if (!targetFg.isAdded)
            ft.add(R.id.frame_layout, targetFg)
        ft.show(targetFg)
        ft.commit()
        when (position) {
            0 -> ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.colorWhite)
                .statusBarDarkFont(true)
                .init()
            1 -> ImmersionBar.with(this).keyboardEnable(false)
                .statusBarDarkFont(true)
                .statusBarColor(R.color.colorScenicTitle)
                .init()
            2 -> ImmersionBar.with(this).keyboardEnable(false)
                .statusBarDarkFont(true)
                .statusBarColor(R.color.colorTabSelect)
                .init()
        }
    }


    private val getFragments
        get() = listOf(homeFragment1, homeFragment2, homeFragment3)


}


