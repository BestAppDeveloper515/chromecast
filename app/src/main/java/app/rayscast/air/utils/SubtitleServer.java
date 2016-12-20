package app.rayscast.air.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubtitleServer extends NanoHTTPD {

    private static final String TAG = "SubtitleServer";
    private File localFile;
    private Context mContext;
    private int index = 0;
    public SubtitleServer(Context context) {
        super(1233);
        mContext = context;
        ExecutorService mPool =  Executors.newFixedThreadPool(1);

    }


    public void setLocalFile(File f) {
        try {
            if (f.getName().contains(".vtt")) {
                localFile = f;
            }
            else {
                localFile = SubtitleConverter.getInstance().convert(f, Environment.getExternalStorageDirectory(), "en");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    @SuppressWarnings("deprecation")
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {

        String mimeType = "text/vtt";
        if (localFile == null) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
        }
        try {
            //File serverFile = SubtitleConverter.getInstance().convert(f, FfmpegCommands.getFFmpegFilesDir(mContext), mySubtitles.get(selected).lang);
            FileInputStream fileInputStream = new FileInputStream(localFile);
            Response response = newChunkedResponse(Response.Status.OK, mimeType, fileInputStream);
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("content-type", "text/vtt");
            return response;
        } catch (Exception e) {
            CustomLog.d(TAG, e.getMessage());
        }
        CustomLog.d(TAG, "File not found");
        return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
    }


}
