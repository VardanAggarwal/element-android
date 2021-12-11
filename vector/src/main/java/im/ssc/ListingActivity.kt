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
class ListingActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var analytics: FirebaseAnalytics
    lateinit var progressBar: ProgressBar
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
            val path = uri.toString()
            webView.loadUrl(path)
        }else{
            webView.loadUrl("https://app.seedsaversclub.com/")
        }
    }
}
