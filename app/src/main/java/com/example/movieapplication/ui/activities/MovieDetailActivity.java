package com.example.movieapplication.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.movieapplication.data.models.Movie;
import com.example.movieapplication.ui.activities.VideoplayerActivity;
import com.example.movieapplication.ui.viewmodel.AuthViewModel;
import com.example.movieapplication.ui.viewmodel.FavoritesViewModel;
import com.google.android.material.button.MaterialButton;

import org.parceler.Parcels;

import com.example.movieapplication.R;


public class MovieDetailActivity extends AppCompatActivity {

    private ImageView      imgBackdrop, imgDetailPoster;
    private TextView       txtDetailTitle, txtDetailRating, txtRuntime, txtOverview;
    private ImageButton    btnBack, btnFavorite;
    private MaterialButton btnPlay;

    private Movie            movie;
    private AuthViewModel    authViewModel;
    private FavoritesViewModel favoritesViewModel;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        authViewModel      = new ViewModelProvider(this).get(AuthViewModel.class);
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        initViews();
        receiveData();
    }

    private void initViews() {
        imgBackdrop     = findViewById(R.id.imgBackdrop);
        imgDetailPoster = findViewById(R.id.imgDetailPoster);
        txtDetailTitle  = findViewById(R.id.txtDetailTitle);
        txtDetailRating = findViewById(R.id.txtDetailRating);
        txtRuntime      = findViewById(R.id.txtRuntime);
        txtOverview     = findViewById(R.id.txtOverview);
        btnBack         = findViewById(R.id.btnBack);
        btnFavorite     = findViewById(R.id.btnFavorite);
        btnPlay         = findViewById(R.id.btnPlay);
    }

    private void receiveData() {
        movie = Parcels.unwrap(getIntent().getParcelableExtra("movie_data"));
        if (movie == null) { finish(); return; }

        displayMovieDetails();
        checkFavoriteStatus();
        setupListeners();
    }

    // ── Display ───────────────────────────────────────────────────────────────

    private void displayMovieDetails() {
        txtDetailTitle.setText(movie.getDisplayTitle());
        txtDetailRating.setText("⭐ " + String.format("%.1f", movie.getVoteAverage()) + " / 10");

        if (movie.getRuntime() > 0) {
            int h = movie.getRuntime() / 60;
            int m = movie.getRuntime() % 60;
            txtRuntime.setText((h > 0 ? h + "h " : "") + m + " phút");
        } else {
            txtRuntime.setText("");
        }

        String overview = movie.getOverview();
        txtOverview.setText(overview != null && !overview.isEmpty()
                ? overview : "Chưa có mô tả.");

        // Poster
        Glide.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .into(imgDetailPoster);

        // Backdrop
        Glide.with(this)
                .load(movie.getBackdropUrl())
                .placeholder(R.drawable.placeholder)
                .into(imgBackdrop);
    }

    // ── Favorite ──────────────────────────────────────────────────────────────

    /** Kiểm tra trạng thái yêu thích từ Firestore khi mở màn hình */
    private void checkFavoriteStatus() {
        if (!authViewModel.isLoggedIn()) {
            updateFavoriteIcon(false);
            return;
        }
        favoritesViewModel.checkIsFavorite(movie.getId()).observe(this, status -> {
            isFavorite = status != null && status;
            updateFavoriteIcon(isFavorite);
        });
    }

    private void updateFavoriteIcon(boolean favorite) {
        btnFavorite.setImageResource(
                favorite ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off);
        int color = favorite
                ? getResources().getColor(android.R.color.holo_red_light, null)
                : getResources().getColor(android.R.color.white, null);
        btnFavorite.setColorFilter(color);
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnFavorite.setOnClickListener(v -> {
            if (!authViewModel.isLoggedIn()) {
                Toast.makeText(this, "Đăng nhập để lưu phim yêu thích", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            // Toggle trên Firestore
            favoritesViewModel.toggleFavorite(movie).observe(this, added -> {
                isFavorite = added != null && added;
                updateFavoriteIcon(isFavorite);
                Toast.makeText(this,
                        isFavorite ? "Đã thêm vào yêu thích ❤" : "Đã xóa khỏi yêu thích",
                        Toast.LENGTH_SHORT).show();
            });
        });

        btnPlay.setOnClickListener(v -> playMovie());
    }

    // ── Video Player ──────────────────────────────────────────────────────────

    private void playMovie() {
        Uri videoUri = getSampleVideoUri();

        if (videoUri != null) {
            Intent intent = new Intent(this, VideoplayerActivity.class);
            intent.putExtra("video_uri",    videoUri.toString());
            intent.putExtra("movie_title",  movie.getDisplayTitle());
            startActivity(intent);
        } else {
            // Fallback: mở trình phát hệ thống
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("video/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Chọn trình phát"));
            } else {
                Toast.makeText(this,
                        "Thêm file video vào res/raw/ để phát (đặt tên: sample.mp4)",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private Uri getSampleVideoUri() {
        try {
            int resId = getResources().getIdentifier("sample", "raw", getPackageName());
            if (resId != 0) {
                return Uri.parse("android.resource://" + getPackageName() + "/" + resId);
            }
        } catch (Exception ignored) {}
        return null;
    }
}