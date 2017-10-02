package com.example.potikorn.twitterwithweb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class SaveSettings(private var context: Context) {

    companion object {
        var userID = ""
    }

    var sharedPref: SharedPreferences = context.getSharedPreferences("myRef", Context.MODE_PRIVATE)

    fun saveSettings(userID: String) {
        val editor = sharedPref.edit()
        editor.putString("userID", userID)
        editor.apply()
        loadSettings()
    }

    fun loadSettings() {
        userID = sharedPref.getString("userID", "0")
        if (userID == "0") {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}