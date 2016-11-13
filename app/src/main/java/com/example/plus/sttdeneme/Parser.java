package com.example.plus.sttdeneme;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Elif SALMAN on 12.11.2016.
 */

public class Parser {

    private String LOG_TAG = "Parser";

    String input;
    Pattern pattern;

    public Parser(String input) {
        this.input = input;
    }

    public boolean find(String search){
        if(input == null && search == null ){
            return false;
        }
        pattern = Pattern.compile(search);
        Matcher matcher = pattern.matcher(input);
        boolean isFound =matcher.find();
        if(isFound){
           Log.e(LOG_TAG, "found: "+matcher.group());
        }
        return isFound;
    }


}
