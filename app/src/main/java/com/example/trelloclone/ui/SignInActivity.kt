package com.example.trelloclone.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloclone.R
import com.example.trelloclone.firebase.FireStoreClass
import com.example.trelloclone.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setUpActionBar()

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_sign_in.setOnClickListener {
            signInUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            toolbar_sign_in_activity.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(applicationContext,MainActivity::class.java))
        finish()
    }

    private fun signInUser(){
        val email = et_email_sign_in.text.toString().trim{it <= ' '}
        val password = et_password_sign_in.text.toString().trim{it <= ' '}

        if(validateForm(email,password)){
            showProgressBarDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful){
                    FireStoreClass().loadUserData(this)
                    Toast.makeText(applicationContext, "Sign In Successfully", Toast.LENGTH_SHORT).show()
                }
                else{
                    hideProgressDialog()
                    Toast.makeText(applicationContext, "Some Error Occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    }
    private fun validateForm(email:String , password:String):Boolean{
        return when{
            TextUtils.isEmpty(email) ->
            {
                showErrorSnackBar("Please Enter an Email-address")
                false
            }

            TextUtils.isEmpty(password) ->
            {
                showErrorSnackBar("Please Enter Password")
                false
            }
            else ->
            {
                true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext , IntroActivity::class.java))
        finish()
    }
}