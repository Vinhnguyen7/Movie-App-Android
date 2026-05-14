package com.example.movieapplication.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Model phim — tương thích TMDB API + Firestore
 * Thay thế Movie.java cũ
 */
public class Movie implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("name")          // TV show dùng "name" thay vì "title"
    private String name;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("media_type")
    private String mediaType;        // "movie" | "tv"

    // ── Constructor ───────────────────────────────────────────────────────────

    public Movie() {}

    protected Movie(Parcel in) {
        id           = in.readInt();
        title        = in.readString();
        posterPath   = in.readString();
        backdropPath = in.readString();
        overview     = in.readString();
        voteAverage  = in.readDouble();
        name         = in.readString();
        runtime      = in.readInt();
        mediaType    = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(name);
        dest.writeInt(runtime);
        dest.writeString(mediaType);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override public Movie createFromParcel(Parcel in) { return new Movie(in); }
        @Override public Movie[] newArray(int size)        { return new Movie[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getId()          { return id; }
    public String getTitle()       { return title; }
    public String getOverview()    { return overview; }
    public double getVoteAverage() { return voteAverage; }
    public String getName()        { return name; }
    public int    getRuntime()     { return runtime; }
    public String getMediaType()   { return mediaType; }

    /**
     * posterPath đầy đủ dùng để hiển thị ảnh với Glide
     * VD: "https://image.tmdb.org/t/p/w500/abc123.jpg"
     */
    public String getPosterPath() {
        if (posterPath == null || posterPath.isEmpty()) return "";
        // Tránh nối URL 2 lần nếu Firestore đã lưu raw path
        if (posterPath.startsWith("http")) return posterPath;
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    /**
     * Raw posterPath — chỉ lưu vào Firestore, KHÔNG dùng để load ảnh
     * VD: "/abc123.jpg"
     */
    public String getRawPosterPath() {
        if (posterPath == null) return "";
        if (posterPath.startsWith("http")) {
            // Strip base URL nếu đã có
            return posterPath.replace("https://image.tmdb.org/t/p/w500", "");
        }
        return posterPath;
    }

    /**
     * backdropPath đầy đủ (width 780) dùng cho Hero Banner và Detail header
     */
    public String getBackdropUrl() {
        if (backdropPath != null && !backdropPath.isEmpty()) {
            return "https://image.tmdb.org/t/p/w780" + backdropPath;
        }
        // Fallback về poster nếu không có backdrop
        return getPosterPath().replace("/w500", "/w780");
    }

    /**
     * Tên hiển thị thống nhất (ưu tiên title, fallback sang name của TV)
     */
    public String getDisplayTitle() {
        if (title != null && !title.isEmpty()) return title;
        if (name  != null && !name.isEmpty())  return name;
        return "Không có tên";
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setId(int id)                   { this.id = id; }
    public void setTitle(String title)           { this.title = title; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public void setBackdropPath(String b)        { this.backdropPath = b; }
    public void setOverview(String overview)     { this.overview = overview; }
    public void setVoteAverage(double v)         { this.voteAverage = v; }
    public void setName(String name)             { this.name = name; }
    public void setRuntime(int runtime)          { this.runtime = runtime; }
    public void setMediaType(String mediaType)   { this.mediaType = mediaType; }
}