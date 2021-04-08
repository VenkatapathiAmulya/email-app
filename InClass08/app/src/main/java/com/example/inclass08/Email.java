package com.example.inclass08;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Email implements Serializable {
    String firstName, lastName, message, subject, date, id;

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }
}
