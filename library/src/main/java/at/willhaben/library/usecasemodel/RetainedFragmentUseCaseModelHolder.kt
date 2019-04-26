package at.willhaben.library.usecasemodel

import androidx.fragment.app.Fragment
import java.util.*
import kotlin.collections.HashMap

class RetainedFragmentUseCaseModelHolder : Fragment(), UseCaseModelStore {

    init {
        retainInstance = true
    }

    private val screenUseCaseModels = HashMap<UUID, Map<String, UseCaseModel>>()

    override fun <T : UseCaseModel> getUseCaseModel(screenUUID: UUID, clazz: Class<out T>, factory: () -> T): T {
        val map : MutableMap<String, UseCaseModel> = screenUseCaseModels.getOrPut(screenUUID) { HashMap() } as MutableMap<String, UseCaseModel>
        val canonicalName = clazz.canonicalName ?: throw IllegalArgumentException("class mustn't be anonymous")
        return map.getOrPut(canonicalName) { factory.invoke() } as T
    }

    override fun clearScreenUseCaseModels(screenUUID: UUID) {
        screenUseCaseModels[screenUUID]?.values?.forEach { it.onCleared() }
        screenUseCaseModels.remove(screenUUID)
    }

    override fun clearAll() {
        screenUseCaseModels.values.forEach { m ->
            m.values.forEach {
                it.onCleared()
            }
        }
        screenUseCaseModels.clear()
    }
}