package at.willhaben.multiscreenflow.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.willhaben.library.Screen
import at.willhaben.library.ScreenFlow
import kotlinx.android.synthetic.main.screen_searchlist.view.*
import at.willhaben.multiscreenflow.R

class SearchListScreen(screenFlow: ScreenFlow) : Screen(screenFlow) {

    private var buttonPushed by state(0)

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.screen_searchlist, parent, false)
    }

    override fun afterInflate(initBundle: Bundle?) {
        view.tvSearchListScreenPushed.text = "Pushed: $buttonPushed"
        view.btnSearchListScreenPush.setOnClickListener {
            buttonPushed++
            view.tvSearchListScreenPushed.text = "Pushed: $buttonPushed"
        }

        view.btnSearchListScreenAddetail.setOnClickListener {
            screenFlow.goToScreen(AdDetailScreen(screenFlow))
        }
    }
}