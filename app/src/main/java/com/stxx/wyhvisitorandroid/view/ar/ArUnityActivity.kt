package com.stxx.wyhvisitorandroid.view.ar

import android.os.Bundle
import com.dreamdeck.wyhapp.UnityPlayerActivity
import com.stxx.wyhvisitorandroid.R
import kotlinx.android.synthetic.main.activity_test.*
import org.jetbrains.anko.startActivity

/**
 * description:
 * Created by liNan on  2020/4/23 09:12
 */
class ArUnityActivity : UnityPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        tvStart.setOnClickListener {
            startActivity<UnityPlayerActivity>()
        }
    }

}