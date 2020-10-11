package com.example.triviaapp.model;

public class Questions {
    private String answer;
    private boolean answerTrue;
    private boolean hasanswered=false;

    public boolean isHasanswered() {
        return hasanswered;
    }

    public void setHasanswered(boolean hasanswered) {
        this.hasanswered = hasanswered;
    }

    public Questions() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        this.answerTrue = answerTrue;
    }

    public Questions(String answer, boolean answerTrue) {
        this.answer = answer;
        this.answerTrue = answerTrue;
    }
}
