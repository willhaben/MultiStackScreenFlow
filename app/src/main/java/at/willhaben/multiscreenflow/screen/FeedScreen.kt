package at.willhaben.multiscreenflow.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.willhaben.library.Screen
import at.willhaben.library.ScreenFlow
import kotlinx.android.synthetic.main.screen_feed.view.*
import at.willhaben.multiscreenflow.R

class FeedScreen(screenFlow: ScreenFlow) : Screen(screenFlow) {

    private var buttonPushed by state(0)

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.screen_feed, parent, false)
    }

    override fun afterInflate(initBundle: Bundle?) {
        view.tvFeedScreenPushed.text = "Pushed: $buttonPushed"
        view.btnFeedScreenPush.setOnClickListener {
            buttonPushed++
            view.tvFeedScreenPushed.text = "Pushed: $buttonPushed"
        }

        view.btnFeedScreenAddetail.setOnClickListener {
            screenFlow.goToScreen(AdDetailScreen(screenFlow))
        }

        view.btnFeedScreenMyAds.setOnClickListener {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("willhaben://multi-screenflow/myAds")))
        }

        view.btnFeedScreenSearch.setOnClickListener {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("willhaben://multi-screenflow/search/immo")))
        }
    }
}