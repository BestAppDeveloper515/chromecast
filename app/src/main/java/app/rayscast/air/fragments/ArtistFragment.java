package app.rayscast.air.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.TextView;

import app.rayscast.air.R;
import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.adapters.AlbumGridViewAdapter;
import app.rayscast.air.adapters.ArtistGridViewAdpater;
import app.rayscast.air.adapters.SongListViewAdapter;
import app.rayscast.air.models.ItemAlbum;
import app.rayscast.air.models.ItemArtist;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {

    ArtistGridViewAdpater mArtistAdpater;
    AlbumGridViewAdapter mAlbumAdapter;
    SongListViewAdapter mSongAdapter;
    private GridView gridView;
    private GridView albumGrid;
    private ListView mlistSong;
    private SearchView searchView;

    public ArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView ", " ArtistFragment");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        gridView = (GridView) view.findViewById(R.id.gridview);

        albumGrid = (GridView) view.findViewById(R.id.albumgrid);

        mlistSong = (ListView) view.findViewById(R.id.list_song);

        mArtistAdpater = ArtistGridViewAdpater.getInstance(getActivity());

        gridView.setAdapter(mArtistAdpater);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemArtist art = (ItemArtist) mArtistAdpater.getItem(position);
                GoToAlbumGrid(art.getArtistID());
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    private void GoToAlbumGrid(int artistID) {
        mAlbumAdapter = new AlbumGridViewAdapter(getActivity(), artistID);
        gridView.setVisibility(View.GONE);

        albumGrid.setAdapter(mAlbumAdapter);
        MainActivity.mAudioRoot = false;
        albumGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemAlbum album = (ItemAlbum) mAlbumAdapter.getItem(position);
                GoToSongList(album.getAlbumID());
            }
        });
        albumGrid.setVisibility(View.VISIBLE);
    }

    private void GoToSongList(int albumID) {
        mSongAdapter = new SongListViewAdapter(getActivity(), albumID);
        albumGrid.setVisibility(View.GONE);
        mlistSong.setAdapter(mSongAdapter);
        mlistSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSongAdapter.itemclicked(position);
            }
        });
        mlistSong.setVisibility(View.VISIBLE);
    }

    public boolean BackStatue() {
        if (albumGrid.getVisibility() == View.VISIBLE) {
            albumGrid.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            return false;
        } else if (mlistSong.getVisibility() == View.VISIBLE) {
            mlistSong.setVisibility(View.GONE);
            albumGrid.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_search, menu);
//        MenuItem searchFragVideo=menu.findItem(R.id.action_search_video_frag);


        MenuItem searchMenuItem = menu.findItem(R.id.action_search_main);
        if (MainActivity.wentToArtistFragment == true) {
//            searchFragVideo.setVisible(false);
            searchMenuItem.setVisible(true);


            searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
            searchView.setIconifiedByDefault(true);


            searchView.setQueryHint("Search for Artist...");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // TODO Auto-generated method stub

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mArtistAdpater.getFilter().filter(newText);
                    if (mSongAdapter != null) {
                        mSongAdapter.getFilter().filter(newText);
                    }
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
        }
        else if(MainActivity.wentToVideoFragment==true){
            searchMenuItem.setVisible(true);
        }
        else if(MainActivity.wentToWebFragment==true){
            searchMenuItem.setVisible(true);
        }
        else {
            searchMenuItem.setVisible(false);
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
