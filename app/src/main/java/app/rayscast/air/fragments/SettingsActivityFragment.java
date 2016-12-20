package app.rayscast.air.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.util.Log;

import app.rayscast.air.R;
import app.rayscast.air.utils.ChromecastApplication;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

    private CheckBoxPreference optionRememberLastPosition;
    private ChromecastApplication mainApplication;
    private CheckBoxPreference optionRememberDevice;

    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("clear_history_button").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivityFragment.this.getActivity());
                builder.setCancelable(true);
                builder.setMessage("Are you sure?");
                builder.setTitle("Clear History");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllUrlHistory();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                return true;
            }
        });


        optionRememberDevice  = (CheckBoxPreference) findPreference("REMEMBER_CAST_DEVICE");
        optionRememberDevice.setChecked(true);

        mainApplication = (ChromecastApplication) getActivity().getApplication();
        optionRememberLastPosition  = (CheckBoxPreference) findPreference("REMEMBER_LAST_POSITION");
        if (mainApplication.getSettingsManager().getResumeLastPosition()) {
            optionRememberLastPosition.setChecked(true);
        } else {
            optionRememberLastPosition.setChecked(false);
        }
        optionRememberLastPosition.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("Settings", "onPreferenceChange: " + newValue);
                mainApplication.getSettingsManager().setResumeLastPosition((Boolean) newValue);
                return true;
            }
        });

    }

    public void deleteAllUrlHistory() {
        SharedPreferences sp = getActivity().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        int size = sp.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            mEdit1.remove("Status_" + i);
        }
        mEdit1.putInt("Status_size", 0);

        mEdit1.apply();

    }
}
