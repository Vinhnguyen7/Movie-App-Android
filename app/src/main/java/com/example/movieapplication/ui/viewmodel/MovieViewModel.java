package com.example.movieapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapplication.data.models.Movie;
import com.example.movieapplication.data.repository.MovieRepository;

import java.util.List;

public class MovieViewModel extends ViewModel {
    private MovieRepository repository;
    public MovieViewModel() {
        repository = new MovieRepository();
    }

    // Lấy danh sách mặc định
    public LiveData<List<Movie>> getPopularMovies() {
        return repository.getPopularMovies();
    }
    // Tìm kiếm phim theo từ khóa
    public LiveData<List<Movie>> searchMovies(String query) {
        return repository.searchMovies(query);
    }
}
