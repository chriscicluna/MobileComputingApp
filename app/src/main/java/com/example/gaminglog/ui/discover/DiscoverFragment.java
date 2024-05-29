package com.example.gaminglog.ui.discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.gaminglog.GameDetail;
import com.example.gaminglog.GameDetailActivity;
import com.example.gaminglog.GamesAdapter;
import com.example.gaminglog.api.GamesRestRepository;
import com.example.gaminglog.databinding.FragmentDiscoverBinding;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private FragmentDiscoverBinding binding;
    private TextView failedToFetchText;
    private GamesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        failedToFetchText = binding.textFailedToFetch;

        setupRecyclerView();
        setupSearchBar();
        loadData();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("DiscoverFragment", "onViewCreated: View ready.");
        loadData();
    }

    //loads game details, error message if unsuccessful
    private void loadData() {
        GamesRestRepository.getInstance().fetchGames().observe(getViewLifecycleOwner(), games -> {
            if (games != null && !games.isEmpty()) {
                adapter.setGameDetails(games);
                failedToFetchText.setVisibility(View.GONE);
            } else {
                failedToFetchText.setVisibility(View.VISIBLE);
            }
        });
    }

    // load game details activity for selected game
    private void navigateToGameDetail(GameDetail gameDetail) {
        Intent intent = new Intent(getContext(), GameDetailActivity.class);
        intent.putExtra("game_id", gameDetail.getId());
        startActivity(intent);
    }

    private void setupSearchBar() {
        EditText searchBar = binding.searchBar;
        Button clearButton = binding.clearSearchButton;

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchBar.getText().toString());
                clearButton.setVisibility(View.VISIBLE);
                hideKeyboard();
                return true;
            }
            return false;
        });

        // once the clear search button is pressed, the search bar text is removed, the button is hidden and the default games are reloaded
        clearButton.setOnClickListener(v -> {
            searchBar.setText("");
            clearButton.setVisibility(View.GONE);
            loadData();
            hideKeyboard();
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    clearButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // uses API to get a list of games from the search entered
    private void performSearch(String query) {
        Log.d("DiscoverFragment", "Starting search for query: " + query);
        adapter.clearGameDetails();

        LiveData<List<GameDetail>> searchResults = GamesRestRepository.getInstance().searchGamesByName(query);
        searchResults.removeObservers(getViewLifecycleOwner());
        searchResults.observe(getViewLifecycleOwner(), gameDetails -> {
            Log.d("DiscoverFragment", "Search results received: " + gameDetails.size());
            if (gameDetails != null && !gameDetails.isEmpty()) {
                adapter.setGameDetails(gameDetails);
            } else {
                Toast.makeText(getContext(), "No games found or error in fetching games", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void setupRecyclerView() {
        adapter = new GamesAdapter(getContext(), new ArrayList<>(), this::navigateToGameDetail);
        binding.gamesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.gamesRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.i("DiscoverFragment", "onDestroyView: Cleaning up view.");
    }
}
