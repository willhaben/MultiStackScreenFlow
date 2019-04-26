package at.willhaben.multiscreenflow.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AzaData(val title : String, val description : String) : Parcelable