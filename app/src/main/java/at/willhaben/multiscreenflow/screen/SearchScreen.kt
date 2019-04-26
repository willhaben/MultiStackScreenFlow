package at.willhaben.multiscreenflow.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.willhaben.library.Screen
import at.willhaben.library.ScreenFlow
import kotlinx.android.synthetic.main.screen_search.view.*
import at.willhaben.multiscreenflow.R
import at.willhaben.multiscreenflow.deeplink.modifiers.SearchEntranceModifier.Companion.EXTRA_INIT_TAB

class SearchScreen(screenFlow: ScreenFlow) : Screen(screenFlow) {

    private var buttonPushed by state(0)
    private var initTab by state("")

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.screen_search, parent, false)
    }

    override fun afterInflate(initBundle: Bundle?) {
        view.tvSearchScreenPushed.text = "Pushed: $buttonPushed"
        view.btnSearchScreenPush.setOnClickListener {
            buttonPushed++
            view.tvSearchScreenPushed.text = "Pushed: $buttonPushed"
        }

        view.btnSearchScreenList.setOnClickListener {
            screenFlow.goToScreen(SearchListScreen(screenFlow))
        }

        if (initBundle != null) {
            initTab = initBundle.getString(EXTRA_INIT_TAB) ?: ""
        }

        if (!initTab.isEmpty()) {
            view.tvScreenSearchTitle.text = "SEARCH ($initTab)"
        }
    }
}