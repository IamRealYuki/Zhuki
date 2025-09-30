package com.example.helloworld.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.helloworld.R

class RulesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rules, container, false)

        val webView: WebView = view.findViewById(R.id.webView)
        // Загружаем HTML из ресурсов
        webView.loadUrl("file:///android_res/raw/rules.html")

        return view
    }
}