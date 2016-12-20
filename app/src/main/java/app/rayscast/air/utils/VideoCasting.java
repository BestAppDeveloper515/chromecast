package app.rayscast.air.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.cast.CastStatusCodes;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.activity.SubtitleDownloadActivity;
import app.rayscast.air.adapters.LibGridViewAdapter;

public class VideoCasting {

    private static VideoCasting sIntance = null;
    private static final String TAG = VideoCasting.class.getSimpleName();

    public static final int REQUEST_CODE_DIALOG_SUBTITLE_FILE = 77;
    public static final int REQUEST_CODE_DIALOG_SUBTITLE_ACTIVITY = 78;

    private ArrayList<LibGridViewAdapter.VideoItem> mDisplayedVideos;
    private int mPreviousPosition;
    private int mFirstPosition;

    public static VideoCasting getsIntance() {
        if (sIntance == null) {
            sIntance = new VideoCasting();
        }
        return sIntance;
    }

    private long mDuration;
    private int mPositionVideoStart = 0;

    private File serverFile;
    private File subtitle;
    private List<MediaTrack> mediaTracks;

    private Context mContext;
    private ChromecastApplication mainApplication;

    private final VideoCastConsumerImpl mCastConsumer = new VideoCastConsumerImpl() {

        @Override
        public void onRemoteMediaPlayerStatusUpdated() {
            Log.d(TAG, "onRemoteMediaPlayerStatusUpdated");
            //updatePlaybackState();
            int status = VideoCastManager.getInstance().getPlaybackStatus();
            int idleReason = VideoCastManager.getInstance().getIdleReason();

            if (status == MediaStatus.PLAYER_STATE_IDLE) {
                if (idleReason == MediaStatus.IDLE_REASON_FINISHED) {
                    mPreviousPosition += 1;
                    if (mPreviousPosition >= mDisplayedVideos.size())
                        mPreviousPosition = 0;
                    loadVideoFromPosition(mPreviousPosition);
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
                    mPreviousPosition += 1;
                    if (mPreviousPosition >= mDisplayedVideos.size())
                        mPreviousPosition = 0;
                } else if (operationId == VideoCastManager.QUEUE_OPERATION_PREV) {
                    mPreviousPosition -= 1;
                    if (mPreviousPosition < 0)
                        mPreviousPosition = mDisplayedVideos.size() - 1;
                }
                loadVideoFromPosition(mPreviousPosition);
            }
        }

    };

    private void loadVideoFromPosition(int pos) {
        final String data;
        data = mDisplayedVideos.get(pos).getVideoLocation();
        mDuration = mDisplayedVideos.get(pos).getVideoDuration();
        mainApplication = (ChromecastApplication) ((Activity) mContext).getApplication();

        serverFile = new File(data);
        if (ChromecastApplication.getInstance().server == null) {
            try {
                ChromecastApplication.getInstance().server = new WebServer(serverFile);
                ChromecastApplication.getInstance().server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ChromecastApplication.getInstance().server.myFile = serverFile;
        }

        subtitle = findSubtitle(serverFile);

        mediaTracks = null;

        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, serverFile.getName());

        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ipdevice;
        String mimeType;
        ipdevice = String.format("http://%d.%d.%d.%d:1235", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        if (serverFile.getName().toLowerCase().contains("webm")) {
            mimeType = "video/webm";
        } else {
            mimeType = "video/mp4";
        }

        if (subtitle != null && mediaTracks == null) {
            mediaTracks = getTrackFromFile(subtitle);
        }
        MediaInfo mSelectedMedia =
                new MediaInfo.Builder(ipdevice)
                        .setContentType(mimeType)
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setMetadata(mediaMetadata)
                        .setMediaTracks(mediaTracks)
                        .setStreamDuration(mDuration)
                        .build();
        try {
            if (mediaTracks == null) {
                VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, mPositionVideoStart, mDisplayedVideos.size(), mPreviousPosition);
            } else {
                VideoCastManager.getInstance().loadMedia(mSelectedMedia, new long[]{1}, true, mPositionVideoStart, null, mDisplayedVideos.size(), mPreviousPosition);
            }

        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            e.printStackTrace();
        }

    }

