package com.example.gymapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class homepage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    profile profile = new profile();
    tracking tracking = new tracking();
    setting setting = new setting();
    home home = new home();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        bottomNavigationView = findViewById(R.id.bot_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, home).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, home).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, profile).commit();
                        return true;
                    case R.id.track:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, tracking).commit();
                        return true;
                    case R.id.setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, setting).commit();
                        return true;
                }
                return false;
            }
        });

    }
}