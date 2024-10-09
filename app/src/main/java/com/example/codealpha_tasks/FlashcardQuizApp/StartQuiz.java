package com.example.codealpha_tasks.FlashcardQuizApp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codealpha_tasks.R;

import java.util.ArrayList;

public class StartQuiz extends AppCompatActivity {

    private ArrayList<FlashCard> flashcards = FlashcardStorage.getInstance().getFlashcards();
    private int currentIndex = 0;
    private TextView question;
    private EditText etAnswerInput;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int totalQuestions = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView arrow=findViewById(R.id.arrow);
        TextView title=findViewById(R.id.app);
        title.setText("Quiz Started");
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        question = findViewById(R.id.tvQuestion);
        etAnswerInput = findViewById(R.id.etAnswerInput);
        totalQuestions = flashcards.size();
        updateQuestion();

        findViewById(R.id.btnSubmitAnswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });


    }

    private void checkAnswer() {
        String userAnswer = etAnswerInput.getText().toString();
        String correctAnswer = flashcards.get(currentIndex).getAnswer();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            correctAnswers++;
            //Toast.makeText(StartQuiz.this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            wrongAnswers++;
            //Toast.makeText(StartQuiz.this, "Incorrect! The correct answer is: " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        if (currentIndex == totalQuestions - 1) {
            showQuizCompletionDialog();
        } else {
            currentIndex = (currentIndex + 1) % flashcards.size();
            updateQuestion();
        }
    }
    private void updateQuestion() {
        question.setText(flashcards.get(currentIndex).getQuestion());
        etAnswerInput.setText("");
    }
    private void showQuizCompletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartQuiz.this);
        builder.setTitle("Quiz Completed");
        builder.setMessage("Your Score: " + correctAnswers + "/" + totalQuestions +
                "\nCorrect Answers: " + correctAnswers +
                "\nWrong Answers: " + wrongAnswers);
        builder.setPositiveButton("OK", (dialog, which) -> {
            finish();
        });
        builder.show();
    }
}