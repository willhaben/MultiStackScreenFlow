package at.willhaben.library

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import at.willhaben.library.backstack.BackStackManager
import at.willhaben.library.backstack.OnScreenPoppedFromStackListener
import at.willhaben.library.backstack.OnStackChangedListener
import at.willhaben.library.dialog.DialogCallback
import at.willhaben.library.state.CompressedBundle
import at.willhaben.library.usecasemodel.RetainedFragmentUseCaseModelHolder
import at.willhaben.library.usecasemodel.UseCaseModelStore
import java.io.Serializable
import java.util.*

abstract class MultiStackScreenFlowActivity : AppCompatActivity(), ScreenFlow,
    OnStackChangedListener, DialogCallback,
    OnScreenPoppedFromStackListener {

    override val activity: AppCompatActivity get() = this
    override var currentScreen: Screen? = null
    override val contentArea: FrameLayout by lazy { provideContentFrameForScreenFlow() }
    override var isBetweenOnResumeAndOnPause: Boolean = false
    override val backStackAble: ScreenFlow.BackStackAble
        get() = backStackManager

    private lateinit var backStackManager: BackStackManager
    override lateinit var useCaseModelStore: UseCaseModelStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialiseUseCaseModels()

        setContentView(getLayoutId())

        val bundle = savedInstanceState?.getParcelable<CompressedBundle>(COMPRESSED_STATE)?.bundle
        backStackManager = if (bundle != null) {
            bundle.getParcelable(EXTRA_BACK_STACK_MANAGER)
        }else {
            BackStackManager(stackConfigurator = provideStackConfigurator())
        }
        backStackManager.onStackChangedListener = this
        backStackManager.onScreenPoppedFromStackListener = this

        handleDeepLinkStackModifier(intent)
        goToScreen(backStackManager.getCurrentScreen(this), saveToStack = false)
    }

    private fun initialiseUseCaseModels() {
        if(supportFragmentManager.findFragmentByTag(USECASE_MODELS_FRAGMENT_TAG) == null)
            supportFragmentManager.beginTransaction().add(
                RetainedFragmentUseCaseModelHolder(),
                USECASE_MODELS_FRAGMENT_TAG
            ).commitNow()

        useCaseModelStore = supportFragmentManager.findFragmentByTag(USECASE_MODELS_FRAGMENT_TAG)!! as RetainedFragmentUseCaseModelHolder
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if(handleDeepLinkStackModifier(intent)) {
            goToScreen(backStackManager.getCurrentScreen(this), saveToStack = false)
        }
    }

    private fun handleDeepLinkStackModifier(intent: Intent) : Boolean{
        if(intent.hasExtra(EXTRA_DEEPLINK_STACK_MODIFIER)) {
            val modifier = intent.getSerializableExtra(EXTRA_DEEPLINK_STACK_MODIFIER) as StackModifier
            intent.removeExtra(EXTRA_DEEPLINK_STACK_MODIFIER)
            modifier.modifyBackStack(backStackManager)
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val nowScreen = currentScreen

        if(nowScreen != null) {
            val uncompressedBundle = Bundle()
            nowScreen.saveState()
            uncompressedBundle.putParcelable(EXTRA_BACK_STACK_MANAGER, backStackManager)
            outState.putParcelable(
                COMPRESSED_STATE,
                CompressedBundle(uncompressedBundle)
            )
        }

        super.onSaveInstanceState(outState)
    }

    override fun reset() {
        currentScreen?.onPause()
        backStackManager.reset()
        useCaseModelStore.clearAll()
        goToScreen(backStackManager.getCurrentScreen(this), saveToStack = false)
    }

    override fun exit() {
        finish()
    }

    protected abstract fun getLayoutId() : Int

    protected abstract fun provideContentFrameForScreenFlow(): FrameLayout

    protected abstract fun provideStackConfigurator() : StackConfigurator

    override fun onScreenStatePopped(screenUUID: UUID) {
        useCaseModelStore.clearScreenUseCaseModels(screenUUID)
    }

    protected fun getCurrentStack() : Int = backStackManager.getCurrentStackId()

    protected fun switchStack(stackId: Int) {
        currentScreen?.saveState()
        if(backStackManager.handleSwitchToSameStackAgain(stackId))
            return

        backStackManager.switchStack(stackId, true)
        goToScreen(backStackManager.getCurrentScreen(this), saveToStack = false)
    }

    override fun onResume() {
        super.onResume()
        currentScreen?.onResume()
        isBetweenOnResumeAndOnPause = true
    }

    override fun onPause() {
        super.onPause()
        currentScreen?.onPause()
        isBetweenOnResumeAndOnPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!isChangingConfigurations) {
            currentScreen?.onActivityDestroyedOnNonConfigChange()
            useCaseModelStore.clearAll()
        }
    }

    override fun onButtonClicked(buttonId: Int, dialogId: Int, extra: Bundle?) {
        currentScreen?.onButtonClicked(buttonId, dialogId, extra)
    }

    override fun onItemSelected(dialogId: Int, extra: Bundle?) {
        currentScreen?.onItemSelected(dialogId, extra)
    }

    override fun onCancel(dialogId: Int, extra: Bundle?) {
        currentScreen?.onCancel(dialogId, extra)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentScreen?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        onBackButtonPressed()
    }

    protected fun getStackCounts() : List<Int> = backStackManager.getStacksCounts()

    interface StackConfigurator : Serializable{
        fun size() : Int
        fun rootScreenFactory() : RootScreenFactory
    }

    interface RootScreenFactory : Serializable {
        fun provideRootScreen(flowStack: Int) : Class<out Screen>
    }

    companion object {
        private const val COMPRESSED_STATE = "COMPRESSED_STATE"
        private const val EXTRA_BACK_STACK_MANAGER = "EXTRA_BACK_STACK_MANAGER"
        private const val USECASE_MODELS_FRAGMENT_TAG = "USECASE_MODELS_FRAGMENT_TAG"

        const val EXTRA_DEEPLINK_STACK_MODIFIER = "EXTRA_DEEPLINK_STACK_MODIFIER"
    }
}