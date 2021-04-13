package com.example.lesson_12.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.lesson_12.R;
import com.example.lesson_12.adapter.UserLocationRecyclerViewAdapter;
import com.example.lesson_12.database.entity.User;
import com.example.lesson_12.database.entity.UserLocation;
import com.example.lesson_12.databinding.FragmentProfileBinding;
import com.example.lesson_12.viewmodel.UserViewModel;

import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private NavController navController;
    private UserViewModel userViewModel;
    private UserLocationRecyclerViewAdapter viewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(UserViewModel.class);

        initRecyclerView();
        initUI();

        binding.navViewProfileFragment.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_exit_menu_item:
                    userViewModel.setCurrentUserImgUri(null);
                    userViewModel.setCurrentUserEmail(null);
                    userViewModel.setCurrentUserId(-1);
                    userViewModel.setIsAuth(false);
                    NavGraph navGraph = navController.getGraph();
                    navGraph.setStartDestination(R.id.authorizationFragment);
                    navController.navigate(R.id.action_profileFragment_to_authorizationFragment);
                    break;
                case R.id.nav_settings_menu_item:
                    navController.navigate(R.id.action_profileFragment_to_settingsFragment);
                    break;
            }
            return true;
        });

        userViewModel.getCurrentUserEmail().observe(getViewLifecycleOwner(), email -> {
                    if (isAdded()) {
                        userViewModel.findUsersWithLocationsByEmail(email).observe(getViewLifecycleOwner(), (userWithLocations) -> {
                            if (isAdded()) {
                                if (userWithLocations != null) {
                                    userViewModel.isUpdate.setValue(true);
                                    User user = userWithLocations.getUser();
                                    List<UserLocation> userLocations = userWithLocations.getUserLocation();
                                    userViewModel.setCurrentUserId(user.getUserId());
                                    View headerLayout =
                                            binding.navViewProfileFragment.getHeaderView(0);
                                    TextView username = headerLayout.findViewById(R.id.tv_navHeader_username);
                                    TextView userLastName = headerLayout.findViewById(R.id.tv_navHeader_userLastName);
                                    ImageView userAvatar = headerLayout.findViewById(R.id.imv_navHeader_ava);

                                    username.setText(user.getUserName());
                                    userLastName.setText(user.getUserLastName());
                                    if (user.getImgUri() != null) {
                                        Glide.with(requireActivity()).load(user.getImgUri()).into(userAvatar);
                                    }
                                    if (userLocations != null) {
                                        viewAdapter.setUserLocations(userLocations);
                                    }

                                }
                            }
                        });
                    }
                }
        );
    }

    private void initUI() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarAcFragment);
        NavigationUI.setupWithNavController(binding.toolbarAcFragment, navController);

        DrawerLayout drawerLayout = binding.getRoot();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),
                drawerLayout,
                binding.toolbarAcFragment,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    private void initRecyclerView() {
        viewAdapter = new UserLocationRecyclerViewAdapter(getContext());
        binding.recyclerViewProfileFragmentLocationsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProfileFragmentLocationsList.setAdapter(viewAdapter);
    }
}