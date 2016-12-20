package app.rayscast.air.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

import app.rayscast.air.R;
import app.rayscast.air.adapters.HomeMenuAdapter;
import app.rayscast.air.utils.HomeMenuHelper;


public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private OnHomeMenuSelectListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Home Fragment "," onCreateView");
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView   = (RecyclerView) view.findViewById(R.id.recyclerViewHome);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        HomeMenuAdapter adapter =  new HomeMenuAdapter(HomeMenuHelper.getHomeMenu());
        mRecyclerView.setAdapter(adapter);
        adapter.setMenuSelectListener(new HomeMenuAdapter.OnHomeMenuSelectListener() {
            @Override
            public void OnMenuSelected(int type) {
                 if(mListener != null){
                    mListener.onFragmentRequested(type);
                }
            }
        });
        return  view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         if (context instanceof OnHomeMenuSelectListener) {
            mListener = (OnHomeMenuSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeMenuSelectListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnHomeMenuSelectListener) {
            mListener = (OnHomeMenuSelectListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnHomeMenuSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("TAG", "onDetach>>");
        mListener = null;
    }


    public interface OnHomeMenuSelectListener {
         void onFragmentRequested(int fragment);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem searchMenuItem=menu.findItem(R.id.action_search_main);
        searchMenuItem.setVisible(false);

    }
}
