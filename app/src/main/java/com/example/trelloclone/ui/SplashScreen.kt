package com.example.trelloclone.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.trelloclone.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if(FirebaseAuth.getInstance().currentUser!=null){
                val intent = Intent(applicationContext , MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(applicationContext , IntroActivity::class.java)
                startActivity(intent)
                finish()
            }

        },2000)
    }

}