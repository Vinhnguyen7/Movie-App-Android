package com.example.movieapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapplication.data.FirebaseAuthManager;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel dùng chung cho LoginActivity và RegisterActivity
 * Tách logic khỏi Activity → dễ test, không leak context
 */
public class AuthViewModel extends ViewModel {

    private final FirebaseAuthManager authManager = FirebaseAuthManager.getInstance();

    // ── Đăng nhập ─────────────────────────────────────────────────────────────

    public LiveData<FirebaseAuthManager.AuthResult> login(String email, String password) {
        return authManager.login(email, password);
    }

    // ── Đăng ký ───────────────────────────────────────────────────────────────

    public LiveData<FirebaseAuthManager.AuthResult> register(String name, String email, String password) {
        return authManager.register(name, email, password);
    }

    // ── Trạng thái ────────────────────────────────────────────────────────────

    public boolean isLoggedIn()              { return authManager.isLoggedIn(); }
    public FirebaseUser getCurrentUser()     { return authManager.getCurrentUser(); }
    public String       getCurrentUid()      { return authManager.getCurrentUid(); }
    public String       getDisplayName()     { return authManager.getCurrentDisplayName(); }
    public String       getEmail()           { return authManager.getCurrentEmail(); }

    public void logout()                     { authManager.logout(); }
}