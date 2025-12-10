package com.poly.realstate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.poly.realstate.BannerAdapter;
import com.poly.realstate.adapters.HouseAdapter;
import com.poly.realstate.models.House;
import com.poly.realstate.network.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private ViewPager2 bannerViewPager;
    private Handler bannerHandler = new Handler();
    private int[] bannerImages = {R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3};

    private RecyclerView recyclerViewHouses;
    private RecyclerView recyclerViewBestOffers;
    private TextView viewAllHouses;

    // URL de base pour les images utilisateur
    private static final String IMAGE_BASE_URL = "http://192.168.1.11:8000/uploads/profiles/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ===== Drawer & Toolbar =====
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ===== Header info (load from cache) =====
        View headerView = navigationView.getHeaderView(0);
        ImageView headerImage = headerView.findViewById(R.id.header_profile_image);
        TextView headerName = headerView.findViewById(R.id.header_name);
        TextView headerEmail = headerView.findViewById(R.id.header_email);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String name = prefs.getString("fullName", "Utilisateur");
        String email = prefs.getString("email", "user@email.com");
        String image = prefs.getString("image", null);

        headerName.setText(name);
        headerEmail.setText(email);

        if (image != null && !image.isEmpty()) {
            Picasso.get()
                    .load(IMAGE_BASE_URL + image)
                    .placeholder(R.drawable.ic_person)
                    .into(headerImage);
        } else {
            headerImage.setImageResource(R.drawable.ic_person);
        }

        // ===== Navigation drawer item click =====
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(HomeActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_creehome) {
                startActivity(new Intent(HomeActivity.this, CreationHomeActivity.class));
            } else if (id == R.id.nav_demandes) {
            startActivity(new Intent(HomeActivity.this, OfferSentActivity.class));
            } else if (id == R.id.nav_offres) {
                startActivity(new Intent(HomeActivity.this, ReceivedOffersActivity.class));
            } else if (id == R.id.nav_signout) {
                startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // ===== Banner ViewPager2 =====
        bannerViewPager = findViewById(R.id.bannerViewPager);
        BannerAdapter bannerAdapter = new BannerAdapter(this, bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);
        startBannerAutoScroll();

        // ===== RecyclerViews =====
        recyclerViewHouses = findViewById(R.id.recyclerViewHouses);
        recyclerViewBestOffers = findViewById(R.id.recyclerViewBestOffers);
        viewAllHouses = findViewById(R.id.textViewViewAllHouses);

        // Charger les données
        loadHouses();
        loadBestOffers();

        // View All Houses click
        viewAllHouses.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, AllHousesActivity.class)));
    }

    // ===== Charger toutes les maisons =====
    private void loadHouses() {
        RetrofitClient.getInstance().getApi().getAllHouses().enqueue(new Callback<List<House>>() {
            @Override
            public void onResponse(Call<List<House>> call, Response<List<House>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<House> houses = response.body();
                    HouseAdapter adapter = new HouseAdapter(HomeActivity.this, houses);
                    recyclerViewHouses.setLayoutManager(new LinearLayoutManager(HomeActivity.this,
                            LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewHouses.setAdapter(adapter);
                } else {
                    Toast.makeText(HomeActivity.this, "Erreur lors du chargement des maisons", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<House>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===== Charger les Best Offers =====
    private void loadBestOffers() {
        RetrofitClient.getInstance().getApi().getBestOffers().enqueue(new Callback<List<House>>() {
            @Override
            public void onResponse(Call<List<House>> call, Response<List<House>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<House> bestOffers = response.body();
                    HouseAdapter adapter = new HouseAdapter(HomeActivity.this, bestOffers);
                    recyclerViewBestOffers.setLayoutManager(new LinearLayoutManager(HomeActivity.this,
                            LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewBestOffers.setAdapter(adapter);
                } else {
                    Toast.makeText(HomeActivity.this, "Erreur lors du chargement des best offers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<House>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===== Banner Auto-Scroll =====
    private void startBannerAutoScroll() {
        final Runnable runnable = new Runnable() {
            int current = 0;

            @Override
            public void run() {
                if (current == bannerImages.length) current = 0;
                bannerViewPager.setCurrentItem(current++, true);
                bannerHandler.postDelayed(this, 3000);
            }
        };
        bannerHandler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerHandler.removeCallbacksAndMessages(null);
    }
}
