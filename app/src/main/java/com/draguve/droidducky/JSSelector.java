package com.draguve.droidducky;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JSSelector extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private JSAdapter mAdapter;
    public List<String> scriptList;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public JSSelector() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jsselector, container, false);
        scriptList = getAllStoredResponse();
        recyclerView = view.findViewById(R.id.jsselector_recyclerview);
        mAdapter = new JSAdapter(scriptList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFormat df = new SimpleDateFormat("EEE,d-MMM-yyyy-HH:mm:ss");
                String newFileName = df.format(Calendar.getInstance().getTime())+".js";
                final int result = 1;
                Intent responseReaderIntent = new Intent(view.getContext(), ResponseReader.class);
                responseReaderIntent.putExtra("fileName",newFileName);
                responseReaderIntent.putExtra("filePath", Environment.getExternalStorageDirectory().toString()+"/Droidducky/DuckyScripts/"+newFileName);
                responseReaderIntent.putExtra("canEdit",true);
                responseReaderIntent.putExtra("scripttype","js");
                startActivityForResult(responseReaderIntent, result);
            }
        });

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.js_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("JavaScript");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    ArrayList<String> getAllStoredResponse(){
        ArrayList<String> responses = new ArrayList<String>();
        String path = Environment.getExternalStorageDirectory().toString()+"/Droidducky/JavaScript/";
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null){
            for (int i = 0; i < files.length; i++)
            {
                responses.add(files[i].getName());
            }
        }
        return responses;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        Log.e("this","ran");
        scriptList = getAllStoredResponse();
        mAdapter.updateScriptList(scriptList);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scriptList = getAllStoredResponse();
        mAdapter.updateScriptList(scriptList);
    }
}
