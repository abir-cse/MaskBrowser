package com.bdbot.maskbrowser;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends Activity {

    WebView webView1;
    String url;
    EditText editText1;
    String loadurl;
    BottomNavigationView bottomNavigationView;
    ProgressBar bar1;
    RelativeLayout webLayout, homeLayout;
    WebSettings webSettings;
    GridView gridview;
    private InterstitialAd fb_interstitialAd;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Counter = "count";

    private com.google.android.gms.ads.InterstitialAd interstitialAd = null;
    private AdRequest adRequest4;
    int isHome = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hiding status bar & nav bar
        if (Build.VERSION.SDK_INT >= 16) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY ;
            decorView.setSystemUiVisibility(uiOptions);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("mask");
        Intent intent_push = getIntent();
        String url_push = intent_push.getStringExtra("url");
        if(url_push !=null) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url_push));
            startActivity(i);
            finish();
        }
        homeLayout = findViewById(R.id.homeLayout);
        webLayout = findViewById(R.id.activity_content);
        bar1 = findViewById(R.id.bar1);
        webView1 = findViewById(R.id.webView1);
        editText1 = findViewById(R.id.editText1);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        webSettings = webView1.getSettings();
        gridview = findViewById(R.id.gridview);
        webLayout.setVisibility(View.GONE);
        bar1.setVisibility(View.GONE);

        //web-setting
        webView1.getSettings().setLoadsImagesAutomatically(true);
        webView1.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        webView1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView1.setWebViewClient(new WebViewClient());

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webView1.getSettings().setAppCacheEnabled(false);
        webView1.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView1.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView1.setScrollbarFadingEnabled(true);

        // open link test
        //String action = intent.getAction();
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.isHierarchical()) {
            String url = intent.getDataString();
            webView1.loadUrl(url);
            homeLayout.setVisibility(View.GONE);
            webLayout.setVisibility(View.VISIBLE);
            editText1.setVisibility(View.GONE);
            bar1.setVisibility(View.VISIBLE);
            webView1.setVisibility(View.VISIBLE);
            bar1.setVisibility(View.VISIBLE);
            // progress-bar-loading
            webView1.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView webView1, int newProgress) {
                    bar1.setProgress(newProgress);
                }
            });

            isHome = 0;
            //
            downloadListener();
            medicineOfDynamicUrl();
        }


        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                isHome = 0;
                //webView1 = findViewById(R.id.webView1);

                homeLayout.setVisibility(View.GONE);
                webLayout.setVisibility(View.VISIBLE);
                editText1.setVisibility(View.GONE);
                bar1.setVisibility(View.VISIBLE);
                // progress-bar-loading
                webView1.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView webView1, int newProgress) {
                        bar1.setProgress(newProgress);
                    }
                });

                if (position == 0) {
                    webView1.loadUrl("https://www.facebook.com");
                }
                if (position == 1) {
                    webView1.loadUrl("https://www.twitter.com");
                }
                if (position == 2) {
                    webView1.loadUrl("https://www.plus.google.com");
                }
                if (position == 3) {
                    webView1.loadUrl("https://www.youtube.com");
                }
                if (position == 4) {
                    webView1.loadUrl("https://www.google.com");
                }
                if (position == 5) {
                    webView1.loadUrl("https://www.yahoo.com");
                }
                if (position == 6) {
                    webView1.loadUrl("https://www.bing.com");
                }
                if (position == 7) {
                    webView1.loadUrl("https://www.apple.com");
                }

            }
        });

        //sp

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.contains(Counter)) { }
        else {
            editor.putString(Counter, "1");
            editor.commit();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //
                load_fb_int();
                // make admob ready
                interstitialAd = new com.google.android.gms.ads.InterstitialAd(getApplicationContext());
                interstitialAd.setAdUnitId(getString(R.string.admob_int_2));
                adRequest4 = new AdRequest.Builder()
                        .build();
                interstitialAd.loadAd(adRequest4);

            }
        }, 3000);
        // am ad



        editText1.setSelection(editText1.getText().length());
        // ---eidtText Cursor Hide & show ---
        editText1.setCursorVisible(false);
        editText1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editText1.setSelection(editText1.getText().toString().length());
                editText1.requestFocus();
                editText1.requestFocusFromTouch();
                editText1.setCursorVisible(true);
                return false;
            }
        });
        //end

        editText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                isHome = 0;
                homeLayout.setVisibility(View.GONE);
                webLayout.setVisibility(View.VISIBLE);
                editText1.setVisibility(View.GONE);
                bar1.setVisibility(View.VISIBLE);
                webView1.setVisibility(View.VISIBLE);

                if (actionId == EditorInfo.IME_ACTION_GO) {
                    url = editText1.getText().toString().trim();

                    if (url.endsWith(".com") || url.endsWith(".net") || url.endsWith(".info") || url.endsWith(".com.bd") || url.endsWith(".af") || url.endsWith(".ac.bd") || url.endsWith(".ai") || url.endsWith(".al") || url.endsWith(".am") || url.endsWith(".an") || url.endsWith(".ao") || url.endsWith(".aq") || url.endsWith(".ar") || url.endsWith(".arpa") || url.endsWith(".as") || url.endsWith(".asia") || url.endsWith(".at") || url.endsWith(".au") || url.endsWith(".aw") || url.endsWith(".ax") || url.endsWith(".az") || url.endsWith(".ba") || url.endsWith(".bb") || url.endsWith(".bd") || url.endsWith(".be") || url.endsWith(".bf") || url.endsWith(".bg") || url.endsWith(".bh") || url.endsWith(".bi") || url.endsWith(".biz") || url.endsWith(".bj") || url.endsWith(".bm") || url.endsWith(".bn") || url.endsWith(".bo") || url.endsWith(".br") || url.endsWith(".bs") || url.endsWith(".bt") || url.endsWith(".bv") || url.endsWith(".bw") || url.endsWith(".by") || url.endsWith(".bz") || url.endsWith(".ca") || url.endsWith(".cat") || url.endsWith(".cc") || url.endsWith(".cd") || url.endsWith(".cf") || url.endsWith(".cg") || url.endsWith(".ch") || url.endsWith(".ci") || url.endsWith(".ck") || url.endsWith(".cl") || url.endsWith(".cm") || url.endsWith(".cn") || url.endsWith(".co") || url.endsWith(".co.uk") || url.endsWith(".com") || url.endsWith(".coop") || url.endsWith(".cr") || url.endsWith(".cs") || url.endsWith(".cu") || url.endsWith(".cv") || url.endsWith(".cx") || url.endsWith(".cy") || url.endsWith(".cz") || url.endsWith(".dd") || url.endsWith(".de") || url.endsWith(".dj") || url.endsWith(".dk") || url.endsWith(".dm") || url.endsWith(".do") || url.endsWith(".dz") || url.endsWith(".ec") || url.endsWith(".edu") || url.endsWith(".ee") || url.endsWith(".eg") || url.endsWith(".eh") || url.endsWith(".er") || url.endsWith(".es") || url.endsWith(".et") || url.endsWith(".eu") || url.endsWith(".fi") || url.endsWith(".firm") || url.endsWith(".fj") || url.endsWith(".fk") || url.endsWith(".fm") || url.endsWith(".fo") || url.endsWith(".fr") || url.endsWith(".fx.ga") || url.endsWith(".gb") || url.endsWith(".gd") || url.endsWith(".ge") || url.endsWith(".gf") || url.endsWith(".gh") || url.endsWith(".gi") || url.endsWith(".gl") || url.endsWith(".gm") || url.endsWith(".gn") || url.endsWith(".gov") || url.endsWith(".gov.uk") || url.endsWith(".gp") || url.endsWith(".gq") || url.endsWith(".gr") || url.endsWith(".gs") || url.endsWith(".gt") || url.endsWith(".gu") || url.endsWith(".gw") || url.endsWith(".gy") || url.endsWith(".hk") || url.endsWith(".hm") || url.endsWith(".hn") || url.endsWith(".hr") || url.endsWith(".ht") || url.endsWith(".hu") || url.endsWith(".id") || url.endsWith(".ie") || url.endsWith(".il") || url.endsWith(".im") || url.endsWith(".in") || url.endsWith(".info") || url.endsWith(".int") || url.endsWith(".io") || url.endsWith(".iq") || url.endsWith(".ir.is") || url.endsWith(".it") || url.endsWith(".je") || url.endsWith(".jm") || url.endsWith(".jo") || url.endsWith(".jobs") || url.endsWith(".jp") || url.endsWith(".ke") || url.endsWith(".kg") || url.endsWith(".kh") || url.endsWith(".ki") || url.endsWith(".km") || url.endsWith(".kn") || url.endsWith(".kp") || url.endsWith(".kr") || url.endsWith(".kw") || url.endsWith(".ky") || url.endsWith(".kz") || url.endsWith(".la") || url.endsWith(".lb") || url.endsWith(".lc") || url.endsWith(".li") || url.endsWith(".lk") || url.endsWith(".lr") || url.endsWith(".ls") || url.endsWith(".lt") || url.endsWith(".ltd.uk") || url.endsWith(".lu") || url.endsWith(".lv") || url.endsWith(".ly") || url.endsWith(".ma") || url.endsWith(".mc") || url.endsWith(".md") || url.endsWith(".me") || url.endsWith(".me.uk") || url.endsWith(".mg") || url.endsWith(".mh") || url.endsWith(".mil") || url.endsWith(".mk") || url.endsWith(".ml") || url.endsWith(".mm") || url.endsWith(".mn") || url.endsWith(".mo") || url.endsWith(".mobi") || url.endsWith(".mod.uk") || url.endsWith(".mp") || url.endsWith(".mq") || url.endsWith(".mr") || url.endsWith(".ms") || url.endsWith(".mt") || url.endsWith(".mu") || url.endsWith(".museum") || url.endsWith(".mv") || url.endsWith(".mw") || url.endsWith(".mx") || url.endsWith(".my") || url.endsWith(".mz.na") || url.endsWith(".name") || url.endsWith(".nato") || url.endsWith(".nc") || url.endsWith(".ne") || url.endsWith(".net") || url.endsWith(".net.uk") || url.endsWith(".nf") || url.endsWith(".ng") || url.endsWith(".nhs.uk") || url.endsWith(".ni") || url.endsWith(".nl") || url.endsWith(".no") || url.endsWith(".nom") || url.endsWith(".np") || url.endsWith(".nr") || url.endsWith(".nt") || url.endsWith(".nu") || url.endsWith(".nz") || url.endsWith(".om") || url.endsWith(".org") || url.endsWith(".org.uk") || url.endsWith(".pa") || url.endsWith(".pe") || url.endsWith(".pf") || url.endsWith(".pg") || url.endsWith(".ph") || url.endsWith(".pk") || url.endsWith(".pl") || url.endsWith(".plc.uk") || url.endsWith(".pm") || url.endsWith(".pn") || url.endsWith(".post") || url.endsWith(".pr") || url.endsWith(".pro") || url.endsWith(".ps") || url.endsWith(".pt") || url.endsWith(".pw") || url.endsWith(".py") || url.endsWith(".qa") || url.endsWith(".re") || url.endsWith(".ro") || url.endsWith(".ru") || url.endsWith(".rw") || url.endsWith(".sa") || url.endsWith(".sb") || url.endsWith(".sc") || url.endsWith(".sch.uk") || url.endsWith(".sd") || url.endsWith(".se") || url.endsWith(".sg") || url.endsWith(".sh") || url.endsWith(".si") || url.endsWith(".sj") || url.endsWith(".sk") || url.endsWith(".sl") || url.endsWith(".sm") || url.endsWith(".sn") || url.endsWith(".so") || url.endsWith(".sr") || url.endsWith(".store") || url.endsWith(".su") || url.endsWith(".sv") || url.endsWith(".sy") || url.endsWith(".sz") || url.endsWith(".tc") || url.endsWith(".td") || url.endsWith(".tel") || url.endsWith(".tf") || url.endsWith(".tg") || url.endsWith(".th") || url.endsWith(".tj") || url.endsWith(".tk") || url.endsWith(".tl") || url.endsWith(".tm") || url.endsWith(".tn") || url.endsWith(".to") || url.endsWith(".tp") || url.endsWith(".tr") || url.endsWith(".travel") || url.endsWith(".tt") || url.endsWith(".tv") || url.endsWith(".tw") || url.endsWith(".tz") || url.endsWith(".ua") || url.endsWith(".ug") || url.endsWith(".uk") || url.endsWith(".um") || url.endsWith(".us") || url.endsWith(".uy") || url.endsWith(".va") || url.endsWith(".vc") || url.endsWith(".ve") || url.endsWith(".vg") || url.endsWith(".vi") || url.endsWith(".vn") || url.endsWith(".vu") || url.endsWith(".web") || url.endsWith(".wf") || url.endsWith(".ws") || url.endsWith(".xxx") || url.endsWith(".ye") || url.endsWith(".yt") || url.endsWith(".yu") || url.endsWith(".za") || url.endsWith(".zm") || url.endsWith(".zr") || url.endsWith(".zw")) {
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            loadurl = "https://" + url;
                        }
                    } else {
                        loadurl = "https://duckduckgo.com/?q=" + url;
                    }

                    webView1.loadUrl(loadurl);
                    webView1.setWebViewClient(new WebViewClient(){

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon)
                        {
                            downloadListener();
                            medicineOfDynamicUrl();
                        }
                    });
                    // progress-bar-loading
                    webView1.setWebChromeClient(new WebChromeClient() {
                        public void onProgressChanged(WebView webView1, int newProgress) {
                            bar1.setProgress(newProgress);
                        }
                    });
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });

        //--bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.back:
                        if (webView1.canGoBack()) {
                            webView1.goBack();
                        } else {
                            showHomePage();
                            isHome = 1;
                        }
                        break;
                    case R.id.forward:
                        if (webView1.canGoForward()) {
                            webView1.goForward();
                            isHome = 0;
                        } else {
                            Toast.makeText(getApplicationContext(), "Nowhere to Go !", Toast.LENGTH_SHORT).show();
                            isHome =1;
                        }
                        break;

                    case R.id.delete1:

                        if (Build.VERSION.SDK_INT < 18) {
                            webView1.clearView();
                            isHome =1;
                        } else {
                            webView1.loadUrl("about:blank");
                            isHome = 1;
                        }
                        showHomePage();
                        webView1.clearHistory();
                        webView1.clearCache(true);
                        Toast.makeText(getApplicationContext(), "History, Cache, Cookies are Cleaned !", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.stop1:
                        webView1.stopLoading();
                        break;
                }
                return false;
            }
        });

        webView1.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                bar1.setVisibility(View.GONE);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                bar1.setVisibility(View.VISIBLE);
            }
        });
    }

    private void load_fb_int() {
        fb_interstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_int_1));
        fb_interstitialAd.loadAd();
        fb_interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) { }

            @Override
            public void onInterstitialDismissed(Ad ad) { }

            @Override
            public void onError(Ad ad, AdError adError) {
                load_admob_int();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                fb_interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) { }

            @Override
            public void onLoggingImpression(Ad ad) { }
        });
    }
    private void load_admob_int() {
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        String historyUrl="";
        WebBackForwardList mWebBackForwardList = webView1.copyBackForwardList();
        if (mWebBackForwardList.getCurrentIndex() > 0) {
            historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
            Log.e("History URL -------", "-------"+historyUrl);
            if (historyUrl.equals("about:blank")) {
                webView1.goBack();
                showHomePage();
            }
        }
        if(webView1.canGoBack()) {
            webView1.goBack();
        } else {
            if (isHome == 1 ) {
                downloadPro();
            } else {
                showHomePage();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (webView1 != null) {
            webView1.destroy();
        }
        if (fb_interstitialAd != null) {
            fb_interstitialAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        webView1.onPause();
        webView1.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onResume() {
        webView1.resumeTimers();
        webView1.onResume();
        super.onResume();
    }
    public void medicineOfDynamicUrl () {
        webView1.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String dynamicURL = webView1.getOriginalUrl();
                if (dynamicURL != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dynamicURL)));
                    if(webView1.canGoBack()) {
                        webView1.goBack();
                    }
                }
            }
        });
    }
    public void downloadListener () {
        webView1.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }
    public void showHomePage() {
        editText1.setCursorVisible(false);
        editText1.getText().clear();
        webLayout.setVisibility(View.GONE);
        bar1.setVisibility(View.GONE);
        editText1.setVisibility(View.VISIBLE);
        homeLayout.setVisibility(View.VISIBLE);
        isHome = 1;
    }
    public void downloadPro() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Remove ads by Downloading Pro Version of Mask Browser");
        alertDialogBuilder.setPositiveButton("Download",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.appmasterbd.maskbrowserpro")));
                    }
                });

        alertDialogBuilder.setNegativeButton("EXIT",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteHistoryCacheCookies();

                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void deleteHistoryCacheCookies() {
        webView1.clearHistory();
        webView1.clearCache(true);
        if (Build.VERSION.SDK_INT < 18) {
            webView1.clearView();
        } else {
            webView1.loadUrl("about:blank");
        }
        Toast.makeText(getApplicationContext(), "History, Cache, Cookies are Cleaned !", Toast.LENGTH_SHORT).show();
    }
}