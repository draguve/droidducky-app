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
                .inflate(R.layout.response_list_row, parent, false);

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
        holder.title.setText(responsesList.get(position).fileName);
    }

    @Override
    public int getItemCount() {
        return responsesList.size();
    }

    public class ResponsesHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public Button response_view_button;

        public ResponsesHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
        }
    }
}
