package com.example.justas.grabble;


import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public final class ServerService {
    public static final String API_URL = "https://grabble-backend-justasb.herokuapp.com/";

    public interface Placemarks {
        @GET("/placemarks")
        Call<List<MarkerItem>> getPlacemarks();
    }

    public static void getAllPlacemarks(Callback<List<MarkerItem>> callback) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Placemarks placemarks = retrofit.create(Placemarks.class);
        Call<List<MarkerItem>> call = placemarks.getPlacemarks();
        call.enqueue(callback);
    }

    public interface Leaderboard {
        @GET("/leaderboard/{timeInterval}")
        Call<LeaderboardFeed> getLeaderboard(@Path("timeInterval") String timeInterval);

    }

    public static void getLeaderboard(String timeInterval, Callback<LeaderboardFeed> callback) throws IOException {
        Log.d("SERVER", "getLeaderboard called");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Leaderboard leaderboard = retrofit.create(Leaderboard.class);
        Call<LeaderboardFeed> call = leaderboard.getLeaderboard(timeInterval);
        call.enqueue(callback);
    }
}