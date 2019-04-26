package at.willhaben.multiscreenflow.usecasemodel

import android.os.Parcelable
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface Question : Parcelable

interface Answer

interface UI : Parcelable

abstract class InteractionUseCaseModel<out Q : Question, in A : Answer, out U : UI> : BaseUseCaseModel() {

    abstract fun getQuestionChannel() : ReceiveChannel<Q>

    abstract fun getAnswerChannel() : SendChannel<A>

    abstract fun getUIChannel() : ReceiveChannel<U>
}