package at.willhaben.library.state

import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import java.io.Serializable
import java.util.*

@Suppress("UNCHECKED_CAST") fun <T> Bundle.put(key : String, value : T) {
    if (value == null) {
        this.putSerializable(key, null)
        return
    }
    var validType = true
    when(value) {
        is String -> this.putString(key, value)
        is Bundle -> this.putBundle(key, value)
        is Byte -> this.putByte(key, value)
        is ByteArray-> this.putByteArray(key, value)
        is Char -> this.putChar(key, value)
        is CharArray -> this.putCharArray(key, value)
        is CharSequence-> this.putCharSequence(key, value)
        is Float -> this.putFloat(key, value)
        is FloatArray -> this.putFloatArray(key, value)
        is Parcelable -> this.putParcelable(key, value)
        is Serializable -> this.putSerializable(key, value)
        is Short -> this.putShort(key, value)
        is ShortArray -> this.putShortArray(key, value)
        is Boolean -> this.putBoolean(key, value)
        is BooleanArray -> this.putBooleanArray(key, value)
        is Double -> this.putDouble(key, value)
        is DoubleArray -> this.putDoubleArray(key, value)
        is Int -> this.putInt(key, value)
        is IntArray -> this.putIntArray(key, value)
        is Long -> this.putLong(key, value)
        is LongArray -> this.putLongArray(key, value)
        is Array<*> -> {
            var first : Any? = null
            for (i in (0..value.lastIndex)) {
                first = value[i]
                if (first != null)
                    break
            }
            when(first) {
                is String -> {
                    val stringArray = Array(value.size) { idx ->
                        value[idx] as String
                    }
                    putStringArray(key, stringArray)
                }
                else -> validType = false
            }
        }
        is SparseArray<*> -> {
            var first : Any? = null
            for (i in (0 until value.size())) {
                first = value.get(value.keyAt(i))
                if (first != null)
                    break
            }
            if (first == null || first is Parcelable) {
                this.putSparseParcelableArray(key, value as SparseArray<Parcelable>)
            }
            else {
                validType = false
            }
        }
        is ArrayList<*> ->  {
            var first : Any? = null
            for (obj in value) {
                first = obj
                if (first != null)
                    break
            }
            when(first) {
                is String -> this.putStringArrayList(key, value as ArrayList<String>)
                is CharSequence -> this.putCharSequenceArrayList(key, value as ArrayList<CharSequence>)
                is Int -> this.putIntegerArrayList(key, value as ArrayList<Int>)
                is Parcelable -> this.putParcelableArrayList(key, value as ArrayList<Parcelable>)
                else -> validType = false
            }
        }
        is Binder -> putBinder(key, value)
        is Size -> putSize(key, value)
        is SizeF -> putSizeF(key, value)
        else -> validType = false
    }
    if (!validType) {
        throw IllegalStateException("Can't save object for $key in state bundle")
    }
}