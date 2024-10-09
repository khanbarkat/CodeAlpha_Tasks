package com.example.codealpha_tasks.FlashcardQuizApp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codealpha_tasks.R;

import java.util.ArrayList;

public class AddFlashcard extends AppCompatActivity {
    private ArrayList<FlashCard> flashcards = FlashcardStorage.getInstance().getFlashcards();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_flashcard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView arrow=findViewById(R.id.arrow);
        TextView title=findViewById(R.id.app);
        title.setText("Add Flashcard");
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etQuestion = findViewById(R.id.question);
                EditText etAnswer = findViewById(R.id.answer);

                String question = etQuestion.getText().toString();
                String answer = etAnswer.getText().toString();

                if (!question.isEmpty() && !answer.isEmpty()) {
                    flashcards.add(new FlashCard(question, answer));
                    Toast.makeText(AddFlashcard.this, "Flashcard saved!", Toast.LENGTH_SHORT).show();
                    etQuestion.setText("");
                    etAnswer.setText("");
                } else {
                    Toast.makeText(AddFlashcard.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}