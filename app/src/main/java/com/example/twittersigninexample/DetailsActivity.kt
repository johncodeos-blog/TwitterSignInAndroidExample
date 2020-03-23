package com.example.twittersigninexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val twitterId = intent.getStringExtra("twitter_id")
        val twitterHandle = intent.getStringExtra("twitter_handle")
        val twitterName = intent.getStringExtra("twitter_name")
        val twitterEmail = intent.getStringExtra("twitter_email")
        val twitterProfilePicURL = intent.getStringExtra("twitter_profile_pic_url")
        val twitterAccessToken = intent.getStringExtra("twitter_access_token")

        twitter_id_textview.text = twitterId
        twitter_handle_textview.text = twitterHandle
        twitter_name_textview.text = twitterName
        twitter_email_textview.text = twitterEmail
        twitter_profile_pic_url_textview.text = twitterProfilePicURL
        twitter_access_token_textview.text = twitterAccessToken
    }

}
