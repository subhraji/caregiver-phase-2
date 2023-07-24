package com.example.caregiverphase2.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.caregiverphase2.R
import com.example.caregiverphase2.databinding.ActivityEarningsBinding
import com.example.caregiverphase2.databinding.ActivityWebViewBinding
import gone
import visible

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras == null) {
            url = ""
        } else {
            url = extras.getString("url").toString()
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.webView.loadUrl(url)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                binding.progressBar.visible()
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                binding.progressBar.gone()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding.webView.canGoBack())
            binding.webView.goBack()

        else
            super.onBackPressed()
    }
}