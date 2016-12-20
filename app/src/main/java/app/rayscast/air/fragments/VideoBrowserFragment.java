package app.rayscast.air.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.player.VideoCastControllerActivity;

import java.io.File;

import app.rayscast.air.R;
import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.adapters.LibGridViewAdapter;
import app.rayscast.air.database.AppDAO;
import app.rayscast.air.utils.ChromecastApplication;
import app.rayscast.air.utils.OptionsUtil;
import app.rayscast.air.utils.VideoCasting;

public class VideoBrowserFragment extends Fragment {

    private static final String TAG = VideoBrowserFragment.class.getSimpleName();

    private LibGridViewAdapter gridViewAdapter;
    private GridView gridView;
    private SearchView searchView;

    Class<?> mTargetActivity;

    private ChromecastApplication mainApplication;
    public int previousPosition = -1;
    int positionVideoStart = 0;
    private long duration;
    MenuItem searchMenuItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mainApplication = (ChromecastApplication) getActivity().getApplication();

        View view = inflater.inflate(R.layout.grid_view, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);

        gridViewAdapter = LibGridViewAdapter.getInstance(getActivity());
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });

        setHasOptionsMenu(true);

        readPersistedData();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (previousPosition == -1)
            return;
        if (((ChromecastApplication) getActivity().getApplicationContext()).getSettingsManager().getResumeLastPosition()) {
            try {
                long lastPosition = VideoCastManager.getInstance().getCurrentMediaPosition();
                Log.d("Video Fragment", "itemClick: lastposition: " + String.valueOf(lastPosition));
                LibGridViewAdapter.VideoItem lastDisplayedVideo = gridViewAdapter.displayedVideos.get(previousPosition);
                AppDAO.getsIntance().updatePositionVideo(getActivity(), lastDisplayedVideo.getVideoLocation(), (int) lastPosition);
            } catch (TransientNetworkDisconnectionException | NoConnectionException |IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e("onCreateOptionsMenu", " VideoBroserFragment");
//        inflater.inflate(R.menu.menu_search,menu);
        searchMenuItem= menu.findItem(R.id.action_search_main);
        searchMenuItem.setVisible(true);

        searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
        searchView.setIconifiedByDefault(true);


        searchView.setQueryHint("Search for videos...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                gridViewAdapter.getFilter().filter(newText);
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    searchView.setIconified(true);
                }
            }
        });
        changeSearchViewTextColor(searchView);
        searchView.setIconified(true);
        super.onCreateOptionsMenu(menu, inflater);


    }

    public void itemClick(final int pos) {
        Log.d(TAG, "onitemClick pos: " + pos);
        final String data;
        if (gridViewAdapter.mTask != null) {
            gridViewAdapter.cancelTask();
        }
        data = gridViewAdapter.displayedVideos.get(pos).getVideoLocation();

        final String thumbPath = gridViewAdapter.displayedVideos.get(pos).getThumbPath();


        if (mainApplication.getSettingsManager().getResumeLastPosition()) {
            if (previousPosition >= 0) {
                try {
                    long lastPosition = VideoCastManager.getInstance().getCurrentMediaPosition();
                    Log.d(TAG, "itemClick: lastposition: " + String.valueOf(lastPosition));
                    AppDAO.getsIntance().updatePositionVideo(getActivity(), gridViewAdapter.displayedVideos.get(previousPosition).getVideoLocation(), (int) lastPosition);
                }  catch (TransientNetworkDisconnectionException | NoConnectionException |IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        } else {
            positionVideoStart = 0;
        }
        previousPosition = pos;

        positionVideoStart = AppDAO.getsIntance().getPositionVideoFromDB(getActivity(), data);

        if (mainApplication.getSettingsManager().getResumeLastPosition() && (positionVideoStart > 0)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("RESUME?")
                    .setMessage("Would you like to resume this video?");
            builder.setPositiveButton("START OVER", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positionVideoStart = 0;
                    completingItemClick(pos, data, thumbPath);
                }
            });
            builder.setNegativeButton("RESUME", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    completingItemClick(pos, data, thumbPath);
                }
            });
            builder.setNeutralButton("DON'T ASK AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    positionVideoStart = 0;
                    mainApplication.getSettingsManager().setResumeLastPosition(false);
                    completingItemClick(pos, gridViewAdapter.displayedVideos.get(pos).getVideoLocation(), thumbPath);
                }
            });
            builder.show();
        } else {
            positionVideoStart = 0;
            completingItemClick(pos, data, thumbPath);
        }
    }

    public void completingItemClick(int pos, String data, String thumbPath) {

        if (OptionsUtil.getBooleanOption(getActivity(), "USE_LOCAL_PLAYER", false)) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File audioFile = new File(data);
            intent.setDataAndType(Uri.fromFile(audioFile), "video/*");
            startActivity(intent);
            return;
        }

        duration = gridViewAdapter.displayedVideos.get(pos).getVideoDuration();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        MainActivity videoChooserActivity = (MainActivity) activity;
        try {
            if (VideoCastManager.getInstance().isRemoteMediaPlaying()) {
                if (data.contains(VideoCastManager.getInstance().getRemoteMediaInformation().getMetadata().getString(MediaMetadata.KEY_TITLE))) {
                    Bundle mediaWrapper = com.google.android.libraries.cast.companionlibrary.utils.Utils.mediaInfoToBundle(VideoCastManager.getInstance().getRemoteMediaInformation());
                    Intent contentIntent = new Intent(getActivity(), mTargetActivity);
                    contentIntent.putExtra(VideoCastManager.EXTRA_MEDIA, mediaWrapper);
                    startActivity(contentIntent);
                    return;
                }
                VideoCastManager.getInstance().pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            VideoCastManager.getInstance().checkConnectivity();
            VideoCasting.getsIntance().initWebServer( data, duration, positionVideoStart, getActivity(), gridViewAdapter.displayedVideos, previousPosition);
        } catch (Exception e) {
            videoChooserActivity.showNotification( data, duration, gridViewAdapter.displayedVideos, previousPosition);
        }
    }

    private void readPersistedData() {
        String targetName = VideoCastManager.getInstance().getPreferenceAccessor().getStringFromPreference(
                VideoCastManager.PREFS_KEY_CAST_ACTIVITY_NAME);
        try {
            if (targetName != null) {
                mTargetActivity = Class.forName(targetName);
            } else {
                mTargetActivity = VideoCastControllerActivity.class;
            }

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Failed to find the targetActivity class", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VideoCasting.REQUEST_CODE_DIALOG_SUBTITLE_FILE) {
                Uri uriFile = data.getData();
                Log.d(TAG, "onActivityResult: " + uriFile.getPath());

                VideoCasting.getsIntance().processActivityResult(uriFile,2);
            }
            else if(requestCode==VideoCasting.REQUEST_CODE_DIALOG_SUBTITLE_ACTIVITY){
                String filePath=data.getStringExtra("result");
                Uri uriFilePath=Uri.parse(filePath);
                VideoCasting.getsIntance().processActivityResult(uriFilePath,1);
            }
        }
    }
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.BLACK);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }
}
