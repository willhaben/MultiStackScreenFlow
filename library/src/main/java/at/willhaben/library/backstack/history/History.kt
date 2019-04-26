package at.willhaben.library.backstack.history

import android.os.Parcel
import android.os.Parcelable
import java.util.*

sealed class History : Parcelable

data class StackSwitch(
    val oldStackId: Int,
    val newStackId: Int,
    val oldStackTopScreenID: UUID
) : History() {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readInt(),
        source.readSerializable() as UUID
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(oldStackId)
        writeInt(newStackId)
        writeSerializable(oldStackTopScreenID)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<StackSwitch> = object : Parcelable.Creator<StackSwitch> {
            override fun createFromParcel(source: Parcel): StackSwitch =
                StackSwitch(source)
            override fun newArray(size: Int): Array<StackSwitch?> = arrayOfNulls(size)
        }
    }
}

data class ScreenPushedToStack(val fromStack: Int, val lasScreenStateId: UUID) : History() {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readSerializable() as UUID
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(fromStack)
        writeSerializable(lasScreenStateId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ScreenPushedToStack> = object : Parcelable.Creator<ScreenPushedToStack> {
            override fun createFromParcel(source: Parcel): ScreenPushedToStack =
                ScreenPushedToStack(source)
            override fun newArray(size: Int): Array<ScreenPushedToStack?> = arrayOfNulls(size)
        }
    }
}