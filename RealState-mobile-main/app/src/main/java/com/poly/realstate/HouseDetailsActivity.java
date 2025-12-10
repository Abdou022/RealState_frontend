package com.poly.realstate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.poly.realstate.ApiService;
import com.poly.realstate.models.House;
import com.poly.realstate.models.OfferRequest;
import com.poly.realstate.models.OfferResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HouseDetailsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.1.11:8000/";

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHouseDetails);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Views
        ImageView imgHouse = findViewById(R.id.imgHouseDetails);
        TextView txtTitle = findViewById(R.id.txtHouseTitle);
        TextView txtPrice = findViewById(R.id.txtHousePrice);
        TextView txtAddress = findViewById(R.id.txtHouseAddress);
        TextView txtArea = findViewById(R.id.txtHouseArea);
        TextView txtRooms = findViewById(R.id.txtHouseRooms);
        TextView txtDescription = findViewById(R.id.txtHouseDescription);
        Button btnReservation = findViewById(R.id.btnReservation);

        // Get house ID from Intent
        int houseId = getIntent().getIntExtra("id", -1);
        if (houseId == -1) {
            Toast.makeText(this, "House ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get user ID from cache (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if(userId == -1){
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        // Call API to get house details
        apiService.getHouseDetails(houseId).enqueue(new Callback<House>() {
            @Override
            public void onResponse(Call<House> call, Response<House> response) {
                if(response.isSuccessful() && response.body() != null) {
                    House house = response.body();

                    txtTitle.setText(house.getTitle());
                    txtPrice.setText("Prix : " + house.getPrice());
                    txtAddress.setText("Adresse : " + house.getAddress());
                    txtArea.setText("Surface : " + house.getSurface());
                    txtRooms.setText("Chambres : " + house.getRooms());
                    txtDescription.setText(house.getDescription());

                    if(house.getImage() != null && !house.getImage().isEmpty()){
                        Picasso.get()
                                .load(BASE_URL + "uploads/images/" + house.getImage())
                                .placeholder(R.drawable.ic_home)
                                .into(imgHouse);
                    } else {
                        imgHouse.setImageResource(R.drawable.ic_home);
                    }

                } else {
                    Toast.makeText(HouseDetailsActivity.this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<House> call, Throwable t) {
                Toast.makeText(HouseDetailsActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Reservation button click
        btnReservation.setOnClickListener(v -> createOffer(apiService, houseId, userId));
    }

    private void createOffer(ApiService apiService, int houseId, int applicantId){
        OfferRequest request = new OfferRequest(houseId, applicantId);

        apiService.createOffer(request).enqueue(new Callback<OfferResponse>() {
            @Override
            public void onResponse(Call<OfferResponse> call, Response<OfferResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    String message = response.body().getMessage();
                    Toast.makeText(HouseDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HouseDetailsActivity.this, "Erreur lors de la réservation", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OfferResponse> call, Throwable t) {
                Toast.makeText(HouseDetailsActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
