package app.rayscast.air.adapters;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import app.rayscast.air.R;
import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.utils.ChromecastApplication;
import app.rayscast.air.utils.WebServer;
import app.rayscast.air.utils.WebServerAudio;
import app.rayscast.air.utils.WebServerImage;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.MyViewHolder> {

    private Context mContext;
    private File[] mObjects;

    private File mCurrentDir = null;
    private File mPreviousDir = null;
    private Stack<File> mHistory;
    public static final String TAG = "FOLDERS";

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView image;
        public CardView cardView;
        FoldersAdapter foldersAdapter;

        public MyViewHolder(View view, FoldersAdapter foldersAdapter) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
            cardView = (CardView) view.findViewById(R.id.card_view);
            this.foldersAdapter = foldersAdapter;

        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(view.getContext(), "Clicked  = " + getPosition(), Toast.LENGTH_SHORT).show();
            foldersAdapter.changeDir(getPosition());
        }

    }

    public FoldersAdapter(Context context, File currentDir) {
        this.mContext = context;
        this.mCurrentDir = currentDir;
        this.mObjects = getAllFiles(currentDir);
        mHistory = new Stack<>();
    }


    public void changeDir(int id) {
        try {
            if (MainActivity.mFoldersRoot) {
                if (mObjects[id].isDirectory()) {
                    Log.d(TAG, "open dir");
                    MainActivity.mFoldersRoot = false;
                    mPreviousDir = mCurrentDir;
                    mCurrentDir = mObjects[id];

                    mObjects = getAllFiles(mCurrentDir);
                    this.notifyDataSetChanged();
                } else {
                    if (isImage(mObjects[id])) {
                        openImage(id);
                    } else {
                        if (isAudio(mObjects[id])) {
                            castAudio(id);
                        } else {
                            if (isVideo(mObjects[id])) {
                                castVideo(id);
                            }
                        }
                    }
                }

            } else {
                if (mObjects[id] != null) {
                    if (mObjects[id].isDirectory()) {
                        //open directory
                        Log.d(TAG, "open dir, push on stack");
                        mHistory.add(mPreviousDir);
                        mPreviousDir = mCurrentDir;
                        mCurrentDir = mObjects[id];

                        mObjects = getAllFiles(mCurrentDir);
                        this.notifyDataSetChanged();

                    } else {
                        //open file
                        if (isImage(mObjects[id])) {
                            openImage(id);
                        } else {
                            if (isAudio(mObjects[id])) {
                                castAudio(id);
                            } else {
                                if (isVideo(mObjects[id])) {
                                    castVideo(id);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void castVideo(int id) {

        File serverFile = mObjects[id];
        if (ChromecastApplication.getInstance().server == null) {
            ChromecastApplication.getInstance().server = new WebServer(serverFile);
            try {
                ChromecastApplication.getInstance().server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ChromecastApplication.getInstance().server.myFile = serverFile;
        }
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, serverFile.getName());

        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ipdevice;
        //added by monu singh
        String mimeType=null;
        if(serverFile!=null)
        mimeType = getMimeType(serverFile.getName());
        if (mimeType == null) {
            mimeType = "video/mp4";
        }
        ipdevice = String.format("http://%d.%d.%d.%d:1235", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        MediaInfo mSelectedMedia =
                new MediaInfo.Builder(ipdevice)
                        .setContentType(mimeType)
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setMetadata(mediaMetadata)
                        .setMediaTracks(null)
                        .build();
        try {
            VideoCastManager.getInstance().checkConnectivity();
            VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
            ((MainActivity) mContext).showNotification(mSelectedMedia);
            return;
        }
    }

    private void castAudio(int id) {


        File audioFile = mObjects[id];

        String mimeType = getMimeType(audioFile.getName());
        if (mimeType == null) {
            mimeType = "audio/mp3";
        }
        if (ChromecastApplication.getInstance().serverAudio == null) {
            ChromecastApplication.getInstance().serverAudio = new WebServerAudio(audioFile, mimeType);
            try {
                ChromecastApplication.getInstance().serverAudio.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ChromecastApplication.getInstance().serverAudio.myFile = audioFile;
            ChromecastApplication.getInstance().serverAudio.mMimeType = mimeType;
        }
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ipdevice;

        ipdevice = String.format("http://%d.%d.%d.%d:1239/", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        ipdevice += audioFile.getName();
        MediaInfo mSelectedMedia = new MediaInfo.Builder(ipdevice)
                .setContentType(mimeType)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)

                .setMediaTracks(null)
                .build();
        try {
            VideoCastManager.getInstance().checkConnectivity();
            VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
            ((MainActivity) mContext).showNotification(mSelectedMedia);
            return;
        }
    }

    private void openImage(int id) {
        //file is image
        //open picture
        final Dialog d = new Dialog(mContext, R.style.CustomFullScreenDialog);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = d.getWindow();
        // window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        d.setTitle(mObjects[id].getName());
        d.setContentView(R.layout.dialog_image_slider);

        final TextView title = (TextView) d.findViewById(R.id.title);
        title.setText(mObjects[id].getName());

        ImageView buttonCancel = (ImageView) d.findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        List<File> currentImagesInDirectory = new ArrayList<>();
        for (int i = 0; i < mObjects.length; i++) {
            currentImagesInDirectory.add(mObjects[i]);
        }
        final ImageSliderAdapter adapter = new ImageSliderAdapter(mContext, currentImagesInDirectory);

        final ViewPager myPager = (ViewPager) d.findViewById(R.id.container);

        myPager.setAdapter(adapter);
        myPager.setCurrentItem(id);

        castImage(id);

        myPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                castImage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        d.show();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new MyViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        File file = mObjects[position];

        if (file.isDirectory()) {
            //holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.mainColor));
            holder.title.setTextColor(mContext.getResources().getColor(R.color.mainColor));
            holder.image.setImageResource(R.drawable.folder_icon);
            String t = "";
            if (file != null && file.listFiles() != null) {
                t = file.getName() + " (" + file.listFiles().length + ")";
            }
            holder.title.setText(t);
        } else {
            //holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.title.setTextColor(mContext.getResources().getColor(R.color.black));

            if (isImage(file)) {
                String url = "file://" + file.getAbsolutePath();
                Glide.with(mContext)
                        .load(url)
                        .centerCrop()
                        //.thumbnail(0.5f)                            //provide the size multiplier for thumbnail size
                        .crossFade()                                //for a smoothly fading in the loading image into ImageView
                        //.placeholder(R.drawable.noimage)
                        //.override(200,200)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   //saves the source and result data (images) into cache. AKA faster loading, but larger cache. Alternatively use DiskCacheStrategy.RESULT
                        .into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.play_icon);
            }

            holder.title.setText(file.getName());
        }

    }

    @Override
    public int getItemCount() {
        return mObjects.length;

    }

    //Returns whether or not we have a previous dir in our history.  If the stack is not empty, we have one.
    public boolean hasPreviousDir() {
        return !mHistory.isEmpty();
    }

    //return the previous dir and remove it from the stack.
    public File getPreviousDir() {
        return mHistory.pop();
    }

    public boolean changeFolderOnBack() {
        if (hasPreviousDir()) {
            Log.d(TAG, "stack pop");
            mCurrentDir = mPreviousDir;
            mPreviousDir = getPreviousDir();
            mObjects = getAllFiles(mCurrentDir);
            this.notifyDataSetChanged();
            return true;
        } else {
            if (mPreviousDir != null) {
                Log.d(TAG, "back to root");
                mCurrentDir = mPreviousDir;
                mPreviousDir = null;
                mObjects = getAllFiles(mCurrentDir);
                this.notifyDataSetChanged();
                return true;
            } else {
                Log.d(TAG, "its root");
                //return changeFolderOnBackRoot();
                return false;
            }
        }

    }

    public boolean changeFolderOnBackRoot() {
        File parentFile = mCurrentDir.getParentFile();
        if (parentFile != null) {
            Log.d(TAG, "on back, has parent");
            mPreviousDir = mCurrentDir;
            mCurrentDir = parentFile;

            mObjects = getAllFiles(mCurrentDir);
            this.notifyDataSetChanged();
            return true;
        }
        return false;
    }


    //Returns a sorted list of all dirs and files in a given directory.
    private File[] getAllFiles(File f) {
        File[] allFiles = f.listFiles();

        List<File> dirs = new ArrayList<>();
        List<File> files = new ArrayList<>();
        File[] sortedFiles = null;

        if (allFiles != null) {
            for (File file : allFiles) {
                if (file != null) {
                    if (file.isDirectory() && !file.getName().equals("")) {
                        dirs.add(file);
                    } else {
                        //only supported files
                        /// if (isImage(file) || isAudioOrVideo(file)) {
                        files.add(file);
                        // }
                    }
                }
            }

            Collections.sort(dirs);
            Collections.sort(files);

            //list of dirs on top and files on bottom
            dirs.addAll(files);

            sortedFiles = new File[dirs.size()];
            for (int i = 0; i < dirs.size(); i++) {
                sortedFiles[i] = dirs.get(i);
            }
        }
        else {
            sortedFiles = new File[0];
        }

        return sortedFiles;
    }

    private boolean isImage(File file) {
        final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    private boolean isVideo(File file) {
        final String[] okFileExtensions = new String[]{"m3u8", "mp4", "webm"};
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAudio(File file) {
        final String[] okFileExtensions = new String[]{"aac", "mp3", "wav"};
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type;
        String extension = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        //added by monu
        if(type!=null)
        Log.d("URL", type);

        return type;
    }

    private void castImage(int position) {

        // File imageFile = imagesDirFile.listFiles()[position];
        File imageFile = mObjects[position];

        String mimeType = getMimeType(imageFile.getName());
        if (mimeType == null) {
            mimeType = "image/png";
        }
     /*   for (ItemImage item : allImagesList) {
            mimeType = item.getmMimeType();
            break;
        }*/

        if (ChromecastApplication.getInstance().serverImage == null) {
            ChromecastApplication.getInstance().serverImage = new WebServerImage(imageFile, mimeType);
            try {
                ChromecastApplication.getInstance().serverImage.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ChromecastApplication.getInstance().serverImage.myFile = imageFile;
            ChromecastApplication.getInstance().serverImage.mMimeType = mimeType;
        }
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, imageFile.getName());
        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ipdevice;

        ipdevice = String.format("http://%d.%d.%d.%d:1237/", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        ipdevice += imageFile.getName();
        MediaInfo mSelectedMedia = new MediaInfo.Builder(ipdevice)
                .setContentType(mimeType)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .setMediaTracks(null)
                .build();
        try {
            VideoCastManager.getInstance().checkConnectivity();
            VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
            ((MainActivity) mContext).showNotification(mSelectedMedia);
            return;
        }

    }


}
