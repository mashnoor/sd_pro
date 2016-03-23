package com.smartdsepro.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mashnoor on 3/15/16.
 */
public class LoginHelper {
    //Get Current Login Status
    public static boolean isLoggedin(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String name = prefs.getString(Constants.USER_NAME, Constants.LOGIN_NAME_NOT_SET);
        if(name.equals(Constants.LOGIN_NAME_NOT_SET))
        {
            return false;
        }
        else
        {
            return true;
        }

    }
    public static String getUserName(Context activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String name = prefs.getString(Constants.USER_NAME, Constants.LOGIN_NAME_NOT_SET);
        return name;

    }
    public static String getUserEmail(Context activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String email = prefs.getString(Constants.EMAIL, Constants.LOGIN_NAME_NOT_SET);
        return email;
    }
    public static void setUserEmail(Context activity, String email)
    {

        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE).edit();

        editor.putString(Constants.EMAIL, email);
        //editor.commit();
        editor.apply();
    }

    public static void setName(Activity activity, String userName)
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE).edit();

        editor.putString(Constants.USER_NAME, userName);
        //editor.commit();
        editor.apply();
    }
    public static void removeName(Activity activity)
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE).edit();

        editor.putString(Constants.USER_NAME, Constants.LOGIN_NAME_NOT_SET);
        //editor.commit();
        editor.apply();
    }
    public static void setLoggedInUsing(Activity activity, String c)
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE).edit();

        editor.putString(Constants.LOGGEDIN_USING, c);
        //editor.commit();
        editor.apply();
    }
    public static String getLoggedInUsing(Activity activity)
    {
        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String loggedInUsing = prefs.getString(Constants.LOGGEDIN_USING, Constants.LOGIN_NAME_NOT_SET);
        return loggedInUsing;
    }




}
