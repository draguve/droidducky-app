package com.draguve.droidducky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

/**
 * Created by Draguve on 4/1/2018.
 */

public class CommandLineScriptAdapter extends RecyclerView.Adapter<CommandLineScriptAdapter.MyViewHolder>{

    private List<CommandLineScript> scriptList;
    private Activity mainActivityContext;
    private CommandLineManager db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;
        public Button run,delete;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            run = (Button)view.findViewById(R.id.list_run);
            delete = (Button)view.findViewById(R.id.list_delete);
        }
    }
    public CommandLineScriptAdapter(List<CommandLineScript> scriptList,Context mainActivityContext) {
        this.scriptList = scriptList;
        this.mainActivityContext = (Activity) mainActivityContext;
        db = new CommandLineManager(mainActivityContext);
    }

    @Override
    public CommandLineScriptAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.script_list_row, parent, false);

        return new CommandLineScriptAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommandLineScriptAdapter.MyViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent codeEditorIntent = new Intent(v.getContext(),CodeEditor.class);
                codeEditorIntent.putExtra("idSelected",scriptList.get(position).getID());
                codeEditorIntent.putExtra("editingMode",1);
                mainActivityContext.startActivityForResult(codeEditorIntent,result);
            }
        });
        holder.run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent codeEditorIntent = new Intent(v.getContext(),ExecuterActivity.class);
                codeEditorIntent.putExtra("idSelected",scriptList.get(position).getID());
                codeEditorIntent.putExtra("currentMode",1);
                mainActivityContext.startActivityForResult(codeEditorIntent,result);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mainActivityContext)
                        .title("Do you really want to delete the Script")
                        .positiveText("Delete")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                db.deleteScript(scriptList.get(position).getID());
                                updateScriptList(db.getAllScripts());
                            }
                        })
                        .show();
            }
        });
        CommandLineScript script = scriptList.get(position);
        holder.title.setText(script.getName());
        holder.genre.setText("Test");
    }

    @Override
    public int getItemCount() {
        return scriptList.size();
    }

    public void updateScriptList(List<CommandLineScript> scripts){
        this.scriptList.clear();
        this.scriptList.addAll(scripts);
        this.notifyDataSetChanged();
    }

    public List<CommandLineScript> getCurrentList(){
        return scriptList;
    }
}

