package com.example.movieapplication.data.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("name")
    private String name;

    @SerializedName("runtime")
    private int runtime;

    // Constructor bình thường dùng cho GSON/Retrofit
    public Movie() {}

    // 1. Constructor đọc dữ liệu từ Parcel (PHẢI ĐÚNG THỨ TỰ VỚI writeToParcel)
    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        name = in.readString();
        runtime = in.readInt();
    }

    // 2. Ghi dữ liệu vào Parcel để truyền đi
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(name);
        dest.writeInt(runtime);
    }

    // 3. Creator bắt buộc phải có
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterPath() {
        // Tránh bị lỗi nối chuỗi nếu posterPath từ API bị null
        return (posterPath != null) ? "https://image.tmdb.org/t/p/w500" + posterPath : "";
    }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getRuntime() { return runtime; }
    public void setRuntime(int runtime) { this.runtime = runtime; }
}