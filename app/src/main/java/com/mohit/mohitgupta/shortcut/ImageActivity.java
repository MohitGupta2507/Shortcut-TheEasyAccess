package com.mohit.mohitgupta.shortcut;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    public Bitmap bitmap1;
    ImageAdapter myImageAdapter;
    private AdView mAdView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Toast.makeText(ImageActivity.this,"Press Again To Exit",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mohit.mohitgupta.shortcut.R.layout.activity_image);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        GridView gridview = (GridView) findViewById(com.mohit.mohitgupta.shortcut.R.id.gridview);
        myImageAdapter = new ImageAdapter(this);

        gridview.setAdapter(myImageAdapter);

        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath()
                .toString();

        String targetPath = ExternalStorageDirectoryPath + "/Shortcut/Images";

        File targetDirector = new File(targetPath);

        final File[] files = targetDirector.listFiles();
        for (File file : files){
            myImageAdapter.add(file.getAbsolutePath());

        }
        if(myImageAdapter.getCount()==0){
            Toast.makeText(ImageActivity.this,"Click On Plus button to Add Item",Toast.LENGTH_SHORT).show();
        }
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                adapterView.getChildAt(i).setBackgroundResource(R.drawable.blue);
                adapterView.getChildAt(i).setSelected(true);
               // fff = file[i];
                class MyActionModeCallback implements  android.view.ActionMode.Callback {


                    @Override
                    public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.context,menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                files[i].delete();
                                Intent intent = new Intent(ImageActivity.this, ImageActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.share:
                                Uri u=Uri.fromFile(files[i]);
                                Intent share=new Intent(Intent.ACTION_SEND);
                                share.setType("image/*")
                                        .putExtra(Intent.EXTRA_STREAM,u)
                                        .putExtra(Intent.EXTRA_SUBJECT,"Sharing...");
                                startActivity(share);
                                /*Intent intent1 = new Intent(VideosActivity.this, VideosActivity.class);
                                startActivity(intent1);
                                finish();*/

                                break;

                        }
                        return false;

                    }

                    @Override
                    public void onDestroyActionMode(android.view.ActionMode mode) {

                    }
                }
                MyActionModeCallback callback = new MyActionModeCallback();
                ActionMode mActionmode = startActionMode(callback);
                mActionmode.setTitle("Selected");


                //      Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_LONG).show();

                /*file[i].delete();
                Toast.makeText(VideosActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                */return true;
            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(files[i].exists()) {
                        String p = files[i].getAbsolutePath().toString();
                        Log.d("App", p);
                        File F = new File(p);
                        Uri u = Uri.fromFile(F);
                        if (u.toString().contains(".jpg") || u.toString().contains(".JPEG") || u.toString().contains(".jpeg") || u.toString().contains(".JPG") || u.toString().contains(".GIF") || u.toString().contains(".gif")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(u, "image/*");
                            startActivity(intent);
                            finish();
                        } else {
                            String message = "Sorry.....\n Only Images Ending with .jpeg or .gif  are valid here \n Cant Open...";
                            Toast.makeText(ImageActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(com.mohit.mohitgupta.shortcut.R.menu.images,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id== com.mohit.mohitgupta.shortcut.R.id.AddImage)
        {
            Intent intent=new Intent(ImageActivity.this,GetImage.class);
            startActivity(intent);
            finish();
            return true;
        }
        if(id== com.mohit.mohitgupta.shortcut.R.id.Close)
        {
            Intent intent=new Intent(ImageActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return true;
    }

    public class ImageAdapter extends BaseAdapter {

        ArrayList<String> itemList = new ArrayList<String>();
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        void add(String path){
            itemList.add(path);
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            DisplayMetrics displayMetrics=new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width=displayMetrics.widthPixels;
            int height=displayMetrics.heightPixels;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(width/2,width/2));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 220, 220);
            imageView.setImageBitmap(bm);
            return imageView;

        }

        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

            Bitmap bm = null;
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(path, options);
            return bm;


        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float)height / (float)reqHeight);
                } else {
                    inSampleSize = Math.round((float)width / (float)reqWidth);
                }
            }

            return inSampleSize;
        }

    }
}

