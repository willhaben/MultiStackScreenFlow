package at.willhaben.multiscreenflow.usecasemodel.likead

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import at.willhaben.multiscreenflow.usecasemodel.Answer
import at.willhaben.multiscreenflow.usecasemodel.Question
import at.willhaben.multiscreenflow.usecasemodel.UI

sealed class LikeAdState {

    abstract class QState : LikeAdState(), Question
    abstract class AState : LikeAdState(), Answer
    abstract class UIState : LikeAdState(), UI

    @Parcelize
    object Initial : UIState()

    @Parcelize
    object AskLogin : QState()

    object Cancel : AState()

    object LoggedIn : AState()

    @Parcelize
    class AskWhichList(val list : Array<String>) : QState()

    class ListChosen(val index : Int) : AState()

    @Parcelize
    object LoadingList : UIState()

    @Parcelize
    class Liking(val index: Int) : UIState()

    @Parcelize
    object Done : UIState()

    @Parcelize
    object Error : UIState()

}

@Parcelize
data class LikeAdStatesHolder(val askLogin : LikeAdState.AskLogin? = null,
                              val loadList : LikeAdState.LoadingList? = null,
                              val askWhichList: LikeAdState.AskWhichList? = null,
                              val liking : LikeAdState.Liking? = null) : Parcelable {
    enum class EntryPoint{
        LOGIN, LOAD_FOLDERS, CHOOSE_LIST, LIKE_TO_LIST;
    }

    fun isEmpty() : Boolean {
        return askLogin != null && loadList != null && askWhichList != null && liking != null
    }

    fun entryPoint() : EntryPoint {
        return if(askWhichList != null && liking != null) {
            EntryPoint.LIKE_TO_LIST
        } else if(askWhichList != null) {
            EntryPoint.CHOOSE_LIST
        } else if(loadList != null) {
            EntryPoint.LOAD_FOLDERS
        } else if(askLogin != null) {
            EntryPoint.LOGIN
        } else {
            throw IllegalStateException("This RestoreHolder is not valid. ${toString()}")
        }
    }
}

