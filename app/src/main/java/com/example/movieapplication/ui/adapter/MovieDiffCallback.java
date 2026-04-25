package com.example.movieapplication.ui.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.movieapplication.data.models.Movie;

import java.util.List;

public class MovieDiffCallback extends DiffUtil.Callback {
    private List<Movie> oldList, newList;

    public MovieDiffCallback(List<Movie> oldList, List<Movie> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() { return oldList.size(); }

    @Override
    public int getNewListSize() { return newList.size(); }

    @Override
    public boolean areItemsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).getId() == newList.get(newPos).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldPos, int newPos) {
        return oldList.get(oldPos).getTitle().equals(newList.get(newPos).getTitle());
    }
}