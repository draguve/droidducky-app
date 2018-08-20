package com.draguve.droidducky;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClipboardFragment extends Fragment {


    public ClipboardFragment() {
        // Required empty public constructor
    }
    @Override
    public void onResume(){
        super.onResume();
        //maybe save this view if needed later
        final EditText clipBoardText = getView().findViewById(R.id.clipboard_text);
        final android.content.ClipboardManager clipboardManager = (ClipboardManager)getContext().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip= clipboardManager.getPrimaryClip();
        if (clip != null) {
            String text=null;
            ClipData.Item item = clip.getItemAt(0);
            if(item!=null){
                clipBoardText.setText(item.getText());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_clipboard, container, false);
        try{
            DuckConverter.loadAllProperties("us",getContext());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        final EditText clipBoardText = view.findViewById(R.id.clipboard_text);
        final android.content.ClipboardManager clipboardManager = (ClipboardManager)getContext().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip= clipboardManager.getPrimaryClip();
        if (clip != null) {
            String text=null;
            ClipData.Item item = clip.getItemAt(0);
            if(item!=null){
                clipBoardText.setText(item.getText());
            }
        }

        Button clipboardButton = view.findViewById(R.id.run_clipboard);
        clipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = clipBoardText.getText().toString();
                if(!text.equals("")){
                    SendKeytrokes(text);
                }
            }
        });

        Button customTextButton = view.findViewById(R.id.run_custom_text);
        customTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText customEditText = view.findViewById(R.id.custom_text);
                String text = customEditText.getText().toString();
                if(!text.equals("")){
                    SendKeytrokes(text);
                }
            }
        });

        return view;
    }

    public void SendKeytrokes(String text){
        try{
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("cd " + DUtils.binHome + '\n');
            ArrayList<String> commands;
            commands = DuckConverter.stringToCommands(text);
            for(String key : commands){
                String command = "echo " + key +" | ./hid-gadget-test /dev/hidg0 keyboard" + '\n';
                os.writeBytes(command);
                os.flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
