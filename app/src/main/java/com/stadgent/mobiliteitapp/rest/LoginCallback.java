package com.stadgent.mobiliteitapp.rest;

import android.util.Log;

import com.squareup.okhttp.Headers;
import com.stadgent.mobiliteitapp.loader.LoginLoader;
import com.stadgent.mobiliteitapp.model.User;
import com.stadgent.mobiliteitapp.repo.ItemRepository;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by floriangoeteyn on 04-Apr-16.
 */
public class LoginCallback implements Callback<User> {

    private LoginLoader loader;

    public LoginCallback(LoginLoader loader, String username, String password) {
        this.loader=loader;
        RestClient restClient = new RestClient(username, password);
        RestClient.UserApiInterface service = restClient.getUserClient();
        Call<User> userCall = service.getUser();
        userCall.enqueue(this);
    }


    @Override
    public void onResponse(Response<User> response) {

        if(response.isSuccess()){
            loader.onUserLoggedIn(response.body());
        }
        else{
            loader.onLoginFailed();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        loader.onLoginFailed();
        Log.d("user login failed", ExceptionUtils.getStackTrace(t));
    }
}
