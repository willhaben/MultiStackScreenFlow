package at.willhaben.library.state

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

internal interface CompressedParcelable {

    companion object {

        @SuppressLint("Recycle")
        fun uncompressParcel(parcelIn: Parcel) : Parcel {
            val uncompressedSize = parcelIn.readInt()
            val compressedSize = parcelIn.readInt()
            val zippedBytes = ByteArray(compressedSize)
            parcelIn.readByteArray(zippedBytes)

            val byteIn = ByteArrayInputStream(zippedBytes)
            val gzipIn = InflaterInputStream(byteIn)
            val bytes = gzipIn.readBytes(estimatedSize = uncompressedSize)
            gzipIn.close()
            byteIn.close()

            val uncompressedParcel = Parcel.obtain()
            uncompressedParcel.unmarshall(bytes, 0, bytes.size)
            uncompressedParcel.setDataPosition(0)
            return uncompressedParcel
        }
    }

    @SuppressLint("Recycle")
    fun obtainUncompressedParcel() : Parcel = Parcel.obtain()

    fun processUncompressedParcel(uncompressedParcel: Parcel, dest: Parcel) {

        val byteOut = ByteArrayOutputStream()
        val deflater = Deflater(Deflater.BEST_COMPRESSION, false)
        val deflateOut = DeflaterOutputStream(byteOut, deflater)

        try {
            val bytes = uncompressedParcel.marshall()
            uncompressedParcel.recycle()
            deflateOut.write(bytes)
            deflateOut.flush()
            deflateOut.finish()
            val compressedBytes = byteOut.toByteArray()
            dest.writeInt(bytes.size)
            dest.writeInt(compressedBytes.size)
            dest.writeByteArray(compressedBytes)
        }
        finally {
            deflateOut.close()
            byteOut.close()
        }
    }
}

internal class CompressedBundle(bundle: Bundle) : Parcelable, CompressedParcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<CompressedBundle> = object : Parcelable.Creator<CompressedBundle> {
            override fun createFromParcel(parcelIn: Parcel): CompressedBundle {
                return CompressedBundle(
                    CompressedParcelable.uncompressParcel(
                        parcelIn
                    )
                )
            }

            override fun newArray(size: Int): Array<CompressedBundle?> {
                return arrayOfNulls(size)
            }
        }
    }

    var bundle: Bundle = bundle
        get() {
            field.classLoader = CompressedBundle::class.java.classLoader
            return field
        }
        private set(value) {
            field = value
        }

    constructor(parcelIn: Parcel) : this(parcelIn.readBundle()) {
        parcelIn.recycle()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest ?: return
        val uncompressedParcel = obtainUncompressedParcel()
        uncompressedParcel.writeBundle(bundle)
        processUncompressedParcel(uncompressedParcel, dest)
    }

    override fun describeContents(): Int = 0
}