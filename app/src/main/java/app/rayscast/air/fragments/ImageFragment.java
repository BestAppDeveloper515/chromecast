package app.rayscast.air.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.adapters.ImageAdapter;
import app.rayscast.air.models.ItemDirectory;
import app.rayscast.air.models.ItemImage;
import app.rayscast.air.utils.CustomLog;


public class ImageFragment extends Fragment {

    private List<ItemDirectory> mDirectoryList = new ArrayList<>();
    private List<ItemImage> mImageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_image);


        //GET ALL IMAGES

        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE
        };

        // content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = getActivity().getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );
        if (cur != null)
            if (cur.moveToFirst()) {
                String data, mime;
                int dataPath = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                int mimePath = cur.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
                do {
                    data = cur.getString(dataPath);
                    mime = cur.getString(mimePath);
                    if (data != null) {
                        File file = new File(data);
                        file = new File(file.getAbsolutePath());

                        if (isImage(file)) {
                            //add image
                            mImageList.add(new ItemImage(data, mime));

                            //get directory
                            String dir = file.getParent();
                            File dirAsFile = file.getParentFile();

                            boolean dirFound = false;
                            for (ItemDirectory item : mDirectoryList) {
                                if (item.getDirectory().equals(dir)) {
                                    dirFound = true;
                                    break;
                                }
                            }
                            if (!dirFound) {
                                mDirectoryList.add(new ItemDirectory(dirAsFile, dir));
                                CustomLog.d("dir", "dir=" + dir);
                            }
                        }
                    }
                } while (cur.moveToNext());

            }


        mAdapter = new ImageAdapter(getActivity(), mDirectoryList, mImageList);


        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        setHasOptionsMenu(true);
        return view;
    }


    public void restart() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (searchView != null) {
                mAdapter.getFilter().filter(searchView.getQuery());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search,menu);
        searchMenuItem = menu.findItem(R.id.action_search_main);
        searchMenuItem.setVisible(false);

    }

    public boolean isImage(File file) {
        final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
