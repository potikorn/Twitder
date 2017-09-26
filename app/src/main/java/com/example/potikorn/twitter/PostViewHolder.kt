package com.example.potikorn.twitter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.ImageView

class PostViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    val txtMessage: EditText = itemView!!.findViewById(R.id.et_text_post)
    val imgAttach: ImageView = itemView!!.findViewById(R.id.img_attach)
    val imgPost: ImageView = itemView!!.findViewById(R.id.img_post)

}