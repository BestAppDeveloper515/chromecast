package app.rayscast.air.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
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
import app.rayscast.air.models.ItemAlbum;

/**
 * Created by Qing on 4/15/2016.
 */
public class AlbumGridViewAdapter extends BaseAdapter implements Filterable {
    private static String TAG = "Albums";
    private Context mContext;
    private LayoutInflater layoutInflater;
    private Cursor cursor;

    private static String[] projection = new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.NUMBER_OF_SONGS};

    private List<ItemAlbum> albumItemLists, filteredItemLists;

    public static class ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView artistView;
        TextView numberofSongs;
    }

    public AlbumGridViewAdapter(Context context, int artistID) {
        mContext = context;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        albumItemLists = new ArrayList<>();
        filteredItemLists = new ArrayList<>();

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";

        if (artistID != -1) {
            selection = MediaStore.Audio.Media.ARTIST_ID + "=?";
            selectionArgs = new String[1];
            selectionArgs[0] = String.valueOf(artistID);

        }

        cursor = new CursorLoader(mContext, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, selection,
                selectionArgs, sortOrder).loadInBackground();
        if (cursor != null)
            while (cursor.moveToNext()) {
                String albumeName = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
                long albumeId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
                String albumeArtist = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
                int noOfSong = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri,
                        albumeId);
                System.out.println("Albums Id " + albumeId
                        + " Albums Name " + albumeName);

                ItemAlbum albumsModel = new ItemAlbum((int) albumeId,
                        albumeName, albumeArtist, albumArtUri.toString(),
                        noOfSong);

                albumItemLists.add(albumsModel);
            }

        filteredItemLists = albumItemLists;
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
            viewHolder.artistView = (TextView) cell.findViewById(R.id.txt_artist);
            cell.setTag(viewHolder);
            //setTags(cell, textView, videoDuration, imageView);
        } else {
            viewHolder = (ViewHolder) cell.getTag();
        }

        if (position < filteredItemLists.size()) {
            viewHolder.titleView.setText(filteredItemLists.get(position).getAlbumName());
            viewHolder.artistView.setText(filteredItemLists.get(position).getAlbumArtist());
            viewHolder.numberofSongs.setText(filteredItemLists.get(position).getNoOfSongs() + " songs");
            if (!filteredItemLists.get(position).getAlbumArt().equals("")) {
                Picasso.with(mContext)
                        .load(filteredItemLists.get(position).getAlbumArt())
                        .placeholder(R.drawable.albums_blankart)
                        .error(R.drawable.albums_blankart).into(viewHolder.imageView);
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
                    results.values = albumItemLists;
                    results.count = albumItemLists.size();
                } else {

                    ArrayList<ItemAlbum> filteredVideos = new ArrayList<>();

                    for (ItemAlbum v : albumItemLists) {
                        if (v.getAlbumName().toLowerCase().contains(constraint.toString().toLowerCase())) {

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
                filteredItemLists = (ArrayList<ItemAlbum>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
