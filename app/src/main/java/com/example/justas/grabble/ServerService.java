package com.example.justas.grabble;


import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public final class ServerService {
    //    public static final String API_URL = "https://grabble-backend-justasb.herokuapp.com/";
    public static final String API_URL = "http://10.0.2.2:3000/";

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

    public interface RandomUsername {
        @GET("/random_username")
        Call<Player> getRandomUsername();
    }

    public static void getRandomUsername(Callback<Player> callback) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RandomUsername randomUsername = retrofit.create(RandomUsername.class);
        Call<Player> call = randomUsername.getRandomUsername();
        call.enqueue(callback);
    }

    public interface User {
        @POST("/new_user")
        Call<Player> newUser(@Body UserDetails body);
    }

    public static void createNewUser(UserDetails userDetails, Callback<Player> callback) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        User user = retrofit.create(User.class);
        Call<Player> call = user.newUser(userDetails);
        call.enqueue(callback);
    }
}