package com.example.movieapplication.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.movieapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ View
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 2. Tìm NavHostFragment (chính là cái FragmentContainerView ở trên)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 3. Kết nối BottomNav với NavController (Phép màu ở đây)
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }
}