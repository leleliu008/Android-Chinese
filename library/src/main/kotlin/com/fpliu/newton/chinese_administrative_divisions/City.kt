package com.fpliu.newton.chinese_administrative_divisions

import android.os.Parcel
import android.os.Parcelable

data class City(val name: String, var districts: ArrayList<String>) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString(),
        source.createStringArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeStringList(districts)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<City> = object : Parcelable.Creator<City> {
            override fun createFromParcel(source: Parcel): City = City(source)
            override fun newArray(size: Int): Array<City?> = arrayOfNulls(size)
        }
    }
}
