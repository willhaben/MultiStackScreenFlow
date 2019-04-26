package at.willhaben.library

import android.os.Parcel
import android.os.Parcelable
import at.willhaben.library.backstack.ScreenState
import at.willhaben.library.backstack.history.History
import java.util.*

data class ScreenStateStack(private val inner: Stack<ScreenState> = Stack()) : Parcelable, List<ScreenState> by inner{

    fun push(item: ScreenState) = inner.push(item)!!

    fun pop() = inner.pop()!!

    fun peek() = inner.peek()!!

    constructor(source: Parcel) : this(
        Stack<ScreenState>().apply {
            val array = source.readParcelableArray(ScreenState::class.java.classLoader)!!
            array.forEach {
                push(it as ScreenState)
            }
        }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        dest.writeParcelableArray(inner.toArray(arrayOf<Parcelable>()), 0)
    }

    fun lastElement(): ScreenState {
        return inner[0]
    }

    fun clear() {
        inner.clear()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ScreenStateStack> = object : Parcelable.Creator<ScreenStateStack> {
            override fun createFromParcel(source: Parcel): ScreenStateStack =
                ScreenStateStack(source)
            override fun newArray(size: Int): Array<ScreenStateStack?> = arrayOfNulls(size)
        }
    }
}

data class HistoryStack(private val inner: Stack<History> = Stack()) : Parcelable, List<History> by inner {
    fun push(item: History) = inner.push(item)!!

    fun pop() = inner.pop()!!

    fun peek() = inner.peek()!!

    fun lastElement(): History {
        return inner[0]
    }

    fun clear() {
        inner.clear()
    }

    constructor(source: Parcel) : this(
        Stack<History>().apply {
            val array = source.readParcelableArray(History::class.java.classLoader)!!
            array.forEach {
                push(it as History)
            }
        }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        dest.writeParcelableArray(inner.toArray(arrayOf<Parcelable>()), 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HistoryStack> = object : Parcelable.Creator<HistoryStack> {
            override fun createFromParcel(source: Parcel): HistoryStack =
                HistoryStack(source)
            override fun newArray(size: Int): Array<HistoryStack?> = arrayOfNulls(size)
        }
    }
}
