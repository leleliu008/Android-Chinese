package com.fpliu.newton.chinese_administrative_divisions.entity

import android.os.Parcel
import android.os.Parcelable

data class AddressNode(
    val id: String = "",
    val name: String = "",
    val children: List<AddressNode> = listOf()
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.createTypedArrayList(AddressNode.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeTypedList(children)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AddressNode> = object : Parcelable.Creator<AddressNode> {
            override fun createFromParcel(source: Parcel): AddressNode = AddressNode(source)
            override fun newArray(size: Int): Array<AddressNode?> = arrayOfNulls(size)
        }
    }
}