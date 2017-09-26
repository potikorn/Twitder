package com.example.potikorn.twitter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.potikorn.twitter.data.Post
import com.example.potikorn.twitter.data.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.content_login.view.*
import kotlinx.android.synthetic.main.layout_my_status.view.*
import kotlinx.android.synthetic.main.layout_posts.view.*

class TweetsAdapter(private var context: Context, private var tweetList: ArrayList<Ticket>) : BaseAdapter() {

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    var onItemClickListener: onClickListener? = null

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var myTweet = tweetList[p0]

        if (myTweet.tweetPersonUID.equals("add")) {
            var view = LayoutInflater.from(context).inflate(R.layout.layout_my_status, null)

            view.img_attach.setOnClickListener {
                onItemClickListener!!.onImageClick()
            }
            view.img_post.setOnClickListener {
                val tweetModel = Post()
                tweetModel.text = view.et_text_post.text.toString()
                onItemClickListener!!.onPostClick(tweetModel)
                view.et_text_post.setText("")
            }
            return view

        }else if (myTweet.tweetPersonUID.equals("loading")){
            val view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null)
            return view
        } else {
            var view = LayoutInflater.from(context).inflate(R.layout.layout_posts, null)
            view.txt_tweet_text.text = myTweet.tweetText
            view.txt_username.text = myTweet.tweetPersonUID

            view.img_tweet_post.loadImage(context, myTweet.tweetImageUrl)


            myRef.child("Users").child(myTweet.tweetPersonUID)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot?) {
                            try {
                                var td = dataSnapshot!!.value as HashMap<String, Any>
                                for (key in td.keys) {
                                    var userInfo = td[key] as String
                                    if (key == "ProfileImage") {
                                        view.img_avatar.loadImage(context, userInfo)
                                    } else {
                                        view.txt_username.text = userInfo
                                    }
                                }
                            } catch (ex: Exception) {

                            }
                        }

                        override fun onCancelled(p0: DatabaseError?) {

                        }
                    })
            return view
        }

    }


    override fun getItem(p0: Int): Any = tweetList[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = tweetList.size

    fun onClick(onImageClickListener: onClickListener) {
        onItemClickListener = onImageClickListener
    }

    interface onClickListener {
        fun onImageClick()
        fun onPostClick(ticket: Post)
    }


}