package app.rayscast.air.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hardsoft.asyncsubtitles.AsyncSubtitles;
import com.hardsoft.asyncsubtitles.ORequest;
import com.hardsoft.asyncsubtitles.OSubtitle;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.adapters.SubtitlesListAdapter;

public class SubtitleDownloadActivity
        extends AppCompatActivity
        implements AdapterView.OnItemClickListener
{

    private static final String TAG = SubtitleDownloadActivity.class.getSimpleName();

    private AsyncSubtitles mASub;

    public static final String DOWNLOAD = "DownloadSubtitles";
    public static final String LOGIN = "LogIn";
    public static final String LOGOUT = "LogOut";
    public static final String SEARCH = "SearchSubtitles";

    SubtitlesListAdapter subtitlesListAdapter;
    private List<OSubtitle> listSubtitles;

    Button getSubtitle;
    ListView listView;
    public String movieName;
    EditText searchMovieEditText;
    public String videoFilePath;

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.subtitle_layout_activity);

        Intent intent = getIntent();

        videoFilePath = intent.getStringExtra("filePathString");
        Log.e("Video File Path Recieved",": "+videoFilePath);
        movieName = intent.getStringExtra("movieName");
        Log.e("Movie Name",": "+movieName);

        getSubtitle = ((Button) findViewById(R.id.get_subtitles));
        searchMovieEditText = ((EditText) findViewById(R.id.search_movie_edit_box));
        searchMovieEditText.setText(this.movieName);

        listView = ((ListView)findViewById(R.id.list_view));

        getSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String filmName = searchMovieEditText.getText().toString();
                    mASub = new AsyncSubtitles(SubtitleDownloadActivity.this, new AsyncSubtitles.SubtitlesInterface() {
                        @Override
                        public void onSubtitlesListFound(List<OSubtitle> list) {
                            if (list.size() == 0) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(SubtitleDownloadActivity.this);
                                builder.setTitle("Subtitles")
                                        .setMessage("Subtitles for " + filmName + " not found")
                                        .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {}
                                        });
                                if(!getParent().isFinishing())
                                builder.show();
                            } else {
                                Log.d(TAG, "onSubtitlesListFound: AsyncSubtitles: " + list.get(0).getMovieName());
                                listSubtitles = list;
                                subtitlesListAdapter = new SubtitlesListAdapter(SubtitleDownloadActivity.this, list);
                                listView.setAdapter(subtitlesListAdapter);
                                listView.setOnItemClickListener(SubtitleDownloadActivity.this);
                            }
                        }

                        @Override
                        public void onSubtitleDownload(boolean b,String filePath) {
                            Log.d(TAG, "onSubtitleDownload: AsyncSubtitles: " + b);
                        }

                        @Override
                        public void onError(int error) {
                            Log.d(TAG, "onError: AsyncSubtitles: " + error);
                        }
                    });

                    Log.e("monu",getApplicationContext().getFilesDir() + "/" + filmName + ".srt");
                    mASub.setLanguagesArray(new String[] { "eng" });
                    ORequest oRequest = new ORequest(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + filmName + ".srt",
                            filmName,
                            null,
                            new String[]{"eng"});
                    mASub.setNeededParamsToSearch(oRequest);
                    mASub.getPossibleSubtitle();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void onItemClick(final AdapterView<?> paramAdapterView, View paramView, final int paramInt, long paramLong)
    {
        Log.e("File Path GOt", ": " + this.videoFilePath);
        final File subFile = new File(this.videoFilePath, ((OSubtitle) listSubtitles.get(paramInt)).getSubFileName());
        Log.e("File Path GOt", ": " + subFile.getPath());
        final String pathSubFile = subFile.getPath();
        AlertDialog.Builder builder = new AlertDialog.Builder(SubtitleDownloadActivity.this);
        builder.setTitle("Want to Download?");
        builder.setMessage(listSubtitles.get(paramInt).getSubFileName());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "File Will be created here: " + pathSubFile);
                        try {
                            mASub = new AsyncSubtitles(SubtitleDownloadActivity.this, new AsyncSubtitles.SubtitlesInterface() {
                                @Override
                                public void onSubtitlesListFound(List<OSubtitle> list) {
                                    Log.d(TAG, "onSubtitlesListFound in ItemClick: AsyncSubtitles: " + list.size());
                                    mASub.downloadSubByIdToPath(listSubtitles.get(paramInt).getIDSubtitleFile(),
                                            pathSubFile);
                                }

                                @Override
                                public void onSubtitleDownload(boolean b,String filePath) {
                                    Log.d(TAG, "onSubtitleDownload in ItemClick: AsyncSubtitles: " + b);
                                    if (b) {
                                        Toast.makeText(SubtitleDownloadActivity.this, "Subtitles downloaded succesfully", Toast.LENGTH_SHORT).show();
                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra("result",filePath);
                                        setResult(Activity.RESULT_OK,returnIntent);
                                        SubtitleDownloadActivity.this.finish();
                                    } else {
                                        Toast.makeText(SubtitleDownloadActivity.this, "Subtitles not downloaded", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(int error) {
                                    Log.d(TAG, "onError in ItemClick: AsyncSubtitles: " + error);
                                }
                            });
                            mASub.setLanguagesArray(new String[] { "eng" });
                            ORequest oRequest = new ORequest(listSubtitles.get(paramInt).getSubFileName(),
                                    listSubtitles.get(paramInt).getSubFileName(),
                                    null,
                                    new String[]{"eng"});
                            mASub.setNeededParamsToSearch(oRequest);
                            mASub.getPossibleSubtitle();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}
