package com.zhangebaoge.hhg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhangebaoge.hhg.Utils.WebViewUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static String TAG = "MainActivity";
    private String mCurrPage = "";
    private WebView mMainWebView;
//    private ImageView mImgBag;
    private RelativeLayout mRllGoShopping;
    private Boolean mLoadError;
    private ValueCallback<Uri> mUploadMessage;
    public static int FILECHOOSER_RESULTCODE = 1;
    private static String INDEX = "http://shop.php9.cn";
    private static String NINENINE= "http://shop.php9.cn/index.php?r=nine/wap";
    private static String GRAD = "http://shop.php9.cn/index.php?r=ddq/wap";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadUrl(INDEX);
                    return true;
                case R.id.navigation_dashboard:
                    loadUrl(NINENINE);
                    return true;
                case R.id.navigation_notifications:
                    loadUrl(GRAD);
                    return true;
            }
            return false;
        }
    };
    private void loadUrl(String url){
        mCurrPage = url;
        mMainWebView.loadUrl(url);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMainWebView.canGoBack()) {
                mMainWebView.goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRllGoShopping = (RelativeLayout) findViewById(R.id.rllGoShopping);
        mMainWebView = (WebView) findViewById(R.id.wvMain);
        WebViewUtils.setDefaultWebSettings(this, mMainWebView);
        WebViewUtils.removeJavascriptInterfaces(mMainWebView);
        mRllGoShopping.setOnClickListener(this);
        mMainWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mMainWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d(TAG, "Download error :" + e.getMessage());
                }
            }
        });
        mMainWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if (url.startsWith("tel:") || url.startsWith("weixin:") || url.startsWith("alipays:") || url.startsWith("tbopen:") || url.startsWith(" taobao:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return true;
                    }
                }
//                view.loadUrl(url);
                loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mRllGoShopping.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mLoadError = true;
                mRllGoShopping.setVisibility(View.VISIBLE);
                mMainWebView.setVisibility(View.GONE);

            }
        });
        mMainWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {

                if ((!TextUtils.isEmpty(title) && title.toLowerCase().contains("error")) || (!TextUtils.isEmpty(title) && title.equals("找不到网页"))) {
                    mLoadError = true;

                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mRllGoShopping.setVisibility(View.GONE);
                    mMainWebView.setVisibility(View.VISIBLE);
                } else {
                    mMainWebView.setVisibility(View.GONE);
                    mRllGoShopping.setVisibility(View.VISIBLE);
                }
            }

            //for android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(intent, "File chooser"), FILECHOOSER_RESULTCODE);
            }

            //for android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(intent, "File Brower"), FILECHOOSER_RESULTCODE);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                MainActivity.this.startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);

            }
        });

        mMainWebView.loadUrl(INDEX);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == mRllGoShopping.getId()){
            loadUrl(mCurrPage);
        }
    }
}
