package com.example.project_eslog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class ChartActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    static Toast s_Toast;
    static Context s_Context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        //  TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  DRAWER
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // NAVIGATION VIEW
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_chart);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (!item.isChecked()) {
                    Intent intent = new Intent(s_Context, MainActivity.class);

                    if (item.getItemId() == R.id.nav_home)          intent = new Intent(s_Context, MainActivity.class);
                    else if (item.getItemId() == R.id.nav_chart)    intent = new Intent(s_Context, ChartActivity.class);
                    else if (item.getItemId() == R.id.nav_settings) intent = new Intent(s_Context, SettingsActivity.class);

                    startActivity(intent);
                }
                else drawer.closeDrawer(GravityCompat.START);

                navView.setCheckedItem(item.getItemId());
                return true;
            }
        });

        //  VARIABLES
        s_Context = this;

        //  WIDGETS

    }
}