package app.rayscast.air.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.adapters.LibGridViewAdapter;
import app.rayscast.air.adapters.WebContentAdapter;
import app.rayscast.air.database.DBOpenHelper;
import app.rayscast.air.fragments.ArtistFragment;
import app.rayscast.air.fragments.AudioAlbumFragment;
import app.rayscast.air.fragments.FoldersFragment;
import app.rayscast.air.fragments.HomeFragment;
import app.rayscast.air.fragments.ImageFragment;
import app.rayscast.air.fragments.SongListFragment;
import app.rayscast.air.fragments.VideoBrowserFragment;
import app.rayscast.air.fragments.WebViewFragment;
import app.rayscast.air.models.HomeMenuItem;
import app.rayscast.air.models.ItemSong;
import app.rayscast.air.models.ItemWebURL;
import app.rayscast.air.utils.AudioCasting;
import app.rayscast.air.utils.CustomLog;
import app.rayscast.air.utils.OptionsUtil;
import app.rayscast.air.utils.VideoCasting;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnHomeMenuSelectListener {

    private static final String TAG = "MainActivity";
    private static final String TAG_DRIVE = "DRIVE";
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int REQUEST_OPEN = 5;

    private VideoCastManager mCastManager;

    private DBOpenHelper m_db_helper;

    private MyPagerAdapter pageAdpater;
    private VideoCastConsumer mCastConsumer;
    public static MediaRouteButton mMediaRouteButton;
    private Fragment videoBrowserFragment = new VideoBrowserFragment();
    private Fragment webFragment = null;
    private Fragment imageFragment = null;
    private Fragment foldersFragmnet = null;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mMainLayout;
    private RelativeLayout mAudioLayout;
    private String[] mMenuItems;
    private ActionBarDrawerToggle mDrawerListener;
    private ListView mListView;
    private ProgressDialog dialog;
    private String mData = null;
    private long mDuration = 0;
    private GoogleApiClient mGoogleApiClient;
    private String[] mSpinnerItems = null;
    private int mCurrentFragment = 0;
    private String mMimeType = null;
    public static boolean mImagesRoot = true;
    public static boolean mAudioRoot = true;
    public static boolean mFoldersRoot = true;
    private MediaInfo mediaInfo = null;
    private InputStream mInputStream = null;
    Intent intent;
    String action;
    String type;
    private String mReturnedMimeType = "";
    private View mLayout;
    private String mThumbPath;
    public MenuItem searchMenuItem;
    public MenuItem searchWebMenuItem;
    public static Boolean cameBackToHome = false;
    public static Boolean wentToVideoFragment = false;
    public static Boolean wentToAudioFragment = false;
    public static Boolean wentToArtistFragment = false;
    public static Boolean wentToSongFragment = false;
    public static Boolean wentToWebFragment = false;
    public static String TOKEN_FAV="FAV";
    public static String TOKEN_HISTORY="HISTORY";


    private static final int REQUEST_READ_STORAGE_PERMISSION = 0;

    /**
     * Id to identify a contacts permission request.
     */
//    private static final int REQUEST_READ_LOGS_REQUEST = 1;
    private static final int REQUEST_READ_PHONE_STATE_REQUEST = 1;


    private String[] mUrlHistory = null;

    private ArrayAdapter<String> mMenuAdapter;

    private ArrayAdapter<String> mUrlArrayAdapter;
    private MediaQueueItem[] audioItems;
    private int startPositionAudio;
    private List<ItemSong> filteredSongs;
    private int mStartPositionVideo;
    private ArrayList<LibGridViewAdapter.VideoItem> mDisplayedVideos;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_home:
                changeFragment(100);
                break;
            case R.id.nav_help:

                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.nav_rate:
                rateApp();
                break;
            case R.id.nav_share:
                shareApp();
                break;

            case R.id.nav_facebook:
                facebook();
                break;
            case R.id.nav_feedback:
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"app_support@rayscast.com"});
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feed back");
                startActivity(Intent.createChooser(sendIntent, "Send email..."));
                break;
            case R.id.nav_setting:
                Intent settingintent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingintent);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentRequested(int fragment) {
        Log.e("TAG", "Requested fragment" + fragment);
        switch (fragment) {
            case HomeMenuItem.AUDIO:
                changeFragment(1);
                break;
            case HomeMenuItem.GALLERY:
                changeFragment(2);
                break;
            case HomeMenuItem.VIDEO:
                changeFragment(0);
                break;
            case HomeMenuItem.WEB:
                changeFragment(4);
                break;
            case HomeMenuItem.DROPBOX:
                DbxChooser mChooser = new DbxChooser("6kph7d8yoc116av");

                mChooser.forResultType(DbxChooser.ResultType.DIRECT_LINK)
                        .launch(MainActivity.this, 0);
                break;
            case HomeMenuItem.ADD_LINK:
                //URL
                final Dialog dialogUrl = new Dialog(MainActivity.this);
                dialogUrl.requestWindowFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater inflater = (LayoutInflater)
                        MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) inflater.inflate(R.layout.load_url, null);
                dialogUrl.setContentView(container);

                final ListView listView = (ListView) dialogUrl.findViewById(R.id.list_history);
                loadUrlArray();
                String[] items = mUrlHistory;

                mUrlArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);

                listView.setAdapter(mUrlArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // ListView Clicked item value
                        String itemValue = (String) listView.getItemAtPosition(position);
                        //asynctask
                        getMimeTypeFromUrl(itemValue);
                        dialogUrl.dismiss();
                    }

                });


                dialogUrl.show();

                TextView deleteHistory = (TextView) dialogUrl.findViewById(R.id.delete_history);
                deleteHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAllUrlHistory();

                        String[] items = {};
                        mUrlArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(mUrlArrayAdapter);

                        mUrlArrayAdapter.notifyDataSetChanged();
                    }
                });


                Button cancel = (Button) dialogUrl.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUrl.dismiss();
                    }
                });

                Button continueBtn = (Button) dialogUrl.findViewById(R.id.continueButton);
                continueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText edittext = (EditText) dialogUrl.findViewById(R.id.url);
                        String YouEditTextValue = edittext.getText().toString();

                        if (!YouEditTextValue.contains(getResources().getString(R.string.http)) && !YouEditTextValue.contains(getResources().getString(R.string.https))) {
                            YouEditTextValue = getResources().getString(R.string.http) + YouEditTextValue;
                        }

                        saveUrl(YouEditTextValue);

                        dialogUrl.dismiss();

                        //test
                        //YouEditTextValue = "https://dl-ssl.google.com/android/repository/android-14_r04.zip";
                        //YouEditTextValue = "http://www.japspitz.com/wp-content/uploads/Azlan-and-Portia-Banner-1.jpg";

                        //asynctask
                        getMimeTypeFromUrl(YouEditTextValue);


                    }
                });
                break;
        }
    }

    private class GetMimeTypeTask extends AsyncTask<String, Void, String> {

        private Context mContext;
        private String requestUrl;

        public GetMimeTypeTask(Context context) {
            mContext = context;
        }


        @Override
        protected String doInBackground(String... urls) {
            String mime = "";
            URL url = null;

            if (urls != null) {
                requestUrl = urls[0];
                try {
                    url = new URL(requestUrl);
                    URLConnection u = url.openConnection();
                    mime = u.getContentType();
                    Log.d("MainActivity", "GetMimeTypeTask mime: " + mime);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return mime;
        }

        @Override
        protected void onPostExecute(String result) {
            MainActivity mainActivity = (MainActivity) mContext;
            mainActivity.mReturnedMimeType = result;

            castSomething(requestUrl, result);
            if (result != null)
                Log.d("URL", "mime: " + result);
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        int pagerCount = 3;

        private String[] m_title = {"ALBUMS", "ARTISTS", "AUDIO"};
        private android.support.v4.app.Fragment currentFragment = null;

        public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 2:
                    return new SongListFragment();
                case 1:
                    return new ArtistFragment();
                case 0:

                    return new AudioAlbumFragment();
                default:
                    return new AudioAlbumFragment();
            }
        }

        public android.support.v4.app.Fragment getCurrentFragment() {
            return currentFragment;
        }

        //...
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                currentFragment = ((android.support.v4.app.Fragment) object);
            }
            if (position == 0) {

                MainActivity.wentToAudioFragment = true;
                MainActivity.wentToArtistFragment = false;
                MainActivity.wentToSongFragment = false;
            } else if (position == 1) {

                MainActivity.wentToArtistFragment = true;
                MainActivity.wentToAudioFragment = false;
                MainActivity.wentToSongFragment = false;
            } else if (position == 2) {

                MainActivity.wentToSongFragment = true;
                MainActivity.wentToAudioFragment = false;
                MainActivity.wentToArtistFragment = false;
            } else {
                MainActivity.wentToAudioFragment = true;
                MainActivity.wentToArtistFragment = false;
                MainActivity.wentToSongFragment = false;
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return pagerCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return m_title[position];
        }

        public boolean BackState() {
            currentFragment = getCurrentFragment();
            if (currentFragment instanceof AudioAlbumFragment)
                return ((AudioAlbumFragment) currentFragment).goToMainScreen();
            else if (currentFragment instanceof ArtistFragment)
                return ((ArtistFragment) currentFragment).BackStatue();
            else
                return true;
        }
    }

    public interface OnButtonPressListener {
        public void onButtonPressed(String msg);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main2);





        mLayout = findViewById(R.id.nav_view);
        BaseCastManager.checkGooglePlayServices(this);
        intent = getIntent();
        action = intent.getAction();
        type = intent.getType();
        getIntent().setAction(Intent.ACTION_MAIN);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMainLayout = (RelativeLayout) findViewById(R.id.navigation_content);
        mAudioLayout = (RelativeLayout) findViewById(R.id.navigation_audio);
        m_db_helper = new DBOpenHelper(MainActivity.this);
        Boolean READ_EXTERNAL_STORAGE_PERMISSION = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT > 22) {

            if (!(READ_EXTERNAL_STORAGE_PERMISSION)) {
                String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};

                int permsRequestCode = 200;

                requestPermissions(perms, permsRequestCode);

            } else {
                startBrowsingStorageData();
            }

            if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)) {
                String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

                int permsRequestCode = 400;

                requestPermissions(perms, permsRequestCode);
            }

        } else {
            startBrowsingStorageData();
        }
    }


    public boolean saveUrl(String url) {
        SharedPreferences sp = this.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        int size = sp.getInt("Status_size", 0);
        Log.d("url", "save size: " + size);

        boolean returnValue = false, sameUrl = false;

        for (int i = 0; i < size; i++) {
            String urlString = sp.getString("Status_" + i, null);
            if (urlString.equals(url)) {
                sameUrl = true;
            }
        }

        if (!sameUrl) {
            mEdit1.remove("Status_" + size);
            mEdit1.putString("Status_" + size, url);
            mEdit1.putInt("Status_size", size + 1);
            returnValue = mEdit1.commit();
        }
        Log.d("url", "b" + returnValue);
        return returnValue;
    }

    public void deleteAllUrlHistory() {
        SharedPreferences sp = this.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        int size = sp.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            mEdit1.remove("Status_" + i);
        }
        mEdit1.putInt("Status_size", 0);

        mEdit1.apply();

    }


    public void loadUrlArray() {
        SharedPreferences mSharedPreference1 = this.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);
        mUrlHistory = null;
        int size = mSharedPreference1.getInt("Status_size", 0);
        Log.d("url", "load size: " + size);
        mUrlHistory = new String[size];

        for (int i = 0; i < size; i++) {
            mUrlHistory[i] = mSharedPreference1.getString("Status_" + i, null);
            Log.d("url", "hist[]" + mUrlHistory[i]);
        }
    }


    public void getMimeTypeFromUrl(String url) {

        GetMimeTypeTask task = new GetMimeTypeTask(this);

        task.execute(new String[]{url});

    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type;
        String extension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (type != null)
            Log.d("URL", type);

        return type;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMediaRouteButton.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.e("onCreateOptionsMenu", " Main Activity");
//        getMenuInflater().inflate(R.menu.menu__search_video_frag,menu);
//        MenuItem searchItem=menu.findItem(R.id.action_search_video_frag);
//        searchItem.setVisible(false);

        mMediaRouteButton.setVisibility(View.VISIBLE);
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search_main);

        if (wentToArtistFragment == true || wentToAudioFragment == true || wentToSongFragment == true || wentToVideoFragment == true || wentToWebFragment==true) {
            searchMenuItem.setVisible(true);
            SearchView searchView = (SearchView) searchMenuItem.getActionView();
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMediaRouteButton.setVisibility(View.GONE);
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mMediaRouteButton.setVisibility(View.VISIBLE);
                    return false;
                }
            });

        } else {
            searchMenuItem.setVisible(false);
        }

        if (intent != null && intent.getData() != null && Intent.ACTION_VIEW.equals(action)) {
            action = Intent.ACTION_MAIN;
            mMimeType = intent.getType();
            Log.d("MainActivity", "mime: " + mMimeType);
            String videoPath = null;
            try {
                videoPath = URLDecoder.decode(intent.getData().toString(), "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                videoPath = intent.getData().toString();
            }
            if (videoPath.toLowerCase().startsWith("file://")) {
                videoPath = videoPath.substring(7);
            } else {
                if (videoPath.toLowerCase().startsWith("/htt")) {
                    videoPath = videoPath.substring(1);
                }
            }
            Log.d("MainActivity", "videoPath: " + videoPath);
            if (videoPath.toLowerCase().contains("http")) {
                if (mMimeType == null) {
                    getMimeTypeFromUrl(videoPath);
                } else {
                    castSomething(videoPath, mMimeType);
                    mMimeType = null;
                }
            } else {
                long videoDuration = 0;
                Cursor cursor = this.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Video.Media.DURATION},
                        MediaStore.Video.Media.DATA + "=? ",
                        new String[]{videoPath}, null);


                if (cursor != null && cursor.moveToFirst()) {
                    videoDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    cursor.close();
                }
                if (videoDuration <= 2) {
                    videoDuration = 14400 * 1000;
                }
                try {
                    VideoCastManager.getInstance().checkConnectivity();

                    //VideoCasting.getsIntance().initWebServer(videoPath, videoDuration, 0, getApplicationContext(), gridViewAdapter.displayedVideos, previousPosition);

                } catch (Exception e) {
                    e.printStackTrace();

                    showNotification(videoPath, videoDuration);
                }
            }
        }
        return true;
    }


    @Override
    protected void onResume() {

        mCastManager = VideoCastManager.getInstance();
        if (null != mCastManager) {
            mCastManager.addVideoCastConsumer(mCastConsumer);
            mCastManager.incrementUiCounter();
            invalidateOptionsMenu();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    protected void onPause() {
        mCastManager.decrementUiCounter();
        //mCastManager.removeVideoCastConsumer(mCastConsumer);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        super.onPause();
    }


    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mCurrentFragment == 2 && !mImagesRoot) {
                //images
                changeFragment(2);
                mImagesRoot = true;
            } else {
                if (mCurrentFragment == 100) {
                    if (pageAdpater.BackState()) {
                        showExitDialog();
                    }
                } else {
                    if (mCurrentFragment == 1) {
                        Log.e("onBackPressed", " : " + String.valueOf(mCurrentFragment));
                        changeFragment(100);
                    } else if (mCurrentFragment == 3) {
                        if (!mFoldersRoot) {
                            boolean res = ((FoldersFragment) foldersFragmnet).changeFolderOnBack();
                            if (!res) {
                                //changeFragment(3);
                                mFoldersRoot = true;
                                res = ((FoldersFragment) foldersFragmnet).changeFolderOnBackRoot();
                                if (!res) {
                                    changeFragment(100);
                                }
                            }
                        } else {
                            //root dir
                            boolean res = ((FoldersFragment) foldersFragmnet).changeFolderOnBackRoot();
                            if (!res) {
                                changeFragment(100);
                            }
                        }
                    } else if (mCurrentFragment == 4) {
                        if (((WebViewFragment) webFragment).canGoBack()) {
                            ((WebViewFragment) webFragment).goBack();
                        } else {
                            changeFragment(100);
                        }
                    } else {
                        changeFragment(100);
                    }
                }
            }
        }
    }

    public void showExitDialog() {
        // exit dialog
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void exit() {
        super.onBackPressed();
    }

//    @Override
//    public void onPostCreate(Bundle b) {
//        super.onPostCreate(b);
//
//    }


    /*public void setMainFragment() {
        changeFragment(0); //selectMenuItem(0);
    }*/

    private void showMainScene() {
        if (mMainLayout.getVisibility() == View.GONE) {
            mAudioLayout.setVisibility(View.GONE);
            mMainLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showAudioScene() {
        if (mAudioLayout.getVisibility() == View.GONE) {
            mAudioLayout.setVisibility(View.VISIBLE);
            mMainLayout.setVisibility(View.GONE);
        }
    }

    public void changeFragment(int fragmentId) {
        Fragment fragment;
        int id = fragmentId;
        System.out.println(id);

        switch (fragmentId) {
            case 100:
                fragment = HomeFragment.newInstance();
                cameBackToHome = true;
                wentToAudioFragment = false;
                wentToArtistFragment = false;
                wentToSongFragment = false;
                wentToVideoFragment = false;
                wentToWebFragment=false;
                break;
            case 0:
                Log.e("Video Fragment", " :");
                if (videoBrowserFragment == null) {
                    videoBrowserFragment = new VideoBrowserFragment();
                }
                fragment = videoBrowserFragment;
                cameBackToHome = false;
                wentToVideoFragment = true;
                wentToAudioFragment = false;
                wentToArtistFragment = false;
                wentToSongFragment = false;
                wentToWebFragment=false;

                break;
            case 1:
                Log.e("Audio Fragment", " :");
                if (videoBrowserFragment == null) {
                    videoBrowserFragment = new VideoBrowserFragment();
                }
                fragment = videoBrowserFragment;
                cameBackToHome = false;
                wentToVideoFragment = false;
                wentToWebFragment=false;

                break;
            case 2:
                mImagesRoot = true;
                Log.e("Gallery Chosen", " Hehehe");
                if (imageFragment == null) {
                    imageFragment = new ImageFragment();
                }
                fragment = imageFragment;
                ((ImageFragment) imageFragment).restart();
                cameBackToHome = false;
                wentToVideoFragment = false;
                wentToAudioFragment = false;
                wentToArtistFragment = false;
                wentToSongFragment = false;
                wentToWebFragment=false;
                break;

            case 3:
                Log.e("I guess Audio Fragment", " :");
                mFoldersRoot = true;
                if (foldersFragmnet == null) {
                    foldersFragmnet = new FoldersFragment();
                }
                fragment = foldersFragmnet;
                ((FoldersFragment) foldersFragmnet).restart();
                cameBackToHome = false;
                wentToVideoFragment = false;
                wentToAudioFragment = false;
                wentToArtistFragment = false;
                wentToSongFragment = false;
                wentToWebFragment=false;
                break;
            case 4:
                if (webFragment == null) {
                    webFragment = new WebViewFragment();
                }
                fragment = webFragment;
                wentToWebFragment=true;
                cameBackToHome = false;
                wentToVideoFragment = false;
                wentToAudioFragment = false;
                wentToArtistFragment = false;
                wentToSongFragment = false;
                break;
            default:
                if (videoBrowserFragment == null) {
                    videoBrowserFragment = new VideoBrowserFragment();
                }
                fragment = videoBrowserFragment;
                cameBackToHome = false;
                wentToVideoFragment = false;
                wentToAudioFragment = false;
                wentToArtistFragment = false;
                wentToSongFragment = false;
                wentToWebFragment=false;
                break;
        }

        if (fragmentId == 1)
            showAudioScene();
        else
            showMainScene();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.navigation_content, fragment);
        ft.commitAllowingStateLoss();

        mCurrentFragment = fragmentId;

        //mDrawerLayout.closeDrawer(Gravity.LEFT);

    }


    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setIcon(R.drawable.ray_cast_xxhdpi);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.items_spinner, R.layout.item_spinner);
        Spinner navigationSpinner = new Spinner(getSupportActionBar().getThemedContext());
        navigationSpinner.setAdapter(spinnerAdapter);
        mToolbar.addView(navigationSpinner, 0);

        navigationSpinner.setOnItemSelectedListener(ic_new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "spinner - selected: " + mSpinnerItems[position], Toast.LENGTH_SHORT).show();
                changeFragment(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    private void castSomething(String uri, String mime) {
        if (mime == null || uri == null) {
            Toast.makeText(this, "The link you provided is not a valid link.", Toast.LENGTH_LONG).show();
            return;
        }
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, "Media Content From Website");
        //mediaMetadata.putString(MediaMetadata.KEY_TITLE, uri);
        MediaInfo mSelectedMedia = null;
        try {
            mSelectedMedia =
                    new MediaInfo.Builder(uri)
                            .setContentType(mime)
                            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                            .setMetadata(mediaMetadata)
                            .setMediaTracks(null)
                            .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            VideoCastManager.getInstance().checkConnectivity();
        } catch (Exception e) {
            e.printStackTrace();
            showNotification(mSelectedMedia);
            return;
        }
        try {
            VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, 0);
        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            e.printStackTrace();
        }
    }

    private void initWebServerDropBox(String uri, long size) {

        CustomLog.d("DropBox", "init called with uri: " + uri + " and size: " + size);

        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        //mediaMetadata.putString(MediaMetadata.KEY_TITLE, uri);

        String mimeType;

        mimeType = getMimeType(uri);
        if (mimeType == null) {
            //TODO FIX THIS. find another way to fetch mime type
            mimeType = "video/mp4";
        }


        MediaInfo mSelectedMedia =
                new MediaInfo.Builder(uri)
                        .setContentType(mimeType)
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setMetadata(mediaMetadata)
                        .setMediaTracks(null)
                        .build();
        try {
            VideoCastManager.getInstance().checkConnectivity();
        } catch (Exception e) {
            e.printStackTrace();
            showNotification(mSelectedMedia);
            return;
        }
        try {
            VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, 0);
        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            e.printStackTrace();
        }

    }

    public void showNotification(MediaInfo mediaInfo) {
        try {
            if (mMediaRouteButton != null) {
                mMediaRouteButton.performClick();
                this.mediaInfo = mediaInfo;
                if (!((Activity) this).isFinishing()) {
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            "Loading. Please wait...", true, true);
                }


            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Toast.makeText(this, "Please connect to a chromecast device first.", Toast.LENGTH_LONG).show();
        }
    }

    public void showNotification(MediaInfo mediaInfo, int position, List<ItemSong> filteredSongs) {
        try {
            if (mMediaRouteButton != null) {
                mMediaRouteButton.performClick();
                this.mediaInfo = mediaInfo;
                this.startPositionAudio = position;
                this.filteredSongs = filteredSongs;

                if (!((Activity) this).isFinishing()) {
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            "Loading. Please wait...", true, true);
                }


            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Toast.makeText(this, "Please connect to a chromecast device first.", Toast.LENGTH_LONG).show();
        }
    }

    public void showNotification(MediaQueueItem[] audioItems, int position, List<ItemSong> filteredSongs) {
        try {
            if (mMediaRouteButton != null) {
                mMediaRouteButton.performClick();
                this.audioItems = audioItems;
                this.startPositionAudio = position;
                this.filteredSongs = filteredSongs;

                if (!((Activity) this).isFinishing()) {
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            "Loading. Please wait...", true, true);
                }


            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Toast.makeText(this, "Please connect to a chromecast device first.", Toast.LENGTH_LONG).show();
        }
    }

    public void showNotification(String data, long duration) {
        try {
            if (mMediaRouteButton != null) {
                mMediaRouteButton.performClick();
                mData = data;
                mDuration = duration;
                if (!((Activity) this).isFinishing()) {
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            "Loading. Please wait...", true, true);
                }


            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Toast.makeText(this, "Please connect to a chromecast device first.", Toast.LENGTH_LONG).show();
        }

    }

    public void showNotification(String data, long duration, ArrayList<LibGridViewAdapter.VideoItem> displayedVideos, int previousPosition) {
        try {
            if (mMediaRouteButton != null) {
                mMediaRouteButton.performClick();
                mData = data;
                mDuration = duration;
                mStartPositionVideo = previousPosition;
                mDisplayedVideos = displayedVideos;
                if (!((Activity) this).isFinishing()) {
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            "Loading. Please wait...", true, true);
                }


            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Toast.makeText(this, "Please connect to a chromecast device first.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG_DRIVE, "onActivityResult. requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);
                CustomLog.d("main", "Link to selected file: " + result.getLink());

                initWebServerDropBox(result.getLink().toString(), result.getSize());
            } else {
                // Failed or was cancelled by the user.
            }
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VideoCasting.REQUEST_CODE_DIALOG_SUBTITLE_FILE) {
                Uri uriFile = data.getData();
                Log.d(TAG, "onActivityResult: " + uriFile.getPath());

                VideoCasting.getsIntance().processActivityResult(uriFile, 2);
            } else if (requestCode == VideoCasting.REQUEST_CODE_DIALOG_SUBTITLE_ACTIVITY) {
                String filePath = data.getStringExtra("result");
                Uri uriFilePath = Uri.parse(filePath);
                VideoCasting.getsIntance().processActivityResult(uriFilePath, 1);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void rateApp() {
        String bitxId = getPackageName() + "&hl=en";
        Uri uri = Uri.parse("market://details?id=" + bitxId);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + bitxId)));
        }
    }


    private void shareApp() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "RaysCast Player");
        String sAux = "\nLet me recommend you this application\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName() + " \n\n";
        i.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(i, "choose one"));
    }

    private void facebook() {
        Intent facebookIntent = getOpenFacebookIntent(this);
        startActivity(facebookIntent);
    }

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/455014944704001")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/RaysCast-For-Chromecast-455014944704001/")); //catches and opens a url to the desired page
        }
    }

    public void showURLListDialog(List<ItemWebURL> urls) {
        final Dialog dialogUrl = new Dialog(MainActivity.this);
        dialogUrl.requestWindowFeature(Window.FEATURE_NO_TITLE);


        LayoutInflater inflater = (LayoutInflater)
                MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.dialog_webhistory, null);

        NativeExpressAdView adView = (NativeExpressAdView) container.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("903F81D6795F2E38F24471C87038F8E8").build();
        adView.loadAd(adRequest);

        if (urls == null) {
            container = (ViewGroup) inflater.inflate(R.layout.dialog_webcontent, null);

            dialogUrl.setContentView(container);

            urls = m_db_helper.getAllWebContents();

            final ListView listView = (ListView) dialogUrl.findViewById(R.id.list_history);


            listView.setAdapter(new WebContentAdapter(MainActivity.this, urls,TOKEN_HISTORY));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ItemWebURL itemWebURL = (ItemWebURL) listView.getAdapter().getItem(position);

                    getMimeTypeFromUrl(itemWebURL.getContentURL());
                    dialogUrl.dismiss();
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final ItemWebURL itemWebURL = (ItemWebURL) listView.getAdapter().getItem(position);

                    CharSequence menuItems[] = new CharSequence[]{"Add to Favorites", "Delete from History"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Select Action");
                    builder.setItems(menuItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    m_db_helper.addUrlToFavorite(itemWebURL);
                                    Toast.makeText(MainActivity.this, "Added to Favorite", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    m_db_helper.deleteWebContent(itemWebURL);
                                    ((WebContentAdapter) listView.getAdapter()).deleteItem(itemWebURL);
                                    break;
                            }

                        }
                    });
                    builder.show();

                    return true;
                }
            });


            TextView txtDeleteHistory = (TextView) container.findViewById(R.id.delete_history);

            txtDeleteHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_db_helper.deleteAllWebContents();
                    ((WebContentAdapter) listView.getAdapter()).deleteAllHistoryItem();
                }
            });


            dialogUrl.show();
            return;
        }


        dialogUrl.setContentView(container);
        dialogUrl.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final ListView listView = (ListView) dialogUrl.findViewById(R.id.list_history);

        listView.setAdapter(new WebContentAdapter(MainActivity.this, urls,TOKEN_HISTORY));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemWebURL itemWebURL = (ItemWebURL) listView.getAdapter().getItem(position);

                getMimeTypeFromUrl(itemWebURL.getContentURL());
                dialogUrl.dismiss();
            }
        });

        dialogUrl.show();

    }

    public void showFavListUrl(List<ItemWebURL> urls) {
        final Dialog dialogUrl = new Dialog(MainActivity.this);
        dialogUrl.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater)
                MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.layout_fav_list, null);

        dialogUrl.setContentView(container);


        final ListView listView = (ListView) dialogUrl.findViewById(R.id.list_history);

        listView.setAdapter(new WebContentAdapter(MainActivity.this, urls,TOKEN_FAV));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemWebURL itemWebURL = (ItemWebURL) listView.getAdapter().getItem(position);
                Log.e("ItemWebURL"," Content Name: "+ itemWebURL.getContentName());
                Log.e("ItemWebURL"," Content URL: "+ itemWebURL.getContentURL());
                CustomModel.getInstance().urlSelectedFinished(itemWebURL.getContentURL());

