package com.example.trelloclone.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.trelloclone.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.please_wait_dialog.*

open class BaseActivity : AppCompatActivity() {

    private var doublePressedBack = false
    private lateinit var mProgressDialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressBarDialog(text:String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.please_wait_dialog)
        mProgressDialog.tv_textView.text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUser() :String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(doublePressedBack){
            super.onBackPressed()
            return
        }

        this.doublePressedBack = true
        Toast.makeText(applicationContext, "Please Press back to Exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.myLooper()!!).postDelayed(
            {
                doublePressedBack = false
            },2000)
    }

    fun showErrorSnackBar(message:String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this , R.color.snackbar_error_color))
        snackBar.show()
    }

}