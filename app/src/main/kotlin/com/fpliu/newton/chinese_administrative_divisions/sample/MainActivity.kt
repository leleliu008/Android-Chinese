package com.fpliu.newton.chinese_administrative_divisions.sample

import android.os.Bundle
import com.fpliu.newton.chinese_administrative_divisions.entity.AddressNode
import com.fpliu.newton.chinese_administrative_divisions.ui.AddressSelectActivity
import com.fpliu.newton.chinese_administrative_divisions.ui.AddressSelectActivity2
import com.fpliu.newton.chinese_administrative_divisions.ui.AddressSelectActivity3
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.base.BaseActivity
import com.jakewharton.rxbinding3.view.clicks
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 地区选择使用示例
 * @author 792793182@qq.com 2018-03-28.
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "地区选择使用示例"

        setContentView(R.layout.activity_main)

        button1.clicks().autoDisposable(disposeOnDestroy()).subscribe(::onClicked1)
        button2.clicks().autoDisposable(disposeOnDestroy()).subscribe(::onClicked2)
        button3.clicks().autoDisposable(disposeOnDestroy()).subscribe(::onClicked3)
    }


    private fun onClicked1(u: Unit) {
        AddressSelectActivity.startForResult(this, ::onResult, ::onError)
    }

    private fun onClicked2(u: Unit) {
        AddressSelectActivity2.startForResult(this, ::onResult, ::onError)
    }

    private fun onClicked3(u: Unit) {
        AddressSelectActivity3.startForResult(this, ::onResult3, ::onError)
    }

    private fun onResult(province: AddressNode, city: AddressNode) {
        showToast("${province.name}, ${city.name}")
    }

    private fun onResult3(province: AddressNode, city: AddressNode, distinct: AddressNode) {
        showToast("${province.name}, ${city.name}, ${distinct.name}")
    }

    private fun onError(e: Throwable) {
        Logger.e("XXX", "onError()", e)
        showToast("出错了")
    }
}