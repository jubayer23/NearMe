package com.creative.nearme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.creative.nearme.utils.CommonMethods;

/**
 * Created by jubayer on 7/17/2017.
 */

public class WikipedidaActivity extends AppCompatActivity {
    EditText e1;
    FloatingActionButton fab;
    ProgressBar progressbar;
    ScrollView scrollView;
    ImageView search;
    String search_query = null;
    Toolbar toolbar;
    WebView wv;




    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_wikipedia);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        this.wv = ((WebView) findViewById(R.id.webview));
        this.progressbar = ((ProgressBar) findViewById(R.id.progressBar));
        this.e1 = ((EditText) findViewById(R.id.edit));
        this.fab = ((FloatingActionButton) findViewById(R.id.fab));
        this.search = ((ImageView) findViewById(R.id.search));
        this.wv.setWebViewClient(new MyBrowser());
        this.fab.hide();
        this.fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                WikipedidaActivity.this.scrollView.scrollTo(0, 0);
            }
        });
        this.scrollView = ((ScrollView) findViewById(R.id.scroll));
        this.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            public void onScrollChanged() {
                int i = WikipedidaActivity.this.scrollView.getScrollY();
                if ((WikipedidaActivity.this.scrollView.getScrollX() == 0) && (i == 0)) {
                    WikipedidaActivity.this.fab.hide();
                    return;
                }
                WikipedidaActivity.this.fab.show();
            }
        });
        this.wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView paramAnonymousWebView, int paramAnonymousInt) {
                if ((paramAnonymousInt < 100) && (WikipedidaActivity.this.progressbar.getVisibility() == View.GONE)) {
                    WikipedidaActivity.this.progressbar.setVisibility(View.VISIBLE);
                }
                WikipedidaActivity.this.progressbar.setProgress(paramAnonymousInt);
                if (paramAnonymousInt == 100) {
                    WikipedidaActivity.this.progressbar.setVisibility(View.GONE);
                    CommonMethods.hideKeyboardForcely(WikipedidaActivity.this, e1);
                }
            }
        });
        this.e1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent) {
                if (paramAnonymousInt == 3) {
                    WikipedidaActivity.this.search_query = ("https://en.m.wikipedia.org/w/index.php?search=" + WikipedidaActivity.this.e1.getText().toString() + "&title=Special:Search&go=Go");
                    WikipedidaActivity.this.wv.getSettings().setLoadsImagesAutomatically(true);
                    WikipedidaActivity.this.wv.getSettings().setJavaScriptEnabled(true);
                    //WikipedidaActivity.this.wv.setScrollBarStyle(0);
                    WikipedidaActivity.this.wv.loadUrl(WikipedidaActivity.this.search_query);
                    WikipedidaActivity.this.findViewById(R.id.linear).setVisibility(View.GONE);
                    WikipedidaActivity.this.progressbar.setVisibility(View.VISIBLE);
                    CommonMethods.hideKeyboardForcely(WikipedidaActivity.this, e1);
                    return true;
                }
                return false;
            }
        });
        this.search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                CommonMethods.hideKeyboardForcely(WikipedidaActivity.this, e1);
                WikipedidaActivity.this.e1.setText("");
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (this.wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }

    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        if (paramMenuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(paramMenuItem);
    }

    private class MyBrowser
            extends WebViewClient {
        private MyBrowser() {
        }

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString) {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }
}