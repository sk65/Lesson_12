package com.example.lesson_12.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.lesson_12.database.entity.User;
import com.example.lesson_12.database.entity.UserLocation;
import com.example.lesson_12.database.entity.UserWithLocations;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class UserDao {
    @Insert(onConflict = REPLACE)
    public abstract long insertUser(User user);

    @Insert
    public abstract long insertUserLocation(UserLocation userLocation);

    @Transaction
    public void insertUserWithUserLocations(User user, List<UserLocation> userLocations) {
        long userId = insertUser(user);
        for (UserLocation userLocation : userLocations) {
            userLocation.setUserOwnerId(userId);
            insertUserLocation(userLocation);
        }
    }

    @Transaction
    @Query("SELECT * FROM user WHERE email = :userEmail")
    public abstract LiveData<UserWithLocations> findUsersWithLocationsByEmail(String userEmail);

    @Query("SELECT *FROM user_location ")
    public abstract LiveData<List<UserLocation>> findAllUserLocation();

    @Query("SELECT * FROM user WHERE email=:email")
    public abstract LiveData<User> findUserByEmail(String email);

    @Query("SELECT * FROM user")
    public abstract LiveData<List<User>> findAll();

    @Query("SELECT * FROM user WHERE userId=:id")
    public abstract LiveData<User> findUserById(long id);

}
