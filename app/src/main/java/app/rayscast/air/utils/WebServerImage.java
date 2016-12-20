package app.rayscast.air.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class WebServerImage extends NanoHTTPD {
    public File myFile = null;
    public String mMimeType;
    String TAG = "WebServerImage";

    public WebServerImage(File f, String mimeType) {
        super(1237);
        myFile = f;
        mMimeType = mimeType;
    }

    public long bufferSize = 1000000;

    @Override
    @SuppressWarnings("deprecation")
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
        CustomLog.d(TAG, "myFile: " + myFile.getAbsolutePath());
        String range = null;
        //CLog.d(TAG, "Request headers:");
        for (String key : headers.keySet()) {
            //CLog.d(TAG, "  " + key + ":" + headers.get(key));
            if ("range".equals(key)) {
                range = headers.get(key);
            }
        }
        try {
            if (range == null) {
                return getFullResponse(mMimeType);
            } else {
                return getPartialResponse(mMimeType, range);
            }
        } catch (IOException e) {

        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, mMimeType, "File not found");
    }

    private Response getFullResponse(String mimeType) throws FileNotFoundException {
        //cleanupStreams();
        FileInputStream fileInputStream = new FileInputStream(myFile);
        return newChunkedResponse(Response.Status.OK, mimeType, fileInputStream);
    }

    private Response getPartialResponse(String mimeType, String rangeHeader) throws IOException {

        String rangeValue = rangeHeader.trim().substring("bytes=".length());
        long fileLength = myFile.length();
        long start, end;
        if (rangeValue.startsWith("-")) {
            end = fileLength - 1;
            start = fileLength - 1
                    - Long.parseLong(rangeValue.substring("-".length()));

        } else {
            String[] range = rangeValue.split("-");
            start = Long.parseLong(range[0]);
            end = range.length > 1 ? Long.parseLong(range[1])
                    : fileLength - 1;
        }
        if (end - start > bufferSize) {
            end = start + bufferSize;
        }
        if (end > fileLength - 1) {
            end = fileLength - 1;
        }
        if (start <= end) {
            long contentLength = end - start + 1;

            FileInputStream fileInputStream = new FileInputStream(myFile);
            fileInputStream.skip(start);
            Response response = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mimeType, fileInputStream, contentLength);

            response.addHeader("Content-Length", contentLength + "");
            response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            response.addHeader("Content-Type", mimeType);
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        } else {
            return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, "html", rangeHeader);
        }
    }
}
