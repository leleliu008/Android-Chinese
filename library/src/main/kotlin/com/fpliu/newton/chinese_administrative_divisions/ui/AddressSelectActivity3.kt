package com.fpliu.newton.chinese_administrative_divisions.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import com.fpliu.newton.chinese_administrative_divisions.Manager
import com.fpliu.newton.chinese_administrative_divisions.R
import com.fpliu.newton.chinese_administrative_divisions.entity.AddressNode
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.base.BaseActivity
import com.fpliu.newton.ui.list.RecyclerViewActivity
import com.fpliu.newton.ui.recyclerview.holder.ItemViewHolder
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import rx_activity_result2.RxActivityResult

open class AddressSelectActivity3 : RecyclerViewActivity<AddressNode>() {

    companion object {

        private val TAG = AddressSelectActivity3::class.java.simpleName

        //返回的结果 - 省份
        private const val KEY_RESULT_PROVINCE = "province"

        //返回的结果 - 城市
        private const val KEY_RESULT_CITY = "city"

        //返回的结果 - 区
        private const val KEY_RESULT_DISTINCT = "distinct"

        fun startForResult(activity: BaseActivity, onResult: (province: AddressNode, city: AddressNode, distinct: AddressNode) -> Unit, onError: (Throwable) -> Unit) {
            RxActivityResult
                .on(activity)
                .startIntent(Intent(activity, AddressSelectActivity3::class.java))
                .filter { it.resultCode() == Activity.RESULT_OK }
                .map { it.data()!! }
                .autoDisposable(activity.disposeOnDestroy())
                .subscribe({
                    val province = it.getParcelableExtra<AddressNode>(KEY_RESULT_PROVINCE)
                    val city = it.getParcelableExtra<AddressNode>(KEY_RESULT_CITY)
                    val distinct = it.getParcelableExtra<AddressNode>(KEY_RESULT_DISTINCT)
                    onResult.invoke(province, city, distinct)
                }, { onError.invoke(it) })
        }
    }

    private var selectedProvinceIndex = -1
    private var selectedCityIndex = -1

    private var provinceList: List<AddressNode>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "选择省份"

        readData {
            provinceList = it
            items = it
        }
    }

    override fun onBindLayout(parent: ViewGroup, viewType: Int) = R.layout.address_select_list_item

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, item: AddressNode) {
        holder.id(R.id.address_select_list_item_text).text(item.name)
    }

    override fun onItemClick(holder: ItemViewHolder, position: Int, item: AddressNode) {
        when (item.id.length) {
            2 -> {
                title = "选择城市"
                selectedProvinceIndex = position
                items = item.children
            }
            4 -> {
                title = "选择区"
                selectedCityIndex = position
                items = item.children
            }
            6 -> {
                val provinceList = provinceList ?: return
                Intent().apply {
                    val selectedProvince = provinceList[selectedProvinceIndex]
                    val selectedCity = selectedProvince.children[selectedCityIndex]
                    putExtra(KEY_RESULT_PROVINCE, selectedProvince)
                    putExtra(KEY_RESULT_CITY, selectedCity)
                    putExtra(KEY_RESULT_DISTINCT, item)
                }.let { setResult(Activity.RESULT_OK, it) }
                finish()
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onLeftBtnClick()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onLeftBtnClick() {
        if (selectedProvinceIndex != -1 && selectedCityIndex != -1) {
            title = "选择城市"
            selectedCityIndex = -1
            items = provinceList!![selectedProvinceIndex].children
        } else if (selectedProvinceIndex != -1 && selectedCityIndex == -1) {
            title = "选择省份"
            selectedProvinceIndex = -1
            items = provinceList
        } else if (selectedProvinceIndex == -1 && selectedCityIndex == -1) {
            super.onLeftBtnClick()
        }
    }

    protected open fun readData(callback: (List<AddressNode>) -> Unit) {
        Manager
            .getAsyncReadFromAssetsObservable(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(disposeOnDestroy())
            .subscribe({
                callback.invoke(it)
            }, {
                Logger.e(TAG, "readData()", it)
                showToast("出错了")
            })
    }
}