package app.rayscast.air.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.models.ItemArtist;

/**
 * Created by Qing on 4/16/2016.
 */
public class ArtistGridViewAdpater extends BaseAdapter implements Filterable{

    private static String TAG = "Albums";
    private static ArtistGridViewAdpater instance = null;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Cursor cursor;

    String[] projection = new String[] { MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS };

    private List<ItemArtist> artistItemLists, filteredItemLists;

    public static class ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView albumView;
        TextView numberofSongs;
    }

    private ArtistGridViewAdpater(Context context)
    {
        mContext = context;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        artistItemLists = new ArrayList<>();
        filteredItemLists = new ArrayList<>();

        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
if(cursor!=null)
        System.out.println("Artist List " + cursor.getCount());
        if(cursor!=null)
        while (cursor.moveToNext()) {
            String artistName = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
            long artistId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));

            Uri artistArt = Uri
                    .parse("content://media/external/audio/albumart");
            int noOfAlbums = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));

            ItemArtist audioListModel = new ItemArtist(
                    (int) artistId, artistName, noOfAlbums,
                    artistArt.toString());

            artistItemLists.add(audioListModel);
        }

        filteredItemLists = artistItemLists;
    }

    public static ArtistGridViewAdpater getInstance(Context context)
    {
        if (instance == null) {
            if(context!=null)
            instance = new ArtistGridViewAdpater(context);
        }
        return instance;
    }

    @Override
    public int getCount() {
        return filteredItemLists.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            cell = layoutInflater.inflate(R.layout.item_album, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleView = (TextView) cell.findViewById(R.id.txt_title);
            viewHolder.numberofSongs = (TextView) cell.findViewById(R.id.txt_numberofsongs);
            viewHolder.imageView = (ImageView) cell.findViewById(R.id.img_albumart);
            viewHolder.albumView = (TextView) cell.findViewById(R.id.txt_artist);
            cell.setTag(viewHolder);
            //setTags(cell, textView, videoDuration, imageView);
        } else {
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (position < filteredItemLists.size()) {
            viewHolder.titleView.setText(filteredItemLists.get(position).getArtistName());
            viewHolder.albumView.setVisibility(View.GONE);
            viewHolder.numberofSongs.setText(filteredItemLists.get(position).getNoOFAlbums() + " Albums");
            if (!filteredItemLists.get(position).getArtistArt().equals(""))
            {
                Picasso.with(mContext)
                        .load(filteredItemLists.get(position).getArtistArt())
                        .placeholder(R.drawable.artist_blankart)
                        .error(R.drawable.artist_blankart).into(viewHolder.imageView);
            }
        }
        return cell;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    results.values = artistItemLists;
                    results.count = artistItemLists.size();
                }
                else {

                    ArrayList<ItemArtist> filteredVideos = new ArrayList<>();

                    for (ItemArtist v : artistItemLists) {
                        if (v.getArtistName().toLowerCase().contains( constraint.toString().toLowerCase() )) {

                            filteredVideos.add(v);
                        }
                    }

                    results.values = filteredVideos;
                    results.count = filteredVideos.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItemLists = (ArrayList<ItemArtist>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
