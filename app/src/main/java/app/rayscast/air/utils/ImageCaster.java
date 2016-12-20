package app.rayscast.air.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.cast.MediaMetadata;

import java.io.File;

/**
 * Created by darshanz on 8/27/16.
 */
public class ImageCaster {

    public final static String castImage(Context context, Uri _uri){
        String url = null;

        Log.e("TAG", ">>URI "+ _uri);
        Log.e("TAG", ">>URI "+ _uri.getPath());

        String filePath = null;
        if (_uri != null && "content".equals(_uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (cursor!= null && cursor.moveToNext()){
                filePath = cursor.getString(0);
             }else{
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                filePath = preferences.getString("default_albumart", null);
                Log.e("TAG", ">>default "+ filePath);

            }
            cursor.close();
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            filePath = preferences.getString("default_albumart", null);
         }

        if(filePath != null) {
            File imageFile = new File(filePath);
            String mimeType = "image/jpeg";

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


            WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            int ipAddress = wifiInf.getIpAddress();
            String ipdevice;

            ipdevice = String.format("http://%d.%d.%d.%d:1237/", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            return ipdevice += imageFile.getName();
        }else{
            return null;
        }

    }

    public final static String castImage(Context context, String filePath){

        if(filePath != null) {
            File imageFile = new File(filePath);
            String mimeType = "image/jpeg";

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


            WifiManager wifiMan = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            int ipAddress = wifiInf.getIpAddress();
            String ipdevice;

            ipdevice = String.format("http://%d.%d.%d.%d:1237/", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            return ipdevice += imageFile.getName();
        }else{
            return null;
        }

    }
}
