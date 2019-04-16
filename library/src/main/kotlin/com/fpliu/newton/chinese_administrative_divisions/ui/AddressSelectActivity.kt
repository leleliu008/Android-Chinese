package com.fpliu.newton.chinese_administrative_divisions.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import com.fpliu.newton.chinese_administrative_divisions.Manager
import com.fpliu.newton.chinese_administrative_divisions.R
import com.fpliu.newton.chinese_administrative_divisions.entity.AddressNode
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.base.BaseActivity
import com.fpliu.newton.ui.expandablelist.PinnedHeaderExpandableListView
import com.fpliu.newton.ui.expandablelist.PullablePinnedHeaderExpandableListViewActivity
import com.fpliu.newton.ui.list.ViewHolder
import com.fpliu.newton.ui.pullable.PullType
import com.fpliu.newton.ui.pullable.PullableViewContainer
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import rx_activity_result2.RxActivityResult

/**
 * 选择省份和城市的界面
 *
 * @author 792793182@qq.com 2017-06-08.
 */
class AddressSelectActivity : PullablePinnedHeaderExpandableListViewActivity<AddressNode, AddressNode>() {

    companion object {

        private val TAG = AddressSelectActivity::class.java.simpleName

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
                .startIntent(Intent(activity, AddressSelectActivity::class.java))
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

    override fun onCreate(savedInstanceState: Bundle?) {
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
            .autoDisposable(disposeOnDestroy())
            .subscribe({
                setGroupItems(it)
                pullableViewContainer.finishRequestSuccess(pullType)
            }, {
                Logger.e(TAG, "", it)
                pullableViewContainer.finishRequestWithAction(pullType, false, "出错了", "刷新", ::refresh)
            })
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
        val province = getGroup(groupPosition)
        val city = getChild(groupPosition, childPosition)

        Intent().apply {
            putExtra(KEY_RESULT_PROVINCE, province)
            putExtra(KEY_RESULT_CITY, city)
        }.let { setResult(Activity.RESULT_OK, it) }

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

    override fun getChildren(group: AddressNode) = group.children
}
