package at.willhaben.multiscreenflow

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        btnLoginActivity.setOnClickListener {
            logIn()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}