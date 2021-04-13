package com.example.lesson_12.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.lesson_12.database.entity.User;
import com.example.lesson_12.database.entity.UserLocation;
import com.example.lesson_12.database.entity.UserWithLocations;
import com.example.lesson_12.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void insert(User user) {
        userRepository.insertUser(user);
    }

    public void update(User user) {
        userRepository.insertUser(user);
    }

    public LiveData<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public LiveData<UserWithLocations> findUsersWithLocationsByEmail(String userEmail) {
        return userRepository.findUserWithLocationsByEmail(userEmail);
    }

    public LiveData<List<UserLocation>> findAllUserLocation() {
        return userRepository.findAllUserLocation();
    }

    public LiveData<User> findUserById(long id) {
        return userRepository.findUserById(id);
    }

    public LiveData<List<User>> findAll() {
        return userRepository.findAll();
    }

}
