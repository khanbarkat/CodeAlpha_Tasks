package com.example.codealpha_tasks.RandomQuotesGeneratorApp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.codealpha_tasks.R;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;

public class RandomQuoteGenerator extends AppCompatActivity {

    private TextView tvQuote;
    private Button btnGenerateQuote, btnShareQuote;
    private String currentQuote;
    ProgressBar progressBar;

    // List of quotes
//    private String[] quotes = {
//            "The only way to do great work is to love what you do. – Steve Jobs",
//            "Success is not how high you have climbed, but how you make a positive difference to the world. – Roy T. Bennett",
//            "Your time is limited, so don’t waste it living someone else’s life. – Steve Jobs",
//            "Life is what happens when you’re busy making other plans. – John Lennon",
//            "The purpose of our lives is to be happy. – Dalai Lama",
//            "Success usually comes to those who are too busy to be looking for it. – Henry David Thoreau"
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_random_quote_generator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvQuote = findViewById(R.id.tvQuote);
        btnGenerateQuote = findViewById(R.id.btnGenerateQuote);
        btnShareQuote = findViewById(R.id.btnShareQuote);


        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchRandomQuote();
                progressBar.setVisibility(View.GONE);
            }
        }, 4000); // Delay of 1 second


        // Generate new quote on button click
        btnGenerateQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                fetchRandomQuote();
            }
        });

        // Share quote on button click
        btnShareQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quoteToShare = tvQuote.getText().toString();
                if (!quoteToShare.isEmpty()) {
                    shareQuote(quoteToShare);
                } else {
                    tvQuote.setText("No quote to share!");
                }
            }
        });
    }


    // Function to share the quote
    private void shareQuote(String quoteToShare) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, quoteToShare);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
//function to fetch quotes
    private void fetchRandomQuote() {
        try {
            QuotesApi quoteApi = RetrofitInstance.getRetrofitInstance().create(QuotesApi.class);

            Call<List<QuotesModel>> call = quoteApi.getRandomQuote();
            call.enqueue(new Callback<List<QuotesModel>>() {

                @Override
                public void onResponse(Call<List<QuotesModel>> call, retrofit2.Response<List<QuotesModel>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        List<QuotesModel> quotes = response.body();
                        if (!quotes.isEmpty()) {
                            QuotesModel randomQuote = quotes.get(0);
                            String quoteText = "\"" + randomQuote.getQ() + "\"\n\n- " + randomQuote.getA();
                            tvQuote.setText(quoteText);

                        }
                    } else {
                        // Handle unsuccessful response
                        tvQuote.setText("Failed to fetch quote");
                    }
                }

                @Override
                public void onFailure(Call<List<QuotesModel>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    tvQuote.setText("Error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            tvQuote.setText("Exception: " + e.getMessage());
        }
    }
}