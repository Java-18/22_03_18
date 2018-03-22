package com.sheygam.java_18_22_03_18;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.input_email)
    EditText inputEmail;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.reg_btn)
    Button regBtn;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.progress_wrapper)
    FrameLayout progressFrame;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

//    @OnClick(R.id.reg_btn)
//    protected void onRegistration(){
//        RegTask regTask = new RegTask();
//        regTask.execute();
//
//    }
    @OnClick(R.id.reg_btn)
    protected void onRegistration(){
        showProgress();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        OkHttpProvider.getInstance().regAsync(email, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showError("Connection error!");
                        hideProgress();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseJson = response.body().string();
                    Log.d("MY_TAG", "onResponse: token " + responseJson);
                    //Todo save token
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            Toast.makeText(MainActivity.this, "Registration ok!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(response.code() == 409){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            showError("User already exist!");
                        }
                    });
                }else{
                    Log.d("MY_TAG", "onResponse: error: " + response.body().string());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            showError("Server error!");
                        }
                    });
                }
            }
        });
    }

    protected void onLogin(){

    }

    private void showProgress(){
        progressFrame.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        progressFrame.setVisibility(View.GONE);
    }

    private void showError(String error){
        new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage(error)
                .setPositiveButton("OK",null)
                .setCancelable(false)
                .create()
                .show();
    }

    class LoginTask extends AsyncTask<Void,Void,String>{
        private String email;
        private String password;
        private boolean isSuccess;

        @Override
        protected void onPreExecute() {
            showProgress();
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Login ok!";
            isSuccess = true;
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(isSuccess){
                //Todo show next view
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }else{
                showError(result);
            }
        }
    }


    class RegTask extends AsyncTask<Void,Void,String>{
        private String email;
        private String password;
        private boolean isSuccess;

        @Override
        protected void onPreExecute() {
            showProgress();
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Registration ok!";
            isSuccess = true;
            try {
                AuthToken token = OkHttpProvider.getInstance().registration(email,password);
                Log.d("MY_TAG", "doInBackground: token: " + token.getToken());
            } catch (IOException e){
                result = "Connection error!";
                isSuccess = false;
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                isSuccess = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(isSuccess){
                //Todo show next view
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }else{
                showError(result);
            }
        }
    }
}
