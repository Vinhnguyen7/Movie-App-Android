package com.example.movieapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.movieapplication.R;
import com.example.movieapplication.data.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movieList = new ArrayList<>();
    private OnMovieClickListener listener; // Để xử lý khi click vào phim

    // Constructor mặc định
    public MovieAdapter() {
        this.movieList = new ArrayList<>();
    }

    // Hàm cập nhật danh sách sử dụng DiffUtil
    public void setMovieList(List<Movie> newMovieList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MovieDiffCallback(movieList, newMovieList));
        this.movieList.clear();
        this.movieList.addAll(newMovieList);
        diffResult.dispatchUpdatesTo(this);
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        holder.rating.setText(String.valueOf(movie.getVoteAverage()));

        // Dùng Glide để tải ảnh (Task 1.1 đã cài thư viện này)
        Glide.with(holder.itemView.getContext())
                .load(movie.getPosterPath())
                .placeholder(R.drawable.placeholder) // Ảnh hiện khi đang tải
                .into(holder.poster);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }
    @Override
    public int getItemCount() {
        return movieList.size();
    }
    // ViewHolder để giữ các View
    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, rating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imgPoster);
            title = itemView.findViewById(R.id.txtTitle);
            rating = itemView.findViewById(R.id.txtRating);
        }
    }
    // Để xử lý khi click vào phim
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }
    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }

}
