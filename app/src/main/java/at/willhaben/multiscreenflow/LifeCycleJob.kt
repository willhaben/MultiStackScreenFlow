package at.willhaben.multiscreenflow

import kotlinx.coroutines.Job
import kotlin.reflect.KProperty

class LifeCycleJob {

    private var job = Job()

    operator fun getValue(thisRef: Any?, property: KProperty<*>) : Job {
        if(job.isCancelled)
            job = Job()
        return job
    }
}