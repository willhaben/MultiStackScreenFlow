package at.willhaben.multiscreenflow.deeplink.modifiers

import android.os.Bundle
import at.willhaben.library.StackModifiable
import at.willhaben.library.StackModifier
import at.willhaben.multiscreenflow.MainActivity.Companion.PROFILE
import at.willhaben.multiscreenflow.screen.MyAdsScreen

class MyAdsModifier : StackModifier {

    override fun modifyBackStack(stackModifiable: StackModifiable) {
        stackModifiable.changeCurrentStack(PROFILE)
        stackModifiable.popAllAndCreateRootIfNecessary()
        stackModifiable.pushToStack(MyAdsScreen::class.java, Bundle().apply {
            putBoolean(EXTRA_FROM_DEEPLINK, true)
        })
    }

    companion object {
        const val EXTRA_FROM_DEEPLINK = "EXTRA_FROM_DEEPLINK"
    }
}