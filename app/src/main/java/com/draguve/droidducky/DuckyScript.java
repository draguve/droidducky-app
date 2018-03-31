package com.draguve.droidducky;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Script> scriptList = new ArrayList<>();
    ScriptsManager db = null;
    private RecyclerView recyclerView;
    private ScriptsAdapter mAdapter;

    static final int OPEN_WRITER = 1;
    static final int FIND_FILE = 1337;





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

        db = new ScriptsManager(getActivity());
        scriptList = db.getAllScripts();
        recyclerView = (RecyclerView) view.findViewById(R.id.duckyscript_recyclerview);
        mAdapter = new ScriptsAdapter(scriptList,getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter.notifyDataSetChanged();


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
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
        Intent codeEditorIntent = new Intent(this.getActivity(),CodeEditor.class);
        codeEditorIntent.putExtra("idSelected",(String) null);
        this.startActivityForResult(codeEditorIntent,OPEN_WRITER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FIND_FILE){
            Uri uri = null;
            if (data != null) {
                //Get script from file
                uri = data.getData();
                InputStream inputStream = null;
                String code="";
                try {
                    inputStream = getActivity().getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        code += (line+"");
                    }
                    reader.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String filename = uri.getPath();
                int cut = filename.lastIndexOf('/');
                if (cut != -1) {
                    filename = filename.substring(cut + 1);
                }else{
                    filename = "Unknown";
                }
                Script script = new Script(filename,code,"us");
                db.addScript(script);
            }
        }
        scriptList = db.getAllScripts();
        mAdapter.updateScriptList(scriptList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
