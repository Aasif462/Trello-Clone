package com.example.trelloclone.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.trelloclone.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_sign_in_intro.setOnClickListener {
            val intent = Intent(applicationContext , SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_sign_up_intro.setOnClickListener {
            val intent = Intent(applicationContext , SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}