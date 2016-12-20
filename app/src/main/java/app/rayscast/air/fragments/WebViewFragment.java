package app.rayscast.air.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import app.rayscast.air.R;
import app.rayscast.air.activity.CustomModel;
import app.rayscast.air.activity.HelpActivity;
import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.adapters.WebContentAdapter;
import app.rayscast.air.database.DBOpenHelper;
import app.rayscast.air.models.ItemWebURL;
import app.rayscast.air.utils.AsyncResponse;
import app.rayscast.air.utils.OptionsUtil;

/**
 * A  simple  {@link  Fragment}  subclass.
 */
public class WebViewFragment extends Fragment implements CustomModel.OnCustomStateListener {

    private SearchView searchView;
    private WebView webView;
    public List<String> urlLists;
    private List<ItemWebURL> urlListRecieved = new ArrayList<ItemWebURL>();
    private String serverURL;
    private ProgressBar mProgressBar;
    String historyUrl = "";

    private String orignialUserAgent = "";

    private boolean desktopSite = false;

    private DBOpenHelper m_db_helper;
    private static String adURL = "";
    private TextView tv_castSomething;
    private Handler castBtnHandler;
    private String WebTitle="";


    public WebViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        m_db_helper = new DBOpenHelper(getActivity());

        urlLists = new ArrayList<>();

        CustomModel.getInstance().setListener(this);


