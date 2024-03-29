package com.example.twittersigninexample

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder


class MainActivity : AppCompatActivity() {

    lateinit var twitterDialog: Dialog
    lateinit var twitter: Twitter
    var accToken: AccessToken? = null

    private var id = ""
    private var handle = ""
    private var name = ""
    private var email = ""
    private var profilePicURL = ""
    private var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val result = isLoggedIn()
            if (result) {
                // Show the Activity with the logged in user
                Log.d("LoggedIn?: ", "YES")
            } else {
                // Show the Home Activity
                Log.d("LoggedIn?: ", "NO")
            }
        }

        val twitterLoginBtn = findViewById<Button>(R.id.twitter_login_btn)
        twitterLoginBtn.setOnClickListener {
            getRequestToken()
        }

    }

    private fun getRequestToken() {
        lifecycleScope.launch(Dispatchers.Default) {
            val builder = ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY)
                .setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET)
                .setIncludeEmailEnabled(true)
            val config = builder.build()
            val factory = TwitterFactory(config)
            twitter = factory.instance
            try {
                val requestToken = twitter.oAuthRequestToken
                withContext(Dispatchers.Main) {
                    setupTwitterWebViewDialog(requestToken.authorizationURL)
                }
            } catch (e: IllegalStateException) {
                Log.e("ERROR: ", e.toString())
            }
        }
    }

    // Show twitter login page in a dialog
    private fun setupTwitterWebViewDialog(url: String) {
        val webView = WebView(this).apply {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            webViewClient = TwitterWebViewClient()
            loadUrl(url)
        }
        twitterDialog = Dialog(this).apply {
            setContentView(webView)
            show()
        }
    }

    // A client to know about WebView navigation
    @Suppress("OverridingDeprecatedMember")
    inner class TwitterWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (request.url.toString().startsWith(TwitterConstants.CALLBACK_URL)) {
                handleUrl(request.url.toString())
                twitterDialog.dismiss()
                return true
            }
            return false
        }

        // Get the oauth_verifier
        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)
            val oauthVerifier = uri.getQueryParameter("oauth_verifier") ?: ""
            lifecycleScope.launch(Dispatchers.Main) {
                accToken =
                    withContext(Dispatchers.IO) { twitter.getOAuthAccessToken(oauthVerifier) }
                getUserProfile()
                twitterDialog.dismiss()
            }
        }
    }

    suspend fun getUserProfile() {
        val usr = withContext(Dispatchers.IO) { twitter.verifyCredentials() }

        //Twitter Id
        val twitterId = usr.id.toString()
        Log.d("Twitter Id: ", twitterId)
        id = twitterId

        //Twitter Handle
        val twitterHandle = usr.screenName
        Log.d("Twitter Handle: ", twitterHandle)
        handle = twitterHandle

        //Twitter Name
        val twitterName = usr.name
        Log.d("Twitter Name: ", twitterName)
        name = twitterName

        //Twitter Email
        val twitterEmail = usr.email
        Log.d(
            "Twitter Email: ",
            twitterEmail
                ?: "'Request email address from users' on the Twitter dashboard is disabled"
        )
        email = twitterEmail
            ?: "'Request email address from users' on the Twitter dashboard is disabled"

        // Twitter Profile Pic URL
        val twitterProfilePic = usr.profileImageURLHttps.replace("_normal", "")
        Log.d("Twitter Profile URL: ", twitterProfilePic)
        profilePicURL = twitterProfilePic


        // Twitter Access Token
        Log.d("Twitter Access Token", accToken?.token ?: "")
        accessToken = accToken?.token ?: ""


        // Save the Access Token (accToken.token) and Access Token Secret (accToken.tokenSecret) using SharedPreferences
        // This will allow us to check user's logging state every time they open the app after cold start.
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        sharedPref.edit().putString("oauth_token", accToken?.token ?: "").apply()
        sharedPref.edit().putString("oauth_token_secret", accToken?.tokenSecret ?: "").apply()

        openDetailsActivity()
    }


    private suspend fun isLoggedIn(): Boolean {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val accessToken = sharedPref.getString("oauth_token", "")
        val accessTokenSecret = sharedPref.getString("oauth_token_secret", "")

        val builder = ConfigurationBuilder()
            .setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY)
            .setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET)
            .setOAuthAccessToken(accessToken)
            .setOAuthAccessTokenSecret(accessTokenSecret)
        val config = builder.build()
        val factory = TwitterFactory(config)
        val twitter = factory.instance
        try {
            withContext(Dispatchers.IO) { twitter.verifyCredentials() }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun openDetailsActivity() {
        val myIntent = Intent(baseContext, DetailsActivity::class.java)
        myIntent.putExtra("twitter_id", id)
        myIntent.putExtra("twitter_handle", handle)
        myIntent.putExtra("twitter_name", name)
        myIntent.putExtra("twitter_email", email)
        myIntent.putExtra("twitter_profile_pic_url", profilePicURL)
        myIntent.putExtra("twitter_access_token", accessToken)
        startActivity(myIntent)
    }
}