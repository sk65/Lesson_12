package com.example.lesson_12.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.lesson_12.SharedPreferencesManager;
import com.example.lesson_12.database.entity.User;
import com.example.lesson_12.database.entity.UserWithLocations;
import com.example.lesson_12.repository.UserRepository;

import static com.example.lesson_12.SharedPreferencesManager.CURRENT_USER_EMAIL_KEY;
import static com.example.lesson_12.SharedPreferencesManager.CURRENT_USER_ID_KEY;
import static com.example.lesson_12.SharedPreferencesManager.IMG_URI_KEY;
import static com.example.lesson_12.SharedPreferencesManager.IS_AUTH_KEY;

public class UserViewModel extends AndroidViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final UserRepository userRepository;
    private final SharedPreferencesManager sharedPreferencesManager;

    private final MutableLiveData<Boolean> isAuth;
    private final MutableLiveData<Long> currentUserId;
    private final MutableLiveData<String> currentUserEmail;
    private final MutableLiveData<String> currentUserImgUri;
    public final MutableLiveData<Boolean> isUpdate;

    public UserViewModel(@NonNull Application application) {
        super(application);

        userRepository = new UserRepository(application);

        sharedPreferencesManager = SharedPreferencesManager.getInstance();
        sharedPreferencesManager.registerOnSharedPreferenceChangeListener(this);
        isUpdate = new MutableLiveData<>(true);
        isAuth = new MutableLiveData<>(sharedPreferencesManager.getIsAuth());
        currentUserId = new MutableLiveData<>(sharedPreferencesManager.getCurrentUserId());
        currentUserEmail = new MutableLiveData<>(sharedPreferencesManager.getCurrentUserEmail());
        currentUserImgUri = new MutableLiveData<>(sharedPreferencesManager.getCurrentUserImageUri());

    }

    public void insertUser(User user) {
        userRepository.insertUser(user);
    }

    public void updateUser(User user) {
        userRepository.insertUser(user);
    }

    public LiveData<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public LiveData<UserWithLocations> findUsersWithLocationsByEmail(String userEmail) {
        return userRepository.findUserWithLocationsByEmail(userEmail);
    }

    public LiveData<User> findUserById(long id) {
        return userRepository.findUserById(id);
    }

    public LiveData<Long> getCurrentUserId() {
        return currentUserId;
    }

    public LiveData<String> getCurrentUserEmail() {
        return currentUserEmail;
    }

    public LiveData<String> getCurrentUserImgUri() {
        return currentUserImgUri;
    }

    public LiveData<Boolean> getIsAuth() {
        return isAuth;
    }

    public void setCurrentUserId(long userId) {
        sharedPreferencesManager.putCurrentUserId(userId);
    }

    public void setCurrentUserEmail(String email) {
        sharedPreferencesManager.putCurrentUserEmail(email);
    }

    public void setIsAuth(boolean isAuth) {
        sharedPreferencesManager.putIsAuth(isAuth);
    }

    public void setCurrentUserImgUri(String imgUri) {
        sharedPreferencesManager.putCurrentImgUri(imgUri);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case CURRENT_USER_EMAIL_KEY:
                currentUserEmail.setValue(sharedPreferencesManager.getCurrentUserEmail());
                break;
            case CURRENT_USER_ID_KEY:
                currentUserId.setValue(sharedPreferencesManager.getCurrentUserId());
                break;
            case IMG_URI_KEY:
                currentUserImgUri.setValue(sharedPreferencesManager.getCurrentUserImageUri());
                break;
            case IS_AUTH_KEY:
                isAuth.setValue(sharedPreferencesManager.getIsAuth());
                break;
        }
    }
}
