package at.willhaben.multiscreenflow.deeplink.modifiers

import android.os.Bundle
import at.willhaben.library.StackModifiable
import at.willhaben.library.StackModifier
import at.willhaben.multiscreenflow.MainActivity.Companion.SEARCH
import at.willhaben.multiscreenflow.screen.AdDetailScreen
import at.willhaben.multiscreenflow.screen.SearchListScreen

class AdDetailModifier(private val adDetailTitle : String) : StackModifier {

    override fun modifyBackStack(stackModifiable: StackModifiable) {
        stackModifiable.changeCurrentStack(SEARCH)
        stackModifiable.popAllAndCreateRootIfNecessary()
        stackModifiable.pushToStack(SearchListScreen::class.java, Bundle())
        stackModifiable.pushToStack(AdDetailScreen::class.java, Bundle().apply {
            putString(EXTRA_ADDETAIL_TITLE, adDetailTitle)
        })
    }

    companion object {
        const val EXTRA_ADDETAIL_TITLE = "EXTRA_ADDETAIL_TITLE"
    }
}