package com.draguve.droidducky;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class selector extends AppCompatActivity {

    private List<Script> scriptList = new ArrayList<>();
    ScriptsManager db = null;
    private RecyclerView recyclerView;
    private ScriptsAdapter mAdapter;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    static final int OPEN_WRITER = 1;
    static final int FIND_FILE = 1337;

    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private httpserver server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        DUtils.setupFilesForInjection(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        db = new ScriptsManager(this);
        scriptList = db.getAllScripts();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ScriptsAdapter(scriptList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter.notifyDataSetChanged();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.selector_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(checkPermissions()){
            createFolder();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FIND_FILE){
            Uri uri = null;
            if (data != null) {
                //Get script from file
                uri = data.getData();
                InputStream inputStream = null;
                String code="";
                try {
                    inputStream = getContentResolver().openInputStream(uri);
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

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                    createFolder();
                } else {
                    Log.d("Permissions","PermissionsProblem");
                }
                return;
            }
        }
    }


    public void addNewCode(View view) {
        Intent codeEditorIntent = new Intent(this,CodeEditor.class);
        codeEditorIntent.putExtra("idSelected",(String) null);
        this.startActivityForResult(codeEditorIntent,OPEN_WRITER);
    }

    public static void createFolder(){
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path,"Droidducky");
        if (!file.exists()) {
            file.mkdirs();
        }
        // TODO create a settings page to change the folders automagicly
        File serverFolder = new File(file,"host");
        if(!serverFolder.exists()){
            serverFolder.mkdirs();
        }
        File codeFolder = new File(file,"code");
        if(!codeFolder.exists()){
            codeFolder.mkdirs();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selector_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_script: {
                addNewCode(item.getActionView());
                break;
            }
            case R.id.add_file:{
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/*");
                    startActivityForResult(intent, FIND_FILE);
                break;
            }
            case R.id.enable_tether:{
                DUtils.setUSBTether(true);
                break;
            }
            case R.id.disable_tether:{
                DUtils.setUSBTether(false);
                break;
            }
            case R.id.enable_server:{
                if(server==null){
                    server = new httpserver();
                    try {
                        server.start();
                    } catch(IOException ioe) {
                        Log.w("Httpd", "The server could not start.");
                    }
                    Log.w("Httpd", "Web server initialized.");
                    Toast.makeText(this,"Server Started",Toast.LENGTH_SHORT);
                }
                break;
            }case R.id.disable_server:{
                if(server!=null){
                    Toast.makeText(this,"Server Stopped",Toast.LENGTH_SHORT);
                    server.stop();
                    server = null;
                }
            }
            // ca`se blocks for other MenuItems (if any)
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null)
            server.stop();
    }
}
