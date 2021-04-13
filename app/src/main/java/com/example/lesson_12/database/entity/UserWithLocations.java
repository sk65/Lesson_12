package com.example.lesson_12.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UserWithLocations {
    @Embedded
    private User user;
    @Relation(
            parentColumn = "userId",
            entityColumn = "userOwnerId"
    )
    private List<UserLocation> userLocation;

    public UserWithLocations(User user, List<UserLocation> userLocation) {
        this.user = user;
        this.userLocation = userLocation;
    }

    public User getUser() {
        return user;
    }

    public List<UserLocation> getUserLocation() {
        return userLocation;
    }
}
