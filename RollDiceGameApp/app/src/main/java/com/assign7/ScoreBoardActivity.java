package com.assign7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScoreBoardActivity extends AppCompatActivity {

    private Button returnBtn;
    private RecyclerView recyclerView;
    private ScoreAdapter scoreAdapter;
    private TextView thanksTextView;
    private TextView totalRollsTextView; // Add a TextView for total dice rolls


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(ScoreBoardActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish the current activity to remove it from the back stack
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        thanksTextView = findViewById(R.id.thanksTextView);
        totalRollsTextView = findViewById(R.id.totalRollsTextView); // Initialize total rolls TextView

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_NAME")) {
            String userName = intent.getStringExtra("USER_NAME");
            thanksTextView.setText("Thanks for playing, " + userName + " !");
        }

        // Initialize RecyclerView and adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scoreAdapter = new ScoreAdapter(getPlayerList());
        recyclerView.setAdapter(scoreAdapter);

        // Display total dice rolls
        displayTotalDiceRolls();
    }

    // Method to display total dice rolls
    private void displayTotalDiceRolls() {
        SharedPreferences sharedPreferences = getSharedPreferences("Stats", MODE_PRIVATE);
        int totalRolls = sharedPreferences.getInt(MainActivity.TOTAL_ROLLS_KEY, 0);
        totalRollsTextView.setText(totalRolls + " Total Dice Rolls");
    }

    // Method to retrieve player data from SharedPreferences
    private List<Player> getPlayerList() {
        List<Player> playerList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("Stats", MODE_PRIVATE);
        String playerData = sharedPreferences.getString(MainActivity.PLAYER_LIST_KEY, "");
        if (!playerData.isEmpty()) {
            String[] players = playerData.split(";");
            for (String player : players) {
                String[] playerInfo = player.split(",");
                if (playerInfo.length >= 4) {
                    String name = playerInfo[0];
                    int score = Integer.parseInt(playerInfo[1]);

                    // Retrieve doubles and triples from SharedPreferences
                    int doubles = sharedPreferences.getInt(name + "_DOUBLES", 0);
                    int triples = sharedPreferences.getInt(name + "_TRIPLES", 0);

                    if (!name.isEmpty()) {
                        playerList.add(new Player(name, score, doubles, triples));
                    }
                }
            }
        }
        return playerList;
    }


}
