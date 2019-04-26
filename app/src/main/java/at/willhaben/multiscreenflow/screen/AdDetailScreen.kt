package at.willhaben.multiscreenflow.screen

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import at.willhaben.library.Screen
import at.willhaben.library.ScreenFlow
import kotlinx.android.synthetic.main.screen_addetail.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import at.willhaben.multiscreenflow.LifeCycleJob
import at.willhaben.multiscreenflow.LoginActivity
import at.willhaben.multiscreenflow.R
import at.willhaben.multiscreenflow.deeplink.modifiers.AdDetailModifier
import at.willhaben.multiscreenflow.dialog.SimpleChooserDialog
import at.willhaben.multiscreenflow.usecasemodel.likead.LikeAdDetailUseCaseModel
import at.willhaben.multiscreenflow.usecasemodel.likead.LikeAdState
import java.util.*

class AdDetailScreen(screenFlow: ScreenFlow) : Screen(screenFlow), MainScopeAble {

    override val job: Job by LifeCycleJob()

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.screen_addetail, parent, false)
    }

    private var liked by state(false)
    private var willhabenCode by state(Math.abs(Random().nextLong()))

    private var adDetailTitle by state("")

    private lateinit var useCaseModel: LikeAdDetailUseCaseModel

    private var uiState : LikeAdState.UIState by state(LikeAdState.Initial)

    override fun afterInflate(initBundle: Bundle?) {
        initLikeAdDetailUseCaseModel()

        view.btnScreenAddetailLike.setOnClickListener {
            useCaseModel.likeAdDetail()
        }

        view.btnScreenAddetailShowAnother.setOnClickListener {
            screenFlow.goToScreen(AdDetailScreen(screenFlow))
        }

        view.tvScreenAddetailID.text = "willhabencode : $willhabenCode"

        if (initBundle != null) {
            adDetailTitle = initBundle.getString(AdDetailModifier.EXTRA_ADDETAIL_TITLE) ?: ""
        }

        if (!adDetailTitle.isEmpty()) {
            view.tvScreenAdDetailTitle.text = adDetailTitle
            view.tvScreenAddetailID.text = "willhabencode : roflcopter code"
        }

        setUIAccordingUseCaseModelState()
    }

    private fun initLikeAdDetailUseCaseModel() {
        useCaseModel = getUseCaseModel(LikeAdDetailUseCaseModel::class.java) {
            LikeAdDetailUseCaseModel(stateBundle)
        }
    }

    override fun onResume() {
        launch {
            for (state in useCaseModel.getUIChannel()) {
                this@AdDetailScreen.uiState = state
                setUIAccordingUseCaseModelState()
            }
        }

        launch {
            for(question in useCaseModel.getQuestionChannel()) {
                when (question) {
                    is LikeAdState.AskLogin -> {
                        activity.startActivityForResult(Intent(activity, LoginActivity::class.java), EXTRA_REQUEST_LOGIN_ACTIVITY_CODE)
                    }

                    is LikeAdState.AskWhichList-> {
                        SimpleChooserDialog().apply {
                            arguments = Bundle().apply {
                                putInt(SimpleChooserDialog.ARG_ID, R.id.simple_chooser_dialog)
                                putStringArray(SimpleChooserDialog.ARG_STRING_LIST, question.list)
                            }
                        }.show(activity.supportFragmentManager, CHOOSER_DIALOG_TAG)
                    }
                }
            }
        }
    }

    override fun onPause() {
        job.cancel()
    }

    private fun setUIAccordingUseCaseModelState() {
        fun setLikedUI() {
            if (liked) {
                view.btnScreenAddetailLike.visibility = View.GONE
                view.tvScreenAddetailLiked.text = "Loaded"
            } else {
                view.tvScreenAddetailLiked.text = "Not Loaded Yet"
            }
        }

        val state = uiState
        when (state) {
            is LikeAdState.Liking, is LikeAdState.LoadingList  -> {
                view.pBarScreenAza.visibility = View.VISIBLE
                view.btnScreenAddetailLike.visibility = View.GONE
                view.tvScreenAddetailLiked.visibility = View.GONE
            }

            is LikeAdState.Done, is LikeAdState.Initial -> {
                if(state is LikeAdState.Done)
                    liked = true

                view.pBarScreenAza.visibility = View.GONE
                view.btnScreenAddetailLike.visibility = View.VISIBLE
                view.tvScreenAddetailLiked.visibility = View.VISIBLE
                setLikedUI()
            }

            is LikeAdState.Error -> {
                Toast.makeText(activity, "Failed to like ad", Toast.LENGTH_SHORT).show()
                view.pBarScreenAza.visibility = View.GONE
                view.btnScreenAddetailLike.visibility = View.VISIBLE
                view.tvScreenAddetailLiked.visibility = View.VISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EXTRA_REQUEST_LOGIN_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                launch {
                    useCaseModel.getAnswerChannel().send(LikeAdState.LoggedIn)
                }
            } else {
                cancelLikeAdFlow()
            }
        }
    }

    private fun cancelLikeAdFlow() {
        launch {
            uiState = LikeAdState.Initial
            useCaseModel.getAnswerChannel().send(LikeAdState.Cancel)
            setUIAccordingUseCaseModelState()
        }
    }

    override fun onItemSelected(dialogId: Int, extra: Bundle?) {
        if (dialogId == R.id.simple_chooser_dialog && extra?.getInt(SimpleChooserDialog.ARG_SELECTED_ITEM_INDEX, -1) != -1) {
            val whichList = extra?.getInt(SimpleChooserDialog.ARG_SELECTED_ITEM_INDEX) ?: return
            launch {
                useCaseModel.getAnswerChannel().send(LikeAdState.ListChosen(whichList))
            }
        }
    }

    override fun onCancel(dialogId: Int, extra: Bundle?) {
        if (dialogId == R.id.simple_chooser_dialog) {
            cancelLikeAdFlow()
        }
    }

    override fun onSaveState() {
        super.onSaveState()
        useCaseModel.saveState(stateBundle)
    }

    companion object {
        const val EXTRA_REQUEST_LOGIN_ACTIVITY_CODE = 10001
        const val CHOOSER_DIALOG_TAG = "CHOOSER_DIALOG_TAG"
    }
}