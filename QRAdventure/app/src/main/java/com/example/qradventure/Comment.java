package com.example.qradventure;

public class Comment {
    private Account author;
    private String text;

    public Comment(Account author, String text) {
        this.author = author;
        this.text = text;
    }
}
