/*
 * Copyright (c) 2021 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ssc

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import im.vector.app.R
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import java.net.URLEncoder

class ListingActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var analytics: FirebaseAnalytics
    lateinit var progressBar: ProgressBar
    lateinit var url:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(this)
        setContentView(R.layout.activity_listing)
        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        webView.settings.setJavaScriptEnabled(true)
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.settings.setUserAgentString("Seed Savers Club")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url.toString())
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                CookieManager.getInstance().acceptCookie()
                CookieManager.getInstance().flush()
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                progressBar.visibility = View.GONE
            }
        }
        val uri: Uri? = intent.data
        if(uri!=null){
            url = uri.toString()
        }else{
            url = "https://app.seedsaversclub.com"
        }
        val extras = intent.extras
        fun login(extras: Bundle){
            Timber.d("session email %s", extras.get("email"))
            Timber.d("session username %s", extras.get("username"))
            Timber.d("session avatarurl %s", extras.getString("avatarurl")?.replace("mxc://", "https://matrix.seedsaversclub.com/_matrix/media/r0/download/"))
            Timber.d("session userid %s", extras.get("userid"))
            /*val postData = "email=${URLEncoder.encode("vardan@seedsaversclub.com", "UTF-8")}" +
                    "&password=${URLEncoder.encode("Vardan93", "UTF-8")}"
            webView.postUrl("https://app.seedsaversclub.com/login", postData.toByteArray())*/
            webView.loadUrl(url)
        }
        if(extras!=null){
            if(extras.containsKey("email")){
                if (CookieManager.getInstance().hasCookies()){
                    val cookies=CookieManager.getInstance().getCookie("https://app.seedsaversclub.com/")
                    Timber.d("cookies saved %s",cookies)
                    if("remember_web" in cookies){
                        Timber.d("logged in")
                        webView.loadUrl(url)
                    }else{
                        Timber.d("not logged in")
                        login(extras)
                    }
                }
                else{
                    Timber.d("no cookies")
                    login(extras)
                }
            }else{
                webView.loadUrl(url)
            }
        }else{
            webView.loadUrl(url)
        }
    }
}
