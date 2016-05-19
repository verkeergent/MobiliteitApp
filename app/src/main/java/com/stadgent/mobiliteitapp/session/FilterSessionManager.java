package com.stadgent.mobiliteitapp.session;

import android.content.Context;
import android.util.Pair;

/**
 * Created by floriangoeteyn on 26-Apr-16.
 */
public class FilterSessionManager {

    //Secured Preferences reference
    private static SecurePreferences secpref;

    // Context
    static Context _context;

    // Sharedpref file name
    private static final String PREFER_NAME = "AndroidExamplePref";

    public FilterSessionManager(Context context) {
        _context = context;
        //pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        //editor = pref.edit();
        secpref = new SecurePreferences(context, PREFER_NAME, "secret", true);
    }


    public void putFilterValue(String key, boolean value){
        secpref.put(key, value);
    }

    public static boolean getFilterValue(String key){
        return secpref.getBoolean(key, true);
    }



}
