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
        url = uri?.toString() ?: "https://app.seedsaversclub.com"
        val extras = intent.extras
        fun needLogin():Boolean{
            return if (CookieManager.getInstance().hasCookies()){
                val cookies=CookieManager.getInstance().getCookie("https://app.seedsaversclub.com/")
                Timber.d("cookies saved")
                if("remember_web" in cookies){
                    Timber.d("logged in")
                    false
                }else{
                    Timber.d("not logged in")
                    true
                }
            }else{
                true
            }
        }
        fun canLogin(extras: Bundle?):Boolean{
            if(extras!=null){
                if(extras.containsKey("email")){
                return true
                }
            }
            return false
        }
        fun login(extras: Bundle?){
            val loginUri = Uri.parse ("https://app.seedsaversclub.com/auth/matrix/login").buildUpon()
                    .appendQueryParameter("name",extras?.getString("username"))
                    .appendQueryParameter("email",extras?.getString("email"))
                    .appendQueryParameter("avatarurl",extras?.getString("avatarurl"))
                    .appendQueryParameter("id",extras?.getString("userid"))
                    .appendQueryParameter("redirecturl",url)
                    .build()
            webView.loadUrl(loginUri.toString())
        }
        if (needLogin() && canLogin(extras)){
            login(extras)
        }else{
            webView.loadUrl(url)
        }
    }
}
