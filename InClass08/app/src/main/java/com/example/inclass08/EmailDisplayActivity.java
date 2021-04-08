package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailDisplayActivity extends AppCompatActivity  {

    private TextView tvSender;
    private TextView tvSubject;
    private TextView tvMessage;
    private TextView tvCreated;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_display);

        tvSender = findViewById(R.id.tv_sender);
        tvSubject = findViewById(R.id.tv_subject);
        tvMessage = findViewById(R.id.tv_message);
        tvCreated = findViewById(R.id.tv_createdDate);

        btnFinish = findViewById(R.id.btn_finish);

        if (getIntent() != null && getIntent().getExtras() != null){

            final String sender_Fname = getIntent().getStringExtra(MyAdapter.EMAIL_SENDER_FNAME);
            final String sender_Lname = getIntent().getStringExtra(MyAdapter.EMAIL_SENDER_LNAME);
            final String subject = getIntent().getStringExtra(MyAdapter.EMAIL_SUBJECT);
            final String message = getIntent().getStringExtra(MyAdapter.EMAIL_MESSAGE);
            final String createdDate = getIntent().getStringExtra(MyAdapter.EMAIL_CREATED);


            Log.d("demo", message+"");



            tvSender.setText("Sender: "+sender_Fname+""+sender_Lname);
            tvSubject.setText("Subject: "+subject);
            tvMessage.setText("Message: "+message);

            String toDate = parseDateToddMMyyyy(createdDate);
            tvCreated.setText("Date:"+    toDate);
        }

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;

    }

}
