package app.rayscast.air.utils;


import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class WebServerDrive extends NanoHTTPD {
    public GoogleApiClient googleApiClient = null;
    public InputStream inputStream;
    public long myLength = 1073741824;
    public String mimeType;
    String TAG = "WebServerDrive";

    public WebServerDrive(GoogleApiClient googleApiClient, InputStream inputStream, String mimeType, long size) {
        super(1240);
        this.googleApiClient = googleApiClient;
        this.inputStream = inputStream;
        this.mimeType = mimeType;
        myLength = size;
    }

    public long bufferSize = 1000000;

    @Override
    @SuppressWarnings("deprecation")
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
        Log.d(TAG, "serve called " + uri);

        String range = null;

        for (String key : headers.keySet()) {

            if ("range".equals(key)) {
                range = headers.get(key);
            }
        }
        try {
            if (range == null) {
                return getFullResponse(mimeType);
            } else {
                return getPartialResponse(mimeType, range);
            }
        } catch (IOException e) {

        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
    }

    private Response getFullResponse(String mimeType) throws FileNotFoundException {
        //cleanupStreams();
        try {


            try {

                InputStream in = inputStream;

                BufferedInputStream fileInputStream = null;
                try {
                    fileInputStream = new BufferedInputStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
                }
                return newChunkedResponse(Response.Status.OK, mimeType, fileInputStream);
            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
        }
    }


    private Response getPartialResponse(String mimeType, String rangeHeader) throws IOException {

        try {

            InputStream in = inputStream;


            BufferedInputStream fileInputStream = null;
            try {
                fileInputStream = new BufferedInputStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
            }
            String rangeValue = rangeHeader.trim().substring("bytes=".length());
            long start, end;
            if (rangeValue.startsWith("-")) {
                end = myLength - 1;
                start = myLength - 1
                        - Long.parseLong(rangeValue.substring("-".length()));

            } else {
                String[] range = rangeValue.split("-");
                start = Long.parseLong(range[0]);
                end = range.length > 1 ? Long.parseLong(range[1])
                        : myLength - 1;
            }
            if (end - start > bufferSize) {
                end = start + bufferSize;
            }
            if (end > myLength - 1) {
                end = myLength - 1;
            }
            if (start <= end) {
                long contentLength = end - start + 1;
                Response response = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mimeType, fileInputStream, contentLength);

                response.addHeader("Content-Length", contentLength + "");
                response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + myLength);
                response.addHeader("Content-Type", mimeType);
                response.addHeader("Access-Control-Allow-Origin", "*");
                return response;
            } else {
                return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, "html", rangeHeader);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.NOT_FOUND, mimeType, "File not found");
        }
    }
}
