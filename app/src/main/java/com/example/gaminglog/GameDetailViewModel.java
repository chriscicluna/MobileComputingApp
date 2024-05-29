package com.example.gaminglog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gaminglog.GameDetail;
import com.example.gaminglog.api.GamesRestRepository;

public class GameDetailViewModel extends ViewModel {
    private MutableLiveData<GameDetail> gameDetail;
    private GamesRestRepository repository;

    public void init(int gameId) {
        if (gameDetail != null) {
            return;
        }
        repository = GamesRestRepository.getInstance();
        gameDetail = (MutableLiveData<GameDetail>) repository.fetchGameById(gameId);
    }

    public LiveData<GameDetail> getGameDetails() {
        return gameDetail;
    }
}