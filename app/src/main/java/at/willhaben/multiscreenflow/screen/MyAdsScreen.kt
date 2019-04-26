package at.willhaben.multiscreenflow.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.willhaben.library.Screen
import at.willhaben.library.ScreenFlow
import kotlinx.android.synthetic.main.screen_myads.view.*
import at.willhaben.multiscreenflow.R
import at.willhaben.multiscreenflow.deeplink.modifiers.MyAdsModifier.Companion.EXTRA_FROM_DEEPLINK

class MyAdsScreen(screenFlow: ScreenFlow) : Screen(screenFlow) {

    private var buttonPushed by state(0)
    private var fromDeepLink by state(false)

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.screen_myads, parent, false)
    }

    override fun afterInflate(initBundle: Bundle?) {
        view.tvMyAdsScreenPushed.text = "Pushed: $buttonPushed"
        view.btnMyAdsScreenPush.setOnClickListener {
            buttonPushed++
            view.tvMyAdsScreenPushed.text = "Pushed: $buttonPushed"
        }

        view.btnMyAdsScreenAddetail.setOnClickListener {
            screenFlow.goToScreen(AdDetailScreen(screenFlow))
        }

        if(initBundle != null)
            this.fromDeepLink = initBundle.getBoolean(EXTRA_FROM_DEEPLINK, false)

        if(this.fromDeepLink) {
            view.tvScreenMyAdsTitle.text = "MyAds (from Deeplink)"
        }
    }
}