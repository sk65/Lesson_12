package com.example.lesson_12.repository;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.lesson_12.SharedPreferencesManager;
import com.example.lesson_12.dao.UserDao;
import com.example.lesson_12.database.UserDatabase;
import com.example.lesson_12.database.entity.User;
import com.example.lesson_12.database.entity.UserLocation;
import com.example.lesson_12.database.entity.UserWithLocations;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final Context context;
    private final UserDao userDao;

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public UserRepository(Context context) {
        this.context = context;
        userDao = UserDatabase.getInstance(context.getApplicationContext()).userDao();
    }

    public void findLocation() {
        try {
            createLocationCallback();
            createLocationRequest();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());

        } catch (SecurityException e) {
            Log.i("dev", "Location das not update", e);
        }

    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
                    userLocation.setUserOwnerId(SharedPreferencesManager.getInstance().getCurrentUserId());
                    insertUserLocation(userLocation);
                }
            }
        };
    }

    public LiveData<UserWithLocations> findUserWithLocationsByEmail(String email) {
        return userDao.findUsersWithLocationsByEmail(email);
    }

    public void insertUser(User user) {
        databaseWriteExecutor.execute(() -> userDao.insertUser(user));
    }

    public void insertUserLocation(UserLocation userLocation) {
        databaseWriteExecutor.execute(() -> userDao.insertUserLocation(userLocation));
    }

    public LiveData<User> findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    public LiveData<List<User>> findAll() {

        return userDao.findAll();
    }

    public LiveData<List<UserLocation>> findAllUserLocation() {
        return userDao.findAllUserLocation();
    }

    public LiveData<User> findUserById(long id) {
        return userDao.findUserById(id);
    }
}
