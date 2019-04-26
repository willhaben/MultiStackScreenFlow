package at.willhaben.library.dialog

import android.os.Bundle

interface DialogCallback {

    fun onButtonClicked(buttonId: Int, dialogId: Int, extra: Bundle?)

    fun onItemSelected(dialogId: Int, extra: Bundle?)

    fun onCancel(dialogId: Int, extra: Bundle?)
}