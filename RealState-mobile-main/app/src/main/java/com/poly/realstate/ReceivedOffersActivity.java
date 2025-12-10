package com.poly.realstate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.realstate.adapters.OffersReceivedAdapter;
import com.poly.realstate.models.OfferReceivedItem;
import com.poly.realstate.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceivedOffersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    OffersReceivedAdapter adapter;
    ApiService apiService;
    int ownerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_offers);

        recyclerView = findViewById(R.id.receivedOffersRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getInstance().getApi();

        // Récupérer ownerId depuis SharedPreferences
        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        ownerId = sp.getInt("userId", -1);

        if (ownerId == -1) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        loadReceivedOffers();
    }

    private void loadReceivedOffers() {
        Call<List<OfferReceivedItem>> call = apiService.getReceivedOffers(ownerId);
        call.enqueue(new Callback<List<OfferReceivedItem>>() {
            @Override
            public void onResponse(Call<List<OfferReceivedItem>> call, Response<List<OfferReceivedItem>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ReceivedOffersActivity.this, "Erreur serveur", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<OfferReceivedItem> offers = response.body();
                adapter = new OffersReceivedAdapter(offers);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<OfferReceivedItem>> call, Throwable t) {
                Toast.makeText(ReceivedOffersActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
