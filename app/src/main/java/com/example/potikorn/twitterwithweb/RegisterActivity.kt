package com.example.potikorn.twitterwithweb

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class RegisterActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    val READ_IMAGE: Int = 253
    val PICK_IMAGE_CODE: Int = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        singInAnonymously()

        btn_register.setOnClickListener {
            btn_register.DISABLE()
            saveImageInFirebase()
        }

        img_avatar.setOnClickListener {
            checkPermission()
        }

    }

    private fun singInAnonymously() {
        mAuth.signInAnonymously().addOnCompleteListener(this, { task ->
            Log.d("LoginInfo", task.isSuccessful.toString())
            Log.d("LoginInfo", mAuth.currentUser!!.email.toString())
        })
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
        // TODO: load image
        var intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    private fun saveImageInFirebase() {
        val currentUser = mAuth.currentUser
        val email: String = currentUser!!.email.toString()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://fcmfirebase-4c687.appspot.com")
        val imagePath = "${email.splitAssign()}.${DateUtility.getcurrentDate(DateUtility.SIMPLE_DATE)}.jpg"
        val imageRef = storageRef.child("images/$imagePath")
        img_avatar.isDrawingCacheEnabled = true
        img_avatar.buildDrawingCache()

        val drawable = img_avatar.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext, "Fail to upload", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            val downloadUrl = taskSnapshot.downloadUrl.toString()
            // TODO : REGISTER TO DATABASE
            Log.d("DownloadURL:", downloadUrl)
            val url = "http://192.168.1.4/twitterwebserver/Register.php?" +
                    "first_name=${et_name.text}&" +
                    "email=${et_email.text}&" +
                    "password=${et_password.text}&" +
                    "picture_path=$downloadUrl"
            Log.d("BEST:", url)
//            val url="http://127.0.0.1/twitterwebserver/Register.php?first_name="+ et_name.text.toString() + "&email="+ et_email.text.toString() +  "&password="+ et_password.text.toString() +"&picture_path="+ downloadUrl

            MyAsyncTask().execute(url)
        }

    }

    // CALL HTTP REQUEST
    inner class MyAsyncTask : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            //Before task started
        }

        override fun doInBackground(vararg p0: String?): String {
            try {

                val url = URL(p0[0])

                val urlConnect = url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout = 7000

                var inString = convertStreamToString(urlConnect.inputStream)
                //Cannot access to ui
                publishProgress(inString)
            } catch (ex: Exception) {
                Log.d("BEST", ex.message)
            }


            return " "

        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                val json = JSONObject(values[0])
                Toast.makeText(applicationContext, json.getString("msg"), Toast.LENGTH_LONG).show()
                if (json.getString("msg") == "user is added to database") {
                    finish()
                } else {
                    btn_register.ENABLE()
                }


            } catch (ex: Exception) {
            }
        }

        override fun onPostExecute(result: String?) {

            //after task done
        }


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
            img_avatar.setImageBitmap(BitmapFactory.decodeFile(pictureAuth))
        }
    }
}
