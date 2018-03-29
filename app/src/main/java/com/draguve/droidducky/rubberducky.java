package com.draguve.droidducky;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class rubberducky extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_rubberducky);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rubberducky, menu);
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
            // case blocks for other MenuItems (if any)
        }
        return false;
    }

    public void addNewCode(View view) {
        Intent codeEditorIntent = new Intent(this,CodeEditor.class);
        codeEditorIntent.putExtra("idSelected",(String) null);
        this.startActivityForResult(codeEditorIntent,OPEN_WRITER);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.rb_rubber_ducky) {

        } else if (id == R.id.rb_command_line) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null)
            server.stop();
    }
}