        webView = (WebView) view.findViewById(R.id.web_view);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);

        orignialUserAgent = webView.getSettings().getUserAgentString();
        Log.e(orignialUserAgent, "bbbbbbbbbbbbbbbbbb");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAllowContentAccess(true);

        webView.setWebViewClient(new myWebClient());
        webView.setWebChromeClient(new myWebChromeClient());
        webView.loadUrl("https://www.google.com");

        castBtnHandler = new Handler();
        tv_castSomething = (TextView) view.findViewById(R.id.btn_cast);
        tv_castSomething.setBackgroundColor(Color.RED);
        tv_castSomething.setTextColor(Color.WHITE);
        tv_castSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlLists != null) {
                    if (urlLists.size() > 0) {
                        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                            ((AppCompatActivity) getActivity()).getSupportActionBar().openOptionsMenu();
                    } else {
                        Toast.makeText(getActivity(), "No media found in this page", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No media found in this page", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_webcontent, menu);

        if (getActivity() != null)
            desktopSite = OptionsUtil.getBooleanOption(getActivity(), "Desktop", false);

        MenuItem item = menu.findItem(R.id.action_useragent);
        item.setChecked(desktopSite);


        final MenuItem searchMenuItem = menu.findItem(R.id.action_search_main);
        if (MainActivity.wentToWebFragment == true) {
            searchMenuItem.setVisible(true);


            searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
            searchView.setIconifiedByDefault(true);


            searchView.setQueryHint("Enter Address");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    urlLists = new ArrayList<String>();
                    if (desktopSite) {
                        webView.getSettings().setUserAgentString("Mozilla/5.0  (Windows  NT  5.1;  rv:31.0)  Gecko/20100101  Firefox/31.0");
                    } else {
                        webView.getSettings().setUserAgentString(orignialUserAgent);
                    }


                    if (query.startsWith("http://") || query.startsWith("https://")) {
                        webView.loadUrl(query.trim());
                    } else {
                        webView.loadUrl("http://" + query.trim());
                    }


                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    MenuItemCompat.collapseActionView(searchMenuItem);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });

            searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        searchView.setQuery(serverURL, false);
                    }
                }
            });
            changeSearchViewTextColor(searchView);
        } else {
            searchMenuItem.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_scan:
                showURLListDialog();

                break;
            case R.id.action_favorite:
//                new getURLTaskRetruningItemURL(this).execute();
//                if(urlListRecieved.isEmpty()){
//                    Toast.makeText(getActivity(),"No URL to Recieve",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    for(int i=0;i<urlListRecieved.size();i++){
//                        m_db_helper.addUrlToFavorite(urlListRecieved.get(i));
//                    }
//                }
                ItemWebURL itemWebURL = new ItemWebURL();
                Log.e("++++ ", "Title " + webView.getTitle());
                Log.e("++++ ", "URL " + webView.getUrl());
                itemWebURL.setContentName(webView.getTitle());
                itemWebURL.setContentURL(webView.getUrl());
                m_db_helper.addUrlToFavorite(itemWebURL);
                Log.e("Hope", "++++");
                break;
            case R.id.action_history:
                showURLHistoryDialog();
                break;
            case R.id.my_bookmark:
                new GetFavTask().execute();
                break;
            case R.id.action_useragent:
                desktopSite = !desktopSite;
                if (getActivity() != null)
                    OptionsUtil.setOption(getActivity(), "Desktop", desktopSite);
                item.setChecked(desktopSite);
                break;
            case R.id.action_help:
                Intent helpIntent = new Intent(getActivity(), HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.refresh_page:
                webView.reload();
                break;
//            case R.id.action_add_link_m3u:
//                ((MainActivity) getActivity()).enterM3UPlaylistUrl();
//                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public boolean canGoBack() {
        if (webView != null)
            return webView.canGoBack();
        else
            return false;
    }

    public void goBack() {
        Log.e("Current Web Page", ": " + webView.getUrl());
        webView.goBack();
        tv_castSomething.setBackgroundColor(Color.RED);
        tv_castSomething.setTextColor(Color.WHITE);
        urlLists.clear();
    }


//    public void loadUrl(String urlToBeLoaded){
//        webView.loadUrl(urlToBeLoaded);
//    }

//    public void checkLastURL() {
//        WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();
//        if (mWebBackForwardList.getCurrentIndex() > 0) {
//            historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
//        }
//        Log.e("historyURL",": "+historyUrl);
//        webView.loadUrl(historyUrl);
//    }

    private void showURLListDialog() {
        if (urlLists.size() > 0) {
            new getURLTask().execute();
//            ((MainActivity)getActivity()).showURLListDialog(urlLists);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No  Content Detected");
            builder.setMessage("Sorry,But We Could Not Detect Any Video Link To Play.Please Wait Untill Loading Has Finished.If The Website Contains a Media Player,Start The Media And Wait Some Seconds. In The Case The Media Link Is Still Not Detected,Request Desktop Site And Try Again.");
            builder.setNegativeButton("OK", null);
            builder.show();
        }
    }

    private void showURLHistoryDialog() {
        ((MainActivity) getActivity()).showURLListDialog(null);
    }

    @Override
    public void urlSelected(String urlSelected) {
        webView.loadUrl(urlSelected);
    }


//    @Override
//    public void processFinish(List<ItemWebURL> output) {
//        urlListRecieved=output;
//    }

    class myWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {

            //  TODO  Auto-generated  method  stub
//            searchView.setQuery(url,  false);
            if (url.toLowerCase().contains("trafficstars.com")
                    || url.toLowerCase().contains("tracker.adxpansion.com")
                    || url.toLowerCase().contains("adxpansion.com")
                    || url.toLowerCase().contains("onclickads.net")
                    || url.toLowerCase().contains("sierra-fox.com")
                    || url.toLowerCase().contains("trackvoluum.com")
                    || url.toLowerCase().contains("voluums.com")
                    || url.toLowerCase().contains("medleyads.com")
                    || url.toLowerCase().contains("bidvertiser.com")
                    || url.toLowerCase().contains("trafficjunky.net")
                    || url.toLowerCase().contains("nativex.com")
                    || url.toLowerCase().contains("juicyads.com")
                    || url.toLowerCase().contains("plugrush.com")
                    || url.toLowerCase().contains("trafficforce.com")
                    || url.toLowerCase().contains("adultadworld.com")
                    || url.toLowerCase().contains("ero-advertising.com")
                    || url.toLowerCase().contains("trafficbroker.com")
                    || url.toLowerCase().contains("plugz.co")
                    || url.toLowerCase().contains("exoclick.com")
                    || url.toLowerCase().contains("plugclick.com")
                    || url.toLowerCase().contains("adclx.com")
                    || url.toLowerCase().contains("exclick.net")
                    || url.toLowerCase().contains("juicyads.com")) {
                return true;
            }

            if (desktopSite) {
                view.getSettings().setUserAgentString("Mozilla/5.0  (Windows  NT  5.1;  rv:31.0)  Gecko/20100101  Firefox/31.0");
            } else {
                view.getSettings().setUserAgentString(orignialUserAgent);
            }
            view.loadUrl(url);

            return false;
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//            Log.e("URLL ",": "+url);
//            Log.e("Current URL Loaded is ",": "+url);
            if (url.toLowerCase().contains("m3u8?") || url.toLowerCase().contains("mp4?") || url.toLowerCase().contains("mp3?") || url.toLowerCase().contains("mkv?") || url.toLowerCase().contains("webm?")
                    || url.toLowerCase().contains("flv?") || url.endsWith(".m3u8") || url.endsWith(".mkv") || url.endsWith("1234") || url.startsWith("rtsp:") || url.startsWith("rtmp") || url.startsWith("http://goo.gl/")
                    || url.endsWith(".flv") || url.endsWith("anil") || url.endsWith(".mp3") || url.startsWith("mms:") || url.endsWith(".flv") || url.endsWith(".mp4")) {
                if (!urlLists.contains(url)) {
                    urlLists.add(0, url);
//                    castBtnHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(urlLists.size() > 0){
//                                Log.e("++++"," Setting the color to Green");
//                                tv_castSomething.setBackgroundColor(Color.GREEN);
//                                tv_castSomething.setTextColor(Color.WHITE);
//                            }else{
//                                Log.e("++++"," Setting the color to Red");
//                                tv_castSomething.setBackgroundColor(Color.RED);
//                                tv_castSomething.setTextColor(Color.WHITE);
//                            }
//                        }
//                    });
                    if (urlLists.size() > 0) {

                        castBtnHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                WebTitle=webView.getTitle();
                                Log.e("++++", " Setting the color to Green");
                                tv_castSomething.setBackgroundColor(Color.GREEN);
                                tv_castSomething.setTextColor(Color.WHITE);
                            }
                        });
//                    } else {
//                        castBtnHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.e("++++", " Setting the color to RED");
//                                tv_castSomething.setBackgroundColor(Color.RED);
//                                tv_castSomething.setTextColor(Color.WHITE);
//                            }
//                        });
                    }
                }
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            urlLists.clear();
            Log.e("onPageStarted", "+++++");
            castBtnHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv_castSomething.setBackgroundColor(Color.RED);
                    tv_castSomething.setTextColor(Color.WHITE);
                }
            });
            mProgressBar.setProgress(0);
            serverURL = url;
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setProgress(100);
            serverURL = url;
            mProgressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }

    class myWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }


    class getURLTask extends AsyncTask<Void, Void, Void> {
        List<ItemWebURL> webURLs;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            webURLs = new ArrayList<>();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Checking  Contents");
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < urlLists.size(); i++) {
                String URL = urlLists.get(i);
                if (m_db_helper.isExistsInContent(URL)) {
                    ItemWebURL webURL = m_db_helper.getWebURL(URL);
                    webURLs.add(webURL);
                } else {
                    ItemWebURL webURL = new ItemWebURL();
                    webURL.setContentURL(URL);
                    webURL.setContentName(getFileNameFromURL(URL));
                    m_db_helper.createWebContentWithItem(webURL);
                    webURLs.add(webURL);
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.dismiss();

            ((MainActivity) getActivity()).showURLListDialog(webURLs);
            super.onPostExecute(aVoid);
        }
    }
