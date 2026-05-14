package com.example.movieapplication.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapplication.data.models.Movie;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton wrapper cho Cloud Firestore
 *
 * Cấu trúc database:
 * users/
 *   {uid}/
 *     favorites/
 *       {movieId}/         ← mỗi phim là 1 document
 *         id: int
 *         title: String
 *         posterPath: String
 *         overview: String
 *         voteAverage: double
 *         name: String
 *         runtime: int
 *         addedAt: long (timestamp)
 */
public class FirestoreManager {

    private static final String COLLECTION_USERS     = "users";
    private static final String COLLECTION_FAVORITES = "favorites";

    private static FirestoreManager instance;
    private final FirebaseFirestore db;

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) instance = new FirestoreManager();
        return instance;
    }

    // ── Helper: document path ─────────────────────────────────────────────────

    private DocumentReference favoriteDoc(String uid, int movieId) {
        return db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_FAVORITES)
                .document(String.valueOf(movieId));
    }

    // ── Thêm / xóa yêu thích ─────────────────────────────────────────────────

    /**
     * Toggle favorite: thêm nếu chưa có, xóa nếu đã có
     * @return LiveData<Boolean> — true = đã thêm, false = đã xóa
     */
    public LiveData<Boolean> toggleFavorite(String uid, Movie movie) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        DocumentReference ref = favoriteDoc(uid, movie.getId());

        // Kiểm tra đã tồn tại chưa
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Đã có → xóa
                ref.delete()
                        .addOnSuccessListener(unused -> result.setValue(false))
                        .addOnFailureListener(e      -> result.setValue(false));
            } else {
                // Chưa có → thêm
                Map<String, Object> data = movieToMap(movie);
                ref.set(data, SetOptions.merge())
                        .addOnSuccessListener(unused -> result.setValue(true))
                        .addOnFailureListener(e      -> result.setValue(false));
            }
        }).addOnFailureListener(e -> result.setValue(false));

        return result;
    }

    /**
     * Kiểm tra phim có trong favorites không
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> isFavorite(String uid, int movieId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        favoriteDoc(uid, movieId).get()
                .addOnSuccessListener(snapshot -> result.setValue(snapshot.exists()))
                .addOnFailureListener(e        -> result.setValue(false));

        return result;
    }

    /**
     * Lấy toàn bộ danh sách yêu thích của user
     * @return LiveData<List<Movie>>
     */
    public LiveData<List<Movie>> getFavorites(String uid) {
        MutableLiveData<List<Movie>> result = new MutableLiveData<>();

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_FAVORITES)
                .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Movie> list = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Movie movie = mapToMovie(doc.getData());
                        if (movie != null) list.add(movie);
                    }
                    result.setValue(list);
                })
                .addOnFailureListener(e -> result.setValue(new ArrayList<>()));

        return result;
    }

    /**
     * Lắng nghe real-time thay đổi favorites (dùng snapshot listener)
     * Tốt hơn getFavorites() vì tự cập nhật khi có thay đổi
     */
    public LiveData<List<Movie>> getFavoritesRealtime(String uid) {
        MutableLiveData<List<Movie>> result = new MutableLiveData<>();

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_FAVORITES)
                .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null || querySnapshot == null) {
                        result.setValue(new ArrayList<>());
                        return;
                    }
                    List<Movie> list = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Movie movie = mapToMovie(doc.getData());
                        if (movie != null) list.add(movie);
                    }
                    result.setValue(list);
                });

        return result;
    }

    // ── Convert Movie ↔ Map ───────────────────────────────────────────────────

    private Map<String, Object> movieToMap(Movie movie) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",           movie.getId());
        map.put("title",        movie.getTitle());
        // Lưu posterPath gốc (không có base URL) để tránh duplicate
        map.put("posterPath",   movie.getRawPosterPath());
        map.put("overview",     movie.getOverview());
        map.put("voteAverage",  movie.getVoteAverage());
        map.put("name",         movie.getName());
        map.put("runtime",      movie.getRuntime());
        map.put("addedAt",      System.currentTimeMillis());
        return map;
    }

    private Movie mapToMovie(Map<String, Object> map) {
        if (map == null) return null;
        try {
            Movie movie = new Movie();
            Object idObj = map.get("id");
            if (idObj instanceof Long)   movie.setId(((Long) idObj).intValue());
            if (idObj instanceof Double) movie.setId(((Double) idObj).intValue());

            movie.setTitle(      (String) map.get("title"));
            movie.setPosterPath( (String) map.get("posterPath"));
            movie.setOverview(   (String) map.get("overview"));
            movie.setName(       (String) map.get("name"));

            Object rating = map.get("voteAverage");
            if (rating instanceof Double) movie.setVoteAverage((Double) rating);

            Object runtime = map.get("runtime");
            if (runtime instanceof Long)   movie.setRuntime(((Long) runtime).intValue());
            if (runtime instanceof Double) movie.setRuntime(((Double) runtime).intValue());

            return movie;
        } catch (Exception e) {
            return null;
        }
    }
}