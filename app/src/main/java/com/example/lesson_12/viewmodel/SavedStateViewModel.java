package com.example.lesson_12.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class SavedStateViewModel extends ViewModel {
    private final SavedStateHandle savedStateHandle;
    private final static String PACKAGE_NAME = "com.example.lesson_11.viewmodel";
    private final static String IS_AUTH_KEY = PACKAGE_NAME + ".isAuthKey";
    private static final String IMG_URI_KEY = PACKAGE_NAME + ".imgUri";
    private final static String CURRENT_USER_EMAIL_KEY = PACKAGE_NAME + ".userEmailKey";
    private final static String CURRENT_USER_ID_KEY = PACKAGE_NAME + "userIDKey";
    private final MutableLiveData<Boolean> isAuthLiveData;
    private final MutableLiveData<Long> userIdLiveData;
    private final MutableLiveData<String> userEmailLiveData;
    private final MutableLiveData<String> imgUriLiveData;

    public SavedStateViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;

        isAuthLiveData = savedStateHandle.getLiveData(IS_AUTH_KEY);

        if (isAuthLiveData.getValue() == null) {
            isAuthLiveData.setValue(false);
        }
        userEmailLiveData = savedStateHandle.getLiveData(CURRENT_USER_EMAIL_KEY);
        userIdLiveData = savedStateHandle.getLiveData(CURRENT_USER_ID_KEY);
        imgUriLiveData = savedStateHandle.getLiveData(IMG_URI_KEY);
    }

    public LiveData<String> getImgUriLiveData() {
        return imgUriLiveData;
    }

    public void setImgUriLiveData(String imgUri) {
        imgUriLiveData.setValue(imgUri);
    }

    public void setUserEmailLiveData(String email) {
        savedStateHandle.set(CURRENT_USER_EMAIL_KEY, email);
        userEmailLiveData.setValue(email);
    }

    public LiveData<String> getUserEmailLiveData() {
        return userEmailLiveData;
    }

    public void setIsAuth(boolean isAuth) {
        savedStateHandle.set(IS_AUTH_KEY, isAuth);
        isAuthLiveData.setValue(isAuth);
    }

    public LiveData<Boolean> getIsAuthLiveData() {
        return isAuthLiveData;
    }

    public void setUserIdLiveData(long id) {
        userIdLiveData.setValue(id);
    }

    public LiveData<Long> getUserIdLiveData() {
        return userIdLiveData;
    }
}
