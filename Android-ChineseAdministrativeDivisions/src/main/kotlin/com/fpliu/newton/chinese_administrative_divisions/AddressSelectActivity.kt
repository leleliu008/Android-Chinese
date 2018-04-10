package com.fpliu.newton.chinese_administrative_divisions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.expandablelist.PinnedHeaderExpandableListView
import com.fpliu.newton.ui.expandablelist.PullablePinnedHeaderExpandableListViewActivity
import com.fpliu.newton.ui.list.ViewHolder
import com.fpliu.newton.ui.pullable.PullType
import com.fpliu.newton.ui.pullable.PullableViewContainer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 选择省份和城市的界面
 *
 * @author 792793182@qq.com 2017-06-08.
 */
class AddressSelectActivity : PullablePinnedHeaderExpandableListViewActivity<City, Province>() {

    companion object {

        private val TAG = AddressSelectActivity::class.java.simpleName

        /**
         * 返回的结果 - 省份名称
         */
        const val KEY_RESULT_PROVINCE_NAME = "provinceName"

        /**
         * 返回的结果 - 城市名称
         */
        const val KEY_RESULT_CITY_NAME = "cityName"


        fun startForResult(activity: Activity, requestCode: Int) {
            Intent(activity, AddressSelectActivity::class.java).apply {
                activity.startActivityForResult(this, requestCode)
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "选择地区"

        canPullDown(false)
        canPullUp(false)
        onRefreshOrLoadMore(pullableViewContainer, PullType.DOWN, 0, 10)
    }

    override fun onRefreshOrLoadMore(pullableViewContainer: PullableViewContainer<PinnedHeaderExpandableListView>, pullType: PullType, pageNum: Int, pageSize: Int) {
        Manager
            .getAsyncReadFromAssetsObservable(this)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setGroupItems(it)
                pullableViewContainer.finishRequestSuccess(pullType)
            }, { Logger.e(TAG, "", it) })
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        return ViewHolder
            .getInstance(R.layout.address_select_list_group_item, convertView, parent)
            .id(R.id.address_select_list_group_item_text)
            .text(getGroup(groupPosition).name)
            .getItemView()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        return ViewHolder
            .getInstance(R.layout.address_select_list_child_item, convertView, parent)
            .id(R.id.address_select_list_child_item_text)
            .text(getChild(groupPosition, childPosition).name)
            .getItemView()
    }

    override fun onGroupClick(parent: ExpandableListView, v: View, groupPosition: Int, id: Long): Boolean {
        val textView = v.findViewById<TextView>(R.id.address_select_list_group_item_text)
        if (parent.isGroupExpanded(groupPosition)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.group_indicator, 0, 0, 0)
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.group_indicator_expand, 0, 0, 0)
        }
        return false
    }

    override fun onChildClick(parent: ExpandableListView, convertView: View?,
                              groupPosition: Int, childPosition: Int, id: Long): Boolean {
        val provinceCity = getGroup(groupPosition)
        val city = getChild(groupPosition, childPosition)

        Intent().apply {
            putExtra(KEY_RESULT_PROVINCE_NAME, provinceCity.name)
            putExtra(KEY_RESULT_CITY_NAME, city.name)
            setResult(Activity.RESULT_OK, this)
        }

        finish()
        return false
    }

    override fun getPinnedHeader(): View {
        return layoutInflater.inflate(R.layout.address_select_list_group_item, findViewById(android.R.id.content), false)
    }

    override fun updatePinnedHeader(headerView: View, firstVisibleGroupPos: Int) {
        getGroup(firstVisibleGroupPos)?.let {
            headerView.findViewById<TextView>(R.id.address_select_list_group_item_text).text = it.name
        }
    }
}
