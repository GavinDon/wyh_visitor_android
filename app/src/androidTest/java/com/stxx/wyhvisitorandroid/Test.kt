package com.stxx.wyhvisitorandroid

import com.orhanobut.logger.Logger
import org.junit.Test

/**
 * description:
 * Created by liNan on  2020/8/19 14:30
 */
class Test {

    @Test
    fun perce() {
        val width = 120
        val height = 109
        Logger.i("PERCENT:=${width / height}")
    }
}