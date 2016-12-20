package app.rayscast.air.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.tracks.OnTracksSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.rayscast.air.R;
import io.fabric.sdk.android.Fabric;

public class ChromecastApplication extends Application {
    private static ChromecastApplication singleton;
    private static String TAG = "ChromecastApplication";
    public WebServer server= null;
    public WebServerImage serverImage = null;
    public WebServerAudio serverAudio = null;
    public SubtitleServer subtitleServer = null;
    public WebServerDropBox serverDropBox= null;
    public WebServerDrive serverDrive= null;
    public WebServerDriveUriVariant serverDriveUri= null;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static ChromecastApplication getInstance() {
        return singleton;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private SettingsManager settingsManager;

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        settingsManager = new SettingsManager();

        Fabric.with(this, new Crashlytics());
        singleton = this;
        String receiverId = getResources().getString(R.string.receiver_id);
        CastConfiguration options = new CastConfiguration.Builder(receiverId)
                .enableAutoReconnect()
                .enableCaptionManagement()
                .enableDebug()
                .enableLockScreen()
                .enableNotification()
                .enableWifiReconnection()
                .setCastControllerImmersive(true)
                .setLaunchOptions(false, Locale.getDefault())
                .setNextPrevVisibilityPolicy(com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration.NEXT_PREV_VISIBILITY_POLICY_DISABLED)
                .addNotificationAction(com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE, true)
                .addNotificationAction(com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration.NOTIFICATION_ACTION_DISCONNECT, true)
                .build();
        VideoCastManager.initialize(this, options);

        VideoCastManager.getInstance().addTracksSelectedListener(new OnTracksSelectedListener() {
            @Override
            public void onTracksSelected(List<MediaTrack> tracks) {
                if (tracks == null) {
                    CustomLog.d(TAG, "Null Tracks passed");
                    return;
                }
                if (tracks.size() == 0) {
                    VideoCastManager.getInstance().setActiveTrackIds(new long[]{});
                } else {
                    VideoCastManager.getInstance().setActiveTrackIds(new long[]{tracks.get(0).getId()});
                }
            }
        });

    }

    public class SettingsManager {

        public static final String RESUME_LAST_POSITION = "resume_last_position";

        private SharedPreferences sharedPreferences;
        private Boolean resumeLastPosition = true;

        public Boolean getResumeLastPosition() {
            return resumeLastPosition;
        }

        public void setResumeLastPosition(Boolean resumeLastPosition) {
            this.resumeLastPosition = resumeLastPosition;

            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean(RESUME_LAST_POSITION, resumeLastPosition);
            edit.apply();
        }

        public SettingsManager() {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (sharedPreferences.contains(RESUME_LAST_POSITION))
                this.resumeLastPosition = sharedPreferences.getBoolean(RESUME_LAST_POSITION, true);
        }

    }
}
