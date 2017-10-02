package com.example.potikorn.twitterwithweb

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
//            btn_login.DISABLE()
            val url = "http://192.168.1.4/twitterwebserver/Login.php?" +
                    "email=${et_email.text}&" +
                    "password=${et_password.text}"
            MyAsyncTask().execute(url)

        }

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
                if (json.getString("msg") == "pass login") {
                    val userInfo = JSONArray(json.getString("info"))
                    val userCredentails = userInfo.getJSONObject(0)

                    val user_id = userCredentails.getString("user_id")
                    Toast.makeText(applicationContext, userCredentails.getString("first_name"), Toast.LENGTH_LONG).show()
                    val saveSettings = SaveSettings(applicationContext)
                    saveSettings.saveSettings(user_id)
                     finish()
                } else {
                    Toast.makeText(applicationContext, json.getString("msg"), Toast.LENGTH_LONG).show()
                }


            } catch (ex: Exception) {
            }
        }

        override fun onPostExecute(result: String?) {

            //after task done
        }


    }

}
