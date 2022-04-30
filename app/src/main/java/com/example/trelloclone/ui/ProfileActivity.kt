package com.example.trelloclone.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.firebase.FireStoreClass
import com.example.trelloclone.model.User
import com.example.trelloclone.util.Constants.IMAGE
import com.example.trelloclone.util.Constants.NAME
import com.example.trelloclone.util.Constants.PHONE
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException

class ProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private var profileImageUrl:String = ""
    private lateinit var mUserDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setUpActionBar()
        FireStoreClass().loadUserData(this)

        profile_image.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select Photo From Gallery", "Capture Photo From Camara")
            dialog.setItems(pictureDialogItems) { dialog, which ->
                when (which) {
                    0 -> choosePhotoFromGallery()
                    2 -> choosePhotoFromCamara()
                }
            }
            dialog.show()
        }

        profile_update.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }
            else
            {
                showProgressBarDialog(resources.getString(R.string.please_wait))
            }

        }
    }

    fun setProfile(user: User){

        mUserDetails = user
        Glide.with(this)
            .load(user.image)
            .placeholder(R.drawable.ic_user_place_holder)
            .into(profile_image)

        profile_name.setText(user.name)
        profile_email.setText(user.email)
        profile_mobile.setText(user.mobileNo.toString())
    }

    private fun uploadUserImage(){
        showProgressBarDialog(resources.getString(R.string.please_wait))


        val srf = FirebaseStorage.getInstance().reference.child("profile_image").child(
            "USER_IMAGE"+System.currentTimeMillis()
                    +getFileExtension(mSelectedImageFileUri))

        srf.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapShot ->
                    taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    profileImageUrl = uri.toString()
                    updateUserProfileData()

            }
                .addOnFailureListener{
                    exception ->
                    Toast.makeText(applicationContext, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
        }


    }

    private fun getFileExtension(uri:Uri?) : String?{
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(contentResolver.getType(uri!!))
    }

     fun profileUpdateSuccess(){
        hideProgressDialog()
         setResult(RESULT_OK)
        finish()
    }

   private fun  updateUserProfileData(){
       val userHashMap = HashMap<String , Any>()
       var anyChangesMade = false
       if(profileImageUrl.isNotEmpty() && profileImageUrl != mUserDetails.image)
       {
           userHashMap[IMAGE] = profileImageUrl
           anyChangesMade = true
       }

       if(profile_name.text.toString() != mUserDetails.name){
           userHashMap[NAME] = profile_name.text.toString()
           anyChangesMade = true
       }

       if(profile_mobile.text.toString() != mUserDetails.mobileNo.toString()){
           userHashMap[PHONE] = profile_mobile.text.toString().toLong()
           anyChangesMade = true
       }

       if(anyChangesMade){
           FireStoreClass().updateUserProfileData(this , userHashMap)
       }
   }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)  {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    mSelectedImageFileUri = data.data!!
                    try {
                        // Here this is used to get an bitmap from URI
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, mSelectedImageFileUri)
//                        Log.e("Saved Image : ", "Path :: $saveImageToInternalStorage")

                        profile_image!!.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == CAMERA) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap // Bitmap from camera

                profile_image!!.setImageBitmap(thumbnail) // Set to the imageView.
            }
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    // Here after all the permission are granted launch the gallery to select and image.
                    if (report!!.areAllPermissionsGranted()) {

                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialog()
                }
            }).onSameThread()
            .check()
    }

    private fun showRationalDialog(){
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("It Looks Like You have turned off Permission required"+
                "For this Feature. It can be enabled Under the "+"Application Settings")
        dialog.setPositiveButton("GO TO SETTINGS"){
                dialog_,which ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",packageName ,null)
                intent.data = uri
                startActivity(intent)
            }
            catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }
        dialog.setNegativeButton("Cancel"){
                dialog,_ ->
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun choosePhotoFromCamara() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Here after all the permission are granted launch the CAMERA to capture an image.
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialog()
                }
            }).onSameThread()
            .check()
    }

    companion object
    {
        const val CAMERA = 1
        const val GALLERY = 2
    }

    private fun setUpActionBar(){
        setSupportActionBar(profile_toolbar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        profile_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}