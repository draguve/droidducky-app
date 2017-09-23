package com.draguve.droidducky;


import java.util.ArrayList;

/**
 * Created by Draguve on 9/24/2017.
 */

public class Parser {

    //Converts All String to print on the be print on the screen
    public static ArrayList<String> convertString(char[] line){
        ArrayList<String> toRetrun = new ArrayList<String>();
        for(char letter : line){
            if(letter == ' '){
                toRetrun.add("space");
            }else if(letter == '!'){
                toRetrun.add("left-shift 1");
            }else if(letter == '.'){
                toRetrun.add("period");
            }else if(letter == '`'){
                toRetrun.add("backquote");
            }else if(letter == '~'){
                toRetrun.add("left-shift tilde");
            }else if(letter == '+'){
                toRetrun.add("kp-plus");
            }else if(letter == '='){
                toRetrun.add("equal");
            }else if(letter == '_'){
                toRetrun.add("left-shift minus");
            }else if(letter == '-'){
                toRetrun.add("minus");
            }else if(letter == '\"'){
                toRetrun.add("left-shift quote");
            }else if(letter == '\''){
                toRetrun.add("quote");
            }else if(letter == ':'){
                toRetrun.add("left-shift semicolon");
            }else if(letter == ';'){
                toRetrun.add("semicolon");
            }else if(letter == '<'){
                toRetrun.add("left-shift comma");
            }else if(letter == ','){
                toRetrun.add("comma");
            }else if(letter == '>'){
                toRetrun.add("left-shift period");
            }else if(letter == '?'){
                toRetrun.add("left-shift slash");
            }else if(letter == '\\'){
                toRetrun.add("backslash");
            }else if(letter == '|'){
                toRetrun.add("left-shift backslash");
            }else if(letter == '/'){
                toRetrun.add("slash");
            }else if(letter == '{'){
                toRetrun.add("left-shift lbracket");
            }else if(letter == '}'){
                toRetrun.add("left-shift rbracket");
            }else if(letter == '('){
                toRetrun.add("left-shift 9");
            }else if(letter == ')'){
                toRetrun.add("left-shift 0");
            }else if(letter == '['){
                toRetrun.add("lbracket");
            }else if(letter == ']'){
                toRetrun.add("rbracket");
            }else if(letter == '#'){
                toRetrun.add("left-shift 3");
            }else if(letter == '@'){
                toRetrun.add("left-shift 2");
            }else if(letter == '$'){
                toRetrun.add("left-shift 4");
            }else if(letter == '%'){
                toRetrun.add("left-shift 5");
            }else if(letter == '^'){
                toRetrun.add("left-shift 6");
            }else if(letter == '&'){
                toRetrun.add("left-shift 7");
            }else if(letter == '*'){
                toRetrun.add("kp-multiply");
            }else if(Character.isUpperCase(letter)){
                toRetrun.add("left-shift " + String.valueOf(Character.toLowerCase(letter)));
            }else{
                toRetrun.add(String.valueOf(letter));
            }

        }
        return toRetrun;
    }
}
