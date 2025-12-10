package com.poly.realstate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.realstate.adapters.OfferSentAdapter;
import com.poly.realstate.models.OfferSent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OfferSentActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.1.11:8000/";
    private RecyclerView recyclerViewOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_sent);

        Toolbar toolbar = findViewById(R.id.toolbarOfferSent);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewOffers = findViewById(R.id.recyclerViewOffers);
        recyclerViewOffers.setLayoutManager(new LinearLayoutManager(this));

        // Récupérer userId depuis SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if(userId == -1){
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        // Appeler l'API
        apiService.getSentOffers(userId).enqueue(new Callback<List<OfferSent>>() {
            @Override
            public void onResponse(Call<List<OfferSent>> call, Response<List<OfferSent>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<OfferSent> offers = response.body();
                    OfferSentAdapter adapter = new OfferSentAdapter(OfferSentActivity.this, offers);
                    recyclerViewOffers.setAdapter(adapter);
                } else {
                    Toast.makeText(OfferSentActivity.this, "Aucune offre envoyée", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OfferSent>> call, Throwable t) {
                Toast.makeText(OfferSentActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
