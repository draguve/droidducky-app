package com.draguve.droidducky;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.eclipsesource.v8.V8;

import java.util.Properties;

public class JSExecuterActivity extends AppCompatActivity {


    /* contains the keyboard configuration */
    private Properties keyboardProps;
    /* contains the language layout */
    private Properties layoutProps;
    /* contains the commands configuration */
    private Properties commandProps;
    Context appContext;

    public void executeJS(String js){
        V8 runtime = V8.createV8Runtime();
        int result = runtime.executeIntegerScript(""
                + "var hello = 'hello, ';\n"
                + "var world = 'world!';\n"
                + "hello.concat(world).length;\n");
        Log.e("Test","JS result = "+result);
        runtime.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsexecuter);

        appContext = getApplicationContext();
        try {
            keyboardProps = DuckConverter.loadProperties("keyboard", appContext);
            //TODO: Change this default language selection
            layoutProps = DuckConverter.loadProperties("us", appContext);
            commandProps = DuckConverter.loadProperties("commands", appContext);
        }catch (Exception e){
            e.printStackTrace();
        }

        executeJS("");

    }
}
