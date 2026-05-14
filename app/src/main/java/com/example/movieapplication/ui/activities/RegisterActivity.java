package com.example.movieapplication.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapplication.ui.viewmodel.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.movieapplication.R;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout   tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton    btnRegister;
    private TextView          tvRegisterError, tvGoLogin;
    private ProgressBar       progressRegister;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tilName            = findViewById(R.id.tilName);
        tilEmail           = findViewById(R.id.tilEmail);
        tilPassword        = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etName             = findViewById(R.id.etName);
        etEmail            = findViewById(R.id.etEmail);
        etPassword         = findViewById(R.id.etPassword);
        etConfirmPassword  = findViewById(R.id.etConfirmPassword);

        btnRegister        = findViewById(R.id.btnRegister);
        tvRegisterError    = findViewById(R.id.tvRegisterError);
        tvGoLogin          = findViewById(R.id.tvGoLogin);
        progressRegister   = findViewById(R.id.progressRegister);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());

        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name     = etName.getText()            != null ? etName.getText().toString().trim() : "";
        String email    = etEmail.getText()           != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText()        != null ? etPassword.getText().toString()      : "";
        String confirm  = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        // Reset lỗi
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tvRegisterError.setVisibility(View.GONE);

        // Validate
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Vui lòng nhập tên hiển thị");
            return;
        }
        if (name.length() < 2) {
            tilName.setError("Tên quá ngắn");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Vui lòng nhập email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }
        if (password.length() < 6) {
            tilPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }
        if (!password.equals(confirm)) {
            tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // Hiện loading
        setLoading(true);

        // Gọi Firebase Auth qua ViewModel
        authViewModel.register(name, email, password).observe(this, result -> {
            setLoading(false);

            if (result.success) {
                // Đăng ký thành công → về MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                tvRegisterError.setText(result.errorMessage);
                tvRegisterError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setLoading(boolean loading) {
        progressRegister.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Đang tạo tài khoản..." : "Tạo tài khoản");
    }
}