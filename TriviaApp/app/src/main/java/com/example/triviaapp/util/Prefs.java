package com.example.triviaapp.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }
    public void saveHighScore(int score){
        int currentScore = score;
        int lastScore = preferences.getInt("high_score",0);
        if(lastScore < currentScore){
            preferences.edit().putInt("high_score",currentScore).apply();
        }
    }

    public int getHighScore(){
        return preferences.getInt("high_score",0);
    }
}
