package com.example.movieapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.ui.activities.MovieDetailActivity
import com.example.movieapplication.R
import com.example.movieapplication.data.models.Movie
import com.example.movieapplication.ui.adapter.MovieAdapter
import com.example.movieapplication.ui.viewmodel.MovieViewModel
import org.parceler.Parcels

class SearchFragment : Fragment() {
    private var searchView: SearchView? = null
    private var ivClearSearch: ImageView? = null
    private var llEmptyState: LinearLayout? = null
    private var rvSearchMovies: RecyclerView? = null

    private var adapter: MovieAdapter? = null
    private var viewModel: MovieViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupSearch()

        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
    }

    private fun initViews(view: View) {
        searchView = view.findViewById<SearchView?>(R.id.searchView)
        ivClearSearch = view.findViewById<ImageView?>(R.id.ivClearSearch)
        llEmptyState = view.findViewById<LinearLayout?>(R.id.llEmptyState)
        rvSearchMovies = view.findViewById<RecyclerView?>(R.id.rvSearchMovies)
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter()
        rvSearchMovies!!.setLayoutManager(GridLayoutManager(getContext(), 3))
        rvSearchMovies!!.setAdapter(adapter)

        adapter!!.setOnMovieClickListener(MovieAdapter.OnMovieClickListener { movie: Movie? ->
            if (movie != null) {
                val intent = Intent(getContext(), MovieDetailActivity::class.java)
                intent.putExtra("movie_data", Parcels.wrap<Movie?>(movie))
                startActivity(intent)
            }
        })
    }

    private fun setupSearch() {
        // Style SearchView cho dark mode
        searchView!!.setQueryHint("Tìm phim, diễn viên...")

        // Tự động focus bàn phím khi vào tab
        searchView!!.setIconifiedByDefault(false)

        // Nút X xóa tìm kiếm
        ivClearSearch!!.setOnClickListener(View.OnClickListener { v: View? ->
            searchView!!.setQuery("", false)
            showEmptyState()
        })

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!TextUtils.isEmpty(query)) performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                ivClearSearch!!.setVisibility(
                    if (TextUtils.isEmpty(newText)) View.GONE else View.VISIBLE
                )

                if (TextUtils.isEmpty(newText)) {
                    showEmptyState()
                } else if (newText!!.length > 1) {
                    performSearch(newText)
                }
                return true
            }
        })
    }

    private fun performSearch(query: String?) {
        viewModel!!.searchMovies(query)
            .observe(getViewLifecycleOwner(), Observer { movies: MutableList<Movie?>? ->
                if (movies != null && !movies.isEmpty()) {
                    showResults()
                    adapter!!.setMovieList(movies)
                } else {
                    showEmptyState()
                }
            })
    }

    private fun showResults() {
        llEmptyState!!.setVisibility(View.GONE)
        rvSearchMovies!!.setVisibility(View.VISIBLE)
    }

    private fun showEmptyState() {
        llEmptyState!!.setVisibility(View.VISIBLE)
        rvSearchMovies!!.setVisibility(View.GONE)
        adapter!!.setMovieList(ArrayList<Movie?>())
    }
}