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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    static int s_timer_setting = 2000;
    private static String[] s_timer_elements = {"2 seconds", "5 seconds", "10 seconds", "30 seconds", "60 seconds"};
    private DrawerLayout drawer;
    static Toast s_Toast;
    static Context s_Context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        navView.setCheckedItem(R.id.nav_settings);

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

        //  TIMER SPINNER
        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, s_timer_elements);
        spinner.setAdapter(adapter);
        if (s_timer_setting == 2000) spinner.setSelection(0);
        else if (s_timer_setting == 5000) spinner.setSelection(1);
        else if (s_timer_setting == 10000) spinner.setSelection(2);
        else if (s_timer_setting == 30000) spinner.setSelection(3);
        else spinner.setSelection(4);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,  int id, long position) {

                switch((int)position)
                {
                    case 0: s_timer_setting = 2000;
                        break;
                    case 1: s_timer_setting = 5000;
                        break;
                    case 2: s_timer_setting = 10000;
                        break;
                    case 3: s_timer_setting = 30000;
                        break;
                    case 4: s_timer_setting = 60000;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });


        //  VARIABLES
        s_Context = this;

    }
}