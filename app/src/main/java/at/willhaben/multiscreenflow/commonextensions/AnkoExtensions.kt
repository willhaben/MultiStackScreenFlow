package at.willhaben.multiscreenflow.commonextensions

import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext

inline fun ViewGroup.rebuild(init: AnkoContext<ViewGroup>.() -> Unit) {
    removeAllViews()
    AnkoContext.createDelegate(this).apply(init)
}