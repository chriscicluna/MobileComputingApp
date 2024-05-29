package com.example.gaminglog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {
    List<GameDetail> gameDetails;
    private List<GameDetail> filteredGameDetails;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GameDetail game);
    }


    public GamesAdapter(Context context, List<GameDetail> gameDetails, OnItemClickListener listener) {
        this.context = context;
        this.gameDetails = gameDetails;
        this.filteredGameDetails = new ArrayList<>(gameDetails);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameDetail game = gameDetails.get(position);
        holder.textView.setText(game.getName());
        Glide.with(context)
                .load(game.getBackgroundImage())
                .centerCrop()
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(game);
            }
        });
    }

    public void setGameDetails(List<GameDetail> gameDetails) {
        this.gameDetails.clear();
        this.gameDetails.addAll(gameDetails);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return gameDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gameImageView);
            textView = itemView.findViewById(R.id.gameNameTextView);
        }
    }

    public void clearGameDetails() {
        if (gameDetails != null) {
            gameDetails.clear();
            notifyDataSetChanged();
        }
    }
}
