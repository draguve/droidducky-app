package com.draguve.droidducky;

/**
 * Created by draguve on Rumsha's Laptop on 1/1/18.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class DuckConverter {


    /* contains the keyboard configuration */
    private static Properties keyboardProps;
    /* contains the language layout */
    private static Properties layoutProps;
    /* contains the commands configuration */
    private static Properties commandProps;
    private static int defaultDelay = 200;
    private static String lastLine;

    public static ArrayList<String> convert(ArrayList<String> DuckLines, String lang, Context appContext) {
        try {
            loadAllProperties(lang, appContext);
        } catch (IOException e) {
            Log.e("DuckConverter", e.toString());
        }
        ArrayList<String> letters = new ArrayList<>();
        for (String line : DuckLines) {
            letters.addAll(convertLine(line, appContext));
        }
        return letters;
    }

    public static void loadAllProperties(String lang, Context context) throws IOException {
        keyboardProps = loadProperties("keyboard", context);
        layoutProps = loadProperties(lang, context);
        commandProps = loadProperties("commands", context);
    }

    public static Properties loadProperties(String file, Context context) throws IOException {
        String filename = file + ".properties";
        Properties prop = new Properties();
        if (context == null) {
            Log.e("DuckConverter", "Context is Null");
        }
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(filename);
        if (inputStream == null) {
            Log.e("DuckConverter", "Language not found");
        }
        prop.load(inputStream);
        return prop;
    }

    public static ArrayList<String> stringToCommands(String input) {
        ArrayList<String> commands = new ArrayList<>();
        for (char x : input.toCharArray()) {
            commands.add(charToCommand(x));
        }
        return commands;
    }

    private static String charToCode(char c) {
        String code;
        if (c < 128) {
            code = "ASCII_" + Integer.toHexString(c).toUpperCase();
        } else if (c < 256) {
            code = "ISO_8859_1_" + Integer.toHexString(c).toUpperCase();
        } else {
            code = "UNICODE_" + Integer.toHexString(c).toUpperCase();
        }
        return code;
    }

    private static String codeToCommand(String str) {
        if (layoutProps.getProperty(str) != null) {
            String keys[] = layoutProps.getProperty(str).split(",");
            StringBuilder code = new StringBuilder();
            for (int j = keys.length - 1; j >= 0; j--) {
                String key = keys[j].trim();
                if (keyboardProps.getProperty(key) != null) {
                    code.append(keyboardProps.getProperty(key).trim());
                    code.append(" ");
                } else if (layoutProps.getProperty(key) != null) {
                    code.append(layoutProps.getProperty(key).trim());
                    code.append(" ");
                } else {
                    System.out.println("Key not found:" + key);
                }
            }
            return code.toString();
        } else {
            System.out.println("Char not found:" + str);
            return null;
        }
    }

    private static String charToCommand(char c) {
        return codeToCommand(charToCode(c));
    }

    public static String convertCommand(String[] words) {
        if (words.length > 1) {
            String word = words[0].trim().toUpperCase();
            word = commandProps.getProperty(word, "");
            return word + " " + convertCommand(Arrays.copyOfRange(words, 1, words.length));
        } else {
            if (words[0].length() == 1) {
                return "" + charToCommand(words[0].charAt(0));
            } else {
                return commandProps.getProperty(words[0].trim().toUpperCase(), "");
            }
        }
    }

    public static ArrayList<String> convertLine(String line, Context context, String last) {
        ArrayList<String> letters = new ArrayList<>();
        String[] words = line.trim().split(" ");
        if (words[0].trim().toUpperCase().equals("STRING")) {
            return stringToCommands(line.trim().substring(6));
        } else if (words[0].trim().toUpperCase().equals("REPEAT")) {
            int numberOfTimes = 1;
            if (words.length > 1) {
                try {
                    numberOfTimes = Integer.parseInt(words[1]);
                } catch (Exception e) {
                    System.out.print("Could'nt Convert Number");
                }
            } else {
                numberOfTimes = 1;
            }
            for (int i = 0; i < numberOfTimes; i++) {
                letters.addAll(convertLine(last, context, null));
            }
            return letters;
        } else if (words[0].trim().toUpperCase().equals("REM")) {
            letters.add("\u0001" + line.substring(3).trim());
            return letters;
        } else if (words[0].trim().toUpperCase().equals("DELAY") || words[0].trim().toUpperCase().equals("SLEEP")) {
            letters.add("\u0002" + line.substring(5).trim());
            return letters;
        } else if (words[0].trim().toUpperCase().equals("LOCAL_IP")) {
            letters.add("\u0006" + 0);
            return letters;
        } else if (words[0].trim().toUpperCase().equals("WIFI_IP")) {
            letters.add("\u0006" + 1);
            return letters;
        } else if (words[0].trim().toUpperCase().equals("DEFAULTDELAY") || words[0].trim().toUpperCase().equals("DEFAULT_DELAY")) {
            if (words.length > 1) {
                try {
                    defaultDelay = Integer.parseInt(words[1]);
                } catch (Exception e) {
                    System.out.print("Couldnt Convert Number");
                }
            } else {
                letters.add("\u0002" + defaultDelay);
                return letters;
            }
        } else if (words[0].trim().toUpperCase().equals("WRITE_FILE")) {
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path, "/DroidDucky/code/" + words[1].trim());
            if (file.exists()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String receiveString;
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        letters.addAll(stringToCommands(receiveString));
                        letters.add("enter");
                    }
                    bufferedReader.close();
                } catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                }
            } else {
                Toast.makeText(context, "Can't Find File , Ignoring File ", Toast.LENGTH_SHORT).show();
            }
            return letters;
        } else {
            letters.add(convertCommand(line.trim().split(" ")));
            return letters;
        }
        return null;
    }

    private static ArrayList<String> convertLine(String line, Context context) {
        ArrayList toReturn = convertLine(line, context, lastLine);
        lastLine = line;
        return toReturn;
    }
}