    public void initWebServer(String videoPath, long duration, int positionVideoStart, Context context, ArrayList<LibGridViewAdapter.VideoItem> displayedVideos, int previousPosition) {

        Log.d(TAG, "init WebServer videopath: " + videoPath + " duration: " + duration + " position: " + positionVideoStart);

        mDuration = duration;
        mPositionVideoStart = positionVideoStart;
        mContext = context;
        mDisplayedVideos = displayedVideos;
        mPreviousPosition = previousPosition;
        mFirstPosition = previousPosition;
        mainApplication = (ChromecastApplication) ((Activity) mContext).getApplication();

        serverFile = new File(videoPath);
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

        subtitle = findSubtitle(serverFile);

        if (subtitle == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Subtitles")
                    .setItems(R.array.closed_caption_click_options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {

                                //String str1 = serverFile.getParent();
                                //String str2 = serverFile.getName();

                                String tempString = serverFile.getPath();

                                String str1 = tempString.substring(tempString.indexOf("/"), tempString.lastIndexOf("/") + 1);
                                String str2 = tempString.substring(tempString.lastIndexOf("/") + 1, tempString.lastIndexOf("."));

                                Intent intent = new Intent(mContext, SubtitleDownloadActivity.class);
                                intent.putExtra("filePathString", str1);
                                intent.putExtra("movieName", str2);
                                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_DIALOG_SUBTITLE_ACTIVITY);
                            } else {
                                Intent intent = new Intent();
                                intent.setType("*/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select subtitles"), REQUEST_CODE_DIALOG_SUBTITLE_FILE);
                            }

                        }
                    })
                    .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            completingInitWebServer(serverFile);
                        }
                    });
            if (!((Activity) mContext).isFinishing())
                builder.show();

        } else {
            mediaTracks = null;
            completingInitWebServer(serverFile);
        }

    }

    private void completingInitWebServer(File serverFile) {

        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, serverFile.getName());

        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ipdevice;
        String mimeType;
        ipdevice = String.format("http://%d.%d.%d.%d:1235", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        if (serverFile.getName().toLowerCase().contains("webm")) {
            mimeType = "video/webm";
        } else {
            mimeType = "video/mp4";
        }

        if (subtitle != null && mediaTracks == null) {
            mediaTracks = getTrackFromFile(subtitle);
        }
        MediaInfo mSelectedMedia =
                new MediaInfo.Builder(ipdevice)
                        .setContentType(mimeType)
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setMetadata(mediaMetadata)
                        .setMediaTracks(mediaTracks)
                        .setStreamDuration(mDuration)
                        .build();
        try {
            if (mDisplayedVideos != null) {
                if (mediaTracks == null) {
                    VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, mPositionVideoStart, mDisplayedVideos.size(), mPreviousPosition);
                } else {
                    VideoCastManager.getInstance().loadMedia(mSelectedMedia, new long[]{1}, true, mPositionVideoStart, null, mDisplayedVideos.size(), mPreviousPosition);
                }
                VideoCastManager.getInstance().addVideoCastConsumer(mCastConsumer);
            } else {
                if (mediaTracks == null) {
                    VideoCastManager.getInstance().loadMedia(mSelectedMedia, true, mPositionVideoStart);
                } else {
                    VideoCastManager.getInstance().loadMedia(mSelectedMedia, new long[]{1}, true, mPositionVideoStart, null, 0, 0);
                }
            }

        } catch (TransientNetworkDisconnectionException | NoConnectionException e) {
            e.printStackTrace();
        }
    }

    private List<MediaTrack> getTrackFromFile(File f) {

        if (mainApplication.subtitleServer == null) {
            mainApplication.subtitleServer = new SubtitleServer(mContext.getApplicationContext());
            try {
                mainApplication.subtitleServer.start();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        mainApplication.subtitleServer.setLocalFile(f);
        CustomLog.d(TAG, "File abs path: " + f.getAbsolutePath());

        String path = f.getAbsolutePath().replaceFirst("[.][^.]+$", "");
        path = path.substring(path.lastIndexOf('/') + 1);
        WifiManager wifiMan = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("http://%d.%d.%d.%d:1233/", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        String subip = ip + path + ".vtt";
        //String subip = ip + path + f.getName().substring(f.getName().lastIndexOf("."));
        CustomLog.d(TAG, " path: " + path);

        CustomLog.d(TAG, " subip: " + subip);
        List<MediaTrack> mTracks = new ArrayList<>();
        long index = mTracks.size() + 1;

        MediaTrack subtitle = new MediaTrack.Builder(index, MediaTrack.TYPE_TEXT)
                .setName("Local Subtitle")
                .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                .setContentId(subip)
                .setLanguage("en")
                .build();


        mTracks.add(subtitle);
        return mTracks;
    }

    private File findSubtitle(File videoFile) {
        if (videoFile == null) {
            return null;
        }
        File parentDirectory = videoFile.getParentFile();
        if (parentDirectory == null)
            return null;
        File[] filesInFolder = parentDirectory.listFiles();
        if (filesInFolder == null)
            return null;
        for (File aFilesInFolder : filesInFolder) {
            if (aFilesInFolder.getName().toLowerCase().contains(".vtt") || aFilesInFolder.getName().toLowerCase().contains(".srt")) {
                Log.d(TAG, "filesInFolder[i]: " + aFilesInFolder.getAbsolutePath());
                String fileName = aFilesInFolder.getName();
                String fileNameWithoutExtension = fileName.replaceFirst("[.][^.]+$", "");
                String videoFileNameWithoutExtensions = videoFile.getName().replaceFirst("[.][^.]+$", "");
                if (fileNameWithoutExtension.toLowerCase().contains(videoFileNameWithoutExtensions.toLowerCase())) {
                    return aFilesInFolder;
                }
            }

        }
        return null;
    }

    public void processActivityResult(Uri uriFile, int a) {

        String filePath = null;
        if (uriFile != null && a == 2) {
            filePath = getPath(mContext, uriFile);
            Log.e("filePath", ": " + filePath);
        } else if (uriFile != null && a == 1) {
            filePath = uriFile.toString();
            Log.e("filePath", ": " + filePath);
        }
        /*
        if (Build.VERSION.SDK_INT > 19) {
            String wholeID = DocumentsContract.getDocumentId(uriFile);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = mContext.getContentResolver().query(uriFile, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        */

        //   Log.d(TAG, "onActivityResult: " + filePath);

        if (filePath == null) {
            Toast.makeText(mContext, "File - null", Toast.LENGTH_SHORT).show();
        } else {
            File subtitleFile = new File(filePath);

            if (subtitleFile.exists()) {
                try {
                    mediaTracks = getTrackFromFile(subtitleFile);
                    completingInitWebServer(serverFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Error")
                            .setMessage("Incorrect subtitle file")
                            .setNegativeButton("CLOSE", null);
                    if (!((Activity) mContext).isFinishing())
                        builder.show();
                }
            } else {
                Toast.makeText(mContext, "Cannot operate with file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        //final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT > 19 && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                return filePath;
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}