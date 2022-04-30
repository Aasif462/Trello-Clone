package com.example.trelloclone.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloclone.R
import com.example.trelloclone.firebase.FireStoreClass
import com.example.trelloclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()

        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            toolbar_sign_up_activity.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun userRegister(){
        hideProgressDialog()
        Toast.makeText(
            applicationContext,
            "Registration Successful",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(applicationContext,MainActivity::class.java))
        finish()
    }

    private fun registerUser(){
        val name = et_name.text.toString().trim{it <= ' '}
        val email = et_email.text.toString().trim{it <= ' '}
        val password = et_password.text.toString().trim{it <= ' '}

        if(validateForm(name , email , password)){
            showProgressBarDialog(resources.getString(R.string.please_wait))
            auth.createUserWithEmailAndPassword(email , password).addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    val firebaseUser:FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid, name , registeredEmail)
                    FireStoreClass().registerUser(this , user)
                }
                else{
                    Toast.makeText(
                        applicationContext,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

private fun validateForm(name:String , email:String , password:String):Boolean{
    return when{
        TextUtils.isEmpty(name) ->
        {
            showErrorSnackBar("Please Enter Name")
            false
        }

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