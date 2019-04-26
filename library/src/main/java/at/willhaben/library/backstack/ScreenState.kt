package at.willhaben.library.backstack

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import at.willhaben.library.Screen
import java.util.*

data class ScreenState(
    val clazz: Class<out Screen>,
    val state: Bundle = Bundle(),
    val uuid: UUID = UUID.randomUUID()
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readSerializable() as Class<out Screen>,
        source.readParcelable<Bundle>(Bundle::class.java.classLoader),
        source.readSerializable() as UUID
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(clazz)
        writeParcelable(state, 0)
        writeSerializable(uuid)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ScreenState> = object : Parcelable.Creator<ScreenState> {
            override fun createFromParcel(source: Parcel): ScreenState =
                ScreenState(source)
            override fun newArray(size: Int): Array<ScreenState?> = arrayOfNulls(size)
        }
    }
}