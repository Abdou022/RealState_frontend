package com.poly.realstate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // ✅ Full screen (hide status bar & nav bar)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        // ✅ Delayed redirect (NO FIREBASE)
        new Handler().postDelayed(() -> {

            // 👉 ALWAYS go to SignInActivity (or HomeActivity if you want)
            startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));

            finish(); // close splash screen

        }, SPLASH_DELAY);
    }
}
