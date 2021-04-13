package com.example.lesson_12.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.lesson_12.R;
import com.example.lesson_12.SharedPreferencesManager;
import com.example.lesson_12.util.FileUtil;
import com.example.lesson_12.view.fragment.RegistrationFragment;
import com.example.lesson_12.viewmodel.SavedStateViewModel;
import com.example.lesson_12.workmanager.TrackLocationWorker;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.lesson_12.SharedPreferencesManager.IS_AUTH_KEY;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE = 34;
    private final String[] locationPermissions = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
    private SavedStateViewModel savedStateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savedStateViewModel = new ViewModelProvider(this,
                new SavedStateViewModelFactory(getApplication(), this)).get(SavedStateViewModel.class);
        if (SharedPreferencesManager.getInstance().getIsAuth()) {
            adjustBackStack();
            requestLocationUpdates();
        }
        SharedPreferencesManager.getInstance().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RegistrationFragment.GALLERY_REQUEST:
                    Uri galleryImageUri = data.getData();
                    savedStateViewModel.setImgUriLiveData(galleryImageUri.toString());
                    break;
                case RegistrationFragment.REQUEST_CAMERA:
                    if (FileUtil.getCurrentPhotoPath() != null) {
                        File file = new File(FileUtil.getCurrentPhotoPath());
                        Uri cameraImageUri = Uri.fromFile(file);
                        savedStateViewModel.setImgUriLiveData(cameraImageUri.toString());
                    }
            }
        }
    }

    private void startLocationWorkManager() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(TrackLocationWorker.class, 15, TimeUnit.MINUTES).build();
//        WorkRequest workRequest
//                = new OneTimeWorkRequest.Builder(TrackLocationWorker.class)
//                .build();
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    }

    private void adjustBackStack() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host);
        NavGraph navGraph = navController.getGraph();
        navGraph.setStartDestination(R.id.profileFragment);
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.authorizationFragment, true)
                .build();
        navController.navigate(R.id.action_authorizationFragment_to_profileFragment, null, navOptions);
    }

    private void requestLocationUpdates() {
        Log.i("dev", "Main Activity requestLocationUpdates()");
        if (!checkPermissions()) {
            Log.i("dev", "Main Activity requestLocationUpdates() !checkPermissions()");
            requestPermissions();
        } else {
            Log.i("dev", "Main Activity requestLocationUpdates() else");
            locationEnabled();
            startLocationWorkManager();
        }
    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            requestLocationSettings();
        }
    }

    private void requestLocationSettings() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.location_alert_title)
                .setMessage(R.string.locatio_alert_explanations)
                .setCancelable(false)
                .setPositiveButton(R.string.enable, (paramDialogInterface, paramInt) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Log.i("dev", "Main Activity requestPermissions()");
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Log.i("dev", "Main Activity requestPermissions() shouldProvideRationale");
            Snackbar.make(
                    findViewById(R.id.nav_host),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                locationPermissions,
                                REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE);
                    })
                    .show();
        } else {
            Log.i("dev", "Main Activity requestPermissions() else");
            ActivityCompat.requestPermissions(MainActivity.this,
                    locationPermissions,
                    REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(IS_AUTH_KEY)) {
            boolean isAuth = sharedPreferences.getBoolean(IS_AUTH_KEY, false);
            if (isAuth) {
                Log.i("dev", "onSharedPreferenceChanged");
                requestLocationUpdates();
            }
        }

    }
}