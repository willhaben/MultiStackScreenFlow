package at.willhaben.multiscreenflow.usecasemodel

import at.willhaben.library.usecasemodel.UseCaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseUseCaseModel : UseCaseModel, CoroutineScope{

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        job.cancel()
    }

    private var aCoroutineRunning = false

    protected fun guardLeakLaunch(block: suspend () -> Unit) {
        launch {
            if (aCoroutineRunning) {
                throw IllegalStateException(
                    "a new couroutine can't be launched, since another is still running, make sure that the" +
                            " flow is canceled before")
            }

            aCoroutineRunning = true
            block()
            aCoroutineRunning = false
        }
    }

    protected fun stateName(): String = "STATE_FROM_${javaClass.name}"
}