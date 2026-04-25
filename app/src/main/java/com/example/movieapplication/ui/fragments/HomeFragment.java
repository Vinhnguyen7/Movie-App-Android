package com.example.movieapplication.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.movieapplication.ui.activities.MovieDetailActivity;
import com.example.movieapplication.R;
import com.example.movieapplication.data.models.Movie;
import com.example.movieapplication.ui.adapter.HeroBannerAdapter;
import com.example.movieapplication.ui.adapter.MovieCardAdapter;
import com.example.movieapplication.ui.viewmodel.MovieViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    // ── Views ──────────────────────────────────────────────────────────────────
    private ViewPager2 vpHeroBanner;
    private LinearLayout llBannerDots;
    private TextView tvFeaturedGenre;
    private View btnPlayFeatured;
    private View btnFeaturedInfo;
    private View btnAddToList;

    private RecyclerView rvPopular, rvTrending, rvAction;

    private TextView tabAll, tabTVShows, tabMovies, tabMyList;
    private TextView tvSeeAllPopular, tvSeeAllTrending, tvSeeAllAction;

    private ProgressBar progressBarOverlay;

    // ── Adapters ───────────────────────────────────────────────────────────────
    private HeroBannerAdapter heroBannerAdapter;
    private MovieCardAdapter  popularAdapter;
    private MovieCardAdapter  trendingAdapter;
    private MovieCardAdapter  actionAdapter;

    // ── ViewModel ──────────────────────────────────────────────────────────────
    private MovieViewModel movieViewModel;

    // ── Auto-scroll ────────────────────────────────────────────────────────────
    private final Handler   autoScrollHandler  = new Handler(Looper.getMainLooper());
    private       Runnable  autoScrollRunnable;
    private static final long AUTO_SCROLL_MS   = 3500L;

    // Full list cached for tab filtering
    private List<Movie> allMovies = new ArrayList<>();

    // ═══════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ═══════════════════════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupCategoryTabs();
        setupHeroBanner();
        setupHorizontalSections();
        setupClickListeners();

        // ViewModel gốc — không thay đổi
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        observeMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (heroBannerAdapter != null && heroBannerAdapter.getItemCount() > 1) {
            startAutoScroll(heroBannerAdapter.getItemCount());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoScrollHandler.removeCallbacksAndMessages(null);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Init
    // ═══════════════════════════════════════════════════════════════════════════

    private void initViews(View view) {
        vpHeroBanner       = view.findViewById(R.id.vpHeroBanner);
        llBannerDots       = view.findViewById(R.id.llBannerDots);
        tvFeaturedGenre    = view.findViewById(R.id.tvFeaturedGenre);
        btnPlayFeatured    = view.findViewById(R.id.btnPlayFeatured);
        btnFeaturedInfo    = view.findViewById(R.id.btnFeaturedInfo);
        btnAddToList       = view.findViewById(R.id.btnAddToList);

        rvPopular          = view.findViewById(R.id.rvPopular);
        rvTrending         = view.findViewById(R.id.rvTrending);
        rvAction           = view.findViewById(R.id.rvAction);

        tabAll             = view.findViewById(R.id.tabAll);
        tabTVShows         = view.findViewById(R.id.tabTVShows);
        tabMovies          = view.findViewById(R.id.tabMovies);
        tabMyList          = view.findViewById(R.id.tabMyList);

        tvSeeAllPopular    = view.findViewById(R.id.tvSeeAllPopular);
        tvSeeAllTrending   = view.findViewById(R.id.tvSeeAllTrending);
        tvSeeAllAction     = view.findViewById(R.id.tvSeeAllAction);

        progressBarOverlay = view.findViewById(R.id.progressBarOverlay);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Category Tabs
    // ═══════════════════════════════════════════════════════════════════════════

    private void setupCategoryTabs() {
        TextView[] tabs = {tabAll, tabTVShows, tabMovies, tabMyList};

        for (TextView tab : tabs) {
            tab.setOnClickListener(v -> {
                // Reset tất cả tab
                for (TextView t : tabs) {
                    t.setTextColor(getResources().getColor(R.color.text_secondary, null));
                    t.setBackgroundResource(R.drawable.bg_tab_unselected);
                }
                // Highlight tab được chọn
                tab.setTextColor(getResources().getColor(android.R.color.white, null));
                tab.setBackgroundResource(R.drawable.bg_tab_selected);

                filterByCategory(tab.getText().toString());
            });
        }
    }

    private void filterByCategory(String category) {
        if (allMovies.isEmpty()) return;

        List<Movie> filtered;
        switch (category) {
            case "TV Shows":
                filtered = new ArrayList<>();
                for (Movie m : allMovies) {
                    if (m.getName() != null && m.getTitle() == null) filtered.add(m);
                }
                break;
            case "Phim":
                filtered = new ArrayList<>();
                for (Movie m : allMovies) {
                    if (m.getTitle() != null) filtered.add(m);
                }
                break;
            default:
                filtered = new ArrayList<>(allMovies);
                break;
        }

        popularAdapter.setMovieList(filtered.size() > 10 ? filtered.subList(0, 10) : filtered);
        trendingAdapter.setMovieList(filtered.size() > 10 ? filtered.subList(0, Math.min(20, filtered.size())) : filtered);
        actionAdapter.setMovieList(filtered.size() > 10 ? filtered.subList(filtered.size() - Math.min(10, filtered.size()), filtered.size()) : filtered);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Hero Banner
    // ═══════════════════════════════════════════════════════════════════════════

    private void setupHeroBanner() {
        heroBannerAdapter = new HeroBannerAdapter(new ArrayList<>(), movie -> navigateToDetail(movie));
        vpHeroBanner.setAdapter(heroBannerAdapter);

        vpHeroBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });
    }

    private void buildDots(int count) {
        llBannerDots.removeAllViews();
        int sizePx   = (int) (8  * getResources().getDisplayMetrics().density);
        int marginPx = (int) (5  * getResources().getDisplayMetrics().density);

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
            lp.setMargins(marginPx, 0, marginPx, 0);
            dot.setLayoutParams(lp);
            dot.setImageResource(R.drawable.dot_inactive);
            llBannerDots.addView(dot);
        }
        updateDots(0);
    }

    private void updateDots(int activeIndex) {
        for (int i = 0; i < llBannerDots.getChildCount(); i++) {
            ImageView dot = (ImageView) llBannerDots.getChildAt(i);
            dot.setImageResource(i == activeIndex ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void startAutoScroll(int count) {
        stopAutoScroll();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int next = (vpHeroBanner.getCurrentItem() + 1) % count;
                vpHeroBanner.setCurrentItem(next, true);
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_MS);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_MS);
    }

    private void stopAutoScroll() {
        if (autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Horizontal RecyclerView sections
    // ═══════════════════════════════════════════════════════════════════════════

    private void setupHorizontalSections() {
        popularAdapter  = new MovieCardAdapter(movie -> navigateToDetail(movie));
        trendingAdapter = new MovieCardAdapter(movie -> navigateToDetail(movie));
        actionAdapter   = new MovieCardAdapter(movie -> navigateToDetail(movie));

        setupRV(rvPopular,  popularAdapter);
        setupRV(rvTrending, trendingAdapter);
        setupRV(rvAction,   actionAdapter);
    }

    private void setupRV(RecyclerView rv, MovieCardAdapter adapter) {
        rv.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Observe ViewModel (giữ nguyên logic gốc, chỉ routing dữ liệu thêm)
    // ═══════════════════════════════════════════════════════════════════════════

    private void observeMovies() {
        progressBarOverlay.setVisibility(View.VISIBLE);

        movieViewModel.getPopularMovies().observe(getViewLifecycleOwner(), movieList -> {
            progressBarOverlay.setVisibility(View.GONE);

            if (movieList != null && !movieList.isEmpty()) {

                // Cache toàn bộ list để filter sau
                allMovies = new ArrayList<>(movieList);

                // ── Hero Banner: 5 phim đầu ──
                List<Movie> featured = new ArrayList<>();
                for (int i = 0; i < Math.min(5, movieList.size()); i++) {
                    if (movieList.get(i) != null) featured.add(movieList.get(i));
                }
                heroBannerAdapter.updateMovies(featured);
                buildDots(featured.size());
                if (featured.size() > 1) startAutoScroll(featured.size());

                // Genre text từ phim đầu tiên (dùng title làm genre giả nếu ko có)
                if (!featured.isEmpty()) {
                    Movie first = featured.get(0);
                    String display = (first.getName() != null) ? "TV · " + first.getName()
                            : (first.getTitle() != null) ? first.getTitle() : "";
                    tvFeaturedGenre.setText(display);
                }

                // ── 3 sections ngang ──
                int total = movieList.size();

                List<Movie> popularList = new ArrayList<>();
                for (int i = 0; i < Math.min(10, total); i++) {
                    if (movieList.get(i) != null) popularList.add(movieList.get(i));
                }

                // Shuffle một bản copy cho Trending
                List<Movie> shuffled = new ArrayList<>(movieList);
                Collections.shuffle(shuffled);
                List<Movie> trendingList = new ArrayList<>();
                for (int i = 0; i < Math.min(10, shuffled.size()); i++) {
                    if (shuffled.get(i) != null) trendingList.add(shuffled.get(i));
                }

                // Action: lấy cuối list
                List<Movie> actionList = new ArrayList<>();
                int start = Math.max(0, total - 10);
                for (int i = start; i < total; i++) {
                    if (movieList.get(i) != null) actionList.add(movieList.get(i));
                }

                popularAdapter.setMovieList(popularList);
                trendingAdapter.setMovieList(trendingList);
                actionAdapter.setMovieList(actionList);

            } else {
                Toast.makeText(getContext(), "Không tìm thấy phim nào!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Click listeners
    // ═══════════════════════════════════════════════════════════════════════════

    private void setupClickListeners() {
        btnPlayFeatured.setOnClickListener(v -> {
            Movie movie = heroBannerAdapter.getMovieAt(vpHeroBanner.getCurrentItem());
            if (movie != null) navigateToDetail(movie);
        });

        btnFeaturedInfo.setOnClickListener(v -> {
            Movie movie = heroBannerAdapter.getMovieAt(vpHeroBanner.getCurrentItem());
            if (movie != null) navigateToDetail(movie);
        });

        btnAddToList.setOnClickListener(v ->
                Toast.makeText(getContext(), "Đã thêm vào danh sách!", Toast.LENGTH_SHORT).show());

        tvSeeAllPopular.setOnClickListener(v ->
                Toast.makeText(getContext(), "Xem tất cả phổ biến", Toast.LENGTH_SHORT).show());
        tvSeeAllTrending.setOnClickListener(v ->
                Toast.makeText(getContext(), "Xem tất cả thịnh hành", Toast.LENGTH_SHORT).show());
        tvSeeAllAction.setOnClickListener(v ->
                Toast.makeText(getContext(), "Xem tất cả hành động", Toast.LENGTH_SHORT).show());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Navigation (giữ nguyên cách dùng Parcels + MovieDetailActivity gốc)
    // ═══════════════════════════════════════════════════════════════════════════

    private void navigateToDetail(Movie movie) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movie_data", Parcels.wrap(movie));
        startActivity(intent);
    }
}