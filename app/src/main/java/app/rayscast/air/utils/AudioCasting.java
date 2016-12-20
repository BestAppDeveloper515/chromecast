package app.rayscast.air.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.android.gms.cast.CastStatusCodes;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.common.images.WebImage;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import java.io.File;
import java.util.List;

import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.models.ItemSong;

public class AudioCasting {

    private static AudioCasting sIntance = null;
    private static final String TAG = AudioCasting.class.getSimpleName();

    public static AudioCasting getsIntance() {
        if (sIntance == null) {
            sIntance = new AudioCasting();
        }
        return sIntance;
    }

    private Context mContext;
    private List<ItemSong> mFilteredSongs;
    private int mPositionStartSong;
    private int mCurrentPositionPlaySong;

    private final VideoCastConsumerImpl mCastConsumer = new VideoCastConsumerImpl() {

        @Override
        public void onRemoteMediaPlayerStatusUpdated() {
            Log.d(TAG, "onRemoteMediaPlayerStatusUpdated");
            //updatePlaybackState();
            int status = VideoCastManager.getInstance().getPlaybackStatus();
            int idleReason = VideoCastManager.getInstance().getIdleReason();

            if (status == MediaStatus.PLAYER_STATE_IDLE) {
                if (idleReason == MediaStatus.IDLE_REASON_FINISHED) {
                    mCurrentPositionPlaySong += 1;
                    if (mCurrentPositionPlaySong >= mFilteredSongs.size())
                        mCurrentPositionPlaySong = 0;
                    loadAudioFromPosition(mCurrentPositionPlaySong);
                }
            }
        }

        @Override
        public void onMediaQueueOperationResult(int operationId, int statusCode) {
            super.onMediaQueueOperationResult(operationId, statusCode);

            Log.d(TAG, "onMediaQueueOperationResult: operationId: " + operationId);
            Log.d(TAG, "onMediaQueueOperationResult: statusCode: " + statusCode);

            if (statusCode == CastStatusCodes.SUCCESS) {
                if (operationId == VideoCastManager.QUEUE_OPERATION_NEXT) {
                    mCurrentPositionPlaySong += 1;
                    if (mCurrentPositionPlaySong >= mFilteredSongs.size())
                        mCurrentPositionPlaySong = 0;
                } else if (operationId == VideoCastManager.QUEUE_OPERATION_PREV) {
                    mCurrentPositionPlaySong -= 1;
                    if (mCurrentPositionPlaySong < 0)
                        mCurrentPositionPlaySong = mFilteredSongs.size() - 1;
                }
                loadAudioFromPosition(mCurrentPositionPlaySong);
            }
        }

    };

    private void loadAudioFromPosition(int position) {

        File audioFile = new File(mFilteredSongs.get(position).getFilePath());

        if (OptionsUtil.getBooleanOption(mContext, "USE_LOCAL_MUSICPLAYER", false))
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(audioFile), "audio/*");
            mContext.startActivity(intent);
            return;
        }

        String author = "Author: " + mFilteredSongs.get(position).getSongArtistName();
        String album = "Album: " + mFilteredSongs.get(position).getAudioAlbum();
        String name = audioFile.getName();
        String mimeType = mFilteredSongs.get(position).getMime_type();

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


        String albumArtUrl = ImageCaster.castImage(mContext, Uri.parse(mFilteredSongs.get(position).getImageUrl()));
        mediaMetadata.addImage(new WebImage(Uri.parse(albumArtUrl)));

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
            ((MainActivity)mContext).showNotification(mSelectedMedia, position, mFilteredSongs);
            return;
        }

        AudioCasting.getsIntance().loadAudio(mSelectedMedia, true, 0);

    }

    public void loadAudioQueue(MediaQueueItem[] audioItems, int position, int repeatMode, Context context, List<ItemSong> filteredSongs) {
        this.mContext = context;
        this.mFilteredSongs = filteredSongs;

        try {
            VideoCastManager.getInstance().queueLoad(audioItems, position, repeatMode, null);
            VideoCastManager.getInstance().addVideoCastConsumer(mCastConsumer);
        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            e.printStackTrace();
        }
    }

    public void loadAudio(MediaInfo media, boolean autoPlay, int position, List<ItemSong> filteredSongs, int positionSong, Context context) {
        try {
            mContext = context;
            mFilteredSongs = filteredSongs;
            mPositionStartSong = positionSong;
            mCurrentPositionPlaySong = positionSong;
            if (filteredSongs != null) {
                VideoCastManager.getInstance().loadMedia(media, autoPlay, position, filteredSongs.size(), positionSong);
                VideoCastManager.getInstance().addVideoCastConsumer(mCastConsumer);
            } else
                VideoCastManager.getInstance().loadMedia(media, autoPlay, position);
        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            e.printStackTrace();
        }
    }

    public void loadAudio(MediaInfo media, boolean autoPlay, int position) {
        try {
            VideoCastManager.getInstance().loadMedia(media, autoPlay, position, mFilteredSongs.size(), mCurrentPositionPlaySong);
            //VideoCastManager.getInstance().addVideoCastConsumer(mCastConsumer);
        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            e.printStackTrace();
        }
    }

}