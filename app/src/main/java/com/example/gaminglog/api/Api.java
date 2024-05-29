package com.example.gaminglog.api;
import com.example.gaminglog.GameDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL = "https://api.rawg.io/api/";
    @GET("games?page_size=100&key=[key]")
    Call<ApiResponse> getGames();

    @GET("games/{id}?key=[key]")
    Call<GameDetail> getGameById(@Path("id") int gameId);

    @GET("games?key=[key]")
    Call<ApiResponse> searchGamesByName(@Query("search") String gameName);
}