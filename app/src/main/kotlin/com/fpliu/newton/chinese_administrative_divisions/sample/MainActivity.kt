package com.fpliu.newton.chinese_administrative_divisions.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.fpliu.newton.chinese_administrative_divisions.AddressSelectActivity
import com.fpliu.newton.ui.base.BaseActivity
import com.fpliu.newton.ui.base.BaseUIConfig
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 地区选择使用示例
 * @author 792793182@qq.com 2018-03-28.
 */
class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_SELECT_ADDRESS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseUIConfig.setHeadHeight(resources.getDimension(R.dimen.dp750_100).toInt())

        super.onCreate(savedInstanceState)
        title = "地区选择使用示例"
        setContentView(R.layout.activity_main)
        click(buttom).subscribe { AddressSelectActivity.startForResult(this, REQUEST_CODE_SELECT_ADDRESS) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_ADDRESS && resultCode == Activity.RESULT_OK && data != null) {
            val provinceName = data.getStringExtra(AddressSelectActivity.KEY_RESULT_PROVINCE_NAME)
            val cityName = data.getStringExtra(AddressSelectActivity.KEY_RESULT_CITY_NAME)
            showToast("$provinceName , $cityName")
        }
    }
}