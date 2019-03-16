package com.mohit.mohitgupta.shortcut;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class GetImage extends AppCompatActivity {

    Context context;
    Button b;
    //EditText imageName;
    private ImageButton img;
    //private String name;
    private Uri selectedImage;
    private Bitmap bitmap;
    private AdView mAdView;
    private AdView mAdView2;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GetImage.this, ImageActivity.class);
        finish();
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mohit.mohitgupta.shortcut.R.layout.activity_get_image);
        mAdView = (AdView) findViewById(com.mohit.mohitgupta.shortcut.R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView = (AdView) findViewById(com.mohit.mohitgupta.shortcut.R.id.adView2);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest1);

        //  imageName = (EditText) findViewById(R.id.ImageName);
        //name=imageName.getText().toString().trim();
        b = (Button) findViewById(com.mohit.mohitgupta.shortcut.R.id.SaveButtonClicked);

        context = this;
    }


    public void GetImageIntent(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedImage = data.getData();
            img = (ImageButton) findViewById(com.mohit.mohitgupta.shortcut.R.id.imageSelected);
            img.setImageURI(selectedImage);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                Log.d("App", "Can't get the bitmap");
            }

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = Environment.getExternalStorageDirectory() + "/Shortcut/Images";
                    OutputStream fOut = null;
                    Random r = new Random();
                    Integer counter = r.nextInt(10000);
                    File file = new File(path, "ShortcutIMG " + counter + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    String s="ShortcutIMG " + counter + ".jpg";
                    try {
                        fOut = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // obtaining the Bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    assert fOut != null;
                    try {
                        fOut.flush(); // Not really required
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fOut.close(); // do not forget to close the stream
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),s, s);
                    } catch (FileNotFoundException e) {
                        Log.d("App", "Cant store");
                    }
                    Toast.makeText(GetImage.this, "Image Saved", Toast.LENGTH_SHORT).show();

                }
            });


        }
    }

}




