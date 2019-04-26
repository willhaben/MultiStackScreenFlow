package at.willhaben.multiscreenflow.screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.willhaben.library.Screen
import at.willhaben.library.ScreenFlow
import kotlinx.android.synthetic.main.screen_aza.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import at.willhaben.multiscreenflow.LifeCycleJob
import at.willhaben.multiscreenflow.R
import at.willhaben.multiscreenflow.commonextensions.color
import at.willhaben.multiscreenflow.commonextensions.dp
import at.willhaben.multiscreenflow.commonextensions.rebuild
import at.willhaben.multiscreenflow.domain.AzaData
import at.willhaben.multiscreenflow.usecasemodel.aza.AzaUseCaseModel
import at.willhaben.multiscreenflow.usecasemodel.aza.UMStates
import org.jetbrains.anko.*

class AzaScreen(screenFlow: ScreenFlow) : Screen(screenFlow), MainScopeAble, AzaAdapter.OnItemClickedListener {

    override val job: Job by LifeCycleJob()

    private lateinit var useCaseModel: AzaUseCaseModel
    private var umState : UMStates by state(UMStates.Initial)

    override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.screen_aza, parent, false)
        view.listScreenAza.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            isSaveEnabled = false
        }
        buildLoadingSkeleton(view)
        return view
    }

    private fun buildLoadingSkeleton(rootView : View) {
        val color = color(R.color.shimmer_background_color)
        val shimmerLayout = rootView.loadingSkeletonScreenAza
        shimmerLayout.rebuild {
            verticalLayout {
                for(i in 0..5) {
                    view {
                        setBackgroundColor(color)
                    }.lparams(dp(120), dp(30)) {
                        leftMargin = dp(8)
                        topMargin = dp(16)
                    }

                    view {
                        setBackgroundColor(color)
                    }.lparams(dp(300), dp(20)) {
                        leftMargin = dp(8)
                        topMargin = dp(8)
                    }
                }
            }
        }
    }

    override fun afterInflate(initBundle: Bundle?) {
        //process bundle

        //setup view models first
        initAzaUseCaseModel()
        if(umState == UMStates.Initial) {
            loadData()
        }

        view.btnRetryScreenAza.setOnClickListener {
            loadData()
            setUIAccordingToState()
        }
        setUIAccordingToState()
    }

    private fun loadData() {
        useCaseModel.fetchData()
    }

    private fun initAzaUseCaseModel() {
        useCaseModel = getUseCaseModel(AzaUseCaseModel::class.java) {
            AzaUseCaseModel(stateBundle)
        }
    }

    override fun onSaveState() {
        super.onSaveState()
        useCaseModel.saveState(stateBundle)
    }

    override fun onResume() {
        launch {
            for(state in useCaseModel.getUIChannel()) {
                this@AzaScreen.umState = state
                setUIAccordingToState()
            }
        }
    }

    override fun onPause() {
        job.cancel()
    }

    override fun onItemClicked(index: Int) {
    }

    private fun setUIAccordingToState() {
        val state = umState
        when(state) {
            is UMStates.Loaded -> {
                view.loadingSkeletonScreenAza.stopShimmerAnimation()
                view.loadingSkeletonScreenAza.visibility = GONE
                view.listScreenAza.visibility = View.VISIBLE
                view.errorContainerScreenAza.visibility = GONE
                view.listScreenAza.adapter = AzaAdapter(state.azaDatas, this)
            }
            is UMStates.Error -> {
                view.listScreenAza.visibility = GONE
                view.loadingSkeletonScreenAza.visibility = View.GONE
                view.errorContainerScreenAza.visibility = VISIBLE
            }
            is UMStates.Loading -> {
                view.listScreenAza.visibility = GONE
                view.loadingSkeletonScreenAza.visibility = View.VISIBLE
                view.errorContainerScreenAza.visibility = GONE
                view.loadingSkeletonScreenAza.startShimmerAnimation()
            }
        }
    }
}

private class AzaAdapter(private val list: List<AzaData>, private val listener : OnItemClickedListener) : RecyclerView.Adapter<AzaAdapter.AzaDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzaDataViewHolder {
        return AzaDataViewHolder(ListUI().createView(AnkoContext.create(parent.context, parent)))
    }

    override fun onBindViewHolder(holder: AzaDataViewHolder, position: Int) {
        val azaData = list[position]
        holder.tvTitle.text = azaData.title
        holder.tvYear.text = azaData.description

        holder.itemView.setOnClickListener {
            listener.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class AzaDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(ListUI.tvTitleId)
        var tvYear: TextView = itemView.findViewById(ListUI.tvYearId)
    }

    interface OnItemClickedListener {
        fun onItemClicked(index : Int)
    }
}

private class ListUI : AnkoComponent<ViewGroup> {

    companion object {
        const val tvTitleId = 1
        const val tvYearId = 2
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui){
        verticalLayout {
            lparams(matchParent, wrapContent)
            padding = dip(8)

            textView {
                id = tvTitleId
                layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
                textSize = 16f // <- it is sp, no worries
                textColor = Color.BLACK
            }

            textView {
                id = tvYearId
                layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
                textSize = 14f
            }
        }
    }
}