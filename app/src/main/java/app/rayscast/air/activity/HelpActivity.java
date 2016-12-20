package app.rayscast.air.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.rayscast.air.R;
import app.rayscast.air.adapters.ExpandableListAdapter;

public class HelpActivity extends AppCompatActivity {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private List<String> listDataChild;
    private Toolbar toolbarHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        toolbarHelp=(Toolbar)findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbarHelp);
        getSupportActionBar().setIcon(R.drawable.ray_cast_xxhdpi);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

    }


    public void prepareListData(){
        listDataHeader= Arrays.asList(getResources().getStringArray(R.array.Questions));
        listDataChild=Arrays.asList(getResources().getStringArray(R.array.Answers));
    }

}
