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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class CreateNewEmail extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText subjectEditTxt;
    TextView multilineTxtView;
    Button sendBtn, cancelBtn;
    ArrayList<User> userArrayList;
    List<String>namesList;
    String selectedUserId;

    final String TAG = this.getClass().getSimpleName();

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    Spinner spinner;

    private static final int REQ_CODE_TW0 = 9004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_email);

        subjectEditTxt = findViewById(R.id.subjectEditTxt);

        spinner=findViewById(R.id.userSpinner);
        spinner.setOnItemSelectedListener(this);

        multilineTxtView = findViewById(R.id.messageMultiTxt);
        sendBtn = findViewById(R.id.sendBtn);
        cancelBtn = findViewById(R.id.cancelButton);

        userArrayList = new ArrayList<User>();
        namesList = new ArrayList<String>();


        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();

        setTitle("Create new Mail");

        getUsers();


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNewEmailAPI();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getUsers() {

        final OkHttpClient client = new OkHttpClient();

        String bearer = pref.getString("token", null); // getting String


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                .header("Authorization", "BEARER " + bearer )
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {

                        System.out.println("Failure");


                    } else {

                        Headers responseHeaders = response.headers();
                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));

                        }

                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray users = jsonObject.getJSONArray("users");

                        for (int i = 0; i < users.length(); i++) {
                            JSONObject c = users.getJSONObject(i);
                            String firstName = c.getString("fname");
                            String lastName = c.getString("lname");
                            String id = c.getString("id");

                            User userObj = new User();
                            userObj.firstName = firstName;
                            userObj.lastName = lastName;
                            userObj.id = id;


                            userArrayList.add(userObj);
                            namesList.add(userObj.getFullName());
                        }
                        System.out.println(userArrayList);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(CreateNewEmail.this,android.R.layout.simple_spinner_item,namesList);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(dataAdapter);

                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void postNewEmailAPI() {

        final OkHttpClient client = new OkHttpClient();
        String bearer = pref.getString("token", null); // getting String


        RequestBody requestBody = new FormBody.Builder()
                .add("subject", subjectEditTxt.getText().toString())
                .add("message", multilineTxtView.getText().toString())
                .add("receiver_id", selectedUserId)
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                .header("Authorization", "BEARER " + bearer )
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {


                        toast(CreateNewEmail.this, "Error in sending message");

                    } else {

                        Headers responseHeaders = response.headers();
                        for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            if (responseHeaders.name(i).equals("token")) {

                            }

                            toast(CreateNewEmail.this, "Message send successfully");

                            Intent toMainActivity = new Intent(CreateNewEmail.this, MainActivity.class);
                            startActivityForResult(toMainActivity, REQ_CODE_TW0);
                        }


//                        toast(SignUp.this, "User has been created successfully");

                        System.out.println(responseBody.string());
                    }
                }
            }
        });
    }


    public void toast(final Context context,final String error) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context,  error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedUserId = userArrayList.get(position).id;
        System.out.println(selectedUserId);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
