package at.willhaben.multiscreenflow.deeplink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.willhaben.library.MultiStackScreenFlowActivity.Companion.EXTRA_DEEPLINK_STACK_MODIFIER
import at.willhaben.multiscreenflow.MainActivity
import at.willhaben.multiscreenflow.deeplink.modifiers.AdDetailModifier
import at.willhaben.multiscreenflow.deeplink.modifiers.MyAdsModifier
import at.willhaben.multiscreenflow.deeplink.modifiers.SearchEntranceModifier

class DeepLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent : Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        val pathSegments = appLinkData?.pathSegments

        if (Intent.ACTION_VIEW == appLinkAction && pathSegments != null) {
            if (pathSegments.size == 1) {
                handleOneSegment(pathSegments)
            } else if (pathSegments.size == 2) {
                handleTwoSegment(pathSegments)
            }
        }

        finish()
    }

    private fun handleOneSegment(pathSegments: List<String>) {
        val segment = pathSegments.first()
        if (segment == "myAds") {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_DEEPLINK_STACK_MODIFIER, MyAdsModifier())
            startActivity(intent)
        }
    }

    private fun handleTwoSegment(pathSegments: List<String>) {
        val (segment1, segment2) = pathSegments
        if (segment1 == "adDetail") {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_DEEPLINK_STACK_MODIFIER, AdDetailModifier(segment2))
            startActivity(intent)
        } else if (segment1 == "search") {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_DEEPLINK_STACK_MODIFIER, SearchEntranceModifier(segment2))
            startActivity(intent)
        }
    }
}