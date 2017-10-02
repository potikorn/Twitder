package com.example.potikorn.twitter

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    val READ_IMAGE: Int = 253
    val PICK_IMAGE_CODE: Int = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()

        img_profile.setOnClickListener {
            checkPermission()
        }

        btn_register.setOnClickListener {
            createUser()
        }

    }

    private fun createUser() {
        when {
            et_email.text.toString().isEmpty() -> et_email.error = "Error is Empty"
            et_password.text.toString().isEmpty() -> et_password.error = "Error is empty"
            et_password.text.toString().length < 8 -> et_password.error = "Password must contain at least 8 letter"
            else -> createNewUserToFirebase(et_email.text.toString().trim(), et_password.text.toString().trim())
        }
    }

    private fun createNewUserToFirebase(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_LONG).show()
                    saveImageInFirebase()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, "Failed to Login :  ${exception.message}", Toast.LENGTH_LONG).show()
                }
    }

    private fun saveImageInFirebase() {
        var currentUser = mAuth!!.currentUser
        val email: String = currentUser!!.email.toString()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://advancedandroiddevelopment-207.appspot.com")
        val imagePath = "${email.splitAssign()}.${getCurrentDateTime()}.jpg"
        val imageRef = storageRef.child("images/$imagePath")
        img_profile.isDrawingCacheEnabled = true
        img_profile.buildDrawingCache()

        val drawable = img_profile.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapShot ->

            val downloadUrl = taskSnapShot.downloadUrl.toString()
            myRef.child("Users").child(currentUser.uid).child("email").setValue(currentUser.email)
            myRef.child("Users").child(currentUser.uid).child("ProfileImage").setValue(downloadUrl)
            loadTweets()
        }

    }

    private fun loadTweets() {
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_IMAGE)
                return
            }
        }
        loadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_IMAGE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImage()
                } else {
                    Toast.makeText(this, "Cannot access your image", Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE && data != null) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val pictureAuth = cursor.getString(columnIndex)
            cursor.close()
            img_profile.setImageBitmap(BitmapFactory.decodeFile(pictureAuth))
        }
    }
}
