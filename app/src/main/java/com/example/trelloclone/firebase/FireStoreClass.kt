package com.example.trelloclone.firebase

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.trelloclone.model.User
import com.example.trelloclone.ui.MainActivity
import com.example.trelloclone.ui.ProfileActivity
import com.example.trelloclone.ui.SignInActivity
import com.example.trelloclone.ui.SignUpActivity
import com.example.trelloclone.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser( activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(USERS).document(getCurrentUserId())
            .set(userInfo , SetOptions.merge()).addOnSuccessListener {
                activity.userRegister()
            }.addOnFailureListener {
                Toast.makeText(activity, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUserProfileData(activity:ProfileActivity , userHashMap: HashMap<String , Any>){
        mFireStore.collection(USERS).document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(activity, "Profile Update Successfully", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e ->
                activity.hideProgressDialog()
                Toast.makeText(activity, "Error While Updating Profile Details!", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity){
        mFireStore.collection(USERS).document(getCurrentUserId())
            .get().addOnSuccessListener {
                document ->
                val loggedInUser = document.toObject(User::class.java)
                when(activity){
                    is SignInActivity ->
                    {
                        if(loggedInUser != null)
                            activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->
                    {
                        activity.updateNavigationProfile(loggedInUser!!)
                    }

                    is ProfileActivity ->
                    {
                        activity.setProfile(loggedInUser!!)
                    }
                }

            }.addOnFailureListener {
                when(activity){

                    is SignInActivity ->
                    {
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->
                    {
                        activity.hideProgressDialog()
                    }
                }

            }
    }

    private fun getCurrentUserId() :String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}