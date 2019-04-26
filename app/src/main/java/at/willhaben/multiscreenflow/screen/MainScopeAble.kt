package at.willhaben.multiscreenflow.screen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface MainScopeAble : CoroutineScope {

    val job : Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
}