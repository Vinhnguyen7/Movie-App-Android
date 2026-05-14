package com.example.movieapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapplication.data.FirebaseAuthManager;
import com.example.movieapplication.data.FirestoreManager;
import com.example.movieapplication.data.models.Movie;

import java.util.List;

/**
 * ViewModel quản lý danh sách yêu thích từ Firestore
 * Dùng trong: MovieDetailActivity, FavoritesActivity
 */
public class FavoritesViewModel extends ViewModel {

    private final FirestoreManager    firestoreManager = FirestoreManager.getInstance();
    private final FirebaseAuthManager authManager      = FirebaseAuthManager.getInstance();

    // Cache trạng thái yêu thích của phim đang xem
    private final MutableLiveData<Boolean> favoriteStatus = new MutableLiveData<>();

    // ── Lấy uid của user hiện tại ─────────────────────────────────────────────

    private String getUid() {
        return authManager.getCurrentUid();
    }

    public boolean isLoggedIn() {
        return authManager.isLoggedIn();
    }

    // ── Favorites list ────────────────────────────────────────────────────────

    /**
     * Lấy danh sách yêu thích real-time (tự cập nhật khi Firestore thay đổi)
     */
    public LiveData<List<Movie>> getFavorites() {
        String uid = getUid();
        if (uid == null) return new MutableLiveData<>();
        return firestoreManager.getFavoritesRealtime(uid);
    }

    // ── Toggle favorite ───────────────────────────────────────────────────────

    /**
     * Thêm/xóa phim khỏi yêu thích
     * @return LiveData<Boolean> — true = đã thêm vào, false = đã xóa
     */
    public LiveData<Boolean> toggleFavorite(Movie movie) {
        String uid = getUid();
        if (uid == null) {
            MutableLiveData<Boolean> err = new MutableLiveData<>();
            err.setValue(false);
            return err;
        }
        return firestoreManager.toggleFavorite(uid, movie);
    }

    // ── Check favorite ────────────────────────────────────────────────────────

    /**
     * Kiểm tra phim có trong favorites không
     * Gọi khi mở MovieDetailActivity
     */
    public LiveData<Boolean> checkIsFavorite(int movieId) {
        String uid = getUid();
        if (uid == null) {
            MutableLiveData<Boolean> notFav = new MutableLiveData<>();
            notFav.setValue(false);
            return notFav;
        }
        return firestoreManager.isFavorite(uid, movieId);
    }

    // ── Cache local ───────────────────────────────────────────────────────────

    public LiveData<Boolean> getFavoriteStatus() {
        return favoriteStatus;
    }

    public void setFavoriteStatus(boolean status) {
        favoriteStatus.setValue(status);
    }
}