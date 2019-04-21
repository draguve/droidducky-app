package com.draguve.droidducky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

public class JSAdapter extends RecyclerView.Adapter<JSAdapter.JSHolder> {


    private List<String> fileList;
    private Activity mainActivityContext;

    public JSAdapter(List<String> scriptList, Context mainActivityContext) {
        this.fileList = scriptList;
        this.mainActivityContext = (Activity) mainActivityContext;
    }

    @Override
    public JSHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(JSHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent responseReaderIntent = new Intent(v.getContext(), ResponseReader.class);
                responseReaderIntent.putExtra("fileName", fileList.get(position));
                responseReaderIntent.putExtra("filePath", Environment.getExternalStorageDirectory().toString()+"/DroidDucky/JavaScript/"+fileList.get(position));
                responseReaderIntent.putExtra("canEdit",true);
                mainActivityContext.startActivityForResult(responseReaderIntent, result);
            }
        });
        holder.runCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code to start jsexecuter
            }
        });
        holder.title.setText(fileList.get(position));

        ScriptDatabase db = new ScriptDatabase(mainActivityContext);
        ScriptID scriptID = db.getScript(fileList.get(position));
        if(scriptID!=null){
            holder.lang.setText(scriptID.getLanguage());
        }else{
            holder.lang.setText("us");
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class JSHolder extends RecyclerView.ViewHolder
    {
        public TextView title;
        public Button runCode;
        public TextView lang;

        public JSHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            runCode = itemView.findViewById(R.id.list_run);
            lang = itemView.findViewById(R.id.lang);
        }
    }
}
