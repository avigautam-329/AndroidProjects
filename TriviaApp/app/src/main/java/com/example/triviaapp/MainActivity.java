package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.data.AnswerListAsyncResponse;
import com.example.triviaapp.data.QuestionBank;
import com.example.triviaapp.model.Questions;
import com.example.triviaapp.model.Score;
import com.example.triviaapp.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView scoreText;
    private TextView questionText;
    private TextView questionCounterText;
    private TextView maxScore;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private List<Questions> questionList;
    private int scoreCounter = 0;
    private Score score;
    private Prefs preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = new Prefs(MainActivity.this);
        score = new Score();
        scoreText = findViewById(R.id.score_field);
        questionText = findViewById(R.id.show_textfield);
        questionCounterText = findViewById(R.id.counter_field);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.previous_button);
        maxScore = findViewById(R.id.maxscore_field);

        maxScore.setText(MessageFormat.format("Max Score : {0}", preference.getHighScore()));

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Questions> questionArrayList) {

                questionText.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterText.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionArrayList.size()));
                Log.d("Inside", "processFinished: " + questionArrayList);

            }
        });
    }


    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        String toastMessageId = "";
        if (userChooseCorrect == answerIsTrue) {
            addScore();
            fadeView();
            toastMessageId = "True Answer!";
        } else {
            deductscore();
            shakeAnimation();
            toastMessageId = "Wrong Answer";
        }
        Toast.makeText(MainActivity.this, toastMessageId,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionText.setText(question);
        questionCounterText.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size())); // 0 / 234

    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_button:
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.next_button:
                currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
                updateQuestion();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    public void addScore(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreText.setText(MessageFormat.format("Current Score : {0}", String.valueOf(score.getScore())));
    }

    public void deductscore(){
        scoreCounter -=100;
        if(scoreCounter > 0){
            score.setScore(scoreCounter);
            scoreText.setText(MessageFormat.format("Current Score : {0}", String.valueOf(score.getScore())));
        }
        else{
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreText.setText(MessageFormat.format("Current Score : {0}", String.valueOf(score.getScore())));
        }


    }

    @Override
    protected void onPause() {
        preference.saveHighScore(score.getScore());
        super.onPause();

    }
}
