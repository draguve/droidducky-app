package com.draguve.droidducky;

/**
 * Created by Draguve on 1/3/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.List;

import javax.xml.datatype.Duration;


public class ScriptsAdapter extends RecyclerView.Adapter<ScriptsAdapter.MyViewHolder> {

    private List<String> scriptList;
    private Activity mainActivityContext;
    DuckyScript selector;


    public ScriptsAdapter(List<String> scriptList, Context mainActivityContext,DuckyScript selector) {
        this.scriptList = scriptList;
        this.mainActivityContext = (Activity) mainActivityContext;
        this.selector = selector;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.script_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent responseReaderIntent = new Intent(v.getContext(), ResponseReader.class);
                responseReaderIntent.putExtra("fileName", scriptList.get(position));
                responseReaderIntent.putExtra("filePath", Environment.getExternalStorageDirectory().toString()+"/DroidDucky/DuckyScripts/"+scriptList.get(position));
                responseReaderIntent.putExtra("canEdit",true);
                responseReaderIntent.putExtra("scripttype","duckyscript");
                mainActivityContext.startActivityForResult(responseReaderIntent, result);
            }
        });
        holder.run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent executeIntent = new Intent(v.getContext(), ExecuterActivity.class);
                executeIntent.putExtra("fileName", scriptList.get(position));
                executeIntent.putExtra("filePath", Environment.getExternalStorageDirectory().toString()+"/Droidducky/DuckyScripts/"+scriptList.get(position));
                executeIntent.putExtra("scripttype","duckyscript");
                mainActivityContext.startActivityForResult(executeIntent, result);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mainActivityContext)
                        .title(R.string.delete_script_dialog)
                        .positiveText(R.string.delete_dialog)
                        .negativeText(R.string.cancel_dialog)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                File toDelete = new File(Environment.getExternalStorageDirectory().toString()+"/Droidducky/DuckyScripts/"+scriptList.get(position));
                                if(toDelete.delete()){
                                    updateScriptList(selector.getAllStoredResponse());
                                    Toast.makeText(mainActivityContext,"FileDeleted", Toast.LENGTH_LONG);
                                }else{
                                    Log.e("ScriptsAdapter","Unable to delete a file");
                                }
                            }
                        })
                        .show();
            }
        });
        String script = scriptList.get(position);
        holder.title.setText(script);
    }

    @Override
    public int getItemCount() {
        return scriptList.size();
    }

    public void updateScriptList(List<String> scripts) {
        this.scriptList.clear();
        this.scriptList.addAll(scripts);
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public Button run, delete;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            run = view.findViewById(R.id.list_run);
            delete = view.findViewById(R.id.list_delete);
        }
    }
}
