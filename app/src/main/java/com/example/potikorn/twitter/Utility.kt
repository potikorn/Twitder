package com.example.potikorn.twitter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*


fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}

fun getDayTimeFormat(date: String): String {
    val dateFormat = SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault())
    val dayHourMinuteFormat = SimpleDateFormat("EEEE HH:mm", Locale.getDefault())
//    val dateFormatted = dateFormat.parse(date)
    return dayHourMinuteFormat.format(date)
}

fun String.splitAssign():String {
    val split = this.split("@")
    return split[0]
}

fun ImageView.loadImage(context: Context, imgUrl: String?) {
    val requestOptions = RequestOptions
            .placeholderOf(android.R.color.darker_gray)
    Glide.with(context)
            .setDefaultRequestOptions(requestOptions)
            .load(imgUrl)
            .into(this)
}