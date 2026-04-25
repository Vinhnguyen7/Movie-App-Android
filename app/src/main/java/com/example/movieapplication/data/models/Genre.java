package com.example.movieapplication.data.models;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
    // Getter & Setter (Dùng Alt + Insert như tôi đã hướng dẫn)
