package com.draguve.droidducky;

import android.content.Context;
import android.util.Log;

import com.eclipsesource.v8.V8;

import java.util.Properties;

public class JSExecuter {

    /* contains the keyboard configuration */
    private static Properties keyboardProps;
    /* contains the language layout */
    private static Properties layoutProps;
    /* contains the commands configuration */
    private static Properties commandProps;

    public JSExecuter(String lang, Context appContext){
        try {
            keyboardProps = DuckConverter.loadProperties("keyboard", appContext);
            layoutProps = DuckConverter.loadProperties(lang, appContext);
            commandProps = DuckConverter.loadProperties("commands", appContext);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void executeJS(String js){
        V8 runtime = V8.createV8Runtime();
        int result = runtime.executeIntegerScript(""
                + "var hello = 'hello, ';\n"
                + "var world = 'world!';\n"
                + "hello.concat(world).length;\n");
        Log.e("Test","JS result = "+result);
        runtime.release();
    }

}
