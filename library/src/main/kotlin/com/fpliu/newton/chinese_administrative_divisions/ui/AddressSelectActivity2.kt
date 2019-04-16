package com.fpliu.newton.chinese_administrative_divisions.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpliu.newton.chinese_administrative_divisions.Manager
import com.fpliu.newton.chinese_administrative_divisions.R
import com.fpliu.newton.chinese_administrative_divisions.entity.AddressNode
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.base.BaseActivity
import com.fpliu.newton.ui.recyclerview.adapter.ItemAdapter
import com.fpliu.newton.ui.recyclerview.holder.ItemViewHolder
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import rx_activity_result2.RxActivityResult

open class AddressSelectActivity2 : BaseActivity() {

    companion object {

        private val TAG = AddressSelectActivity2::class.java.simpleName

        /**
         * 返回的结果 - 省份名称
         */
        private const val KEY_RESULT_PROVINCE = "province"

        /**
         * 返回的结果 - 城市名称
         */
        private const val KEY_RESULT_CITY = "city"

        fun startForResult(activity: BaseActivity, onResult: (province: AddressNode, city: AddressNode) -> Unit, onError: (Throwable) -> Unit) {
            RxActivityResult
                .on(activity)
                .startIntent(Intent(activity, AddressSelectActivity2::class.java))
                .filter { it.resultCode() == Activity.RESULT_OK }
                .map { it.data()!! }
                .autoDisposable(activity.disposeOnDestroy())
                .subscribe({
                    val province = it.getParcelableExtra<AddressNode>(KEY_RESULT_PROVINCE)
                    val city = it.getParcelableExtra<AddressNode>(KEY_RESULT_CITY)
                    onResult.invoke(province, city)
                }, {
                    Logger.e(TAG, "startForResult()", it)
                    onError.invoke(it)
                })
        }
    }

    private val provinceRecyclerView: RecyclerView by lazy { RecyclerView(this) }

    private val cityRecyclerView: RecyclerView by lazy { RecyclerView(this) }

    private var province: AddressNode? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_RESULT_PROVINCE, province)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        province = savedInstanceState?.getParcelable(KEY_RESULT_PROVINCE)

        super.onCreate(savedInstanceState)

        cityRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddressSelectActivity2)
            addItemDecoration(DividerItemDecoration(this@AddressSelectActivity2, RecyclerView.VERTICAL))
            adapter = object : ItemAdapter<AddressNode>(null) {
                override fun onBindLayout(parent: ViewGroup, viewType: Int) = R.layout.address_select_list_item
                override fun onBindViewHolder(holder: ItemViewHolder, position: Int, item: AddressNode) {
                    holder.id(R.id.address_select_list_item_text).text(item.name)
                }
            }.apply {
                setOnItemClickListener { _, _, item ->
                    Intent().apply {
                        putExtra(KEY_RESULT_PROVINCE, province)
                        putExtra(KEY_RESULT_CITY, item)
                    }.let { setResult(Activity.RESULT_OK, it) }
                    finish()
                }
            }
        }
        addContentView(cityRecyclerView)

        provinceRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AddressSelectActivity2)
            addItemDecoration(DividerItemDecoration(this@AddressSelectActivity2, RecyclerView.VERTICAL))
            adapter = object : ItemAdapter<AddressNode>(null) {
                override fun onBindLayout(parent: ViewGroup, viewType: Int) = R.layout.address_select_list_item
                override fun onBindViewHolder(holder: ItemViewHolder, position: Int, item: AddressNode) {
                    holder.id(R.id.address_select_list_item_text).text(item.name)
                }
            }.apply {
                setOnItemClickListener { _, _, item ->
                    title = "选择城市"
                    province = item
                    (cityRecyclerView.adapter as ItemAdapter<AddressNode>).items = item.children
                    provinceRecyclerView.visibility = View.GONE
                    cityRecyclerView.visibility = View.VISIBLE
                }
            }
        }
        addContentView(provinceRecyclerView)

        if (province == null) {
            title = "选择省份"
            provinceRecyclerView.visibility = View.VISIBLE
            cityRecyclerView.visibility = View.GONE
        } else {
            title = "选择城市"
            provinceRecyclerView.visibility = View.GONE
            cityRecyclerView.visibility = View.VISIBLE
            (cityRecyclerView.adapter as ItemAdapter<AddressNode>).items = province!!.children
        }

        readData {
            (provinceRecyclerView.adapter as ItemAdapter<AddressNode>).items = it
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
        if (cityRecyclerView.visibility == View.VISIBLE) {
            title = "选择省份"
            provinceRecyclerView.visibility = View.VISIBLE
            cityRecyclerView.visibility = View.GONE
            return
        }
        super.onLeftBtnClick()
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