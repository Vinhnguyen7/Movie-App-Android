package com.example.movieapplication.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.movieapplication.data.models.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper quản lý trạng thái đăng nhập và danh sách phim yêu thích
 * Dùng SharedPreferences — không cần server, phù hợp demo intern
 */
public class UserPreferences {

    private static final String PREF_NAME       = "MovieAppPrefs";
    private static final String KEY_LOGGED_IN   = "is_logged_in";
    private static final String KEY_USER_NAME   = "user_name";
    private static final String KEY_USER_EMAIL  = "user_email";
    private static final String KEY_USER_PASS   = "user_password";   // lưu demo, thực tế phải hash
    private static final String KEY_FAVORITES   = "favorites_json";

    private static UserPreferences instance;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private UserPreferences(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserPreferences getInstance(Context context) {
        if (instance == null) instance = new UserPreferences(context);
        return instance;
    }

    // ── Auth ─────────────────────────────────────────────────────────────────

    /** Lưu tài khoản và đánh dấu đã đăng nhập */
    public void register(String name, String email, String password) {
        prefs.edit()
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_PASS, password)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    /**
     * Kiểm tra email + mật khẩu, trả về true nếu đúng
     * và tự set trạng thái đăng nhập
     */
    public boolean login(String email, String password) {
        String savedEmail = prefs.getString(KEY_USER_EMAIL, "");
        String savedPass  = prefs.getString(KEY_USER_PASS, "");

        if (savedEmail.equalsIgnoreCase(email) && savedPass.equals(password)) {
            prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply();
            return true;
        }
        return false;
    }

    public void logout() {
        prefs.edit().putBoolean(KEY_LOGGED_IN, false).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUserName()  { return prefs.getString(KEY_USER_NAME, ""); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, ""); }

    /** Kiểm tra email đã đăng ký chưa */
    public boolean isEmailRegistered(String email) {
        return prefs.getString(KEY_USER_EMAIL, "").equalsIgnoreCase(email);
    }

    // ── Favorites ─────────────────────────────────────────────────────────────

    public List<Movie> getFavorites() {
        String json = prefs.getString(KEY_FAVORITES, "[]");
        Type type = new TypeToken<List<Movie>>(){}.getType();
        List<Movie> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public boolean isFavorite(int movieId) {
        for (Movie m : getFavorites()) {
            if (m.getId() == movieId) return true;
        }
        return false;
    }

    /** Toggle: thêm nếu chưa có, xóa nếu đã có. Trả về trạng thái mới. */
    public boolean toggleFavorite(Movie movie) {
        List<Movie> favorites = getFavorites();
        boolean found = false;

        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getId() == movie.getId()) {
                favorites.remove(i);
                found = true;
                break;
            }
        }

        if (!found) favorites.add(movie);

        prefs.edit().putString(KEY_FAVORITES, gson.toJson(favorites)).apply();
        return !found; // true = đã thêm, false = đã xóa
    }

    public void clearFavorites() {
        prefs.edit().putString(KEY_FAVORITES, "[]").apply();
    }
}