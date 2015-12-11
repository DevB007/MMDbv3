package com.example.deveshwar.mmdbv3.models;

/**
 * Created by Deveshwar on 11-12-2015.
 */
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Table(name = "movies")
public class Movie extends Model {

    @Column(name = "external_id", index = true)
    @SerializedName(value = "id")
    private int externalId;

    @Column(name = "title")
    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(name = "backdrop_path")
    private String backdropPath;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name =  "overview")
    private String overview;

    @Column(name = "popularity")
    private float popularity;

    @Column(name = "vote_average")
    private float voteAverage;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "is_favorite")
    private boolean isFavorite;

    private static final String BASE_IMAGE_PATH = "http://image.tmdb.org/t/p/";
    private static final String BACKDROP_DEFAULT_SIZE = "w1280";
    private static final String COVER_DEFAULT_SIZE = "w185";

    public void setExternalId(int externalId) {
        this.externalId = externalId;
    }

    public int getExternalId() {
        return externalId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPosterPath() {
        return BASE_IMAGE_PATH + COVER_DEFAULT_SIZE + posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getBackdropPath() {
        return BASE_IMAGE_PATH + BACKDROP_DEFAULT_SIZE + backdropPath;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOverview() {
        return overview;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

}
