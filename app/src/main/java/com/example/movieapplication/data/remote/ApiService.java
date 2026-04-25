package com.example.movieapplication.data.remote;

import com.example.movieapplication.data.models.Movie;
import com.example.movieapplication.data.models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // 1. Lấy danh sách phim phổ biến (Màn hình chính)
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    // 2. Lấy phim theo thể loại hoặc xu hướng (Carousel)
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    // 3. Tìm kiếm phim (Thanh Search)
    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("api_key") String apiKey, @Query("query") String query);

    // 4. Xem chi tiết (Màn hình Detail)
    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") int id, @Query("api_key") String apiKey);
}
