package com.csstj.nurtree.data.model;

import java.util.List;

public class Question {
    private String questionName;
    private List<String> options;
    private String correctAnswer;
    private String note;

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Question(){

    }

    public Question(String questionName, List<String> options, String correctAnswer, String note) {
        this.questionName = questionName;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.note = note;
    }

    public String getQuestionName() {
        return questionName;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getNote() {
        return note;
    }
}


