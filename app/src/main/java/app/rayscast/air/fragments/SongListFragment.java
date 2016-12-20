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
import android.widget.ListView;
import android.widget.TextView;

import app.rayscast.air.R;
import app.rayscast.air.activity.MainActivity;
import app.rayscast.air.adapters.SongListViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    private ListView mlistSong;

    SongListViewAdapter mSongAdapter;

    private SearchView searchView;

    public SongListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("onCreateView "," SongListFragment");

        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        mlistSong = (ListView) view.findViewById(R.id.list_song);

        mSongAdapter = new SongListViewAdapter(getActivity(), -1);

        mlistSong.setAdapter(mSongAdapter);

        mlistSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSongAdapter.itemclicked(position);
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        MenuItem searchFragVideo=menu.findItem(R.id.action_search_video_frag);

//        inflater.inflate(R.menu.menu_search,menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search_main);
        if(MainActivity.wentToSongFragment==true) {
//            searchFragVideo.setVisible(false);
            searchMenuItem.setVisible(true);

            searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
            searchView.setIconifiedByDefault(true);


            searchView.setQueryHint("Search for Song...");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // TODO Auto-generated method stub

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mSongAdapter.getFilter().filter(newText);
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
