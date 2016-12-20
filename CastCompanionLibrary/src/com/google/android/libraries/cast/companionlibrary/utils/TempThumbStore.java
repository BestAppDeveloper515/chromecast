package com.google.android.libraries.cast.companionlibrary.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by darshanz on 8/29/16.
 */
public class TempThumbStore {


    public static String getThumbnail(String name, Bitmap bitmap){
        File tempDir = new File(Environment.getExternalStorageDirectory()+"/RayCast");
        name = name.replace("/","_");

        File file = new File(tempDir+ "/" + name + ".png");

        if(file.exists()){
            return  file.getPath();
        }

        FileOutputStream out = null;

        if(!tempDir.exists()){
            tempDir.mkdir();
        }

         // Path to the just created empty db
         try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }
}
