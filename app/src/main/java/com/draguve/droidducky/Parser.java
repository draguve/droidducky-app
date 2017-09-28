package com.draguve.droidducky;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Draguve on 9/24/2017.
 */

public class Parser {

    public static ArrayList<String> staySame = new ArrayList<>(Arrays.asList("CAPSLOCK","DELETE","HOME","END","INSERT","NUMLOCK","PAGEUP","PAGEDOWN","SCROLLLOCK","SPACE","TAB","F1","F2","F3","F4","F5","F6","F7","F8","F9","F10","F11","F12"));

    //Converts All String to print on the be print on the screen
    public static ArrayList<String> parseString(char[] line){
        ArrayList<String> toRetrun = new ArrayList<String>();
        for(char letter : line){
            toRetrun.add(convertLetter(letter));
        }
        return toRetrun;
    }

    public static String convertLetter(char letter){
        if(letter == ' '){
            return "space";
        }else if(letter == '!'){
            return "left-shift 1";
        }else if(letter == '.'){
            return ("period");
        }else if(letter == '`'){
            return ("backquote");
        }else if(letter == '~'){
            return ("left-shift tilde");
        }else if(letter == '+'){
            return ("kp-plus");
        }else if(letter == '='){
            return ("equal");
        }else if(letter == '_'){
            return ("left-shift minus");
        }else if(letter == '-'){
            return ("minus");
        }else if(letter == '\"'){
            return "left-shift quote";
        }else if(letter == '\''){
            return ("quote");
        }else if(letter == ':'){
            return ("left-shift semicolon");
        }else if(letter == ';'){
            return ("semicolon");
        }else if(letter == '<'){
            return ("left-shift comma");
        }else if(letter == ','){
            return ("comma");
        }else if(letter == '>'){
            return ("left-shift period");
        }else if(letter == '?'){
            return ("left-shift slash");
        }else if(letter == '\\'){
            return ("backslash");
        }else if(letter == '|'){
            return ("left-shift backslash");
        }else if(letter == '/'){
            return ("slash");
        }else if(letter == '{'){
            return ("left-shift lbracket");
        }else if(letter == '}'){
            return ("left-shift rbracket");
        }else if(letter == '('){
            return ("left-shift 9");
        }else if(letter == ')'){
            return ("left-shift 0");
        }else if(letter == '['){
            return ("lbracket");
        }else if(letter == ']'){
            return ("rbracket");
        }else if(letter == '#'){
            return ("left-shift 3");
        }else if(letter == '@'){
            return ("left-shift 2");
        }else if(letter == '$'){
            return ("left-shift 4");
        }else if(letter == '%'){
            return ("left-shift 5");
        }else if(letter == '^'){
            return ("left-shift 6");
        }else if(letter == '&'){
            return ("left-shift 7");
        }else if(letter == '*'){
            return ("kp-multiply");
        }else if(Character.isUpperCase(letter)){
            return "left-shift " + String.valueOf(Character.toLowerCase(letter));
        }
        return "" + letter;
    }

    //Line By Line Decodes DuckyScript
    public static ArrayList<String> parseDucky(ArrayList<String> duckyScript){
        Log.e("123","parseDucky");
        ArrayList<String> commands = new ArrayList<>();
        for(String command : duckyScript){
            parseDuckyCommand(command,commands);
        }
        return commands;
    }

    public static void parseDuckyCommand(String command,ArrayList<String> allCommands){
        String[] words = command.split(" ");
        if(words[0].toUpperCase().equals("STRING")){
            allCommands.addAll(parseString(command.substring(6).trim().toCharArray()));
        }else if(words[0].toUpperCase().equals("GUI") || words[0].toUpperCase().equals("WINDOWS")){
            allCommands.add("left-meta " + convertLetter(words[1].charAt(0)));
        }else if(words[0].toUpperCase().equals("DELAY")){
            allCommands.add(command.toUpperCase());
        }else if(words[0].toUpperCase().equals("ENTER")){
            allCommands.add("enter");
        }
    }
}
