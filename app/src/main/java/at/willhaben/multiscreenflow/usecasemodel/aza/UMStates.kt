package at.willhaben.multiscreenflow.usecasemodel.aza

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import at.willhaben.multiscreenflow.domain.AzaData

sealed class UMStates : Parcelable{

    @Parcelize
    object Initial : UMStates()

    @Parcelize
    object Loading : UMStates()

    @Parcelize
    object Error : UMStates()

    @Parcelize
    class Loaded(val azaDatas : List<AzaData>) : UMStates()
}