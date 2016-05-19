package com.stadgent.mobiliteitapp.rest;

import android.util.Base64;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.stadgent.mobiliteitapp.model.User;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by floriangoeteyn on 15-Mar-16.
 */
public class RestClient {

    private static Retrofit client;

    public RestClient(String username, String password){
        client = createClient(username, password);
    }

    //creëer en return de clients
    public ItemApiInterface getItemClient(){
        return client.create(ItemApiInterface.class);
    }

    public UserApiInterface getUserClient(){
        return client.create(UserApiInterface.class);
    }

    //standaard retrofit client
    private Retrofit createClient(final String username, final String password){


        OkHttpClient okClient = new OkHttpClient();

            okClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    final String credentials = username + ":" + password;
                    final String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    final String basic = "Basic " + encodedCredentials;

                    Request request = chain.request();
                    request = request.newBuilder()
                            .addHeader("Authorization", basic)
                            .build();
                    Response response = chain.proceed(request);
                    return response;
                }
            });


        return new Retrofit.Builder()
                .baseUrl(Values.BASE_URL)
                .addConverter(String.class, new ToStringConverter())
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public List<Call<CallbackItems>> getItemCalls(){
        List<Call<CallbackItems>> calls = new ArrayList<>();
        calls.add(getItemClient().getParkingItems());
        calls.add(getItemClient().getWazeItems());
        calls.add(getItemClient().getTwitterItems());
        calls.add(getItemClient().getCoyoteItems());
        calls.add(getItemClient().getVgsItems());
        return calls;
    }

    public List<Call<ResponseBody>> deleteItemCalls(String id){
        List<Call<ResponseBody>> calls = new ArrayList<>();
        calls.add(getItemClient().deleteParkingItem(id));
        calls.add(getItemClient().deleteTwitterItem(id));
        calls.add(getItemClient().deleteWazeItem(id));
        calls.add(getItemClient().deleteCoyoteItems(id));
        calls.add(getItemClient().deleteVgsItems(id));
        return calls;
    }

    public List<Call<ResponseBody>> undoItemCalls(String id){
        List<Call<ResponseBody>> calls = new ArrayList<>();
        calls.add(getItemClient().undoDeleteParkingItem(id));
        calls.add(getItemClient().undoDeleteTwitterItem(id));
        calls.add(getItemClient().undoDeleteWazeItem(id));
        calls.add(getItemClient().undoDeleteCoyoteItems(id));
        calls.add(getItemClient().undoDeleteVgsItems(id));
        return calls;
    }




    //Api calls naar elk json bestand
    public interface ItemApiInterface{
        @GET(Values.URL_WAZEJSON)
        Call<CallbackItems> getWazeItems();

        @DELETE(Values.URL_WAZEJSON+"/{item_id}")
        Call<ResponseBody> deleteWazeItem(@Path("item_id") String itemId);

        @PUT(Values.URL_WAZEJSON+"/{item_id}")
        Call<ResponseBody> undoDeleteWazeItem(@Path("item_id") String itemId);

        //------------------------//

        @GET(Values.URL_TWITTERJSON)
        Call<CallbackItems> getTwitterItems();

        @DELETE(Values.URL_TWITTERJSON+"/{item_id}")
        Call<ResponseBody> deleteTwitterItem(@Path("item_id") String itemId);

        @PUT(Values.URL_TWITTERJSON+"/{item_id}")
        Call<ResponseBody> undoDeleteTwitterItem(@Path("item_id") String itemId);

        //------------------------//

        @GET(Values.URL_PARKINGJSON)
        Call<CallbackItems> getParkingItems();

        @DELETE(Values.URL_PARKINGJSON+"/{item_id}")
        Call<ResponseBody> deleteParkingItem(@Path("item_id") String itemId);

        @PUT(Values.URL_PARKINGJSON+"/{item_id}")
        Call<ResponseBody> undoDeleteParkingItem(@Path("item_id") String itemId);


        //------------------------//
        @GET(Values.URL_COYOTEJSON)
        Call<CallbackItems> getCoyoteItems();

        @DELETE(Values.URL_COYOTEJSON+"/{item_id}")
        Call<ResponseBody> deleteCoyoteItems(@Path("item_id") String itemId);

        @PUT(Values.URL_COYOTEJSON+"/{item_id}")
        Call<ResponseBody> undoDeleteCoyoteItems(@Path("item_id") String itemId);


        //------------------------//
        @GET(Values.URL_VGSJSON)
        Call<CallbackItems> getVgsItems();

        @DELETE(Values.URL_VGSJSON+"/{item_id}")
        Call<ResponseBody> deleteVgsItems(@Path("item_id") String itemId);

        @PUT(Values.URL_VGSJSON+"/{item_id}")
        Call<ResponseBody> undoDeleteVgsItems(@Path("item_id") String itemId);

    }

    public interface UserApiInterface{
        @GET(Values.URL_USERJSON)
        Call<User> getUser();
    }

}
