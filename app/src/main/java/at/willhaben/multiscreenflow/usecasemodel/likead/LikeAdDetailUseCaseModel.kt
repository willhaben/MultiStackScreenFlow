package at.willhaben.multiscreenflow.usecasemodel.likead

import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import at.willhaben.multiscreenflow.isLoggedIn
import at.willhaben.multiscreenflow.usecasemodel.InteractionUseCaseModel

class LikeAdDetailUseCaseModel(bundle: Bundle) :
    InteractionUseCaseModel<LikeAdState.QState, LikeAdState.AState, LikeAdState.UIState>() {

    private val questionChannel = Channel<LikeAdState.QState>()
    private val answerChannel = Channel<LikeAdState.AState>()
    private val uiChannel = Channel<LikeAdState.UIState>()

    override fun getQuestionChannel(): ReceiveChannel<LikeAdState.QState> {
        return questionChannel
    }

    override fun getAnswerChannel(): SendChannel<LikeAdState.AState> {
        return answerChannel
    }

    override fun getUIChannel(): ReceiveChannel<LikeAdState.UIState> {
        return uiChannel
    }

    private var holder: LikeAdStatesHolder = LikeAdStatesHolder()

    init {
        val holder: LikeAdStatesHolder? = bundle.getParcelable(stateName())
        if (holder != null) {
            launchFlow {
                when (holder.entryPoint()) {
                    LikeAdStatesHolder.EntryPoint.LOGIN -> proceedLogin()
                    LikeAdStatesHolder.EntryPoint.LOAD_FOLDERS -> loadList()
                    LikeAdStatesHolder.EntryPoint.CHOOSE_LIST -> proceedChosenList(holder.askWhichList!!.list)
                    LikeAdStatesHolder.EntryPoint.LIKE_TO_LIST -> likeList(holder.liking!!.index, holder.askWhichList!!.list)
                }
            }
        }
    }

    fun saveState(bundle: Bundle) {
        Log.d("UseCaseModel", holder.toString())
        holder.let {
            if(!it.isEmpty()) {
                bundle.putParcelable(stateName(), it)
            }
        }
    }

    private fun launchFlow(block: suspend () -> Unit) {
        guardLeakLaunch {
            resetHolder()
            try {
                block()
            } catch (e: Exception) {
                Log.e("UseCaseModel", e.message, e)
                uiChannel.send(LikeAdState.Error)
            }
            resetHolder()
        }
    }

    fun likeAdDetail() = launchFlow {
        start()
    }

    private fun resetHolder() {
        holder = LikeAdStatesHolder()
    }

    private suspend fun start() {
        if (!isLoggedIn()) {
            sendAskLogin()
            proceedLogin()
        } else {
            sendLoadList()
            loadList()
        }
    }

    private suspend fun sendAskLogin() {
        val askLogin = LikeAdState.AskLogin
        holder = holder.copy(askLogin = askLogin)
        questionChannel.send(askLogin)
    }

    //jump point 1
    private suspend fun proceedLogin() {
        if (answerChannel.receive() !is LikeAdState.LoggedIn) return

        sendLoadList()
        loadList()
    }

    private suspend fun sendLoadList() {
        val loadingList = LikeAdState.LoadingList
        holder = holder.copy(loadList = loadingList)
        uiChannel.send(loadingList)
    }

    //jump point 2
    private suspend fun loadList() {
        val list: Array<String> = loadFavoriteList()

        if (list.size == 1) {
            sendLiking()
            likeList(0, list)
        } else {
            sendAskWhichList(list)
            proceedChosenList(list)
        }
    }

    private suspend fun sendAskWhichList(list: Array<String>) {
        //optimise
        val whichList = LikeAdState.AskWhichList(list)
        holder = holder.copy(askWhichList = whichList)
        questionChannel.send(whichList)
    }

    private suspend fun sendLiking() {
        //optimise
        val liking = LikeAdState.Liking(0)
        holder = holder.copy(liking = liking)
        uiChannel.send(liking)
    }

    //jump point 3
    private suspend fun proceedChosenList(list: Array<String>) {
        val answer = answerChannel.receive() as? LikeAdState.ListChosen ?: return

        //optimise
        val liking = LikeAdState.Liking(answer.index)
        holder = holder.copy(liking = liking)
        uiChannel.send(liking)

        likeList(answer.index, list)
    }

    //jump point 4
    private suspend fun likeList(index: Int, favouriteList: Array<String>) {
        likeListIO(index, favouriteList)
        uiChannel.send(LikeAdState.Done)
    }

    private suspend fun loadFavoriteList() = withContext(IO) {
        delay(1000)
        arrayOf("FavouriteList1", "FavouriteList2", "FavouriteList3", "FavouriteList4", "FavouriteList5")
    }

    private suspend fun likeListIO(index: Int, favouriteList: Array<String>) = withContext(IO) {
        delay(2000)
        if (index < 0 || index >= favouriteList.size) {
            throw RuntimeException("Index invalid: $index")
        }

        if (index % 2 == 0) {
            throw RuntimeException("I fail sometimes...")
        }
    }
}