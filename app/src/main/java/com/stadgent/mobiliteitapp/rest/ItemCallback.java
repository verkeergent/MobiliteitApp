package com.stadgent.mobiliteitapp.rest;

import android.util.Log;

import com.squareup.okhttp.ResponseBody;
import com.stadgent.mobiliteitapp.loader.ItemLoader;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.User;
import com.stadgent.mobiliteitapp.session.UserSessionManager;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by floriangoeteyn on 17-Mar-16.
 */
public class ItemCallback implements Callback<CallbackItems> {

    private ItemLoader itemLoader;

    private List<Call<CallbackItems>> calls;
    private int index;
    RestClient restClient;

    public ItemCallback(ItemLoader itemLoader) {
        this.itemLoader = itemLoader;
        try{
            HashMap<String, String> user = UserSessionManager.getUserDetails();
            restClient = new RestClient(user.get(UserSessionManager.KEY_NAME), user.get(UserSessionManager.KEY_EMAIL));
        }
        catch(NullPointerException ignored){
        }
    }

    public void getItems(){
        calls = new ArrayList<>();
        index = 0;

        try {
            calls= restClient.getItemCalls();
        }

        catch(NullPointerException ignored){

        }
        startCallback();
    }


    public void deleteItem(String id){
        List<Call<ResponseBody>> calls = restClient.deleteItemCalls(id);
        final boolean[] itemRemoved = new boolean[1];

        for(Call<ResponseBody> call:calls){
            if(itemRemoved[0]){
                break;
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Response<ResponseBody> response) {
                    if(response.isSuccess()) {
                        Log.d("remove item", "item removed"+response.body());
                        itemRemoved[0] = true;
                    }else{
                        Log.d("remove item", "item not removed:\n"+response.errorBody());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("remove item", "failed:\n"+ExceptionUtils.getMessage(t));
                }
            });
        }
    }

    public void undoDeleteItem(String id){
        List<Call<ResponseBody>> calls = restClient.undoItemCalls(id);
        final boolean[] itemAdded = new boolean[1];

        for(Call<ResponseBody> call:calls){
            if(itemAdded[0]){
                break;
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Response<ResponseBody> response) {
                    if(response.isSuccess())
                        itemAdded[0] =true;
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }
    }


    //hulpmethode voor de callback toe te voegen (mag pas nadat de vorige call een response heeft gegeven)
    private void startCallback() {
        if (!(index >= calls.size())) {
            calls.get(index).enqueue(this);
            index++;
            //startCallback();
        }else{
            itemLoader.onAllItemsLoaded();
        }
    }


    @Override
    public void onResponse(Response<CallbackItems> response) {
        //lijst met items uit de callback
        List<Item> items = new ArrayList<>();
        if(response.isSuccess()){
            items.addAll(response.body().getAllItems());

            //stuur een melding naar loader met de items
            if (itemLoader != null)
                itemLoader.onItemsLoaded(items);

        }else{
            try {
                Log.d("response unsuccessful", response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //itemLoader.onLoadFailed();
        }
        //volgende callback starten
        startCallback();
    }

    @Override
    public void onFailure(Throwable t) {
        //stuur een melding naar de loader dat geen verbinding kon gemaakt worden (meestal omdat er geen internet is)
        Log.d("no response", ExceptionUtils.getStackTrace(t));
        //itemLoader.onLoadFailed();
        startCallback();
    }

}
