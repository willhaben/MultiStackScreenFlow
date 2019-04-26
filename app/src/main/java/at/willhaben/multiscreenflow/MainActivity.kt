package at.willhaben.multiscreenflow

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import at.willhaben.library.MultiStackScreenFlowActivity
import at.willhaben.library.Screen
import kotlinx.android.synthetic.main.activity_main.*
import at.willhaben.multiscreenflow.commonextensions.color
import at.willhaben.multiscreenflow.screen.*

class MainActivity : MultiStackScreenFlowActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuFeedActivityMain.setOnClickListener {
            switchStack(HOME)
        }

        menuSearchActivityMain.setOnClickListener {
            switchStack(SEARCH)
        }

        menuAzaActivityMain.setOnClickListener {
            switchStack(AZA)
        }

        menuChatActivityMain.setOnClickListener {
            switchStack(MESSAGING)
        }

        menuProfileActivityMain.setOnClickListener {
            switchStack(PROFILE)
        }

        onStackChanged(getCurrentStack())
    }

    override fun onStackChanged(stackId: Int) {
        when(stackId) {
            HOME -> selectMenu(menuFeedActivityMain)
            SEARCH -> selectMenu(menuSearchActivityMain)
            AZA -> selectMenu(menuAzaActivityMain)
            MESSAGING -> selectMenu(menuChatActivityMain)
            PROFILE -> selectMenu(menuProfileActivityMain)
        }
        stackVisualizerMainActivity.stacksCounts = getStackCounts()
    }

    override fun goToScreen(screen: Screen, saveToStack: Boolean) {
        super.goToScreen(screen, saveToStack)
        stackVisualizerMainActivity.stacksCounts = getStackCounts()
    }

    private fun selectMenu(view : View) {
        menuFeedActivityMain.setColorFilter(Color.BLACK)
        menuSearchActivityMain.setColorFilter(Color.BLACK)
        menuAzaActivityMain.setColorFilter(Color.BLACK)
        menuChatActivityMain.setColorFilter(Color.BLACK)
        menuProfileActivityMain.setColorFilter(Color.BLACK)
        (view as ImageView).setColorFilter(color(R.color.colorPrimary))
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun provideContentFrameForScreenFlow(): FrameLayout {
        return contentActivityMain
    }

    override fun provideStackConfigurator(): StackConfigurator = StackConfiguratorImpl()

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "On MainActivity Resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "On MainActivity Paused")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "On MainActivity Started")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "On MainActivity Stoped")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "On Activity Result")
    }

    companion object {
        const val HOME = 0
        const val SEARCH = 1
        const val AZA = 2
        const val MESSAGING = 3
        const val PROFILE = 4

        const val TAG = "MainActivity"
    }
}

private class StackConfiguratorImpl : MultiStackScreenFlowActivity.StackConfigurator {

    override fun size(): Int = 5

    override fun rootScreenFactory(): MultiStackScreenFlowActivity.RootScreenFactory {
        return RootScreenFactoryImpl()
    }

    private class RootScreenFactoryImpl : MultiStackScreenFlowActivity.RootScreenFactory {

        override fun provideRootScreen(flowStack: Int): Class<out Screen> {
            return when(flowStack) {
                0 -> FeedScreen::class.java
                1 -> SearchScreen::class.java
                2 -> AzaScreen::class.java
                3 -> MessagingScreen::class.java
                4 -> ProfileScreen::class.java
                else -> FeedScreen::class.java
            }
        }
    }
}