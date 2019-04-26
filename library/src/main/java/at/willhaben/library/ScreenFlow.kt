package at.willhaben.library

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import at.willhaben.library.usecasemodel.UseCaseModelStore

/**
 * Created by panmingk on 16/08/2017.
 */
interface ScreenFlow {

    val activity: AppCompatActivity
    val backStackAble: BackStackAble
    var currentScreen: Screen?
    val contentArea: FrameLayout
    var isBetweenOnResumeAndOnPause: Boolean
    var useCaseModelStore: UseCaseModelStore

    fun goToScreen(screen: Screen, saveToStack: Boolean = true) {
        val nowScreen = currentScreen

        if (nowScreen != null) {
            nowScreen.onPause()
            nowScreen.saveState()
        }

        if (saveToStack) {
            backStackAble.saveScreenToStackAndInitScreenId(screen)
        }

        screen.inflate(contentArea)
        val newLayout = screen.view
        contentArea.removeAllViews()
        contentArea.addView(newLayout)

        this.currentScreen = screen
        if (isBetweenOnResumeAndOnPause) {
            screen.onResume()
        }
    }

    fun onBackButtonPressed(goBackArguments: Bundle? = null) {
        if (currentScreen?.handleBackButton() == true)
            return

        val screen = backStackAble.retrieveNextScreenFromBackStackHistory(this)
        if (screen == null) {
            exit()
        } else {
            goToScreen(screen, saveToStack = false)
        }
    }

    fun reset()

    fun exit()

    interface BackStackAble {
        fun saveScreenToStackAndInitScreenId(nowScreen: Screen)
        fun retrieveNextScreenFromBackStackHistory(screenFlow: ScreenFlow): Screen?
    }
}