package com.example.movieapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.movieapplication.R;
import com.example.movieapplication.data.models.Movie;
import com.example.movieapplication.ui.adapter.MovieDiffCallback;

import java.util.ArrayList;
import java.util.List;

public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.CardVH> {

    public interface OnCardClickListener {
        void onCardClick(Movie movie);
    }

    private List<Movie> movieList = new ArrayList<>();
    private final OnCardClickListener listener;

    public MovieCardAdapter(OnCardClickListener listener) {
        this.listener = listener;
    }

    /** Dùng DiffUtil giống MovieAdapter gốc */
    public void setMovieList(List<Movie> newList) {
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(
                new MovieDiffCallback(movieList, newList));
        movieList.clear();
        movieList.addAll(newList);
        diff.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public CardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false);
        return new CardVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardVH holder, int position) {
        holder.bind(movieList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    // ─── ViewHolder ───────────────────────────────────────────────────────────

    static class CardVH extends RecyclerView.ViewHolder {
        private final ImageView ivPoster;
        private final TextView  tvTitle;

        CardVH(View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivMoviePoster);
            tvTitle  = itemView.findViewById(R.id.tvMovieTitle);
        }

        void bind(Movie movie, OnCardClickListener listener) {
            // Movie.getPosterPath() đã tự nối "https://image.tmdb.org/t/p/w500" rồi
            Glide.with(itemView.getContext())
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .into(ivPoster);

            String title = movie.getTitle() != null ? movie.getTitle()
                    : movie.getName()  != null ? movie.getName() : "";
            tvTitle.setText(title);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onCardClick(movie);
            });

            // Scale animation khi nhấn giữ
            itemView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false; // Cho phép onClick vẫn fire
            });
        }
    }
}