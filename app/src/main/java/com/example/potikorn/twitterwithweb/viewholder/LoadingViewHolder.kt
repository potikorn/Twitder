package com.example.potikorn.twitterwithweb.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.example.potikorn.twitterwithweb.R

class LoadingViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    val progressbar: ProgressBar = itemView!!.findViewById(R.id.pb)

}