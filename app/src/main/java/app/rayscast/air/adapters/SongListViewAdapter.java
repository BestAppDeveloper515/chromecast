package app.rayscast.air.adapters;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.cast.CastStatusCodes;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.common.images.WebImage;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.CastException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.tracks.OnTracksSelectedListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.models.ItemSong;
import app.rayscast.air.utils.AudioCasting;
import app.rayscast.air.utils.ChromecastApplication;
import app.rayscast.air.utils.ImageCaster;
import app.rayscast.air.utils.OptionsUtil;
import app.rayscast.air.utils.WebServerAudio;

/**
 * Created by Qing on 4/16/2016.
 */
public class SongListViewAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = SongListViewAdapter.class.getSimpleName();

    private List<ItemSong> songLists, filteredSongs;
    private Context mContext;
    private LayoutInflater inflater;

    public class ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView artistView;
    }

    public SongListViewAdapter(Context context, int albumid)
    {
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        songLists = new ArrayList<>();
        filteredSongs = new ArrayList<>();

        String[] projection = new String[] { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.MIME_TYPE};

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] selectionArgs = null;
        if (albumid != -1) {
            selection += " AND " + MediaStore.Audio.Media.ALBUM_ID + "=?";
            selectionArgs = new String[1];
            selectionArgs[0] = String.valueOf(albumid);
        }
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, null);
        Uri sArtworkUri;
        while (cursor.moveToNext()) {
            String songName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            String albumname = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

            String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            File file = new File(data);

            long songId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            System.out.println(songId);
            long albumeId = cursor
                    .getLong(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            System.out.println("Albums Id External ..." + albumeId);
            String artistName = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

            Uri albumArtUri;
            String songUri = cursor
                    .getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            String mimetype = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
            sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            albumArtUri = ContentUris.withAppendedId(sArtworkUri,
                    albumeId);
if(songName!=null) {
    final int lastPeriodPos = songName.lastIndexOf('.');
    if (lastPeriodPos > 0) {
        songName = songName.substring(0, lastPeriodPos);
    }
}
            ItemSong audioListModel = new ItemSong((int) songId,
                    songName, artistName, songUri,
                    albumArtUri.toString(), mimetype, albumname, file.getAbsolutePath());
            songLists.add(audioListModel);
        }

        filteredSongs = songLists;
    }

    @Override
    public int getCount() {
        return filteredSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            cell = inflater.inflate(R.layout.item_song, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleView = (TextView) cell.findViewById(R.id.txtSongTitle);
            viewHolder.imageView = (ImageView) cell.findViewById(R.id.img_list_all);
            viewHolder.artistView = (TextView) cell.findViewById(R.id.txtArtistTitle);
            cell.setTag(viewHolder);
            //setTags(cell, textView, videoDuration, imageView);
        } else {
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (position < filteredSongs.size()) {
            viewHolder.titleView.setText(filteredSongs.get(position).getSongName());
            viewHolder.artistView.setText(filteredSongs.get(position).getSongArtistName());

            if (!filteredSongs.get(position).getImageUrl().equals(""))
            {
                Picasso.with(mContext).load(filteredSongs.get(position).getImageUrl())
                        .placeholder(R.drawable.audio_icon_hdpi)
                        .error(R.drawable.audio_icon_hdpi).into(viewHolder.imageView);
            }
        }
        return cell;
    }

    public void itemclicked(int position)
    {
        castAudio(position);
    }

    private void castAudio(int position) {

        File audioFile = new File(filteredSongs.get(position).getFilePath());

        if (OptionsUtil.getBooleanOption(mContext, "USE_LOCAL_MUSICPLAYER", false))
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(audioFile), "audio/*");
            mContext.startActivity(intent);
            return;
        }

        String author = "Author: " + filteredSongs.get(position).getSongArtistName();
        String album = "Album: " + filteredSongs.get(position).getAudioAlbum();
        String name = audioFile.getName();
        String mimeType = filteredSongs.get(position).getMime_type();

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

        mediaMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, author);
        mediaMetadata.putString(MediaMetadata.KEY_ALBUM_TITLE, album);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, name);

        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ipdevice;

        ipdevice = String.format("http://%d.%d.%d.%d:1239/", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        //ipdevice = String.format("http://%d.%d.%d.%d:1235", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        ipdevice += audioFile.getName();
        if(filteredSongs.get(position).getImageUrl()!=null) {
            String albumArtUrl = ImageCaster.castImage(mContext, Uri.parse(filteredSongs.get(position).getImageUrl()));
            if(albumArtUrl!=null)
            mediaMetadata.addImage(new WebImage(Uri.parse(albumArtUrl)));

        }
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
            ((MainActivity)mContext).showNotification(mSelectedMedia, position, filteredSongs);
            return;
        }

        //VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, 0);
        AudioCasting.getsIntance().loadAudio(mSelectedMedia, true, 0, filteredSongs, position, mContext);

    }

    private void castAudioQueue(int position) {

        MediaQueueItem[] audioItems = new MediaQueueItem[filteredSongs.size()];

        for (int i = 0; i < filteredSongs.size(); i++) {

            File audioFile = new File(filteredSongs.get(i).getFilePath());

            if (OptionsUtil.getBooleanOption(mContext, "USE_LOCAL_MUSICPLAYER", false))
            {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(audioFile), "audio/*");
                mContext.startActivity(intent);
                return;
            }

            String author = "Author: " + filteredSongs.get(i).getSongArtistName();
            String album = "Album: " + filteredSongs.get(i).getAudioAlbum();
            String name = audioFile.getName();
            String mimeType = filteredSongs.get(i).getMime_type();

            if (i == position) {
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
            }

            MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

            mediaMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, author);
            mediaMetadata.putString(MediaMetadata.KEY_ALBUM_TITLE, album);
            mediaMetadata.putString(MediaMetadata.KEY_TITLE, name);

            WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            int ipAddress = wifiInf.getIpAddress();
            String ipdevice;

            ipdevice = String.format("http://%d.%d.%d.%d:1239/", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            //ipdevice = String.format("http://%d.%d.%d.%d:1235", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            ipdevice += audioFile.getName();


            String albumArtUrl = ImageCaster.castImage(mContext, Uri.parse(filteredSongs.get(position).getImageUrl()));
            mediaMetadata.addImage(new WebImage(Uri.parse(albumArtUrl)));


            MediaInfo mSelectedMedia = new MediaInfo.Builder(ipdevice)
                    .setContentType(mimeType)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(mediaMetadata)
                    .setMediaTracks(null)
                    .build();

            //MediaQueueItem mediaQueueItem = ic_new MediaQueueItem.Builder(mSelectedMedia).setAutoplay(true).setPreloadTime(20).build();
            MediaQueueItem mediaQueueItem = new MediaQueueItem.Builder(mSelectedMedia).setAutoplay(true).build();
            //MediaQueueItem mediaQueueItem = ic_new MediaQueueItem.Builder(mSelectedMedia).build();

            audioItems[i] = mediaQueueItem;

        }

        try {
            VideoCastManager.getInstance().checkConnectivity();
        } catch (Exception e) {
            e.printStackTrace();
            ((MainActivity)mContext).showNotification(audioItems, position, filteredSongs);
            return;
        }

        //VideoCastManager.getInstance().loadMedia(testSelectedMedia, true, 0);
        //VideoCastManager.getInstance().loadMedia(audioItems[position].getMedia(), true, 0);

        AudioCasting.getsIntance().loadAudioQueue(audioItems, position, MediaStatus.REPEAT_MODE_REPEAT_ALL, mContext, filteredSongs);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = songLists;
                    results.count = songLists.size();
                }
                else {

                    ArrayList<ItemSong> filteredVideos = new ArrayList<>();

                    for (ItemSong v : songLists) {
                        if (v.getSongName().toLowerCase().contains( constraint.toString().toLowerCase() )) {

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
                filteredSongs = (ArrayList<ItemSong>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
