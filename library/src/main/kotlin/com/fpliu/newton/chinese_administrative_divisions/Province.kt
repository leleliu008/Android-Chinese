package com.fpliu.newton.chinese_administrative_divisions

import android.os.Parcel
import android.os.Parcelable
import com.fpliu.newton.ui.expandablelist.Group_

data class Province(val name: String, var cities: ArrayList<City>) : Group_<City>, Parcelable {
    override fun getChildren(): MutableList<City> {
        return cities.toMutableList()
    }

    constructor(source: Parcel) : this(
        source.readString(),
        ArrayList<City>().apply { source.readList(this, City::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeList(cities)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Province> = object : Parcelable.Creator<Province> {
            override fun createFromParcel(source: Parcel): Province = Province(source)
            override fun newArray(size: Int): Array<Province?> = arrayOfNulls(size)
        }
    }
}