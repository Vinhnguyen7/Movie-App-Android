package com.example.movieapplication.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Singleton wrapper cho Firebase Authentication
 * Xử lý: đăng ký, đăng nhập, đăng xuất, lấy user hiện tại
 */
public class FirebaseAuthManager {

    // ── Kết quả trả về cho UI ─────────────────────────────────────────────────
    public static class AuthResult {
        public final boolean  success;
        public final String   errorMessage;
        public final FirebaseUser user;

        private AuthResult(boolean success, String errorMessage, FirebaseUser user) {
            this.success      = success;
            this.errorMessage = errorMessage;
            this.user         = user;
        }

        public static AuthResult ok(FirebaseUser user)     { return new AuthResult(true,  null, user); }
        public static AuthResult fail(String error)        { return new AuthResult(false, error, null); }
    }

    // ─────────────────────────────────────────────────────────────────────────
    private static FirebaseAuthManager instance;
    private final FirebaseAuth auth;

    private FirebaseAuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseAuthManager getInstance() {
        if (instance == null) instance = new FirebaseAuthManager();
        return instance;
    }

    // ── Kiểm tra trạng thái ───────────────────────────────────────────────────

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public String getCurrentUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public String getCurrentDisplayName() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return "";
        return user.getDisplayName() != null ? user.getDisplayName() : "";
    }

    public String getCurrentEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null && user.getEmail() != null ? user.getEmail() : "";
    }

    // ── Đăng ký ───────────────────────────────────────────────────────────────

    /**
     * Đăng ký tài khoản mới
     * @param name     Tên hiển thị
     * @param email    Email
     * @param password Mật khẩu (≥ 6 ký tự)
     * @return LiveData<AuthResult> để observe trong Activity
     */
    public LiveData<AuthResult> register(String name, String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        result.setValue(AuthResult.fail("Đăng ký thất bại"));
                        return;
                    }

                    // Lưu displayName vào Firebase Auth profile
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdate)
                            .addOnSuccessListener(unused -> result.setValue(AuthResult.ok(user)))
                            .addOnFailureListener(e  -> result.setValue(AuthResult.ok(user))); // vẫn ok dù update name lỗi
                })
                .addOnFailureListener(e -> result.setValue(AuthResult.fail(
                        translateFirebaseError(e.getMessage()))));

        return result;
    }

    // ── Đăng nhập ─────────────────────────────────────────────────────────────

    /**
     * Đăng nhập bằng email + password
     * @return LiveData<AuthResult>
     */
    public LiveData<AuthResult> login(String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult ->
                        result.setValue(AuthResult.ok(authResult.getUser())))
                .addOnFailureListener(e ->
                        result.setValue(AuthResult.fail(translateFirebaseError(e.getMessage()))));

        return result;
    }

    // ── Đăng xuất ─────────────────────────────────────────────────────────────

    public void logout() {
        auth.signOut();
    }

    // ── Dịch lỗi Firebase sang tiếng Việt ────────────────────────────────────

    private String translateFirebaseError(String firebaseMessage) {
        if (firebaseMessage == null) return "Đã có lỗi xảy ra";

        if (firebaseMessage.contains("email address is already in use"))
            return "Email này đã được đăng ký";
        if (firebaseMessage.contains("badly formatted"))
            return "Email không đúng định dạng";
        if (firebaseMessage.contains("password is invalid") || firebaseMessage.contains("wrong-password"))
            return "Email hoặc mật khẩu không đúng";
        if (firebaseMessage.contains("no user record") || firebaseMessage.contains("user-not-found"))
            return "Tài khoản không tồn tại";
        if (firebaseMessage.contains("weak-password"))
            return "Mật khẩu quá yếu, cần ít nhất 6 ký tự";
        if (firebaseMessage.contains("network"))
            return "Lỗi kết nối mạng, vui lòng thử lại";
        if (firebaseMessage.contains("too-many-requests"))
            return "Quá nhiều lần thử, vui lòng thử lại sau";

        return "Đăng nhập thất bại, vui lòng thử lại";
    }
}