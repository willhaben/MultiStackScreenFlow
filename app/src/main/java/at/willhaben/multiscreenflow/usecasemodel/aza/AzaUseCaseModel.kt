package at.willhaben.multiscreenflow.usecasemodel.aza

import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import at.willhaben.multiscreenflow.domain.AzaData
import at.willhaben.multiscreenflow.usecasemodel.BaseUseCaseModel

class AzaUseCaseModel(bundle: Bundle) : BaseUseCaseModel() {

    init {
        if(bundle.getBoolean(EXTRA_LOADING, false)) {
            launchFlow {
                load()
            }
        }
    }

    private val channel = Channel<UMStates>()
    private var isLoading = false

    fun getUIChannel() : ReceiveChannel<UMStates>{
        return channel
    }

    fun saveState(bundle : Bundle) {
        bundle.putBoolean(EXTRA_LOADING, isLoading)
    }

    fun fetchData() {
        launchFlow {
            channel.send(UMStates.Loading)
            load()
        }
    }

    private fun launchFlow(block : suspend () -> Unit) {
        launch {
            isLoading = true
            try {
                block()
            }catch (exception : RuntimeException) {
                channel.send(UMStates.Error)
            }
            isLoading = false
        }
    }

    private suspend fun load() {
        val azaDatas = AzaUseCase().execute()
        channel.send(UMStates.Loaded(azaDatas))
    }

    companion object {
        const val EXTRA_LOADING = "EXTRA_LOADING"
    }

    private class AzaUseCase {

        suspend fun execute(): List<AzaData> = withContext(Dispatchers.IO) {
            delay(2000)

            if (System.currentTimeMillis().toInt() % 3 == 0) {
                throw RuntimeException("I fail sometimes...")
            }

            delay(3000)
            ArrayList<AzaData>().apply {
                for (i in 0..6) {
                    add(AzaData("Title $i", "Long Description $i..."))
                }
            }
        }
    }
}