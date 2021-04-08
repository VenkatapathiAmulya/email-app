package com.example.inclass08;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<Email> emailArrayList;

    public static InteractWithMainActivity interact;
    static String TAG = "demo";

    static String EMAIL_SENDER_FNAME = "SenderFname";
    static String EMAIL_SENDER_LNAME = "SenderLname";
    static String EMAIL_SUBJECT = "Subject";
    static String EMAIL_MESSAGE = "Message";
    static String EMAIL_CREATED = "Created Date";

    private Context context;

    public MyAdapter(ArrayList<Email> emailArrayList, Context context) {
        this.emailArrayList = emailArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        interact = (InteractWithMainActivity) context;

        final Email emailObj = emailArrayList.get(position);
        holder.subjectTxtView.setText(emailObj.subject);

        String toDate = parseDateToddMMyyyy(emailObj.date);
        holder.createdTxtView.setText(toDate);

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Delete clicked from: "+emailArrayList.get(position));
                interact.deleteItem(position);
            }
        });

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Email emailObject = emailArrayList.get(position);
                Intent toEmailDisplay = new Intent(v.getContext(), EmailDisplayActivity.class);
                toEmailDisplay.putExtra(EMAIL_SENDER_FNAME, emailObject.getFirstName());
                toEmailDisplay.putExtra(EMAIL_SENDER_LNAME, emailObject.getLastName());
                toEmailDisplay.putExtra(EMAIL_SUBJECT, emailObject.getSubject());
                toEmailDisplay.putExtra(EMAIL_MESSAGE, emailObject.getMessage());
                toEmailDisplay.putExtra(EMAIL_CREATED, emailObject.getDate());
                context.startActivity(toEmailDisplay);

            }
        });
    }

    @Override
    public int getItemCount() {
        return emailArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView subjectTxtView, createdTxtView;
        public ImageView deleteImageView;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTxtView = itemView.findViewById(R.id.emailSubjectTxtView);
            createdTxtView = itemView.findViewById(R.id.emailDateTxtView);
            deleteImageView = itemView.findViewById(R.id.deleteImageView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);

        }
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

    public interface InteractWithMainActivity {
        void deleteItem(int position);
    }
}
