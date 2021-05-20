package com.example.paysheetemployee.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final String pPaysheetToken = "com.example.paysheetemployee.token";
    private static final String pUsername = "com.example.paysheetemployee.username";
    private static final String pLattitude = "com.example.paysheetemployee.lattitude";
    private static final String pLongitude = "com.example.paysheetemployee.longitude";
    private static final String pLoginStatus = "com.example.paysheetemployee.login";

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

    public String getPLoginStatus() {
        return preferences.getString(pLoginStatus,"").replace("\"","");
    }

    public void setPLoginStatus(String login){
        editor = preferences.edit();
        editor.putString(pLoginStatus, login);
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

    public String getPLattitude() {
        return preferences.getString(pLattitude,"").replace("\"","");
    }

    public void setPLattitude(String lattitude){
        editor = preferences.edit();
        editor.putString(pLattitude, lattitude);
        editor.commit();
    }

    public String getPLongitude() {
        return preferences.getString(pLongitude,"").replace("\"","");
    }

    public void setPLongitude(String longitude){
        editor = preferences.edit();
        editor.putString(pLongitude, longitude);
        editor.commit();
    }

}
