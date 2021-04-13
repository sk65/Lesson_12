package com.example.lesson_12.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.lesson_12.R;
import com.example.lesson_12.SharedPreferencesManager;
import com.example.lesson_12.database.entity.User;
import com.example.lesson_12.databinding.FragmentRegistraitionBinding;
import com.example.lesson_12.util.FileUtil;
import com.example.lesson_12.util.ValidationUtil;
import com.example.lesson_12.viewmodel.SavedStateViewModel;
import com.example.lesson_12.viewmodel.UserViewModel;

import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RegistrationFragment extends Fragment {

    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO = 3;
    public static final int GALLERY_REQUEST = 100;
    public static final int REQUEST_CAMERA = 101;

    private NavController navController;
    private FragmentRegistraitionBinding binding;

    private UserViewModel userViewModel;
    private SavedStateViewModel savedStateViewModel;

    private String emailInput;
    private String passwordInput;
    private String userNameInput;
    private String userLastNameInput;
    private String confirmPasswordInput;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);
        binding = FragmentRegistraitionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init view models
        savedStateViewModel = new ViewModelProvider(requireActivity(),
                new SavedStateViewModelFactory(requireActivity().getApplication(), this)).get(SavedStateViewModel.class);

        userViewModel = new ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(UserViewModel.class);

        //init action bar
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarRegFragment);
        NavigationUI.setupWithNavController(binding.toolbarRegFragment, navController);

        //observe image
        savedStateViewModel.getImgUriLiveData().observe(getViewLifecycleOwner(), uri -> {
            if (!(uri == null || uri.isEmpty())) {
                Glide.with(requireActivity()).load(uri).into(binding.imvRegFragmentAva);
            }
        });

        //button pick image
        binding.btnPickImage.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.choose_image)
                    .setItems(R.array.pick_img, (dialog, which) -> {
                        if (which == 0) {
                            if (checkStoragePermission()) {
                                dispatchTakePictureIntent();
                            }
                        } else if (which == 1) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            getActivity().startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                        } else {
                            dialog.dismiss();
                        }
                    }).create().show();
        });

        // button sign up
        boolean isAuth = SharedPreferencesManager.getInstance().getIsAuth();
        if (!isAuth) {
            binding.btnRegFragmentSignUp.setOnClickListener(v -> registerUser());
        } else {
            Log.i("dev", "updateUser");
            long id = SharedPreferencesManager.getInstance().getCurrentUserId();
            userViewModel.findUserById(id).observe(getViewLifecycleOwner(), user -> {
                binding.btnRegFragmentSignUp.setText(R.string.update);
                binding.tilRegFragmentUsername.getEditText().setText(user.getUserName());
                binding.tilRegFragmentUserLastName.getEditText().setText(user.getUserLastName());
                binding.tilRegFragmentEmail.getEditText().setText(user.getEmail());
                binding.tilRegFragmentPassword.getEditText().setText(user.getPassword());
                binding.tilRegFragmentConfirmPassword.getEditText().setText(user.getPassword());
                binding.btnRegFragmentSignUp.setOnClickListener(v -> updateUser());
            });
        }
    }

    private void registerUser() {
        if (validateInput()) {
            return;
        }
        User user = createUser();
        userViewModel.findUserByEmail(user.getEmail()).observe(requireActivity(), userFromDB -> {
            if (isAdded()) {
                if (userFromDB != null) {
                    Toast.makeText(requireActivity(),
                            getActivity().getString(R.string.user_exist_explanations),
                            Toast.LENGTH_SHORT).show();
                } else {
                    userViewModel.insert(user);
                    savedStateViewModel.setUserEmailLiveData(user.getEmail());
                    SharedPreferencesManager.getInstance().putIsAuth(true);
                    navController.navigate(R.id.action_registrationFragment_to_profileFragment);
                }
            }
        });

    }

    private void updateUser() {
        if (validateInput()) {
            return;
        }
        User user = createUser();
        long userId = SharedPreferencesManager.getInstance().getCurrentUserId();
        if (isAdded()) {
            user.setUserId(userId);
            userViewModel.update(user);
            savedStateViewModel.setUserEmailLiveData(user.getEmail());
            SharedPreferencesManager.getInstance().putIsAuth(true);
            navController.navigate(R.id.action_registrationFragment_to_profileFragment);
        }

    }

    private boolean validateInput() {
        userNameInput = binding.tilRegFragmentUsername.getEditText().getText().toString().trim();
        userLastNameInput = binding.tilRegFragmentUserLastName.getEditText().getText().toString().trim();
        emailInput = binding.tilRegFragmentEmail.getEditText().getText().toString().trim();
        passwordInput = binding.tilRegFragmentPassword.getEditText().getText().toString().trim();
        confirmPasswordInput = binding.tilRegFragmentConfirmPassword.getEditText().getText().toString().trim();

        String userNameError = ValidationUtil.validateName(userNameInput);
        String userLastNameError = ValidationUtil.validateLastName(userLastNameInput);
        String emailError = ValidationUtil.validateEmail(emailInput);
        String passwordError = ValidationUtil.validatePassword(passwordInput);
        String confirmPasswordError = ValidationUtil.validateConfirmPassword(confirmPasswordInput, passwordInput);

        binding.tilRegFragmentUsername.setError(userNameError);
        binding.tilRegFragmentUserLastName.setError(userLastNameError);
        binding.tilRegFragmentEmail.setError(emailError);
        binding.tilRegFragmentPassword.setError(passwordError);
        binding.tilRegFragmentConfirmPassword.setError(confirmPasswordError);

        return userNameError != null || userLastNameError != null || emailError != null
                || passwordError != null || confirmPasswordError != null;
    }

    private User createUser() {
        String userNameInput = ValidationUtil.getStringFromInputLayout(binding.tilRegFragmentUsername);
        String userLastNameInput = ValidationUtil.getStringFromInputLayout(binding.tilRegFragmentUserLastName);
        String emailInput = ValidationUtil.getStringFromInputLayout(binding.tilRegFragmentEmail);
        String passwordInput = ValidationUtil.getStringFromInputLayout(binding.tilRegFragmentPassword);

        String imgUri = savedStateViewModel.getImgUriLiveData().getValue();
        return new User(imgUri, userNameInput, userLastNameInput, emailInput, passwordInput);
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                getContext(), WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.alertDialog_checkPerm_title)
                    .setMessage(R.string.alertDialog_checkPerm_desc)
                    .setPositiveButton(R.string.alertDialog_checkPerm_posButton, (dialogInterface, i) -> {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{WRITE_EXTERNAL_STORAGE}, RegistrationFragment.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO
                        );
                        dialogInterface.dismiss();
                    }).setNegativeButton(R.string.alertDialog_checkPerm_negButton,
                    (dialog, which) -> dialog.dismiss()).show();
        } else {
            this.requestPermissions(
                    new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO);
        }
        return false;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = FileUtil.createImageFile(getContext());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    FileUtil.FILES_AUTHORITY,
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO) {
            if (grantResults.length > 0
                    && grantResults[0] == PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}