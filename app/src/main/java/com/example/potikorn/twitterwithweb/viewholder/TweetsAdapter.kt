package com.example.potikorn.twitter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.potikorn.twitter.data.Post
import com.example.potikorn.twitter.data.Ticket
import com.example.potikorn.twitterwithweb.DateUtility.Companion.getDayTimeFormat
import com.example.potikorn.twitterwithweb.R
import com.example.potikorn.twitterwithweb.loadImage
import com.example.potikorn.twitterwithweb.viewholder.LoadingViewHolder
import java.net.URLEncoder

class TweetsAdapter(private var context: Context, private var tweetList: ArrayList<Ticket>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemItemClickListener: OnItemClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val tweet = tweetList[position]
        if (tweet.TYPE == 3) {
            bindTweetViewHolder(holder as TweetViewHolder, tweet)
        } else if (tweet.TYPE == 2) {
            bindPostViewHolder(holder as PostViewHolder)
        } else {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val itemView: View = LayoutInflater.from(parent?.context).inflate(R.layout.layout_posts, parent, false)
            return TweetViewHolder(itemView)
        } else if (viewType == 1){
            val itemView: View = LayoutInflater.from(parent?.context).inflate(R.layout.layout_my_tweet, parent, false)
            return PostViewHolder(itemView)
        } else {
            val itemView: View = LayoutInflater.from(parent?.context).inflate(R.layout.layout_loading, parent, false)
            return LoadingViewHolder(itemView)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (tweetList[position].TYPE == 3) {
            0
        } else if (tweetList[position].TYPE == 2) {
            1
        } else {
            2
        }
    }

    override fun getItemCount(): Int = tweetList.size


    private fun bindTweetViewHolder(tweetViewHolder: TweetViewHolder, tweet: Ticket) {
        tweetViewHolder.tweetText.text = tweet.tweetText
//        if (tweet.date != null)
//            tweet.date = getDayTimeFormat(tweet.date!!)
        tweetViewHolder.dateTime.text = tweet.date
        if (tweet.tweetImageUrl == "noImage") {
            tweetViewHolder.postImage.visibility = View.GONE
        } else {
            tweetViewHolder.postImage.loadImage(context, tweet.tweetImageUrl)
        }
    }

    private fun bindPostViewHolder(postViewHolder: PostViewHolder) {
        postViewHolder.imgAttach.setOnClickListener {
            onItemItemClickListener!!.onImageClick()
        }
        postViewHolder.imgPost.setOnClickListener {
            val tweetModel = Post()
            tweetModel.text = URLEncoder.encode(postViewHolder.txtMessage.text.toString(), "utf-8")
            onItemItemClickListener!!.onPostClick(tweetModel)
            postViewHolder.txtMessage.setText("")
        }
    }

    fun onClick(onImageItemClickListener: OnItemClickListener) {
        onItemItemClickListener = onImageItemClickListener
    }

    interface OnItemClickListener {
        fun onImageClick()
        fun onPostClick(post: Post)
    }


}