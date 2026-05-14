package com.example.movieapplication.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapplication.ui.activities.LoginActivity;
import com.example.movieapplication.R;
import com.example.movieapplication.ui.activities.RegisterActivity;
import com.example.movieapplication.ui.activities.FavoritesActivity;
import com.example.movieapplication.ui.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment extends Fragment {

    // Logged-in views
    private LinearLayout       layoutLoggedIn;
    private ShapeableImageView imgAvatar;
    private TextView           txtName, txtEmail;
    private LinearLayout       btnFavorite, btnHistory, btnLogout;

    // Logged-out views
    private LinearLayout  layoutLoggedOut;
    private MaterialButton btnGoLogin, btnGoRegister;

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews(view);
        setupListeners();
        refreshUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại UI mỗi lần quay về tab (sau login/logout)
        refreshUI();
    }

    // ── Init ──────────────────────────────────────────────────────────────────

    private void initViews(View view) {
        layoutLoggedIn  = view.findViewById(R.id.layoutLoggedIn);
        imgAvatar       = view.findViewById(R.id.imgAvatar);
        txtName         = view.findViewById(R.id.txtName);
        txtEmail        = view.findViewById(R.id.txtEmail);
        btnFavorite     = view.findViewById(R.id.btnFavorite);
        btnHistory      = view.findViewById(R.id.btnHistory);
        btnLogout       = view.findViewById(R.id.btnLogout);

        layoutLoggedOut = view.findViewById(R.id.layoutLoggedOut);
        btnGoLogin      = view.findViewById(R.id.btnGoLogin);
        btnGoRegister   = view.findViewById(R.id.btnGoRegister);
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    private void setupListeners() {
        // Logged-out
        btnGoLogin.setOnClickListener(v ->
                startActivity(new Intent(getContext(), LoginActivity.class)));

        btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(getContext(), RegisterActivity.class)));

        // Logged-in
        btnFavorite.setOnClickListener(v ->
                startActivity(new Intent(getContext(), FavoritesActivity.class)));

        btnHistory.setOnClickListener(v ->
                Toast.makeText(getContext(), "Lịch sử xem (đang phát triển)", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
            refreshUI();
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    // ── UI state ──────────────────────────────────────────────────────────────

    private void refreshUI() {
        if (authViewModel.isLoggedIn()) {
            showLoggedInUI();
        } else {
            showLoggedOutUI();
        }
    }

    private void showLoggedInUI() {
        layoutLoggedIn.setVisibility(View.VISIBLE);
        layoutLoggedOut.setVisibility(View.GONE);

        // Lấy thông tin từ Firebase Auth
        String name  = authViewModel.getDisplayName();
        String email = authViewModel.getEmail();

        txtName.setText(name.isEmpty()  ? "Người dùng" : name);
        txtEmail.setText(email.isEmpty() ? ""           : email);

        // Hiện chữ cái đầu của tên trong avatar (nếu chưa có ảnh)
        // (Nâng cấp sau: dùng Glide load photoUrl từ Firebase Auth)
    }

    private void showLoggedOutUI() {
        layoutLoggedIn.setVisibility(View.GONE);
        layoutLoggedOut.setVisibility(View.VISIBLE);
    }
}