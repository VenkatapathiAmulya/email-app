

package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import static androidx.core.content.ContextCompat.getSystemService;

public class MainActivity extends AppCompatActivity implements MyAdapter.InteractWithMainActivity {

    ImageView addEmailImageView, signoOutImageView;
    TextView userNameTxtView;
    ArrayList<Email> emailList;
    RecyclerView inboxRecycleView;

    final String TAG = this.getClass().getSimpleName();
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private static final int REQ_CODE = 9000;

    private static final int REQ_CODE1 = 9001;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addEmailImageView = findViewById(R.id.addEmailImageView);
        signoOutImageView = findViewById(R.id.logoutImageView);
        userNameTxtView = findViewById(R.id.userNameTxtView);
        inboxRecycleView = findViewById(R.id.inboxRecycleView);


         pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
         editor = pref.edit();

        String firstName = pref.getString("user_fname", null); // getting String

        String lastName = pref.getString("user_lname", null); // getting String

        userNameTxtView.setText(firstName + " " + lastName);

        emailList = new ArrayList<Email>();

         getInboxEmails();

         myAdapter = new MyAdapter(emailList, MainActivity.this);

         RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
         inboxRecycleView.setLayoutManager(layoutManager);
         inboxRecycleView.setItemAnimator(new DefaultItemAnimator());
         inboxRecycleView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.HORIZONTAL));

         inboxRecycleView.setAdapter(myAdapter);
         inboxRecycleView.setVisibility(View.VISIBLE);

         addEmailImageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent toMainActivity = new Intent(MainActivity.this, CreateNewEmail.class);
                 startActivityForResult(toMainActivity, REQ_CODE);

             }
         });

        signoOutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, Login.class);
                startActivityForResult(it,REQ_CODE1);
            }
        });


    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }



    public void getInboxEmails() {

        final OkHttpClient client = new OkHttpClient();

        String bearer = pref.getString("token", null); // getting String


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox")
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
                        JSONArray emails = jsonObject.getJSONArray("messages");

                        for (int i = 0; i < emails.length(); i++) {
                            JSONObject c = emails.getJSONObject(i);
                            String firstName = c.getString("sender_fname");
                            String lastName = c.getString("sender_lname");
                            String message = c.getString("message");
                            String subject = c.getString("subject");
                            String createdAt = c.getString("created_at");
                            String id = c.getString("id");

                            Email emailObj = new Email();
                            emailObj.firstName = firstName;
                            emailObj.lastName = lastName;
                            emailObj.message = message;
                            emailObj.subject = subject;
                            emailObj.date = createdAt;
                            emailObj.id = id;

                            emailList.add(emailObj);
                        }
                        System.out.println(emailList);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //notifyDataSetChanged here or update your UI on different thread
                                myAdapter.notifyDataSetChanged();
                            }
                        });



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void deleteItem(int position) {

        final OkHttpClient client = new OkHttpClient();
        Request request = null;
        String bearer = pref.getString("token", null);

        String emailId = emailList.get(position).id;

        request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+ emailId)
                .header("Authorization","BEARER "+ bearer)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d("bha","onReponse: " + responseHeaders.name(i) + ": " + responseHeaders.value(i));

                    }

                    Log.d("bha",responseBody.string() );
                }
            }
        });

        emailList.remove(position);
        myAdapter.notifyDataSetChanged();
    }
}
