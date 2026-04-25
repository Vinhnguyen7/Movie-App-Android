package com.example.movieapplication.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapplication.R;
import com.example.movieapplication.data.UserPreferences;
import com.example.movieapplication.data.models.Movie;
import com.example.movieapplication.ui.adapter.MovieAdapter;

import org.parceler.Parcels;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private LinearLayout llEmptyFavorites;
    private MovieAdapter adapter;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Phim yêu thích");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(
                            android.graphics.Color.parseColor("#141414")));
        }

        userPrefs = UserPreferences.getInstance(this);

        rvFavorites      = findViewById(R.id.rvFavorites);
        llEmptyFavorites = findViewById(R.id.llEmptyFavorites);

        setupRecyclerView();
        loadFavorites();
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter();
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 3));
        rvFavorites.setAdapter(adapter);

        adapter.setOnMovieClickListener(movie -> {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("movie_data", Parcels.wrap(movie));
            startActivity(intent);
        });
    }

    private void loadFavorites() {
        List<Movie> favorites = userPrefs.getFavorites();
        if (favorites.isEmpty()) {
            llEmptyFavorites.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
        } else {
            llEmptyFavorites.setVisibility(View.GONE);
            rvFavorites.setVisibility(View.VISIBLE);
            adapter.setMovieList(favorites);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại khi quay lại từ detail (có thể đã bỏ yêu thích)
        loadFavorites();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}