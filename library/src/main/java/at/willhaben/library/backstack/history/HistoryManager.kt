package at.willhaben.library.backstack.history

import android.os.Parcel
import android.os.Parcelable
import at.willhaben.library.HistoryStack
import java.util.*

class HistoryManager(private val stack: HistoryStack = HistoryStack()) : Parcelable {

    sealed class Action{
        class Finish : Action()
        class StackSwitch(val stackId: Int) : Action()
        class PopScreen : Action()
    }

    constructor(source: Parcel) : this(
        source.readParcelable<HistoryStack>(HistoryStack::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = dest.writeParcelable(stack, 0)

    fun onSwitchStack(currentStackId: Int, newStackId: Int, uuid: UUID) {
        if(currentStackId == newStackId)
            return

        fun handleToggleBehaviour() : Boolean {
            if(stack.size < 1) return false

            val history : History = stack[stack.size - 1]
            if(history is StackSwitch && history.oldStackId == newStackId) {
                stack.pop()
                return true
            }

            return false
        }

        if(handleToggleBehaviour())
            return

        stack.push(StackSwitch(currentStackId, newStackId, uuid))
    }

    fun onNewScreen(currentStackId: Int, lasScreenStateId : UUID) {
        stack.push(ScreenPushedToStack(currentStackId, lasScreenStateId))
    }

    fun retrieveNextAction(retriever: TopScreenStateIdFromStackRetriever): Action {
        sanitizeStack(retriever)

        if (stack.isEmpty())
            return Action.Finish()

        val history = stack.pop()
        return when (history) {
            is StackSwitch -> Action.StackSwitch(
                history.oldStackId
            )
            is ScreenPushedToStack -> Action.PopScreen()
        }
    }

    private fun sanitizeStack(retriever: TopScreenStateIdFromStackRetriever) {
        if(stack.isEmpty())
            return

        fun popAllUntilNextSwitchIsHitOrStackGetsEmpty() {
            while (stack.isNotEmpty() && stack.peek() is ScreenPushedToStack) {
                stack.pop() //pop all pushed screen history until next stackId switch is hit
            }
        }

        val history = stack.peek()
        when (history) {
            is StackSwitch -> {
                if(retriever.retrieveTopIdFromStack(history.oldStackId) != history.oldStackTopScreenID) {
                    stack.pop() //pop stackId switch
                    popAllUntilNextSwitchIsHitOrStackGetsEmpty()
                    sanitizeStack(retriever)
                }
            }
            is ScreenPushedToStack -> {
                if(retriever.hasStackOnlyOneEntry(history.fromStack)) {
                    popAllUntilNextSwitchIsHitOrStackGetsEmpty()
                    sanitizeStack(retriever)
                }
            }
        }
    }

    fun reset() {
        stack.clear()
    }

    interface TopScreenStateIdFromStackRetriever {
        fun retrieveTopIdFromStack(stackId: Int) : UUID
        fun hasStackOnlyOneEntry(stackId: Int) : Boolean
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HistoryManager> = object : Parcelable.Creator<HistoryManager> {
            override fun createFromParcel(source: Parcel): HistoryManager =
                HistoryManager(source)
            override fun newArray(size: Int): Array<HistoryManager?> = arrayOfNulls(size)
        }
    }
}