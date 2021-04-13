package com.example.lesson_12.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    private long userId;
    private String userName;
    private String userLastName;
    private String email;
    private String password;
    private String imgUri;

    public User(String imgUri, String userName, String userLastName, String email, String password) {
        this.userName = userName;
        this.userLastName = userLastName;
        this.email = email;
        this.password = password;
        this.imgUri = imgUri;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImgUri() {
        return imgUri;
    }
}
