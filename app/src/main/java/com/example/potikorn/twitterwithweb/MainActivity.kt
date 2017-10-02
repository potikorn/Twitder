package com.example.potikorn.twitterwithweb

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import com.example.potikorn.twitter.TweetsAdapter
import com.example.potikorn.twitter.data.Post
import com.example.potikorn.twitter.data.Ticket
import com.example.potikorn.twitterwithweb.DateUtility.Companion.getCurrentDateTime
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

class MainActivity : AppCompatActivity(), TweetsAdapter.OnItemClickListener {

    var listOfTweets = ArrayList<Ticket>()
    lateinit var adapter: TweetsAdapter

    var myEmail: String? = null
    var userUID: String? = null

    var downloadURL: String? = "noImage"

    val PICK_IMAGE_CODE: Int = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val saveSettings = SaveSettings(this)
        saveSettings.loadSettings()

//        Log.d("BEST", SaveSettings.userID)

        adapter = TweetsAdapter(this, listOfTweets)
        adapter.onClick(this)

        rv_tweets.layoutManager = LinearLayoutManager(this@MainActivity)
        rv_tweets.adapter = adapter


        searchInDatabase("%", 0)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchView: SearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchInDatabase(query, 0)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.home -> {
                    searchInDatabase("%", 0)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchInDatabase(searchText: String, startFrom: Int) {
        val url = "http://192.168.1.4/twitterwebserver/TweetList.php?" +
                "query=$searchText&" +
                "startFrom=$startFrom&" +
                "op=3"
        MyAsyncTask().execute(url)
    }


    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    private fun uploadImage(bitmap: Bitmap) {
        listOfTweets.add(0, Ticket("0", "him", "url", "", "", "", 1))
        rv_tweets.adapter.notifyDataSetChanged()

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://fcmfirebase-4c687.appspot.com")
        val imagePath = "${SaveSettings.userID}.${getCurrentDateTime()}.jpg"
        val imageRef = storageRef.child("imagePost/$imagePath")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed to upload", Toast.LENGTH_LONG).show()
            listOfTweets.removeAt(0)
            rv_tweets.adapter.notifyDataSetChanged()
        }.addOnSuccessListener { taskSnapShot ->
            downloadURL = taskSnapShot.downloadUrl.toString()
            listOfTweets.removeAt(0)
            rv_tweets.adapter.notifyDataSetChanged()
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
        loadImage()
    }

    override fun onPostClick(post: Post) {
        listOfTweets.add(0, Ticket("0", "him", "url", "loading", "", "", 1))
        rv_tweets.adapter.notifyDataSetChanged()

        // CALL HTTP REQUEST
        post.userUID = SaveSettings.userID
        post.postImage = URLEncoder.encode(downloadURL, "utf-8")
        val url = "http://192.168.1.4/twitterwebserver/TweetAdd.php?" +
                "user_id=${post.userUID}&" +
                "tweet_text=${post.text}&" +
                "tweet_pix=${post.postImage}"

        MyAsyncTask().execute(url)
    }

    // CALL HTTP REQUEST
    inner class MyAsyncTask : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            showProgressDialog(this@MainActivity, "")
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
            Log.d("BEST", "PROGRESS UPDATE")
            Log.d("BEST", values[0].toString())

            try {
                val json = JSONObject(values[0])
                Toast.makeText(applicationContext, json.getString("msg"), Toast.LENGTH_LONG).show()
                if (json.getString("msg") == "New tweet added to database") {
                    Log.d("BEST", "NEW TWEET ADD")
                    downloadURL = "noImage"
                    listOfTweets.removeAt(0)
                    rv_tweets.adapter.notifyDataSetChanged()
                } else if (json.getString("msg") == "has tweet") {
                    Log.d("BEST", "HAS TWEET")
                    listOfTweets.clear()
                    listOfTweets.add(0, Ticket("0", "him", "url", "add", "", "", 2))

                    val tweets = JSONArray(json.getString("info"))
                    for (i in 0 until tweets.length()) {
                        val ticketModel = Ticket()
                        val singleTweet = tweets.getJSONObject(i)
                        ticketModel.tweetID = singleTweet.getString("tweet_id")
                        ticketModel.tweetText = singleTweet.getString("tweet_text")
                        ticketModel.tweetImageUrl = singleTweet.getString("tweet_pix")
                        ticketModel.date = singleTweet.getString("tweet_date")
                        ticketModel.personName = singleTweet.getString("first_name")
                        ticketModel.personImage = singleTweet.getString("picture_path")
                        ticketModel.TYPE = 3
                        listOfTweets.add(ticketModel)
                    }
                } else if (json.getString("msg") == "no tweet") {
                    Log.d("BEST", "NO TWEET")
                    listOfTweets.clear()
                    listOfTweets.add(0, Ticket("0", "him", "url", "add", "", "", 2))
                }

                rv_tweets.adapter.notifyDataSetChanged()

            } catch (ex: Exception) {
                Log.d("BEST", ex.message)
            }
        }

        override fun onPostExecute(result: String?) {
            dismissProgressDialog()
            //after task done
        }


    }
}
