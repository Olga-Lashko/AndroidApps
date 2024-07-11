package com.assign7;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Random;

public class Dice implements Parcelable {

    private int roll1=1;
    private int roll2=2;
    private int roll3=3;
    private int scoreThisRoll;
    private int totalScore;
    private Random random;
    private String name;
    private int numDoubles; // Track number of doubles
    private int numTriples; // Track number of triples

    public Dice() {
        random = new Random();
    }

    protected Dice(Parcel in) {
        roll1 = in.readInt();
        roll2 = in.readInt();
        roll3 = in.readInt();
        scoreThisRoll = in.readInt();
        totalScore = in.readInt();
        name = in.readString();
        numDoubles = 0;
        numTriples = 0;
    }


    public static final Creator<Dice> CREATOR = new Creator<Dice>() {
        @Override
        public Dice createFromParcel(Parcel in) {
            return new Dice(in);
        }

        @Override
        public Dice[] newArray(int size) {
            return new Dice[size];
        }
    };

    public void rollDice(boolean doubleChecked, boolean tripleChecked) {
        roll1 = random.nextInt(6) + 1;
        roll2 = random.nextInt(6) + 1;
        roll3 = random.nextInt(6) + 1;
        scoreThisRoll = roll1 + roll2 + roll3;

        // Update total sum with the score of this roll
        if (MainActivity.isTripleChecked && roll1 == roll2 && roll1 == roll3) {
            scoreThisRoll += 100;
            numTriples++;
        } else if (MainActivity.isDoubleChecked && (roll1 == roll2 || roll1 == roll3 || roll2 == roll3)) {
            scoreThisRoll += 50;
            numDoubles++;
        }
        totalScore += scoreThisRoll;
    }


    public int getRoll1() {
        return roll1;
    }
    public int getRoll2() {
        return roll2;
    }
    public int getRoll3() {
        return roll3;
    }
    public int getScoreThisRoll() {
        return scoreThisRoll;
    }
    public int getTotalScore() {
        return totalScore;
    }

    public String getName() {
        return name;
    }

    public int getNumDoubles() {
       // Log.d("Dice", "Num Doubles: " + numDoubles);
        return numDoubles;
    }

    public int getNumTriples() {
        return numTriples;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(roll1);
        dest.writeInt(roll2);
        dest.writeInt(roll3);
        dest.writeInt(scoreThisRoll);
        dest.writeInt(totalScore); // Save total sum to parcel
        dest.writeString(name);
        dest.writeInt(numDoubles);
        dest.writeInt(numTriples);
    }


}
