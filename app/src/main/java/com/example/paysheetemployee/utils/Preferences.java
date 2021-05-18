package com.example.paysheetemployee.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final String pPaysheetToken = "com.example.paysheetemployee.token";
    private static final String pUsername = "com.example.paysheetemployee.username";

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getPaysheetToken() {
        return preferences.getString(pPaysheetToken,"").replace("\"","");
    }

    public void setPaysheetToken(String pToken){
        editor = preferences.edit();
        editor.putString(pPaysheetToken, pToken);
        editor.commit();
    }

    public String getPUsername() {
        return preferences.getString(pUsername,"").replace("\"","");
    }

    public void setPUsername(String name){
        editor = preferences.edit();
        editor.putString(pUsername, name);
        editor.commit();
    }

}
