package com.example.mbil

import android.os.Parcel
import android.os.Parcelable

data class CartItem(
    val name: String,
    val price: String,
    val quantity: Int,
    val imageUrl: String? // Menyimpan URL gambar (opsional)
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", // Nilai default jika null
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "" // Pastikan imageUrl tidak null
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeInt(quantity)
        parcel.writeString(imageUrl) // Menyimpan imageUrl ke Parcel
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}

