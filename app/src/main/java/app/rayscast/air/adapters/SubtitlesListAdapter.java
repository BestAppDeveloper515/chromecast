package app.rayscast.air.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hardsoft.asyncsubtitles.OSubtitle;

import java.util.List;

import app.rayscast.air.R;

public class SubtitlesListAdapter
        extends BaseAdapter
{
    Context context;
    LayoutInflater layoutInflater;
    private List<OSubtitle> listSubtitles;

    public SubtitlesListAdapter(Context paramContext, List<OSubtitle> listSubtitles)
    {
        this.context = paramContext;
        this.listSubtitles = listSubtitles;
    }

    public int getCount()
    {
        return listSubtitles.size();
    }

    public Object getItem(int paramInt)
    {
        return listSubtitles.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
        return 0L;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
        Log.e("Entered", "GetView");
        View localView = paramView;
        if (paramView == null) {
            localView = LayoutInflater.from(this.context).inflate(R.layout.subtitle_layout, paramViewGroup, false);
        }
        TextView textViewMovieName = (TextView) localView.findViewById(R.id.movie_name);
        TextView textViewMovieYear = (TextView) localView.findViewById(R.id.movie_year);
        TextView localTextView = (TextView)localView.findViewById(R.id.sub_lang);
        textViewMovieName.setText("Movie Name: " + ((OSubtitle)listSubtitles.get(paramInt)).getSubFileName());
        textViewMovieYear.setText("Movie Year: " + ((OSubtitle)listSubtitles.get(paramInt)).getMovieYear());
        localTextView.setText("Lang: " + ((OSubtitle)listSubtitles.get(paramInt)).getSubLanguageID());
        return localView;
    }
}
