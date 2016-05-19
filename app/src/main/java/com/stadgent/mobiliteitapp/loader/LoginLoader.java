package com.stadgent.mobiliteitapp.loader;

import com.stadgent.mobiliteitapp.model.User;
import com.stadgent.mobiliteitapp.repo.ItemRepository;
import com.stadgent.mobiliteitapp.rest.LoginCallback;

/**
 * Created by floriangoeteyn on 18-May-16.
 */
public class LoginLoader {

    LoginCallback callback;

    public LoginLoader(String username, String password){
        this.callback = new LoginCallback(this, username, password);
    }

    public void onUserLoggedIn(User user){
        ItemRepository.onLoginSuccess(user);
    }

    public void onLoginFailed(){
        ItemRepository.onLoginFailed();
    }

}
