package com.example.potikorn.twitter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.potikorn.twitter.data.Post
import com.example.potikorn.twitter.data.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TweetsAdapter(private var context: Context, private var tweetList: ArrayList<Ticket>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    private var onItemItemClickListener: OnItemClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val tweet = tweetList[position]
        if (tweet.TYPE == 3) {
            bindTweetViewHolder(holder as TweetViewHolder, tweet)
        } else {
            bindPostViewHolder(holder as PostViewHolder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val itemView: View = LayoutInflater.from(parent?.context).inflate(R.layout.layout_posts, parent, false)
            return TweetViewHolder(itemView)
        } else {
            val itemView: View = LayoutInflater.from(parent?.context).inflate(R.layout.layout_my_status, parent, false)
            return PostViewHolder(itemView)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (tweetList[position].TYPE == 3) {
            0
        } else {
            1
        }
    }

    override fun getItemCount(): Int = tweetList.size


    private fun bindTweetViewHolder(tweetViewHolder: TweetViewHolder, tweet: Ticket) {
        tweetViewHolder.tweetText.text = tweet.tweetText
        if (tweet.tweetImageUrl != null) {
            tweetViewHolder.postImage.loadImage(context, tweet.tweetImageUrl)
        } else {
            tweetViewHolder.postImage.visibility = View.GONE
        }
        myRef.child("Users").child(tweet.tweetPersonUID)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot?) {
                            try {
                                var td = dataSnapshot!!.value as HashMap<String, Any>
                                for (key in td.keys) {
                                    var userInfo = td[key] as String
                                    if (key == "ProfileImage") {
                                        tweetViewHolder.avatar.loadImage(context, userInfo)
                                    } else {
                                        tweetViewHolder.username.text = userInfo
                                    }
                                }
                            } catch (ex: Exception) {

                            }
                        }

                        override fun onCancelled(p0: DatabaseError?) {

                        }
                    })
    }

    private fun bindPostViewHolder(postViewHolder: PostViewHolder) {
        postViewHolder.imgAttach.setOnClickListener {
            onItemItemClickListener!!.onImageClick()
        }
        postViewHolder.imgPost.setOnClickListener {
            val tweetModel = Post()
            tweetModel.text = postViewHolder.txtMessage.text.toString()
            onItemItemClickListener!!.onPostClick(tweetModel)
            postViewHolder.txtMessage.setText("")
        }
    }

    fun onClick(onImageItemClickListener: OnItemClickListener) {
        onItemItemClickListener = onImageItemClickListener
    }

    interface OnItemClickListener {
        fun onImageClick()
        fun onPostClick(ticket: Post)
    }


}