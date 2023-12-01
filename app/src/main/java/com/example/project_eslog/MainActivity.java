package com.example.project_eslog;

import android.media.Image;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NdefFormatable;

import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.nfc.Tag;
import android.widget.Toast;
import android.content.Context;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private boolean isTagDetected = false;
    static NfcAdapter s_NfcAdapter;
    private static Intent s_Intent;
    private static PendingIntent s_PendingIntent;
    private static IntentFilter s_IntentFilter;
    private static final String[][] s_TechList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    MifareUltralight.class.getName(),
                    Ndef.class.getName(),
                    NdefFormatable.class.getName()
            }
    };

    public static Tag s_MyTag;
    private DrawerLayout drawer;

    static TextView header;
    static TextView idView;
    static TextView lastView;
    static TextView s_NfcContent;
    static ProgressBar progressBar;
    static CardView middleLogo;
    static ImageView bottomLogo;
    static CardView middleLogo_big;
    static NavigationView navView;
    static Toolbar toolbar;



    static public byte[] s_OpCode;
    static Toast s_Toast;
    static Context s_Context;

    private Handler handler = new Handler(Looper.getMainLooper());
    public int intervalInMiliseconds;
    private int CurrentProgress=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2 sec repeat prototype
        /*ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            runOnUiThread(() -> {
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String formattedTime = dateFormat.format(currentDate);
            });
        }, 10, 2, TimeUnit.SECONDS);*/

        //  TOOLBAR
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  DRAWER
        drawer = findViewById(R.id.drawer_layout);


        // NAVIGATION VIEW
        navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_home);
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
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        //  WIDGETS
        header = (TextView) findViewById(R.id.header);
        s_NfcContent = (TextView) findViewById(R.id.nfc_content);
        idView = (TextView) findViewById(R.id.idView);
        lastView = (TextView) findViewById(R.id.lastView);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        middleLogo = (CardView) findViewById(R.id.middleLogo);
        middleLogo_big = (CardView) findViewById(R.id.middleLogo_big);
        bottomLogo = (ImageView) findViewById(R.id.bottom_logo);

        //  INTENT
        s_Intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        s_PendingIntent = PendingIntent.getActivity(this, 0, s_Intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        s_IntentFilter = new IntentFilter();
        s_IntentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        s_IntentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        s_IntentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);

        //  VARIABLES
        s_Context = this;
        intervalInMiliseconds = SettingsActivity.s_timer_setting;
        s_Toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        s_OpCode = new byte[] {(byte) 0, (byte) 0};

        //  OTHERS
        try {
            detectTag(getIntent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //startNfcReadLoop();
        findViewById(R.id.test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String formattedTime = dateFormat.format(currentDate);

                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                    s_MyTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    if (s_MyTag != null) {
                        // Tutaj możesz przetwarzać odczytane dane z tagu NFC
                        NfcComm.NfcComm_ReadNDEF();
                    } else {
                        showText("Brak tagu NFC" + formattedTime);
                    }
                } else {
                    showText("Przytrzymaj telefon blisko tagu NFC i spróbuj ponownie" + formattedTime);
                }
            }
        });
    }

    // NAVIGATION
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    // FUNCTIONS
    @Override
    protected void onResume()
    {
        super.onResume();
        s_NfcAdapter = NfcAdapter.getDefaultAdapter(this);
        s_NfcAdapter.enableForegroundDispatch(this, s_PendingIntent, new IntentFilter[]{s_IntentFilter}, s_TechList);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        NfcAdapter.getDefaultAdapter(this);
        s_NfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            detectTag(intent);
        } catch (IOException e) {
            showException(e);
        }
    }

    private void detectTag(Intent intent) throws IOException  {
        s_MyTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //showText("detect Tag");
        if (s_MyTag != null) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    runOnUiThread(() -> {
                        CurrentProgress = CurrentProgress + 1;
                        progressBar.setProgress(CurrentProgress);
                        progressBar.setMax(100);
                        if (progressBar.getProgress() == 100)  {
                            //  SET INVISIBLE
                            header.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            middleLogo.setVisibility(View.INVISIBLE);
                            bottomLogo.setVisibility(View.INVISIBLE);

                            //  SET VISIBLE
                            middleLogo_big.setVisibility(View.VISIBLE);
                            s_NfcContent.setVisibility(View.VISIBLE);
                            idView.setVisibility(View.VISIBLE);
                            lastView.setVisibility(View.VISIBLE);

                            //  DRAWER & TOOLBAR
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
                            drawer.addDrawerListener(toggle);
                            toggle.syncState();



                            /*setContentView(R.layout.activity_main_scanned);
                            initializeVariables();
                            s_NfcContent = (TextView) findViewById(R.id.nfc_content);
                            idView = (TextView) findViewById(R.id.idView);
                            lastView = (TextView) findViewById(R.id.lastView);*/

                            scheduler.shutdown();
                            NfcComm.NfcComm_ReadNDEF();
                        }
                    });
                }, 0, 10, TimeUnit.MILLISECONDS);
            }
            else NfcComm.NfcComm_ReadNDEF();

        }
    }

    private void startNfcReadLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (s_MyTag != null) {
                    NfcComm.NfcComm_ReadNDEF();
                }
                handler.postDelayed(this, intervalInMiliseconds);
            }
        }, intervalInMiliseconds);
    }

    //  SHOW FUNCTIONS
    private void showException(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    protected static void showText(String text) {
        s_Toast.cancel();
        s_Toast.makeText(s_Context, text, Toast.LENGTH_SHORT).show();
    }
}