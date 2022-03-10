package com.example.qradventure;

/**
 * Represents a comment made by an Account
 */
public class Comment {
    private Account author;
    private String text;

    public Comment(Account author, String text) {
        this.author = author;
        this.text = text;
    }
}
