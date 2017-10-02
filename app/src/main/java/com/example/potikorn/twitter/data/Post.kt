package com.example.potikorn.twitter.data

data class Post(

        var dateTime: String? = null,
        var userUID: String? = null,
        var text: String? = null,
        var postImage: String? = null,
        var TYPE: Int = DetailsType.TYPE_POST

)