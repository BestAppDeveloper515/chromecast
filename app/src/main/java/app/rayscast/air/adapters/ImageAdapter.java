package app.rayscast.air.adapters;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.models.ItemDirectory;
import app.rayscast.air.models.ItemImage;
import app.rayscast.air.utils.ChromecastApplication;
import app.rayscast.air.utils.CustomLog;
import app.rayscast.air.utils.WebServerImage;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> implements Filterable {

    private File imagesDirFile;
    private Context mContext;
    private List<ItemDirectory> directoryList;
    private List<ItemDirectory> filteredDirectories;
    private List<ItemImage> allImagesList;
    private List<File> currentImagesInDirectory = new ArrayList<>();
    private List<File> filteredImagesInDirectory;
    private final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
    private CharSequence mFilterText;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView image;
        ImageAdapter imageAdapter;
        View card;

        public MyViewHolder(View view, ImageAdapter imageAdapter) {
            super(view);
            view.setOnClickListener(this);
            card = view;
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);

            this.imageAdapter = imageAdapter;

        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(view.getContext(), "Clicked  = " + getPosition(), Toast.LENGTH_SHORT).show();
            imageAdapter.changeDir(getPosition());
        }

    }

    public ImageAdapter(Context context, List<ItemDirectory> directoryList, List<ItemImage> imageList) {
        this.mContext = context;
        this.directoryList = directoryList;
        filteredDirectories = directoryList;
        this.allImagesList = imageList;
    }


    public boolean isImage(File file) {
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    public void changeDir(int id) {
        if (MainActivity.mImagesRoot) {
            //open directory
            if (filteredDirectories.get(id).getDirectoryFile().isDirectory()) {
                imagesDirFile = filteredDirectories.get(id).getDirectoryFile();
                MainActivity.mImagesRoot = false;

                currentImagesInDirectory.clear();
                File files[] = imagesDirFile.listFiles();
                for (File item : files) {
                    if (item.isDirectory() || isImage(item)) {
                        currentImagesInDirectory.add(item);
                    }
                }

                currentImagesInDirectory = getAllFiles(currentImagesInDirectory);
                filteredImagesInDirectory = currentImagesInDirectory;
                this.getFilter().filter(mFilterText);
                this.notifyDataSetChanged();
            }

        } else {
            if(imagesDirFile!=null)
            if (imagesDirFile.listFiles()[id].isDirectory()) {
                //open directory
                imagesDirFile = imagesDirFile.listFiles()[id];
                currentImagesInDirectory.clear();
                File files[] = imagesDirFile.listFiles();
                for (File item : files) {
                    if (item.isDirectory() || isImage(item)) {
                        currentImagesInDirectory.add(item);
                    }
                }
                currentImagesInDirectory = getAllFiles(currentImagesInDirectory);
                filteredImagesInDirectory = currentImagesInDirectory;
                this.getFilter().filter(mFilterText);
                this.notifyDataSetChanged();
            } else {
                //open picture
                final Dialog d = new Dialog(mContext, R.style.CustomFullScreenDialog);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Window window = d.getWindow();
                // window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                d.setTitle(imagesDirFile.getName());
                d.setContentView(R.layout.dialog_image_slider);

                final TextView title = (TextView) d.findViewById(R.id.title);
                title.setText(imagesDirFile.getName());

                ImageView buttonCancel = (ImageView) d.findViewById(R.id.cancel);
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });

               // final ImageSliderAdapter adapter = ic_new ImageSliderAdapter(mContext, imagesDirFile);
                final ImageSliderAdapter adapter = new ImageSliderAdapter(mContext, filteredImagesInDirectory);

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
        }

    }

    private void castImage(int position) {

       // File imageFile = imagesDirFile.listFiles()[position];
        File imageFile = filteredImagesInDirectory.get(position);

        String mimeType = "audio/mp3";
        for (ItemImage item : allImagesList) {
            mimeType = item.getmMimeType();
            break;
        }
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
        } catch (Exception e) {
            e.printStackTrace();
            ((MainActivity) mContext).showNotification(mSelectedMedia);
            return;
        }
        try {
            VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, 0);
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new MyViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        File imageFile = null;
try {

    if (MainActivity.mImagesRoot) {
        imageFile = filteredDirectories.get(position).getDirectoryFile();

    } else {
        // imageFile = imagesDirFile.listFiles()[position];
        imageFile = filteredImagesInDirectory.get(position);
    }
}catch (Exception e)
{

}
        if(imageFile!=null) {

            String url = "file://" + imageFile.getAbsolutePath();
            CustomLog.d("onBind", "dir=" + url);


            if (!isImage(imageFile) && !imageFile.isDirectory()) {
                //if its not a image, do not show it
                holder.title.setVisibility(View.GONE);

                holder.image.setVisibility(View.GONE);
                holder.card.setVisibility(View.GONE);

            } else {
                holder.card.setVisibility(View.VISIBLE);

                if (imageFile.isDirectory()) {
                    holder.title.setText(imageFile.getName());
                    File files[] = imageFile.listFiles();
                    //addede by monu
                    if (files != null) {

                        if (files.length > 0) {
                            int i = 0;
                            boolean previewFound = false;
                            while (i < files.length) {
                                if (files[i].isDirectory() || !isImage(files[i])) {
                                    i++;
                                } else {
                                    previewFound = true;
                                    break;
                                }
                            }
                            if (previewFound) {
                                url = "file://" + files[i].getAbsolutePath();
                            } else {
                                url = "file://" + files[0].getAbsolutePath();
                            }
                        }
                    }
                    holder.image.setVisibility(View.VISIBLE);
                    holder.title.setVisibility(View.VISIBLE);

                } else {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.title.setVisibility(View.GONE);

                }

                Glide.with(mContext)
                        .load(url)
                        .centerCrop()
                        //.thumbnail(0.5f)                            //provide the size multiplier for thumbnail size
                        .crossFade()                                //for a smoothly fading in the loading image into ImageView
                        //.placeholder(R.drawable.noimage)
                        //.override(200,200)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   //saves the source and result data (images) into cache. AKA faster loading, but larger cache. Alternatively use DiskCacheStrategy.RESULT
                        .into(holder.image);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (MainActivity.mImagesRoot) {
            if (filteredDirectories != null) {
                return filteredDirectories.size();
            }
        } else {
            if (filteredImagesInDirectory != null) {
                //return imagesDirFile.listFiles().length;
                return filteredImagesInDirectory.size();
            }
        }
        return 0;
    }

    //Returns a sorted list of all dirs and files in a given directory.
    private List<File> getAllFiles(List<File> listOfPictures) {

        List<File> dirs = new ArrayList<>();
        List<File> files = new ArrayList<>();

        if (listOfPictures != null) {
            for (int i=0; i<listOfPictures.size(); i++) {
                File file = listOfPictures.get(i);
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

        }
        else {
            //no pictures
        }

        return dirs;
    }



    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                mFilterText = constraint;
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    if (MainActivity.mImagesRoot) {
                        results.values = directoryList;
                        results.count = directoryList.size();
                    }
                    else {
                        results.values = currentImagesInDirectory;
                        results.count = currentImagesInDirectory.size();
                    }
                }
                else {
                    if (MainActivity.mImagesRoot) {
                        List<ItemDirectory> filtereddirs = new ArrayList<>();

                        for (ItemDirectory v : directoryList) {
                            if (v.getDirectoryFile().getName().toLowerCase().contains( constraint.toString().toLowerCase() )) {
                                filtereddirs.add(v);
                            }
                        }

                        results.values = filtereddirs;
                        results.count = filtereddirs.size();
                    } else {
                        List<File> filteredVideos = new ArrayList<>();

                        for (File v : currentImagesInDirectory) {
                            if (v.getName().toLowerCase().contains( constraint.toString().toLowerCase() )) {
                                filteredVideos.add(v);
                            }
                        }

                        results.values = filteredVideos;
                        results.count = filteredVideos.size();
                    }

                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (MainActivity.mImagesRoot) {
                    filteredDirectories = (List<ItemDirectory>) results.values;
                }
                else {
                    filteredImagesInDirectory = (ArrayList<File>) results.values;
                }
                notifyDataSetChanged();
            }
        };
    }
}
