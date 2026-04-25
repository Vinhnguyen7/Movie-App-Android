package com.example.movieapplication.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapplication.R;
import com.example.movieapplication.data.UserPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvLoginError, tvGoRegister;
    private ProgressBar progressLogin;

    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ẩn ActionBar cho màn hình login
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        userPrefs = UserPreferences.getInstance(this);

        // Nếu đã đăng nhập rồi thì về thẳng MainActivity
        if (userPrefs.isLoggedIn()) {
            goToMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilEmail       = findViewById(R.id.tilEmail);
        tilPassword    = findViewById(R.id.tilPassword);
        etEmail        = findViewById(R.id.etEmail);
        etPassword     = findViewById(R.id.etPassword);
        btnLogin       = findViewById(R.id.btnLogin);
        tvLoginError   = findViewById(R.id.tvLoginError);
        tvGoRegister   = findViewById(R.id.tvGoRegister);
        progressLogin  = findViewById(R.id.progressLogin);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        // Reset lỗi
        tilEmail.setError(null);
        tilPassword.setError(null);
        tvLoginError.setVisibility(View.GONE);

        // Validate
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Vui lòng nhập email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        progressLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Giả lập delay network nhỏ cho UX đẹp
        btnLogin.postDelayed(() -> {
            boolean success = userPrefs.login(email, password);
            progressLogin.setVisibility(View.GONE);
            btnLogin.setEnabled(true);

            if (success) {
                goToMain();
            } else {
                tvLoginError.setText("Email hoặc mật khẩu không đúng");
                tvLoginError.setVisibility(View.VISIBLE);
            }
        }, 800);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}