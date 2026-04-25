package com.example.movieapplication.data.repository;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.movieapplication.data.models.Movie;
import com.example.movieapplication.data.models.MovieResponse;
import com.example.movieapplication.data.remote.ApiService;
import com.example.movieapplication.data.remote.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private ApiService apiService;
    private final String API_KEY = "7b984d4e8312104b00caa19acc0d8cf5";

    public MovieRepository(){
        apiService = RetrofitClient.getService();
    }

    public LiveData<List<Movie>> getPopularMovies() {
        MutableLiveData<List<Movie>> data = new MutableLiveData<>();

        apiService.getPopularMovies(API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body().getMovies());
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                android.util.Log.e("REPO_ERROR", "Lỗi kết nối: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }
    public LiveData<List<Movie>> searchMovies(String query) {
        MutableLiveData<List<Movie>> data = new MutableLiveData<>();
        apiService.searchMovies(API_KEY, query).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body().getMovies());
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
