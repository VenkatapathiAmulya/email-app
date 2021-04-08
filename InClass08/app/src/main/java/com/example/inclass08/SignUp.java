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

public class SignUp extends AppCompatActivity {

    EditText firstNameTxtView, lastNameTxtView, emailTxtView, choosePasswordTxtView, repeatPasswordTxtView;
    Button signUpBtn, cancelBtn;
    private static final int REQ_CODE = 9000;
    final String TAG = this.getClass().getSimpleName();
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstNameTxtView = findViewById(R.id.firstNameTxtView);
        lastNameTxtView = findViewById(R.id.lastNameTxtView);
        emailTxtView = findViewById(R.id.emailTxtView);
        choosePasswordTxtView = findViewById(R.id.choosePasswordTxtView);
        repeatPasswordTxtView = findViewById(R.id.repeatPasswordTxtView);
        signUpBtn = findViewById(R.id.setUpsignUpBtn);
        cancelBtn = findViewById(R.id.cancelButton);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSignUpAPI();
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


    }


    public void postSignUpAPI() {

        final OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("fname", firstNameTxtView.getText().toString())
                .add("lname", lastNameTxtView.getText().toString())
                .add("email", emailTxtView.getText().toString())
                .add("password", repeatPasswordTxtView.getText().toString())
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {


                        toast(SignUp.this, "Error in creating user");

                    } else {

                        Headers responseHeaders = response.headers();
                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            if (responseHeaders.name(i).equals("token")) {
                                editor.putString("token", responseHeaders.value(i)); // Storing string

                            }

                            Intent toMainActivity = new Intent(SignUp.this, MainActivity.class);
                            startActivityForResult(toMainActivity, REQ_CODE);
                        }


                        toast(SignUp.this, "User has been created successfully");

                        System.out.println(responseBody.string());
                    }
                }
            }
        });
    }

    public void toast(final Context context, final String errorMessage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
