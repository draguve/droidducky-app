package com.draguve.droidducky;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DuckyScript.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DuckyScript#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DuckyScript extends Fragment {
    static final int OPEN_WRITER = 1;
    static final int FIND_FILE = 1337;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ScriptsManager db = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> scriptList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ScriptsAdapter mAdapter;
    private OnFragmentInteractionListener mListener;

    public DuckyScript() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DuckyScript.
     */
    // TODO: Rename and change types and number of parameters
    public static DuckyScript newInstance(String param1, String param2) {
        DuckyScript fragment = new DuckyScript();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ducky_script, container, false);

        scriptList = getAllStoredResponse();
        recyclerView = view.findViewById(R.id.duckyscript_recyclerview);
        mAdapter = new ScriptsAdapter(scriptList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter.notifyDataSetChanged();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("DuckyScript");

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCode(view);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void addNewCode(View view) {
//        Intent codeEditorIntent = new Intent(getActivity(), CodeEditor.class);
//        codeEditorIntent.putExtra("idSelected", (String) null);
//        codeEditorIntent.putExtra("editingMode", 0);
//        startActivityForResult(codeEditorIntent, OPEN_WRITER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scriptList = getAllStoredResponse();
        mAdapter.updateScriptList(scriptList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ArrayList<String> getAllStoredResponse(){
        ArrayList<String> responses = new ArrayList<String>();
        String path = Environment.getExternalStorageDirectory().toString()+"/DroidDucky/DuckyScripts/";
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null){
            for (int i = 0; i < files.length; i++)
            {
                responses.add(files[i].getName());
                Log.e("Test",files[i].getAbsolutePath());
            }
        }
        return responses;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
