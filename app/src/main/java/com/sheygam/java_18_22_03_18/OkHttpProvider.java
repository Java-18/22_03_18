package com.sheygam.java_18_22_03_18;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gregorysheygam on 22/03/2018.
 */

public class OkHttpProvider {
    public static final String BASE_URL = "https://telranstudentsproject.appspot.com/_ah/api/contactsApi/v1";
    private static final OkHttpProvider ourInstance = new OkHttpProvider();
    private Gson gson;
    private OkHttpClient client;
    private MediaType JSON;

    public static OkHttpProvider getInstance() {
        return ourInstance;
    }

    private OkHttpProvider() {
        gson = new Gson();
//        client = new OkHttpClient();
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build();
        JSON = MediaType.parse("application/json; charset=utf-8");
    }

    public AuthToken registration(String email, String password) throws Exception {
        Auth auth = new Auth(email,password);
        String jsonRequest = gson.toJson(auth);

        RequestBody body = RequestBody.create(JSON,jsonRequest);

        Request request = new Request.Builder()
                .url(BASE_URL + "/registration")
                .post(body)
//                .addHeader("Authorization","token")
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()){
            String jsonResponse = response.body().string();
            AuthToken authToken = gson.fromJson(jsonResponse,AuthToken.class);
            return authToken;
        }else if(response.code() == 409){
            throw new Exception("User already exist!");
        }else{
            Log.d("WEB", "registration: error: " + response.body().string() );
            throw new Exception("Server error! Call to support!");
        }
    }

    public void regAsync(String email, String password, Callback callback){
        Auth auth = new Auth(email,password);
        String jsonRequest = gson.toJson(auth);

        RequestBody body = RequestBody.create(JSON,jsonRequest);

        Request request = new Request.Builder()
                .url(BASE_URL + "/registration")
                .post(body)
//                .addHeader("Authorization","token")
                .build();

        client.newCall(request).enqueue(callback);
    }
}
