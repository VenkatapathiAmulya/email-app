package com.example.inclass08;

import androidx.annotation.NonNull;

public class User {

    String id, firstName, lastName;

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public User() {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + lastName;
    }
}
