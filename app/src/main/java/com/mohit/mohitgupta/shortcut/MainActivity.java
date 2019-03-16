package com.mohit.mohitgupta.shortcut;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import java.io.File;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    CircleMenu circle;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mohit.mohitgupta.shortcut.R.layout.activity_main);

        requestPermission();
        data();
//        getActionBar().hide();
        ActionBar a=getSupportActionBar();
        a.hide();
        mAdView = (AdView) findViewById(com.mohit.mohitgupta.shortcut.R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        circle = (CircleMenu) findViewById(com.mohit.mohitgupta.shortcut.R.id.CircleMenu);
        circle.setMainMenu(Color.parseColor("#FFFFFF"), com.mohit.mohitgupta.shortcut.R.drawable.ic_add_black_24dp, com.mohit.mohitgupta.shortcut.R.drawable.ic_close_black_24dp);
        circle.addSubMenu(Color.parseColor("#BA68C8"), com.mohit.mohitgupta.shortcut.R.drawable.ic_photo_white_24dp);
        circle.addSubMenu(Color.parseColor("#BA68C8"), com.mohit.mohitgupta.shortcut.R.drawable.ic_receipt_white_24dp);
        circle.addSubMenu(Color.parseColor("#BA68C8"), com.mohit.mohitgupta.shortcut.R.drawable.ic_video_library_white_24dp);
        circle.setOnMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int i) {
                if (i == 0) {
                    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Shortcut/Images");

                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.d("App", "failed to create directory");

                        }
                    }
                    Intent intent1 = new Intent(MainActivity.this, ImageActivity.class);
                    startActivity(intent1);
                }
                if (i == 1) {
                    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Shortcut/Documents");

                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.d("App", "failed to create directory");

                        }
                    }
                    Intent intent1 = new Intent(MainActivity.this, DocActivity.class);
                    startActivity(intent1);
                }
                if (i == 2) {
                    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Shortcut/Videos");
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.d("App", "failed to create directory");

                        }
                    }
                    Intent intent1 = new Intent(MainActivity.this, VideosActivity.class);
                    startActivity(intent1);
                }

            }
        });

    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            Toast.makeText(getApplicationContext(),"Permission Ok",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Permission Ok",Toast.LENGTH_SHORT).show();
        }
    }




    void data() {
        boolean mobileDataEnabled = false; // Assume disabled
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            mobileDataEnabled = (Boolean) m.invoke(cm);
            if (mobileDataEnabled != true) {
                Toast.makeText(MainActivity.this, "Please Switch on Mobile Data For Smooth Flow ", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}