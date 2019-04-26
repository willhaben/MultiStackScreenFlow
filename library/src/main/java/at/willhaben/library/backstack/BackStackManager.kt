package at.willhaben.library.backstack

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import at.willhaben.library.*
import at.willhaben.library.backstack.history.HistoryManager
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

private fun createStacks(count: Int): SparseArray<ScreenStateStack> {
    if (count < 1 || count > 5) {
        throw IllegalArgumentException("Stack count must be between 1 and 5 (inclusive)!")
    }
    return SparseArray<ScreenStateStack>().apply {
        for (i in 0 until count) {
            put(i, ScreenStateStack())
        }
    }
}

class BackStackManager(
    private val stackConfigurator: MultiStackScreenFlowActivity.StackConfigurator,
    private var currentStackId: Int = 0,
    private val stacks: SparseArray<ScreenStateStack> = createStacks(
        stackConfigurator.size()
    ),
    private val historyManager: HistoryManager = HistoryManager()
) : Parcelable, ScreenFlow.BackStackAble, HistoryManager.TopScreenStateIdFromStackRetriever,
    StackModifiable {

    override fun changeCurrentStack(stackId: Int) {
        switchStack(stackId, true)
    }

    override fun popAllAndCreateRootIfNecessary() {
        val stack = stacks[currentStackId]!!
        if (stack.isEmpty()) {
            createRootForEmptyStack()
        } else {
            popUntilRoot(stack)
        }
    }

    override fun pushToStack(clazz: Class<out Screen>, initBundle: Bundle) {
        val bundle = Bundle()
        bundle.putBundle(Screen.INIT_ARGUMENT, initBundle)
        pushScreenStateToCurrentStack(ScreenState(clazz, bundle))
    }

    override fun passBundleToRoot(initBundle: Bundle) {
        val stack = stacks[currentStackId]!!
        if (stack.isNotEmpty()) {
            stack.lastElement().state.putBundle(Screen.INIT_ARGUMENT, initBundle)
        }
        createRootForEmptyStack(initBundle)
    }

    constructor(source: Parcel) : this(
        source.readSerializable() as MultiStackScreenFlowActivity.StackConfigurator,
        source.readInt(),
        source.readSparseArray(BackStackManager::class.java.classLoader) as SparseArray<ScreenStateStack>,
        source.readParcelable<HistoryManager>(HistoryManager::class.java.classLoader)
    )

    @Volatile
    var onStackChangedListener: OnStackChangedListener? = null

    @Volatile
    var onScreenPoppedFromStackListener: OnScreenPoppedFromStackListener? = null

    fun getCurrentStackId(): Int = currentStackId

    private fun createRootForEmptyStack(initBundle: Bundle? = null) {
        val stack = stacks[currentStackId]!!
        if (stack.isEmpty()) {
            val bundle = Bundle()
            if (initBundle != null) {
                bundle.putBundle(Screen.INIT_ARGUMENT, bundle)
            }
            stack.push(
                ScreenState(
                    stackConfigurator.rootScreenFactory().provideRootScreen(
                        currentStackId
                    )
                )
            )
        }
    }

    fun getCurrentScreen(screenFlow: ScreenFlow): Screen {
        createRootForEmptyStack()
        val stack = stacks[currentStackId]!!
        val screenState: ScreenState = stack.peek()
        val screen = screenState.clazz.getConstructor(ScreenFlow::class.java).newInstance(screenFlow)
        screen.screenUUID = screenState.uuid
        screen.restoreState(screenState.state)
        return screen
    }

    fun handleSwitchToSameStackAgain(stackId: Int): Boolean {
        if (currentStackId == stackId) {
            val stack = stacks[currentStackId]!!
            if (stack.size == 1)
                return true

            popUntilRoot(stack)
        }

        return false
    }

    private fun popUntilRoot(stack: ScreenStateStack) {
        while (stack.size > 1) {
            val state = stack.pop()
            onScreenPoppedFromStackListener?.onScreenStatePopped(state.uuid)
        }
    }

    fun switchStack(stackId: Int, saveToHistory: Boolean) {
        val stack = stacks[currentStackId]!!
        if (saveToHistory && stack.isNotEmpty()) {
            historyManager.onSwitchStack(currentStackId, stackId, stack.peek().uuid)
        }

        currentStackId = stackId
        onStackChangedListener?.onStackChanged(stackId)
    }

    override fun saveScreenToStackAndInitScreenId(nowScreen: Screen) {
        val screenState = ScreenState(nowScreen::class.java, nowScreen.stateBundle)
        nowScreen.screenUUID = screenState.uuid
        pushScreenStateToCurrentStack(screenState)
    }

    private fun pushScreenStateToCurrentStack(screenState: ScreenState) {
        val stack = stacks[currentStackId]!!
        if (stack.isEmpty())
            throw java.lang.IllegalStateException("You can't save a new screen to history from an empty fromStack! The fromStack must have at least one screen!")
        val lastScreenStateId = stack.peek().uuid
        historyManager.onNewScreen(currentStackId, lastScreenStateId)

        stacks[currentStackId]!!.push(screenState)
    }

    override fun retrieveNextScreenFromBackStackHistory(screenFlow: ScreenFlow): Screen? {
        val history = historyManager.retrieveNextAction(this)
        return when (history) {
            is HistoryManager.Action.PopScreen -> {
                val stack = stacks[currentStackId]!!
                if (stack.size <= 1) {
                    throw IllegalStateException("something is wrong, you can't pop a screen from from stack which has a size of 1!")
                }
                val state = stack.pop()
                onScreenPoppedFromStackListener?.onScreenStatePopped(state.uuid)
                return getCurrentScreen(screenFlow)
            }
            is HistoryManager.Action.StackSwitch -> {
                switchStack(history.stackId, saveToHistory = false)
                getCurrentScreen(screenFlow)
            }
            else -> null
        }
    }

    override fun retrieveTopIdFromStack(stackId: Int): UUID {
        val stack = stacks[stackId]!!
        if (stack.isEmpty()) {
            throw IllegalStateException("something is wrong, the logic shouldn't go there if stackId is empty!")
        }

        return stack.peek().uuid
    }

    override fun hasStackOnlyOneEntry(stackId: Int): Boolean {
        return stacks[stackId]!!.size == 1
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(stackConfigurator)
        writeInt(currentStackId)
        writeSparseArray(stacks as SparseArray<Any>)
        writeParcelable(historyManager, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BackStackManager> = object : Parcelable.Creator<BackStackManager> {
            override fun createFromParcel(source: Parcel): BackStackManager =
                BackStackManager(source)
            override fun newArray(size: Int): Array<BackStackManager?> = arrayOfNulls(size)
        }
    }

    @Deprecated("only use for prototype")
    fun getStacksCounts(): List<Int> {
        val list = ArrayList<Int>()
        for (i in 0 until stacks.size()) {
            list.add(stacks.valueAt(i)!!.size)
        }
        return list
    }

    fun reset() {
        for (i in 0 until stacks.size()) {
            stacks.valueAt(i).clear()
        }
        historyManager.reset()
        switchStack(0, saveToHistory = false)
    }
}