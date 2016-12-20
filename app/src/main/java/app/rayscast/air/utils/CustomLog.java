package app.rayscast.air.utils;

import android.util.Log;

import app.rayscast.air.BuildConfig;

public class CustomLog {
    private static boolean showLog = BuildConfig.DEBUG;

    /**
     * Set an info log message.
     * @param msg			Log message to output to the console.
     */
    public static void i(String tag, String msg)
    {
        if (showLog)
            Log.i(tag, msg);
    }

    /**
     * Set an error log message.
     * @param msg			Log message to output to the console.
     */
    public static void e(String tag, String msg)
    {
        if (showLog)
            Log.e(tag , msg);
    }

    /**
     * Set a warning log message.
     * @param msg			Log message to output to the console.
     */
    public static void w(String tag, String msg)
    {
        if (showLog)
            Log.w(tag, msg);
    }

    /**
     * Set a debug log message.
     * @param msg			Log message to output to the console.
     */
    public static void d(String tag, String msg)
    {
        if (showLog)
            Log.d(tag, msg);
    }


    /**
     * Set a verbose log message.
     * @param msg			Log message to output to the console.
     */
    public static void v(String tag, String msg)
    {
        if (showLog)
            Log.v(tag, msg);
    }
}
