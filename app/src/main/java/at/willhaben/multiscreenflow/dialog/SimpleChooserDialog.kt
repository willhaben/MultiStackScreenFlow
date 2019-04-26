package at.willhaben.multiscreenflow.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import at.willhaben.library.dialog.DialogCallback
import at.willhaben.multiscreenflow.R

class SimpleChooserDialog : DialogFragment() {

    private val dialogId by lazy { arguments?.getInt(ARG_ID) ?: R.id.dialog_default }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val array = arguments?.getStringArray(ARG_STRING_LIST) ?: arrayOf()

        return AlertDialog.Builder(activity!!)
            .setTitle("Choose a item")
            .setItems(array) { _, i ->
                val bundle = Bundle().apply {
                    putInt(ARG_SELECTED_ITEM_INDEX, i)
                }
                getCallback()?.onItemSelected(dialogId, bundle)
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface?) {
        getCallback()?.onCancel(dialogId, null)
    }

    private fun getCallback(): DialogCallback? {
        val act = activity
        if (act is DialogCallback) {
            return act
        }
        return null
    }

    companion object {
        const val ARG_ID = "ARG_ID"
        const val ARG_STRING_LIST = "ARG_STRING_LIST"

        const val ARG_SELECTED_ITEM_INDEX = "ARG_SELECTED_ITEM_INDEX"
    }
}