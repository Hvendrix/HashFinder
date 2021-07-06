package com.example.hashfinder

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.HashSet


@Parcelize
data class Hash(val hashSet: HashSet<String>) : Parcelable {
    fun checkContains(strHash: String): Boolean {
        return hashSet.contains(strHash)
    }
}
