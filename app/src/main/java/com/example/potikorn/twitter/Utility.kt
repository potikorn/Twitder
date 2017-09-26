package com.example.potikorn.twitter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


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