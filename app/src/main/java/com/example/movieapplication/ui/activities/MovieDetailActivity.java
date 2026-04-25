package com.example.movieapplication.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieapplication.R;
import com.example.movieapplication.data.UserPreferences;
import com.example.movieapplication.data.models.Movie;
import com.google.android.material.button.MaterialButton;

import org.parceler.Parcels;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imgBackdrop;
    private ImageView imgDetailPoster;
    private TextView txtDetailTitle, txtDetailRating, txtRuntime, txtOverview;
    private ImageButton btnBack, btnFavorite;
    private MaterialButton btnPlay;

    private Movie movie;
    private UserPreferences userPrefs;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Ẩn ActionBar — tự thiết kế header
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        userPrefs = UserPreferences.getInstance(this);

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

        if (movie == null) {
            finish();
            return;
        }

        displayMovieDetails();
        setupListeners();
    }

    private void displayMovieDetails() {
        String title = movie.getTitle() != null ? movie.getTitle()
                : movie.getName()  != null ? movie.getName() : "Không có tên";

        txtDetailTitle.setText(title);
        txtDetailRating.setText("⭐ " + String.format("%.1f", movie.getVoteAverage()) + " / 10");

        if (movie.getRuntime() > 0) {
            int h = movie.getRuntime() / 60;
            int m = movie.getRuntime() % 60;
            txtRuntime.setText((h > 0 ? h + "h " : "") + m + " phút");
        } else {
            txtRuntime.setText("");
        }

        txtOverview.setText(
                movie.getOverview() != null && !movie.getOverview().isEmpty()
                        ? movie.getOverview()
                        : "Chưa có nội dung mô tả.");

        // Poster
        Glide.with(this)
                .load(movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .into(imgDetailPoster);

        // Backdrop (dùng poster làm backdrop nếu chưa có backdrop riêng)
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w780" + movie.getPosterPath()
                        .replace("https://image.tmdb.org/t/p/w500", ""))
                .placeholder(R.drawable.placeholder)
                .into(imgBackdrop);

        // Trạng thái yêu thích
        updateFavoriteIcon();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnFavorite.setOnClickListener(v -> {
            if (!userPrefs.isLoggedIn()) {
                Toast.makeText(this, "Đăng nhập để lưu phim yêu thích", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            isFavorite = userPrefs.toggleFavorite(movie);
            updateFavoriteIcon();
            Toast.makeText(this,
                    isFavorite ? "Đã thêm vào yêu thích ❤" : "Đã xóa khỏi yêu thích",
                    Toast.LENGTH_SHORT).show();
        });

        btnPlay.setOnClickListener(v -> playMovie());
    }

    // ── Yêu thích icon ───────────────────────────────────────────────────────

    private void updateFavoriteIcon() {
        isFavorite = userPrefs.isLoggedIn() && userPrefs.isFavorite(movie.getId());
        btnFavorite.setImageResource(
                isFavorite ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off);
        btnFavorite.setColorFilter(
                isFavorite ? getResources().getColor(android.R.color.holo_red_light, null)
                        : getResources().getColor(android.R.color.white, null));
    }

    // ── Phát phim local ──────────────────────────────────────────────────────

    /**
     * Chiến lược phát video:
     * 1. Nếu phim có localVideoUri (từ bộ nhớ thiết bị) → mở VideoPlayerActivity
     * 2. Fallback → mở trình phát video mặc định của hệ thống
     *
     * Để test: đặt file .mp4 vào res/raw/ (ví dụ: sample.mp4)
     * rồi dùng Uri: Uri.parse("android.resource://" + packageName + "/" + R.raw.sample)
     */
    private void playMovie() {
        // Thử load từ res/raw (demo)
        Uri videoUri = getSampleVideoUri();

        if (videoUri != null) {
            // Mở VideoPlayerActivity nội bộ
            Intent intent = new Intent(this, VideoplayerActivity.class);
            intent.putExtra("video_uri", videoUri.toString());
            intent.putExtra("movie_title",
                    movie.getTitle() != null ? movie.getTitle() : movie.getName());
            startActivity(intent);
        } else {
            // Fallback: mở intent chooser (VLC, MX Player,...)
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("video/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Chọn trình phát"));
            } else {
                Toast.makeText(this,
                        "Chưa có file video. Thêm file .mp4 vào res/raw/",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Trả về URI của video mẫu từ res/raw
     * Đổi R.raw.sample thành tên file thực của bạn
     */
    private Uri getSampleVideoUri() {
        try {
            // Trỏ trực tiếp vào file trailer_avatar3 bạn vừa thêm
            int resId = R.raw.trailer_avatar3;
            return Uri.parse("android.resource://" + getPackageName() + "/" + resId);
        } catch (Exception e) {
            return null;
        }
    }
}