package at.willhaben.library

import android.os.Bundle
import java.io.Serializable

interface StackModifiable {
    fun changeCurrentStack(stackId: Int)
    fun popAllAndCreateRootIfNecessary()
    fun pushToStack(clazz : Class<out Screen>, initBundle : Bundle)
    fun passBundleToRoot(initBundle: Bundle)
}

interface StackModifier : Serializable{
    fun modifyBackStack(stackModifiable: StackModifiable)
}