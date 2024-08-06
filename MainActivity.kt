package com.example.homifywebversion

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*

class MainActivity : AppCompatActivity() {
    // Variable declaration
    private lateinit var webContainer: WebView

    private var uploadMsg: ValueCallback<Array<Uri>>? = null
    private val FILE_CHOOSER_REQUEST_CODE = 1

    //@SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {

        //hide ActionBar
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize declared variable
        webContainer = findViewById(R.id.web_container)
        val webSettings: WebSettings = webContainer.settings

        // Enable JavaScript and link functions
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        webContainer.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }
        }

        webContainer.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                uploadMsg?.onReceiveValue(null)
                uploadMsg = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_CHOOSER_REQUEST_CODE)

                return true
            }
        }

        // Load the web application URL
        webContainer.loadUrl("http://192.168.25.107/homify")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (uploadMsg == null) return
            if (resultCode == Activity.RESULT_OK && data != null) {
                val clipData = data.clipData
                if (clipData != null) {
                    val uris = Array(clipData.itemCount) { i ->
                        clipData.getItemAt(i).uri
                    }
                    uploadMsg?.onReceiveValue(uris)
                } else {
                    data.data?.let {
                        uploadMsg?.onReceiveValue(arrayOf(it))
                    }
                }
            } else {
                uploadMsg?.onReceiveValue(null)
            }
            uploadMsg = null
        }
    }

    // Handle back button press to navigate within WebView
    override fun onBackPressed() {
        if (webContainer.canGoBack()) {
            webContainer.goBack()
        } else {
            super.onBackPressed()
        }
    }

}