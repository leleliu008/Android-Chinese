package com.fpliu.newton.chinese_administrative_divisions.sample

import android.app.Application
import com.fpliu.newton.ui.base.BaseUIConfig
import rx_activity_result2.RxActivityResult

open class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RxActivityResult.register(this)
        BaseUIConfig.headHeight = resources.getDimension(R.dimen.dp750_100).toInt()
    }
}