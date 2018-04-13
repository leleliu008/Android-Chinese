package com.fpliu.newton.chinese_administrative_divisions

import android.content.Context
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

    fun getAsyncReadFromAssetsObservable(context: Context): Observable<ArrayList<Province>> {
        return Observable
            .just("city.json")
            .map {
                val bufferedSource = Okio.buffer(Okio.source(context.resources.assets.open(it)))
                val jsonStr = bufferedSource.readString(Charset.forName("UTF-8"))
                val jsonArray = JSONArray(jsonStr)
                val provinceListLength = jsonArray.length()
                val provinceList = ArrayList<Province>()
                for (i in 0 until provinceListLength) {
                    val provinceJSONObject = jsonArray.getJSONObject(i)
                    val provinceName = provinceJSONObject.keys().next()
                    val cities = ArrayList<City>()
                    val cityJSONArray = provinceJSONObject.getJSONArray(provinceName)
                    val cityListLength = cityJSONArray.length()
                    for (j in 0 until cityListLength) {
                        val cityJSONObject = cityJSONArray.getJSONObject(j)
                        val cityName = cityJSONObject.keys().next()
                        val distinctes = ArrayList<String>()
                        val distinctJSONArray = cityJSONObject.getJSONArray(cityName)
                        val distinctListLength = distinctJSONArray.length()
                        for (k in 0 until distinctListLength) {
                            distinctes.add(distinctJSONArray.getString(k))
                        }
                        cities.add(City(cityName, distinctes))
                    }
                    provinceList.add(Province(provinceName, cities))
                }
                return@map provinceList
            }
            .subscribeOn(Schedulers.io())
    }
}