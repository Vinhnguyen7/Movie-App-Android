package com.example.movieapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.movieapplication.R;
import com.example.movieapplication.data.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class HeroBannerAdapter extends RecyclerView.Adapter<HeroBannerAdapter.BannerVH> {

    public interface OnBannerClickListener {
        void onBannerClick(Movie movie);
    }

    private List<Movie> movies = new ArrayList<>();
    private final OnBannerClickListener listener;

    public HeroBannerAdapter(List<Movie> movies, OnBannerClickListener listener) {
        this.movies   = movies;
        this.listener = listener;
    }

    /** Cập nhật danh sách banner */
    public void updateMovies(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    /** Lấy phim theo index để dùng cho nút Play/Info */
    public Movie getMovieAt(int index) {
        if (index >= 0 && index < movies.size()) return movies.get(index);
        return null;
    }

    @NonNull
    @Override
    public BannerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hero_banner, parent, false);
        return new BannerVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerVH holder, int position) {
        holder.bind(movies.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    // ─── ViewHolder ───────────────────────────────────────────────────────────

    static class BannerVH extends RecyclerView.ViewHolder {
        private final ImageView ivBanner;
        private final TextView  tvTitle;
        private final TextView  tvBadge;

        BannerVH(View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBannerImage);
            tvTitle  = itemView.findViewById(R.id.tvBannerTitle);
            tvBadge  = itemView.findViewById(R.id.tvSeriesBadge);
        }

        void bind(Movie movie, OnBannerClickListener listener) {
            // Movie.getPosterPath() đã tự nối base URL rồi
            Glide.with(itemView.getContext())
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(ivBanner);

            // Ưu tiên title, fallback sang name (TV show)
            String displayTitle = movie.getTitle() != null ? movie.getTitle()
                    : movie.getName()  != null ? movie.getName() : "";
            tvTitle.setText(displayTitle);

            // Hiện badge "SERIES" nếu là TV (ko có title, chỉ có name)
            boolean isSeries = movie.getTitle() == null && movie.getName() != null;
            tvBadge.setVisibility(isSeries ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onBannerClick(movie);
            });
        }
    }
}