//                ((WebViewFragment)webFragment).loadUrl(itemWebURL.getContentURL());
//                ((WebViewFragment) webFragment).loadUrl(itemWebURL.getContentURL());
//                getMimeTypeFromUrl(itemWebURL.getContentURL());

                dialogUrl.dismiss();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ItemWebURL itemWebURL = (ItemWebURL) listView.getAdapter().getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Do you want to delete this favorite Item?");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_db_helper.deletFavorite(itemWebURL);
                        ((WebContentAdapter) listView.getAdapter()).deleteItem(itemWebURL);
                    }
                });

                builder.show();

                return true;
            }
        });


        TextView txtDeleteHistory = (TextView) container.findViewById(R.id.delete_history);

        txtDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_db_helper.deleteAllFavoritesContents();
                ((WebContentAdapter) listView.getAdapter()).deleteAllHistoryItem();
            }
        });


        dialogUrl.show();
        return;
    }

    public void enterM3UPlaylistUrl(){
        final Dialog dialogUrl = new Dialog(MainActivity.this);
        dialogUrl.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater)
                MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.add_m3u_channel, null);
        dialogUrl.setContentView(container);
        final ListView listView=(ListView)container.findViewById(R.id.list_view_for_m3u_playlist);
        String[] list_of_channel=getResources().getStringArray(R.array.m3uPlaylist);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list_of_channel);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition     = position;
                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                CustomModel.getInstance().urlSelectedFinished(itemValue);
                dialogUrl.dismiss();

            }
        });



        final EditText textUrlM3uChannel=(EditText)container.findViewById(R.id.text_url_m3u_channel);
        TextView okButtonM3uDialog=(TextView)container.findViewById(R.id.ok_button_m3u_channel_url);
        TextView cancelButtonM3uDialog=(TextView)container.findViewById(R.id.cancel_button_m3u_channel_url);
        okButtonM3uDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m3uChannelrl=textUrlM3uChannel.getText().toString().trim();
                if(m3uChannelrl.isEmpty()){
                    Toast.makeText(MainActivity.this, "The URL is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("m3u channel url entered is",": "+m3uChannelrl);
                    CustomModel.getInstance().urlSelectedFinished(m3uChannelrl);
                    dialogUrl.dismiss();
                }
            }
        });
        cancelButtonM3uDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUrl.dismiss();
            }
        });
        dialogUrl.show();
        return;
    }







    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        switch (requestCode) {

            case 200:
                if (grantResults.length != 0) {
                    boolean externalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;


                    if ((externalStorage)) {
                        Snackbar.make(mLayout, "External Storage Permission granted.",
                                Snackbar.LENGTH_SHORT)
                                .show();
                        startBrowsingStorageData();
                    } else {
                        Snackbar.make(mLayout, "External Storage Permission was denied.",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            case 300:
                if (grantResults.length != 0) {
                    boolean readPhoneState = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readPhoneState) {


                        Snackbar.make(mLayout, "Read Phone state Permission granted.",
                                Snackbar.LENGTH_SHORT)
                                .show();

                    } else {
                        // Permission request was denied.
                        Snackbar.make(mLayout, "Read Phone state Permission was denied.",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            case 400:
                if (grantResults.length != 0) {
                    boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeExternalStorage) {
                        Snackbar.make(mLayout, "Write external storage Permission granted.",
                                Snackbar.LENGTH_SHORT)
                                .show();

                    } else {
                        // Permission request was denied.
                        Snackbar.make(mLayout, "Write external storage Permission was denied.",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
                break;

        }


    }


    void startBrowsingStorageData() {

        ViewPager mPager = (ViewPager) findViewById(R.id.vpPager);
        pageAdpater = new MyPagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(pageAdpater);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int previousPage;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                previousPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupActionBar();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        mMenuItems = getResources().getStringArray(R.array.items_menu);
        mSpinnerItems = getResources().getStringArray(R.array.items_spinner);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        loadUrlArray();

        mDrawerListener = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerListener);
        mDrawerListener.syncState();
        mCastConsumer = new VideoCastConsumerImpl() {

            @Override
            public void onFailed(int resourceId, int statusCode) {
            }

            @Override
            public void onApplicationConnected(ApplicationMetadata appMetadata, String sessionId,
                                               boolean wasLaunched) {
                if (mData != null) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    VideoCasting.getsIntance().initWebServer(mData, mDuration, 0, MainActivity.this, mDisplayedVideos, mStartPositionVideo);
                    mData = null;
                }
                if (mediaInfo != null) {
                    //VideoCastManager.getInstance().loadMedia(mediaInfo, true, 0);
                    AudioCasting.getsIntance().loadAudio(mediaInfo, true, 0, filteredSongs, startPositionAudio, MainActivity.this);
                    if (mCastManager.getRouteInfo() != null) {
                        OptionsUtil.setOption(getApplicationContext(), "selectedRoute", mCastManager.getRouteInfo().getId());
                    }
                    mediaInfo = null;
                }
                if (audioItems != null) {
                    if (audioItems.length > 0) {
                        AudioCasting.getsIntance().loadAudioQueue(audioItems, startPositionAudio, MediaStatus.REPEAT_MODE_REPEAT_ALL, MainActivity.this, filteredSongs);
                        if (mCastManager.getRouteInfo() != null) {
                            OptionsUtil.setOption(getApplicationContext(), "selectedRoute", mCastManager.getRouteInfo().getId());
                        }
                        audioItems = null;
                    }
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }

            }

            @Override
            public void onDisconnected() {
                invalidateOptionsMenu();
            }

            @Override
            public void onConnectionSuspended(int cause) {

            }

            @Override
            public void onConnectivityRecovered() {
                invalidateOptionsMenu();
            }

            @Override
            public void onCastDeviceDetected(final MediaRouter.RouteInfo info) {
                if (OptionsUtil.getBooleanOption(getApplicationContext(), "REMEMBER_CAST_DEVICE", false)) {
                    String selectedRoute = OptionsUtil.getStringOption(getApplicationContext(), "selectedRoute", "QINGLI");
                    if (!TextUtils.isEmpty(selectedRoute) && info.getId().equals(selectedRoute)) {
                        mCastManager.setDevice(CastDevice.getFromBundle(info.getExtras()));
                    }
                }
            }
        };

        mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
//        mMediaRouteButton.setVisibility(View.GONE);
        VideoCastManager.getInstance().addMediaRouterButton(mMediaRouteButton);
        changeFragment(100);
        mDrawerListener.syncState();
        new SaveDefaultIconTask().execute();
    }


    class SaveDefaultIconTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Open your local db as the input stream
            InputStream myInput = null;
            try {
                myInput = getAssets().open("default_album_art.png");

                File tempDir = new File(Environment.getExternalStorageDirectory() + "/.RayCast");
                if (!tempDir.exists()) {
                    tempDir.mkdir();
                }

                // Path to the just created empty db
                String outFileName = tempDir + "/default_album_art.png";

                // Open the empty db as the output stream
                OutputStream myOutput = new FileOutputStream(outFileName);

                // transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                // Close the streams
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                sharedPreferences.edit().putString("default_albumart", outFileName).commit();
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



}
