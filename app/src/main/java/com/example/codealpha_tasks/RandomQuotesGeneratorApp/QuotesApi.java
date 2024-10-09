package com.example.codealpha_tasks.RandomQuotesGeneratorApp;

import retrofit2.Call;

import java.util.List;

import retrofit2.http.GET;

public interface QuotesApi {
    @GET("random")
    Call<List<QuotesModel>> getRandomQuote();
}
