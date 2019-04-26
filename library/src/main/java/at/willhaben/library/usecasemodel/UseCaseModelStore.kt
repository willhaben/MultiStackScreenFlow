package at.willhaben.library.usecasemodel

import java.util.*

interface UseCaseModelStore {

    fun <T : UseCaseModel>getUseCaseModel(screenUUID: UUID, clazz : Class<out T>, factory : () -> T) : T

    fun clearScreenUseCaseModels(screenUUID: UUID)

    fun clearAll()
}