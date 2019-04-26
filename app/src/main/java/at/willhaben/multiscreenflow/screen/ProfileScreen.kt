package at.willhaben.multiscreenflow.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.willhaben.library.Screen
import at.willhaben.library.ScreenFlow
import kotlinx.android.synthetic.main.screen_profile.view.*
import at.willhaben.multiscreenflow.R
import at.willhaben.multiscreenflow.logOut

class ProfileScreen(screenFlow: ScreenFlow) : Screen(screenFlow) {
    private var buttonPushed by state(0)

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.screen_profile, parent, false)
    }

    override fun afterInflate(initBundle: Bundle?) {
        view.tvProfileScreenPushed.text = "Pushed: $buttonPushed"
        view.btnProfileScreenPush.setOnClickListener {
            buttonPushed++
            view.tvProfileScreenPushed.text = "Pushed: $buttonPushed"
        }

        view.btnProfileScreenMyAds.setOnClickListener {
            screenFlow.goToScreen(MyAdsScreen(screenFlow))
        }

        view.btnProfileScreenLogout.setOnClickListener {
            logOut()
            screenFlow.reset()
        }
    }
}