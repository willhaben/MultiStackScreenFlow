package at.willhaben.library

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.willhaben.library.dialog.DialogCallback
import at.willhaben.library.state.StateBundleBinderBase
import at.willhaben.library.state.StatePersistenceTrait
import at.willhaben.library.usecasemodel.UseCaseModel
import java.util.*

/**
 * Created by panmingk on 16/08/2017.
 */
abstract class Screen(protected val screenFlow: ScreenFlow) : StatePersistenceTrait,
    DialogCallback {

	override var stateBundle: Bundle = Bundle()
	override val stateVariables: MutableMap<String, StateBundleBinderBase<*>> = HashMap()

	lateinit var view : View
		private set

	val activity = screenFlow.activity
	private var viewState : SparseArray<Parcelable> by state(SparseArray())
    lateinit var screenUUID : UUID

	fun inflate(parent : ViewGroup) {
		view = inflateView(LayoutInflater.from(activity), parent)
		view.restoreHierarchyState(viewState)

		val bundle = stateBundle.getBundle(INIT_ARGUMENT)
		stateBundle.remove(INIT_ARGUMENT)
		afterInflate(bundle)
	}

	protected abstract fun inflateView(inflater : LayoutInflater, parent : ViewGroup) : View

	override fun onSaveState() {
		view.saveHierarchyState(viewState)
	}

	protected abstract fun afterInflate(initBundle : Bundle?)

	open fun onResume() {

	}

	open fun onPause() {

	}

	open fun onActivityDestroyedOnNonConfigChange() {

	}

	open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

	}

	override fun onButtonClicked(buttonId: Int, dialogId: Int, extra: Bundle?) {

	}

	override fun onCancel(dialogId: Int, extra: Bundle?) {

	}

	override fun onItemSelected(dialogId: Int, extra: Bundle?) {

	}

	open fun handleBackButton(): Boolean = false

	protected fun <T : UseCaseModel>getUseCaseModel(clazz : Class<out T>, factory : () -> T) : T {
		return screenFlow.useCaseModelStore.getUseCaseModel(screenUUID, clazz, factory)
	}

	companion object {
		const val INIT_ARGUMENT = "INIT_ARGUMENT"
	}
}
