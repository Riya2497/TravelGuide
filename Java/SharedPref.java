package com.jignesh.streety;

import android.content.Context;
import android.content.SharedPreferences;

import static java.lang.Integer.parseInt;

public class SharedPref
{
    SharedPreferences sp;
    private static Context context;
    public final static String PREFS_NAME = "appname_prefs";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    SharedPref(Context context)
    {
        this.context = context;
        sharedPref = context.getSharedPreferences(PREFS_NAME,0);
        editor = sharedPref.edit();
    }
    public void setFBId( String value,Boolean b) {

        editor.putString("fbid",value);
        editor.putBoolean("loggedf",b);
        editor.commit();
    }
    public Boolean getFBId() {
        return sharedPref.getBoolean("loggedf",false);
    }

    public void setGoogleId( String value,Boolean b) {

        editor.putString("gid",value);
        editor.putBoolean("loggedg",b);
        editor.commit();
    }
    public Boolean getGoogleId() {
        return sharedPref.getBoolean("loggedg",false);
    }


    public void setEmail( String value,Boolean b) {

        editor.putString("email",value);
        editor.putBoolean("loggede",b);
        editor.commit();
    }
    public Boolean getEmail() {
        return sharedPref.getBoolean("loggede",false);
    }

    public String getEmailValue() { return sharedPref.getString("email","");}

    public String getFBIdValue() { return sharedPref.getString("fbid","");}

    public String getGoogleIdValue() { return sharedPref.getString("gid","");}


    public void setUsername( String value) {

        editor.putString("user",value);
        editor.commit();
    }

    public String getUsername() {
        return sharedPref.getString("user","");
    }

    public void setBool(Boolean b)
    {
        editor.putBoolean("first",b);
        editor.commit();
    }

    public Boolean getBool()
    {
        return sharedPref.getBoolean("first",false);
    }

    public void setTripCount(int count)
    {
        editor.putInt("trip_count",count);
        editor.commit();
    }
    public int getTripCount()
    {
        return sharedPref.getInt("trip_count",0);
    }
}


