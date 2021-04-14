package com.example.lesson_12.view.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.lesson_12.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private NavController navController;
    private static final String PREFERENCE_USER_PROFILE_KEY = "userProfile";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);

        Toolbar toolbar = view.findViewById(R.id.toolbar_settings);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(toolbar, navController);

        Preference preference = findPreference(PREFERENCE_USER_PROFILE_KEY);
        preference.setOnPreferenceClickListener(p -> {
            navController.navigate(R.id.action_settingsFragment_to_registrationFragment);
            return true;
        });
    }


}