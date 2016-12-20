package app.rayscast.air.adapters;


import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import app.rayscast.air.R;
import app.rayscast.air.utils.TempThumbStore;


public class LibGridViewAdapter extends BaseAdapter implements Filterable {
    private static String TAG = "Videos";
    private static LibGridViewAdapter instance = null;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Cursor cursor;
    private Bitmap[] thumbnails;
    private long[] durations;
    public VideoThumbnailLoadTask mTask = null;
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CONTACTS};

    private ArrayList<VideoItem> allVideos;
    public ArrayList<VideoItem> displayedVideos;

    private String[] params = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION
    };

    public class VideoItem {
        String videoLocation;
        String videoDisplayName;
        long videoDuration;
        String videoExtension;
        Bitmap thumb;
        String thumbPath;

        public VideoItem(String videoLocation, String videoDisplayName, long videoDuration, Bitmap thumb) {
            this.videoLocation = videoLocation;
            this.videoDisplayName = videoDisplayName;
            this.videoDuration = videoDuration;
            this.videoExtension = videoDisplayName.substring(videoDisplayName.lastIndexOf(".") + 1).toUpperCase();
            this.thumb = thumb;
        }

        public String getVideoLocation() {
            return videoLocation;
        }

        public long getVideoDuration() {
            return videoDuration;
        }

        public String getThumbPath() {
            return thumbPath;
        }

    }

    public void cancelTask() {
        mTask.cancel(true);
    }

    private class VideoThumbnailLoadTask extends AsyncTask<Void, Integer, Bitmap[]> {

        private Cursor c;
        private int count;
        private Bitmap[] thumbs;
        String[] videoNames;

        public VideoThumbnailLoadTask(Cursor cc) {
            c = cc;
            count = 0;
            thumbs = new Bitmap[thumbnails.length];
        }

        @Override
        protected Bitmap[] doInBackground(Void... params) {
            videoNames = new String[thumbs.length];
            while (count < thumbs.length) {
                String data = null;
                try {
                    c.moveToPosition(count);
                    data = c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA));
                    String videoName = c.getString(c.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    durations[count] = c.getLong(c.getColumnIndex(MediaStore.Video.Media.DURATION));
                    videoNames[count] = videoName;

                    Bitmap curThumb = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MICRO_KIND);
                    thumbs[count] = curThumb;
                    //addItem(ic_new VideoItem(data, videoName, durations[count], curThumb));
                    VideoItem videoItem = allVideos.get(count);
                    videoItem.thumb = curThumb;
                    allVideos.set(count, videoItem);
                    //allVideos.add(ic_new VideoItem(data, videoName, durations[count], curThumb));


                    String thumbPath = TempThumbStore.getThumbnail(videoName,curThumb);
                    videoItem.thumbPath = thumbPath;


                    publishProgress(count);
                    count++;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    count++;
                }

            }

            return thumbs;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            thumbnails[values[0]] = thumbs[values[0]];
            LibGridViewAdapter.this.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Bitmap[] bitmaps) {
            thumbnails = thumbs;
            displayedVideos = allVideos;
            LibGridViewAdapter.this.notifyDataSetChanged();
        }
    }

    public static LibGridViewAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new LibGridViewAdapter(context);
        }
        return instance;
    }

    public int getCursorCount() {
        return cursor.getCount();
    }

    private LibGridViewAdapter(Context c) {
        mContext = c;
        allVideos = new ArrayList<>();
        displayedVideos = new ArrayList<>();

        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = new CursorLoader(c, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, params,
                null, // Return all rows
                null, null).loadInBackground();
        //added by monu
        if (cursor != null) {
            thumbnails = new Bitmap[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                thumbnails[i] = null;
            }

            durations = new long[thumbnails.length];
            int count = 0;
            while (count < cursor.getCount()) {
                String data = null;
                try {
                    cursor.moveToPosition(count);
                    data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    durations[count] = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                    //addItem(ic_new VideoItem(data, videoName, durations[count], curThumb));
                    allVideos.add(new VideoItem(data, videoName, durations[count], null));

                    count++;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    count++;
                }

            }
            displayedVideos = allVideos;
            mTask = new VideoThumbnailLoadTask(cursor);
            mTask.execute();
        }
    }


    public int getCount() {
        return displayedVideos.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView videoDuration;
        TextView extension;
    }

    // create a ic_new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        View cell = convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            cell = layoutInflater.inflate(R.layout.lib_grid_view_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) cell.findViewById(R.id.textView);
            viewHolder.videoDuration = (TextView) cell.findViewById(R.id.duration);
            viewHolder.imageView = (ImageView) cell.findViewById(R.id.imageView);
            viewHolder.extension = (TextView) cell.findViewById(R.id.extension);
            cell.setTag(viewHolder);
            //setTags(cell, textView, videoDuration, imageView);
        } else {
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (position < displayedVideos.size()) {
            viewHolder.textView.setText(displayedVideos.get(position).videoDisplayName);
            viewHolder.extension.setText(displayedVideos.get(position).videoExtension);
            viewHolder.videoDuration.setText(formatTime(displayedVideos.get(position).videoDuration));

            if (displayedVideos.get(position).thumb != null) {
                viewHolder.imageView.setImageBitmap(displayedVideos.get(position).thumb);
                viewHolder.imageView.setTag(position);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.inner);
            }

        }
        return cell;
    }


    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = seconds / 60 % 60;
        seconds = seconds % 3600 % 60;
        String time = ((hours < 10) ? "0" + hours : hours) + ":" + ((minutes < 10) ? "0" + minutes : minutes) + ":" + ((seconds >= 10) ? seconds : (seconds == 0) ? "01" : "0" + seconds);
        return time;
    }

    public void showAllVideos() {
        displayedVideos = allVideos;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = allVideos;
                    results.count = allVideos.size();
                } else {

                    ArrayList<VideoItem> filteredVideos = new ArrayList<>();

                    for (VideoItem v : allVideos) {
                        if (v.videoDisplayName.toLowerCase().contains(constraint.toString().toLowerCase())) {

                            filteredVideos.add(v);
                        }
                    }

                    results.values = filteredVideos;
                    results.count = filteredVideos.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                displayedVideos = (ArrayList<VideoItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
