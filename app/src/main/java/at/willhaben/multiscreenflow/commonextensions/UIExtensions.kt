package at.willhaben.multiscreenflow.commonextensions

import androidx.core.content.ContextCompat
import at.willhaben.multiscreenflow.App

fun color(id : Int) = ContextCompat.getColor(App.CONTEXT, id)

fun dp(dp : Float) : Float = App.CONTEXT.resources.displayMetrics.density * dp

fun dp(dp : Int) : Int = (App.CONTEXT.resources.displayMetrics.density * dp).toInt()