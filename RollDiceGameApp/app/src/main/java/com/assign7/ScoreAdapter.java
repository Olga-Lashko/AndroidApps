package com.assign7;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private List<Player> playerList;

    public ScoreAdapter(List<Player> playerList) {
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.playerNameTextView.setText(player.getName());
        holder.scoreTextView.setText("Score: " + player.getScore());
        holder.doublesTextView.setText("Doubles: " + player.getDoubles());
        holder.triplesTextView.setText("Triples: " + player.getTriples());
        Log.d("Adapter", player.getName() + " Num Doubles: " + player.getDoubles());
    }


    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        TextView scoreTextView;
        TextView doublesTextView;
        TextView triplesTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
            doublesTextView = itemView.findViewById(R.id.doublesTextView);
            triplesTextView = itemView.findViewById(R.id.triplesTextView);
        }
    }
}
