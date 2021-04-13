package com.example.lesson_12.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.lesson_12.R;
import com.example.lesson_12.databinding.FragmentAuthorizationBinding;
import com.example.lesson_12.util.ValidationUtil;
import com.example.lesson_12.viewmodel.UserViewModel;


public class AuthorizationFragment extends Fragment {
    private NavController navController;
    private FragmentAuthorizationBinding binding;
    private UserViewModel userViewModel;
    private String emailInput;
    private String passwordInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);
        binding = FragmentAuthorizationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(UserViewModel.class);

        binding.btnSignUp.setOnClickListener((v) ->
                navController.navigate(R.id.action_authorizationFragment_to_registrationFragment));
        binding.btnSignIn.setOnClickListener(v -> {
            if (validateInput()) {
                return;
            }
            userViewModel.findUserByEmail(emailInput).observe(getViewLifecycleOwner(), user -> {
                if (user == null) {
                    Toast.makeText(getActivity(), getContext().getString(R.string.no_user_explanations),
                            Toast.LENGTH_SHORT).show();
                } else if (!user.getPassword().equals(passwordInput)) {
                    Toast.makeText(getActivity(), getContext().getString(R.string.wrong_password_explanations),
                            Toast.LENGTH_SHORT).show();
                } else {
                    userViewModel.setCurrentUserImgUri(user.getImgUri());
                    userViewModel.setCurrentUserEmail(emailInput);
                    userViewModel.setCurrentUserId(user.getUserId());
                    userViewModel.setIsAuth(true);
                    navController.navigate(R.id.action_authorizationFragment_to_profileFragment);
                }
            });
        });
    }

    private boolean validateInput() {
        emailInput = binding.tilAuthFragmentEmail.getEditText().getText().toString().trim();
        passwordInput = binding.tilAuthFragmentPassword.getEditText().getText().toString().trim();

        String emailError = ValidationUtil.validateEmail(emailInput);
        String passwordError = ValidationUtil.validatePassword(passwordInput);

        binding.tilAuthFragmentEmail.setError(emailError);
        binding.tilAuthFragmentPassword.setError(passwordError);
        return emailError != null || passwordError != null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}