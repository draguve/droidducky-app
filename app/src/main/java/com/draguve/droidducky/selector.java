package com.draguve.droidducky;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class selector extends AppCompatActivity {

    private List<Script> scriptList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ScriptsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DUtils.setupFilesForInjection(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        ScriptsManager db = new ScriptsManager(this);
        scriptList = db.getAllScripts();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ScriptsAdapter(scriptList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter.notifyDataSetChanged();
    }
}
