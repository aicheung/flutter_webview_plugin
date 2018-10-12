package com.flutter_webview_plugin;

import android.graphics.Bitmap;
import android.webkit.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lejard_h on 20/12/2017.
 */

public class BrowserClient extends WebViewClient {
    public BrowserClient() {
        super();
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error){
        handler.proceed();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest (WebView view,
                                                       WebResourceRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("url", request.getUrl().toString());
        data.put("method", request.getMethod());
        data.put("requestHeaders", request.getRequestHeaders());
        FlutterWebviewPlugin.channel.invokeMethod("onRequestStart", data);
        return null;
    }

    @Override
    public boolean shouldOverrideUrlLoading (WebView view,
                                             WebResourceRequest request){
        Map<String, Object> data = new HashMap<>();
        data.put("url", request.getUrl().toString());
        data.put("method", request.getMethod());
        data.put("requestHeaders", request.getRequestHeaders());
        FlutterWebviewPlugin.channel.invokeMethod("onUrlLoading", data);
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("type", "startLoad");
        FlutterWebviewPlugin.channel.invokeMethod("onState", data);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);

        FlutterWebviewPlugin.channel.invokeMethod("onUrlChanged", data);

        data.put("type", "finishLoad");
        FlutterWebviewPlugin.channel.invokeMethod("onState", data);

    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        Map<String, Object> data = new HashMap<>();
        data.put("url", request.getUrl().toString());
        data.put("code", Integer.toString(errorResponse.getStatusCode()));
        data.put("requestHeaders", request.getRequestHeaders());
        data.put("responseHeaders", errorResponse.getResponseHeaders());
        FlutterWebviewPlugin.channel.invokeMethod("onHttpError", data);
    }
}