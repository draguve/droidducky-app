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

public class ResponsesAdapter extends RecyclerView.Adapter<ResponsesAdapter.ResponsesHolder> {

    private List<Response.ResponseItem> responsesList;
    private Activity mainActivityContext;

    public ResponsesAdapter(List<Response.ResponseItem> responsesList, Context mainActivityContext) {
        this.responsesList = responsesList;
        this.mainActivityContext = (Activity) mainActivityContext;
    }

    @Override
    public ResponsesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.script_list_row, parent, false);

        return new ResponsesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResponsesHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int result = 1;
                Intent responseReaderIntent = new Intent(v.getContext(), ResponseReader.class);
                responseReaderIntent.putExtra("fileName", responsesList.get(position).fileName);
                responseReaderIntent.putExtra("filePath", responsesList.get(position).fileLocation);
                mainActivityContext.startActivityForResult(responseReaderIntent, result);
            }
        });
        holder.response_view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int result = 1;
                Intent responseReaderIntent = new Intent(view.getContext(), ResponseReader.class);
                responseReaderIntent.putExtra("fileName", responsesList.get(position).fileName);
                responseReaderIntent.putExtra("filePath", responsesList.get(position).fileLocation);
                mainActivityContext.startActivityForResult(responseReaderIntent, result);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mainActivityContext)
                        .title(R.string.delete_script_dialog)
                        .positiveText(R.string.delete_dialog)
                        .negativeText(R.string.cancel_dialog)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                File toDelete = new File(Environment.getExternalStorageDirectory().toString()+"/Droidducky/responses/"+responsesList.get(position));
                                if(toDelete.delete()){
                                    Toast.makeText(mainActivityContext,"FileDeleted", Toast.LENGTH_LONG);
                                }else{
                                    Log.e("Responses","Unable to delete a file");
                                }
                            }
                        })
                        .show();
            }
        });
        holder.title.setText(responsesList.get(position).fileName);
    }

    @Override
    public int getItemCount() {
        return responsesList.size();
    }

    public class ResponsesHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public Button response_view_button,delete;

        public ResponsesHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            response_view_button = view.findViewById(R.id.list_run);
            response_view_button.setText("View");
            delete = view.findViewById(R.id.list_delete);
        }
    }
}
