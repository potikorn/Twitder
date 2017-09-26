package com.example.potikorn.twitter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.potikorn.twitter.data.Post
import com.example.potikorn.twitter.data.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), TweetsAdapter.onClickListener {

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    //Dummies data
    var ListTweets = ArrayList<Ticket>()
    var adapter: TweetsAdapter? = null

    var myEmail: String? = null
    var userUID: String? = null

    var downloadURL: String? = null

    val PICK_IMAGE_CODE: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var b: Bundle = intent.extras
        myEmail = b.getString("email")
        userUID = b.getString("uid")

        //Dummies Data
        ListTweets.add(Ticket("0", "some text", "imgUrl", "add"))
//        ListTweets.add(Ticket("1", "some text", "imgUrl", "potikorn"))
//        ListTweets.add(Ticket("2", "some text", "imgUrl", "potikorn"))
//        ListTweets.add(Ticket("3", "some text", "imgUrl", "potikorn"))



        adapter = TweetsAdapter(this, ListTweets)
        adapter!!.onClick(this)


        rv_tweets.adapter = adapter
        loadPost()

    }

    private fun loadPost() {
        myRef.child("posts")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        try {
                            ListTweets.clear()
                            ListTweets.add(Ticket("0", "some text", "imgUrl", "add"))
                            var td = dataSnapshot!!.value as HashMap<String, Any>
                            for (key in td.keys) {
                                var post = td[key] as HashMap<String, Any>
                                ListTweets.add(Ticket(key,
                                        post["text"] as String,
                                        post["postImage"] as String?,
                                        post["userUID"] as String))
                            }
                            adapter!!.notifyDataSetChanged()
                        }catch (ex: Exception) {

                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })
    }



    private fun loadImage() {
        // TODO: load image
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    fun uploadImage(bitmap: Bitmap) {

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://advancedandroiddevelopment-207.appspot.com")
        val dateFormat = SimpleDateFormat("ddMMyyHHmmss")
        val date = Date()
        val imagePath = "${myEmail!!.splitAssign()}.${dateFormat.format(date)}.jpg"
        val imageRef = storageRef.child("imagePost/$imagePath")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext, "Failed to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener{ taskSnapShot ->

             downloadURL = taskSnapShot.downloadUrl.toString()

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
            uploadImage(BitmapFactory.decodeFile(pictureAuth))
        }
    }

    override fun onImageClick() {
        Toast.makeText(applicationContext, "ON IMAGE CLICK", Toast.LENGTH_LONG).show()
        loadImage()
    }

    override fun onPostClick(post: Post) {
        post.userUID = userUID
        post.postImage = downloadURL
        Toast.makeText(applicationContext, "ON POST CLICK", Toast.LENGTH_LONG).show()
        myRef.child("posts").push().setValue(post)
    }
}
