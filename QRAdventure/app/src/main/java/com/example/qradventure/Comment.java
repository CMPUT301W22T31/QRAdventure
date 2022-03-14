package com.example.qradventure;

/**
 * Represents a comment made by an Account
 */
public class Comment {
    private String authorUsername;
    private String text;

    /**
     * Constructor
     * @param authorUsername
     *      username of the player who has entered the comment
     * @param text
     *      the content of the comment
     */
    public Comment(String authorUsername, String text) {
        this.authorUsername = authorUsername;
        this.text = text;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getText() {
        return text;
    }
}
