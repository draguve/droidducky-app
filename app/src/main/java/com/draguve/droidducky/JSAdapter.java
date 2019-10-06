package com.draguve.droidducky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class JSAdapter extends RecyclerView.Adapter<JSAdapter.JSHolder> {


    private List<String> fileList;
    private Activity mainActivityContext;
    JSSelector selector;

    public JSAdapter(List<String> scriptList, Context mainActivityContext,JSSelector selector) {
        this.fileList = scriptList;
        this.mainActivityContext = (Activity) mainActivityContext;
        this.selector = selector;
    }

    @Override
    public JSHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.script_list_row, parent, false);

        return new JSHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JSHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent responseReaderIntent = new Intent(v.getContext(), ResponseReader.class);
                responseReaderIntent.putExtra("fileName", fileList.get(position));
                responseReaderIntent.putExtra("filePath", Environment.getExternalStorageDirectory().toString()+"/Droidducky/JavaScript/"+fileList.get(position));
                responseReaderIntent.putExtra("canEdit",true);
                responseReaderIntent.putExtra("scripttype","js");
                mainActivityContext.startActivityForResult(responseReaderIntent, result);
            }
        });
        holder.runCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent executerIntent = new Intent(v.getContext(), JSExecuterActivity.class);
                executerIntent.putExtra("filePath", Environment.getExternalStorageDirectory().toString()+"/Droidducky/JavaScript/"+fileList.get(position));
                mainActivityContext.startActivityForResult(executerIntent, result);
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
                                File toDelete = new File(Environment.getExternalStorageDirectory().toString()+"/Droidducky/JavaScript/"+fileList.get(position));
                                if(toDelete.delete()){
                                    updateScriptList(selector.getAllStoredResponse());
                                    Toast.makeText(mainActivityContext,"FileDeleted", Toast.LENGTH_LONG);
                                }else{
                                    Log.e("JSAdapter","Unable to delete a file");
                                }
                            }
                        })
                        .show();
            }
        });
        holder.title.setText(fileList.get(position));
    }

    public void updateScriptList(List<String> scripts) {
        fileList.clear();
        fileList.addAll(scripts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class JSHolder extends RecyclerView.ViewHolder
    {
        public TextView title;
        public Button runCode;
        public Button delete;

        public JSHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            runCode = itemView.findViewById(R.id.list_run);
            delete = itemView.findViewById(R.id.list_delete);
        }
    }
}
