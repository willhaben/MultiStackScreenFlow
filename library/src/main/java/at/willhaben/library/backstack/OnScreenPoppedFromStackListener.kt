package at.willhaben.library.backstack

import java.util.*

interface OnScreenPoppedFromStackListener {
    fun onScreenStatePopped(screenUUID: UUID)
}

interface OnStackChangedListener {
    fun onStackChanged(stackId: Int)
}