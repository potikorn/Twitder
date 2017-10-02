package com.example.potikorn.twitterwithweb

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

var progressDialog: ProgressDialog? = null

fun showProgressDialog(context: Context, word: String) {
    progressDialog = ProgressDialog(context)
    progressDialog!!.setCancelable(false)
    progressDialog!!.isIndeterminate = true
    progressDialog!!.setMessage(word)
    progressDialog!!.show()
}

fun dismissProgressDialog() {
    progressDialog!!.dismiss()
}


fun String.splitAssign():String {
    val split = this.split("@")
    return split[0]
}

fun convertStreamToString(inputStream: InputStream):String{

    val bufferReader= BufferedReader(InputStreamReader(inputStream))
    var line:String
    var allString=""

    try {
        do{
            line=bufferReader.readLine()
            if(line!=null){
                allString+=line
            }
        }while (line!=null)
        inputStream.close()
    }catch (ex:Exception){}



    return allString
}

fun ImageView.loadImage(context: Context, imgUrl: String?) {
    val requestOptions = RequestOptions
            .placeholderOf(android.R.color.darker_gray)
    Glide.with(context)
            .setDefaultRequestOptions(requestOptions)
            .load(imgUrl)
            .into(this)
}

fun View.HIDE() {
    this.visibility = View.GONE
}

fun View.VISIBLE() {
    this.visibility = View.VISIBLE
}

fun View.DISABLE() {
    this.isEnabled = false
}

fun View.ENABLE() {
    this.isEnabled = true
}
