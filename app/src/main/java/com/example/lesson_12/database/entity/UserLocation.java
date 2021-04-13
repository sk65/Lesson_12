package com.example.lesson_12.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_location")
public class UserLocation {
    @PrimaryKey(autoGenerate = true)
    private long locationId;
    private long userOwnerId;
    private double latitude;
    private double longitude;

    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setUserOwnerId(long userOwnerId) {
        this.userOwnerId = userOwnerId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getUserOwnerId() {
        return userOwnerId;
    }

    public long getLocationId() {
        return locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "locationId=" + locationId +
                ", userOwnerId=" + userOwnerId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
