package com.stadgent.mobiliteitapp.repo;

import com.stadgent.mobiliteitapp.model.User;

/**
 * Created by floriangoeteyn on 04-Apr-16.
 */
public interface OnLoggedInListener {

    void onLoginSuccess(User user);
    void onLoginFailed();

}
