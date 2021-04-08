package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Login extends AppCompatActivity {

    EditText loginTxt, passwordTxt;
    Button loginBtn, signUpBtn;
    final String TAG = this.getClass().getSimpleName();
    SharedPreferences.Editor editor;
    private static final int REQ_CODE = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTxt = findViewById(R.id.emailTxtView);
        passwordTxt = findViewById(R.id.passwordTxtView);
        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signupBtn);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postLoginAPI();

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tosignUpActivity = new Intent(Login.this, SignUp.class);
                startActivityForResult(tosignUpActivity, REQ_CODE);
            }
        });

    }




    public void postLoginAPI() {

        final OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("email", loginTxt.getText().toString())
                .add("password", passwordTxt.getText().toString())
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {


                        toast(Login.this);

                    } else {


                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        System.out.println( jsonObject.getString("token"));


                        editor.putString("token",  jsonObject.getString("token"));
                        editor.putString("user_fname",  jsonObject.getString("user_fname"));
                        editor.putString("user_lname",  jsonObject.getString("user_lname"));
                        editor.commit();

                        Intent toMainActivity = new Intent(Login.this, MainActivity.class);
                        startActivityForResult(toMainActivity, REQ_CODE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void toast(final Context context) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, "Login is not successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
