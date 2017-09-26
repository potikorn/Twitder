package com.example.potikorn.twitter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class TweetViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    val username: TextView = itemView!!.findViewById(R.id.txt_username)
    val tweetText: TextView = itemView!!.findViewById(R.id.txt_tweet_text)
    val avatar: ImageView = itemView!!.findViewById(R.id.img_avatar)
    val postImage: ImageView = itemView!!.findViewById(R.id.img_tweet_post)

}