package com.draguve.droidducky;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    public DDScreen currentScreen = null;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void createFolder() {
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "Droidducky");
        if (!file.exists()) {
            file.mkdirs();
        }
        File serverFolder = new File(file, "host");
        if (!serverFolder.exists()) {
            serverFolder.mkdirs();
        }
        File codeFolder = new File(file, "code");
        if (!codeFolder.exists()) {
            codeFolder.mkdirs();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        DUtils.setupFilesForInjection(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        currentScreen = DDScreen.DUCKYSCRIPT;
        if (checkPermissions()) {
            createFolder();
        }

        DuckyScript duckyScriptScreen = new DuckyScript();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, duckyScriptScreen)
                .addToBackStack(null)
                .commit();

        //Code to get custom status bar and nav bar color
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.source_code:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.repo)));
                startActivity(browserIntent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dd_duckyscript && currentScreen != DDScreen.DUCKYSCRIPT) {
            currentScreen = DDScreen.DUCKYSCRIPT;
            DuckyScript duckyScriptScreen = new DuckyScript();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, duckyScriptScreen)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.dd_terminal && currentScreen != DDScreen.TERMINAL) {
            currentScreen = DDScreen.TERMINAL;
            TerminalFragment terminalFragment = new TerminalFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, terminalFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.dd_keyboard && currentScreen != DDScreen.KEYBOARD) {
            String packageName = "remote.hid.keyboard.client";
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);

            if (intent == null) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            }
            startActivity(intent);
        } else if (id == R.id.dd_clipboard && currentScreen != DDScreen.CLIPBOARD) {
            currentScreen = DDScreen.CLIPBOARD;
            ClipboardFragment clipboardFragment = new ClipboardFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, clipboardFragment)
                    .addToBackStack(null)
                    .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    createFolder();
                } else {
                    Log.d("Permissions", "PermissionsProblem");
                }
                return;
            }
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public enum DDScreen {DUCKYSCRIPT, TERMINAL, KEYBOARD, CLIPBOARD}
}
