package com.example.movieapplication.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapplication.ui.activities.MovieDetailActivity;
import com.example.movieapplication.R;
import com.example.movieapplication.ui.adapter.MovieAdapter;
import com.example.movieapplication.ui.viewmodel.FavoritesViewModel;

import org.parceler.Parcels;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView   rvFavorites;
    private LinearLayout   llEmptyFavorites;
    private MovieAdapter   adapter;

    private FavoritesViewModel favoritesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Style ActionBar cho dark theme
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Phim yêu thích");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#141414")));
        }

        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        rvFavorites      = findViewById(R.id.rvFavorites);
        llEmptyFavorites = findViewById(R.id.llEmptyFavorites);

        setupRecyclerView();
        observeFavorites();
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

    /**
     * Observe real-time từ Firestore
     * Tự động cập nhật khi user thêm/xóa yêu thích từ màn hình khác
     */
    private void observeFavorites() {
        favoritesViewModel.getFavorites().observe(this, movies -> {
            if (movies == null || movies.isEmpty()) {
                llEmptyFavorites.setVisibility(View.VISIBLE);
                rvFavorites.setVisibility(View.GONE);
            } else {
                llEmptyFavorites.setVisibility(View.GONE);
                rvFavorites.setVisibility(View.VISIBLE);
                adapter.setMovieList(movies);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}