package com.fpliu.newton.chinese_administrative_divisions

import android.content.Context
import com.fpliu.newton.chinese_administrative_divisions.entity.AddressNode
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okio.Okio
import org.json.JSONArray
import java.nio.charset.Charset
import java.util.*

/**
 *
 * @author 792793182@qq.com 2018-04-10.
 */
object Manager {

    fun getAsyncReadFromAssetsObservable(context: Context): Observable<ArrayList<AddressNode>> {
        return Observable
            .just("address.json")
            .map {
                val bufferedSource = Okio.buffer(Okio.source(context.resources.assets.open(it)))
                val jsonStr = bufferedSource.readString(Charset.forName("UTF-8"))

                val jsonArray = JSONArray(jsonStr)
                val provinceListLength = jsonArray.length()
                val provinceList = ArrayList<AddressNode>(provinceListLength)
                for (i in 0 until provinceListLength) {
                    val provinceJSONObject = jsonArray.getJSONObject(i)
                    val provinceId = provinceJSONObject.getString("id")
                    val provinceName = provinceJSONObject.getString("name")
                    val cities = ArrayList<AddressNode>()
                    val cityJSONArray = provinceJSONObject.getJSONArray("children")
                    val cityListLength = cityJSONArray.length()
                    for (j in 0 until cityListLength) {
                        val cityJSONObject = cityJSONArray.getJSONObject(j)
                        val cityId = cityJSONObject.getString("id")
                        val cityName = cityJSONObject.getString("name")
                        val distinctJSONArray = cityJSONObject.getJSONArray("children")
                        val distinctListLength = distinctJSONArray.length()
                        val distinctes = ArrayList<AddressNode>(distinctListLength)
                        for (k in 0 until distinctListLength) {
                            val distinctJSONObject = distinctJSONArray.getJSONObject(k)
                            val distinctId = distinctJSONObject.getString("id")
                            val distinctName = distinctJSONObject.getString("name")
                            distinctes.add(AddressNode(distinctId, distinctName))
                        }
                        cities.add(AddressNode(cityId, cityName, distinctes))
                    }
                    provinceList.add(AddressNode(provinceId, provinceName, cities))
                }
                return@map provinceList
            }
            .subscribeOn(Schedulers.io())
    }
}