//    class getURLTaskRetruningItemURL extends AsyncTask<Void, Void, Void> {
//        List<ItemWebURL> webURLs = new ArrayList<>();
//
//        ProgressDialog pDialog;
//        public AsyncResponse delegate;
//        public getURLTaskRetruningItemURL(AsyncResponse listner){
//            this.delegate=listner;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setTitle("Checking  Contents");
//            pDialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            for (int i = 0; i < urlLists.size(); i++) {
//                String URL = urlLists.get(i);
//                if (m_db_helper.isExistsInContent(URL)) {
//                    ItemWebURL webURL = m_db_helper.getWebURL(URL);
//                    webURLs.add(webURL);
//                } else {
//                    ItemWebURL webURL = new ItemWebURL();
//                    webURL.setContentURL(URL);
//                    webURL.setContentName(getFileNameFromURL(URL));
//                    m_db_helper.createWebContentWithItem(webURL);
//                    webURLs.add(webURL);
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            pDialog.dismiss();
////            ((MainActivity) getActivity()).showURLListDialog(webURLs);
//            delegate.processFinish(webURLs);
//            super.onPostExecute(aVoid);
//        }
//    }

    private String getFileNameFromURL(String serverURL) {
        Log.e("Server URL ",": "+serverURL);
        String fileName = "";
        int index = serverURL.lastIndexOf("/") + 1;
        while (index >= 0) {
            fileName = serverURL.substring(index).split("\\?")[0].split("#")[0];

            if (fileName.endsWith(".m3u8") || fileName.endsWith(".mkv") || fileName.endsWith("1234") || fileName.endsWith(".flv") || fileName.endsWith("anil") ||
                    fileName.endsWith(".mp3") || fileName.endsWith(".flv") || fileName.endsWith(".mp4")) {
                break;
            }
            index--;
        }
        return WebTitle;
    }


    class GetFavTask extends AsyncTask<Void, Void, Void> {

        List<ItemWebURL> webURLs;


        @Override
        protected void onPreExecute() {
            webURLs = new ArrayList<>();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            webURLs = m_db_helper.getAllFavorites();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (webURLs.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("No content");
                builder.setMessage("No  favorite  content found");
                builder.setNegativeButton("OK", null);
                builder.show();
            } else {
                ((MainActivity) getActivity()).showFavListUrl(webURLs);
            }
            super.onPostExecute(aVoid);
        }
    }


    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.BLACK);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }


}
