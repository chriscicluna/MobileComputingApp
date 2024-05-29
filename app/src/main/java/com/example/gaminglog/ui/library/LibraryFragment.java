package com.example.gaminglog.ui.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gaminglog.GameDetail;
import com.example.gaminglog.GameDetailActivity;
import com.example.gaminglog.R;
import com.example.gaminglog.account.UserManager;
import com.example.gaminglog.api.GamesRestRepository;
import com.example.gaminglog.data.UserDbHelper;
import com.example.gaminglog.GamesAdapter;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private GamesAdapter adapter;
    private List<GameDetail> gamesList = new ArrayList<>();
    private UserDbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = view.findViewById(R.id.gamesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new GamesAdapter(getContext(), gamesList, this::navigateToGameDetail);
        recyclerView.setAdapter(adapter);
        dbHelper = new UserDbHelper(getContext());
        loadGames();
        return view;
    }

    private void navigateToGameDetail(GameDetail gameDetail) {
        Intent intent = new Intent(getContext(), GameDetailActivity.class);
        intent.putExtra("game_id", gameDetail.getId());
        startActivity(intent);
    }
    // gets the games from the library of the logged in user
    private void loadGames() {
        int userId = UserManager.getInstance().getUserId();
        List<Integer> gameIds = dbHelper.getGamesInLibrary(userId);

        for (Integer id : gameIds) {
            LiveData<GameDetail> gameLiveData = GamesRestRepository.getInstance().fetchGameById(id);
            gameLiveData.observe(getViewLifecycleOwner(), gameDetail -> {
                if (gameDetail != null) {
                    gamesList.add(gameDetail);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


}
