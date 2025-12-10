package com.poly.realstate;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.realstate.adapters.HouseAdapter;
import com.poly.realstate.models.House;
import com.poly.realstate.network.RetrofitClient;
import com.poly.realstate.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllHousesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HouseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_houses);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAllHouses);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAllHouses);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 colonnes

        // Charger les maisons depuis l'API
        loadHouses();
    }

    private void loadHouses() {
        ApiService api = RetrofitClient.getInstance().getApi();
        Call<List<House>> call = api.getAllHouses(); // méthode à créer dans ApiService

        call.enqueue(new Callback<List<House>>() {
            @Override
            public void onResponse(Call<List<House>> call, Response<List<House>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<House> houses = response.body();
                    adapter = new HouseAdapter(AllHousesActivity.this, houses);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(AllHousesActivity.this, "Erreur: impossible de charger les maisons", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<House>> call, Throwable t) {
                Toast.makeText(AllHousesActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
