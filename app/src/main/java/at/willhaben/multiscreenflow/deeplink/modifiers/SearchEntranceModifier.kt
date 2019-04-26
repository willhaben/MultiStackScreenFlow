package at.willhaben.multiscreenflow.deeplink.modifiers

import android.os.Bundle
import at.willhaben.library.StackModifiable
import at.willhaben.library.StackModifier
import at.willhaben.multiscreenflow.MainActivity.Companion.SEARCH

class SearchEntranceModifier(private val tabText : String) : StackModifier {

    override fun modifyBackStack(stackModifiable: StackModifiable) {
        stackModifiable.changeCurrentStack(SEARCH)
        stackModifiable.popAllAndCreateRootIfNecessary()
        stackModifiable.passBundleToRoot(Bundle().apply {
            putString(EXTRA_INIT_TAB, tabText)
        })
    }

    companion object {
        const val EXTRA_INIT_TAB = "EXTRA_INIT_TAB"
    }
}