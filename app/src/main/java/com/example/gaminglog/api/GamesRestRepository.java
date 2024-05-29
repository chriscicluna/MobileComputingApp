package com.example.gaminglog.api;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gaminglog.GameDetail;

import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class GamesRestRepository {
    private static GamesRestRepository instance = null;
    private Api api;
    private GamesRestRepository() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }
    public static synchronized GamesRestRepository getInstance() {
        if (instance == null) {
            instance = new GamesRestRepository();
        }
        return instance;
    }

    //gets a list of game details
    public LiveData<List<GameDetail>> fetchGames() {
        final MutableLiveData<List<GameDetail>> games = new MutableLiveData<>();
        api.getGames().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    games.setValue(response.body().getResults());
                } else {
                    games.setValue(null);
                    try {
                        Log.e("API Error", "Response Code: " + response.code() + " Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API Error", "Error reading errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e("fetchGames", "API Call Failed: " + t.toString());
                games.setValue(null);
            }
        });
        return games;
    }

    // gets details for a game with a specified id
    public LiveData<GameDetail> fetchGameById(int gameId) {
        final MutableLiveData<GameDetail> gameDetail = new MutableLiveData<>();
        api.getGameById(gameId).enqueue(new Callback<GameDetail>() {
            @Override
            public void onResponse(@NonNull Call<GameDetail> call, @NonNull Response<GameDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameDetail.setValue(response.body());
                } else {
                    gameDetail.setValue(null);
                    try {
                        Log.e("API Error", "Failed to fetch game details - Response Code: " + response.code() + " Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API Error", "Error reading errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GameDetail> call, @NonNull Throwable t) {
                Log.e("fetchGameDetails", "API Call Failed: " + t.toString());
                gameDetail.setValue(null);
            }
        });
        return gameDetail;
    }

    // searches for a game by its name
    public LiveData<List<GameDetail>> searchGamesByName(String query) {
        final MutableLiveData<List<GameDetail>> games = new MutableLiveData<>();
        api.searchGamesByName(query).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    games.setValue(response.body().getResults());
                } else {
                    games.setValue(null);
                    Log.e("API Error", "Failed to search games - Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.e("SearchGames", "API Call Failed: " + t.getMessage());
                games.setValue(null);
            }
        });
        return games;
    }


}