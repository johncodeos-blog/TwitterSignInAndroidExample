package com.example.twittersigninexample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val twitterIdTextView = findViewById<TextView>(R.id.twitter_id_textview)
        val twitterHandleTextView = findViewById<TextView>(R.id.twitter_handle_textview)
        val twitterNameTextView = findViewById<TextView>(R.id.twitter_name_textview)
        val twitterEmailTextView = findViewById<TextView>(R.id.twitter_email_textview)
        val twitterProfilePicUrlTextView = findViewById<TextView>(R.id.twitter_profile_pic_url_textview)
        val twitterAccessTokenTextView = findViewById<TextView>(R.id.twitter_access_token_textview)

        intent.getStringExtra("twitter_id")?.let { twitterIdTextView.text = it }
        intent.getStringExtra("twitter_handle")?.let { twitterHandleTextView.text = it }
        intent.getStringExtra("twitter_name")?.let { twitterNameTextView.text = it }
        intent.getStringExtra("twitter_email")?.let { twitterEmailTextView.text = it }
        intent.getStringExtra("twitter_profile_pic_url")?.let { twitterProfilePicUrlTextView.text = it }
        intent.getStringExtra("twitter_access_token")?.let { twitterAccessTokenTextView.text = it }
    }
}
