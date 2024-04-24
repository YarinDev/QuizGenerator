package com.example.quiz.model;

public enum PlayerSortField {
    id("id"),
    createdAt("created_at"),
    fullName("fullname");



    public final String fieldName;

    private PlayerSortField(String fieldName) {
        this.fieldName = fieldName;
    }
}
