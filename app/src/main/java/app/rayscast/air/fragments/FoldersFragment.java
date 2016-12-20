package app.rayscast.air.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import app.rayscast.air.R;
import app.rayscast.air.adapters.FoldersAdapter;

public class FoldersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FoldersAdapter mAdapter;

    private File mCurrentDir; //Our current location.
    public static final String TAG = "FOLDERS"; //for debugging purposes.

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        Log.e("Gallery Fragment"," Called");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_folders);

        //if the storage device is writable and readable, set the current directory to the external storage location.
        mCurrentDir = Environment.getExternalStorageDirectory();
        mAdapter = new FoldersAdapter(getActivity(), mCurrentDir);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);
        return view;
    }

    public void restart() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean changeFolderOnBack() {
       return mAdapter.changeFolderOnBack();
    }

    public boolean changeFolderOnBackRoot() {
        return mAdapter.changeFolderOnBackRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search_main);
        searchMenuItem.setVisible(false);

    }



}
