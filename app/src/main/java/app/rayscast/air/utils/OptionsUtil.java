package app.rayscast.air.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Qing on 4/17/2016.
 */
public class OptionsUtil {
    public static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static String getStringOption(Context context, String key, String defValue) {
        return getPreferences(context.getApplicationContext()).getString(key, defValue);
    }
    public static boolean getBooleanOption(Context context, String key, Boolean defValue) {
        return getPreferences(context.getApplicationContext()).getBoolean(key, defValue.booleanValue());
    }
    public static void setOption(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences.Editor editor = getPreferences(context.getApplicationContext()).edit();
            editor.putString(key, value);
            editor.commit();
        }
    }
    public static void setOption(Context context, String key, Boolean value) {
        if (context != null) {
            SharedPreferences.Editor editor = getPreferences(context.getApplicationContext()).edit();
            editor.putBoolean(key, value.booleanValue());
            editor.commit();
        }
    }
}
