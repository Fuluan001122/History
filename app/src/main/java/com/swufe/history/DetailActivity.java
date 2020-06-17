package com.swufe.history;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
     WebView webView;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_detail);
          webView = findViewById(R.id.webview);
          String url = getIntent().getExtras().getString("url");
          WebSettings mWebSettings = webView.getSettings();
          mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
          mWebSettings.setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
          mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
          mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
          webView.loadUrl(url);
          webView.setWebViewClient(new WebViewClient() {
               @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
               @Override
               public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    view.loadUrl(request.getUrl().toString());
                    return true;
               }
          });
          webView.setWebChromeClient(new WebChromeClient() {
               @Override
               public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                    } else {
                    }
               }
          });
     }

     //点击返回上一页面而不是退出浏览器
     @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
               webView.goBack();
               return true;
          }

          return super.onKeyDown(keyCode, event);
     }

     @Override
     protected void onDestroy() {
          if (webView != null) {
               webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
               webView.clearHistory();
               ((ViewGroup) webView.getParent()).removeView(webView);
               webView.destroy();
               webView = null;
          }
          super.onDestroy();

     }

}
