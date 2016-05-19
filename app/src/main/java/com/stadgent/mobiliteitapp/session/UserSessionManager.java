package com.stadgent.mobiliteitapp.session;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import com.stadgent.mobiliteitapp.activities.LoginActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by floriangoeteyn on 04-Apr-16.
 */
public class UserSessionManager {

    //Secured Preferences reference
    private static SecurePreferences secpref;

    // Context
    static Context _context;

    // Sharedpref file name
    private static final String PREFER_NAME = "AndroidExamplePref";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "icon_email";


    // Constructor
    public UserSessionManager(Context context) {
        _context = context;
        secpref = new SecurePreferences(context, PREFER_NAME, "secret", true);
    }


    //Create login session
    public void createUserLoginSession(String name, String email) {
        // Storing login value as TRUE
        secpref.put(IS_USER_LOGIN, true);

        // Storing name in pref
        secpref.put(KEY_NAME, name);

        // Storing icon_email in pref
        secpref.put(KEY_EMAIL, email);

        // commit changes
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     */
    public boolean checkLogin() {
        // Check login status
        if (!this.isUserLoggedIn()) {

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return false;
        }
        return true;
    }


    /**
     * Get stored session data
     */
    public static HashMap<String, String> getUserDetails() {

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<>();

        // user name
        user.put(KEY_NAME, secpref.getString(KEY_NAME));


        // user icon_email id
        user.put(KEY_EMAIL, secpref.getString(KEY_EMAIL));

        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public static void logoutUser() {

        // Clearing all user data from Shared Preferences
        secpref.clear();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn() {
        return secpref.getBoolean(IS_USER_LOGIN);
    }
}
