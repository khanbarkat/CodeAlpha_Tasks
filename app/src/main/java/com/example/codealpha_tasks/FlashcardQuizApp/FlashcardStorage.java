package com.example.codealpha_tasks.FlashcardQuizApp;

import java.util.ArrayList;

public class FlashcardStorage {
    private static FlashcardStorage instance = null;
    private ArrayList<FlashCard> flashcards;

    private FlashcardStorage() {
        flashcards = new ArrayList<>();
    }

    public static FlashcardStorage getInstance() {
        if (instance == null) {
            instance = new FlashcardStorage();
        }
        return instance;
    }

    public ArrayList<FlashCard> getFlashcards() {
        return flashcards;
    }
}
