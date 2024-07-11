/**
 * Assignment 9: Program generates 3 random numbers from 1 to 6 when user clicks
 * button "Roll the Dice", shows accordingly pictures and shows sum of numbers plus bonus 50 or 100.
 * Also there is score that shows sum of all roll dice
 * Option menu was added
 * Scoreboard was added
 *
 * Class: CITC 2376, Spring 2024
 *
 * @author  Olga Lashko
 * @version March 29, 2024
 */
package com.assign7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String PLAYER_LIST_KEY = "player_list_key";
    private static final String DICE_KEY = "dice_key";
    private static final String TOTAL_SUM_KEY = "total_sum_key";
    public static final String TOTAL_ROLLS_KEY = "total_rolls_key"; // Changed key name

    private ImageView resultNum1ImageView;
    private ImageView resultNum2ImageView;
    private ImageView resultNum3ImageView;
    private TextView scoreEnhancerTextView;
    private TextView scoreThisRollNumTextView;
    private TextView totalNumTextView;
    private CheckBox doubleCheckBox;
    private CheckBox tripleCheckBox;
    private TextView nameEditText;

    private int numDoubles;
    private int numTriples;

    private Dice dice;
    private SharedPreferences sharedPreferences;

    private Map<String, Player> userScores;

    public static boolean isDoubleChecked = true;
    public static boolean isTripleChecked = true;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        resultNum1ImageView = findViewById(R.id.resultNum1ImageView);
        resultNum2ImageView = findViewById(R.id.resultNum2ImageView);
        resultNum3ImageView = findViewById(R.id.resultNum3ImageView);
        scoreEnhancerTextView = findViewById(R.id.scoreEnhancerTextView);
        scoreThisRollNumTextView = findViewById(R.id.scoreThisRollNumTextView);
        totalNumTextView = findViewById(R.id.totalNumTextView);
        doubleCheckBox = findViewById(R.id.doubleCheckBox);
        tripleCheckBox = findViewById(R.id.tripleCheckBox);
        nameEditText = findViewById(R.id.editTextName); // Initialize nameEditText

        // Inflate the checkbox_options layout in a hidden manner
        LayoutInflater inflater = LayoutInflater.from(this);
        View hiddenLayout = inflater.inflate(R.layout.dialog_options, null, false);

        // Find the CheckBox views in the inflated layout
        doubleCheckBox = hiddenLayout.findViewById(R.id.doubleCheckBox);
        tripleCheckBox = hiddenLayout.findViewById(R.id.tripleCheckBox);

        // Set a TextWatcher to editTextName to detect changes in text
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }
            @Override
            public void afterTextChanged(Editable s) {
                String userName = s.toString().trim();
                if (!userName.isEmpty()) {
                    dice = new Dice(); // Create a new Dice object only if username is not empty
                }
            }
        });

        if (savedInstanceState != null) {
            dice = savedInstanceState.getParcelable(DICE_KEY);
            isDoubleChecked = savedInstanceState.getBoolean("isDoubleChecked", true);
            isTripleChecked = savedInstanceState.getBoolean("isTripleChecked", true);
        } else {
            dice = new Dice();
            sharedPreferences = getSharedPreferences("Stats", MODE_PRIVATE);
            userScores = new HashMap<String, Player>();
        }
        updateUI();

        sharedPreferences = getSharedPreferences("Stats", MODE_PRIVATE);
        userScores = new HashMap<String, Player>();
        updateTotalScore();
    }

    // Method to update total score and other stats from SharedPreferences
    private void updateTotalScore() {
        // Retrieve player data from SharedPreferences
        String playerData = sharedPreferences.getString(PLAYER_LIST_KEY, "");
        if (!playerData.isEmpty()) {
            // Split the string to extract player information
            String[] players = playerData.split(";");
            for (String player : players) {
                String[] playerInfo = player.split(",");
                if (playerInfo.length >= 4) {
                    String name = playerInfo[0];
                    int score = Integer.parseInt(playerInfo[1]);
                    int doubles = Integer.parseInt(playerInfo[2]);
                    int triples = Integer.parseInt(playerInfo[3]);

                    // Update the userScores map with player information
                    userScores.put(name, new Player(name, score, doubles, triples));
                }
            }
        }
    }

    public void rollDiceBtnClicked(View view) {
        dice.rollDice(doubleCheckBox.isChecked(), tripleCheckBox.isChecked());
        updateUI();
       // updateDoublesAndTriples();


        String userName = nameEditText.getText().toString().trim();

        // Save the user's name, score, doubles, and triples in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(userName, dice.getTotalScore());
        editor.putInt(userName + "_DOUBLES", dice.getNumDoubles()); // Save doubles
        editor.putInt(userName + "_TRIPLES", dice.getNumTriples()); // Save triples

        editor.putInt(TOTAL_ROLLS_KEY, sharedPreferences.getInt(TOTAL_ROLLS_KEY, 0) + 1);
        editor.apply();


        // Update the score, doubles, and triples for the current player
        Player player = userScores.get(userName);

        // Log the update information
        Log.d("PlayerUpdate", "Player Name: " + userName +
                ", Score: " + dice.getTotalScore() +
                ", Doubles: " + dice.getNumDoubles() +
                ", Triples: " + dice.getNumTriples());

        if (player == null) {
            player = new Player(userName, 0, 0, 0);
        }
        player.setScore(dice.getTotalScore());
        // Update player's doubles and triples
        player.setDoubles(dice.getNumDoubles());
        player.setTriples(dice.getNumTriples());
        userScores.put(userName, player);

        // Update SharedPreferences with the updated player data
        savePlayerData();
    }

    // Method to save player data to SharedPreferences
    private void savePlayerData() {
        StringBuilder playerData = new StringBuilder();
        for (Player player : userScores.values()) {
            playerData.append(player.getName()).append(",");
            playerData.append(player.getScore()).append(",");
            playerData.append(player.getDoubles()).append(",");
            playerData.append(player.getTriples()).append(";");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PLAYER_LIST_KEY, playerData.toString());
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DICE_KEY, dice);
        outState.putInt(TOTAL_SUM_KEY, Integer.parseInt(totalNumTextView.getText().toString()));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dice = savedInstanceState.getParcelable(DICE_KEY);
        updateUI();
    }

    private void updateUI() {
        int roll1 = dice.getRoll1();
        int roll2 = dice.getRoll2();
        int roll3 = dice.getRoll3();
        //set enhancer
        if (isTripleChecked && dice.getRoll1() == dice.getRoll2() && dice.getRoll1() == dice.getRoll3()) {
            scoreEnhancerTextView.setText(R.string.tripleScoreEnhancer);
        } else if (isDoubleChecked && (dice.getRoll1() == dice.getRoll2() || dice.getRoll1() == dice.getRoll3() || dice.getRoll2() == dice.getRoll3())) {
            scoreEnhancerTextView.setText(R.string.doubleScoreEnhancer);
        } else {
            scoreEnhancerTextView.setText(R.string.scoreEnhancer);
        }
        //set score this roll and score
        scoreThisRollNumTextView.setText(String.valueOf(dice.getScoreThisRoll()));
        totalNumTextView.setText(String.valueOf(dice.getTotalScore()));

        // Set images
        setImage(roll1, resultNum1ImageView);
        setImage(roll2, resultNum2ImageView);
        setImage(roll3, resultNum3ImageView);
    }

    private static void setImage(int roll, ImageView imageView){
        switch (roll) {
            case 1:
                imageView.setImageResource(R.drawable.die_1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.die_2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.die_3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.die_4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.die_5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.die_6);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu );
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check the ID of the clicked item and perform the appropriate action
        if (item.getItemId() == R.id.settings) {
            openSettingsDialog(); // Open settings dialog
            return true;
        } else if (item.getItemId() == R.id.about) {
            openAboutDialog(); // Open about dialog
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void openSettingsDialog() {
        DialogOptions dialogOptions = new DialogOptions();
        dialogOptions.show(getSupportFragmentManager(), "settings_dialog");
    }

    private void openAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.show(getSupportFragmentManager(), "about_dialog");
    }

    public void updateDiceSettings(boolean doubleChecked, boolean tripleChecked) {
        // Update the status of checkboxes
        isDoubleChecked = doubleChecked;
        isTripleChecked = tripleChecked;
        updateUI(); // Update UI based on new checkbox status
    }

    public void scoreboardBtnClicked(View view) {
        // Create an intent to start the ScoreboardActivity
        Intent intent = new Intent(this, ScoreBoardActivity.class);

        // Pass the user's name as an extra to the ScoreboardActivity
        String userName = nameEditText.getText().toString().trim(); // Get the user's name from the EditText
        intent.putExtra("USER_NAME", userName);

        // Start the ScoreboardActivity
        startActivity(intent);
    }


}



