package com.mohit.mohitgupta.shortcut;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity {

    private String path;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 123;
    public File fff;
    public String path2, path22;
    private AdView mAdView;
    private android.view.ActionMode mActionmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mohit.mohitgupta.shortcut.R.layout.activity_videos);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ActionBar a=getSupportActionBar();
        a.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.video)));
        mAdView = (AdView) findViewById(com.mohit.mohitgupta.shortcut.R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.mohit.mohitgupta.shortcut.R.menu.images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == com.mohit.mohitgupta.shortcut.R.id.AddImage) {
            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

            intent1.setType("video/*");
            startActivityForResult(Intent.createChooser(intent1, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
            return true;
        }
        if (id == com.mohit.mohitgupta.shortcut.R.id.Close) {
            Intent intent = new Intent(VideosActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                    {
                        Uri selectedImageUri = data.getData();
                        path = getPath(this, selectedImageUri);
                        //    Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
                        path22 = path.substring(path.lastIndexOf('/') + 1);
                        //Toast.makeText(getApplicationContext(), path22, Toast.LENGTH_LONG).show();
                        moveFile(path, path22);

                    }
                }
            }
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /*    private void copy(InputStream in) throws IOException {
        try {
            String p;
            if(!path22.contains("."))
            {
                p=path22+".mp4";
            }
            else
            {
                p=path22;
            }

            OutputStream out = new FileOutputStream(android.os.Environment.getExternalStorageDirectory().getPath()+"Shortcut/Videos/"+p);

            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (IOException e) {
                 Log.d("TAAAAAAG","KKKKKK");
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }*/


/*    private void copy(File source, File destination) throws IOException {

        FileChannel in = new FileInputStream(source).getChannel();
        FileChannel out = new FileOutputStream(destination).getChannel();

        try {
            in.transferTo(0, in.size(), out);
        } catch(Exception e) {
            // post to log
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
        File f=new File(path);
        f.delete();
    }

  /*  public String getRealPathFromURI (Uri uri) {
        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

   /* void savefile(URI sourceuri)
    {
        String sourceFilename= sourceuri.getPath();
        String Filename=sourceuri.getLastPathSegment();

        String p;
        if(!Filename.contains("."))
        {
            p=Filename+".mp4";
        }
        else
        {
            p=Filename;
        }

        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()+"Shortcut/Videos/"+p;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File f=new File(sourceFilename);
        f.delete();
    }

    /*public void getRealPathFromURI(Uri contentUri) {
        if (isExternalStorageDocument(contentUri)) {
            final String docId = DocumentsContract.getDocumentId(contentUri);
            final String[] split = docId.split(":");
            final String type = split[0];

            String extDirectory = Environment.getExternalStorageDirectory().toString();

            if ("primary".equalsIgnoreCase(type)) { //Primary storage
                return extDirectory + "/" + split[1];
            }

            // handle non-primary volumes
            else{
                extDirectory = extDirectory.replace(
                        "emulated/0",
                        type);
                return extDirectory + "/" + split[1];
            }
        }
    }*/


    private void moveFile(String inputPath, String inputFile) {

        InputStream in = null;
        OutputStream out = null;
        String p;
        if (!inputFile.contains(".mp4")) {
            p = inputFile + ".mp4";
        } else {
            p = inputFile;
        }

        String outputPath = Environment.getExternalStorageDirectory().toString() + "/Shortcut/Videos/";
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + p);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        File f = new File(inputPath);
        f.delete();

    }

/*    private void SaveImage(String p) {
        // your sd card
        String sdCard = Environment.getExternalStorageDirectory().toString() + "/Shortcut/Videos";

        // the file to be moved or copied
        File sourceLocation = new File(p);

        // make sure your target location folder exists!

        File targetLocation = new File(sdCard + "/" + path22);

        // just to take note of the location sources
        Log.v("App", "sourceLocation: " + sourceLocation);
        Log.v("App", "targetLocation: " + targetLocation);

        try {

            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;

            // moving the file to another directory
            if (actionChoice == 1) {

                if (sourceLocation.renameTo(targetLocation)) {
                    Log.v("App", "Move file successful.");
                } else {
                    Log.v("App", "Move file failed.");
                }

            }

            // we will copy the file
            else {

                // make sure the target file exists

                if (sourceLocation.exists()) {

                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                    Log.d("App", "Copy file successful.");
                    Toast.makeText(this, "Saved Video", Toast.LENGTH_SHORT).show();
                    sourceLocation.delete();
                } else {
                    Log.d("App", "Copy file failed. Source file missing.");
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    //Creating Video Library..


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(VideosActivity.this, "Press Again to Exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<String> Document_list = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter;
        ListView l;
        l = (ListView) findViewById(com.mohit.mohitgupta.shortcut.R.id.List_view);

        String path = Environment.getExternalStorageDirectory().toString() + "/Shortcut/Videos";
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        final File file[] = f.listFiles();
        Log.d("Files", "Size: " + file.length);
        for (int i = 0; i < file.length; i++) {
            //here populate your listview
            Document_list.add(file[i].getName());
            Log.d("Files", "FileName:" + file[i].getName());
        }
        arrayAdapter =
                new ArrayAdapter<String>(this, com.mohit.mohitgupta.shortcut.R.layout.sample_list_view_video, com.mohit.mohitgupta.shortcut.R.id.VideoName, Document_list);
        arrayAdapter.notifyDataSetChanged();
        l.setAdapter(arrayAdapter);
        if (arrayAdapter.getCount() == 0) {
            Toast.makeText(VideosActivity.this, "Click on Plus icon to Add Videos", Toast.LENGTH_SHORT).show();
        }
        l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                adapterView.getChildAt(i).setBackgroundResource(R.drawable.blue);
                adapterView.getChildAt(i).setSelected(true);
                fff = file[i];

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
                                                file[i].delete();
                                                Intent intent = new Intent(VideosActivity.this, VideosActivity.class);
                                                startActivity(intent);
                                                finish();
                                                break;
                            case R.id.share:
                                                Uri u=Uri.fromFile(file[i]);
                                                Intent share=new Intent(Intent.ACTION_SEND);
                                                share.setType("video/*")
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
                mActionmode = startActionMode(callback);
                mActionmode.setTitle("Selected");


                //      Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_LONG).show();

                /*file[i].delete();
                Toast.makeText(VideosActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                */return true;
            }
        });
        l.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getChildAt(position).isSelected()) {
                    Toast.makeText(getApplicationContext(), "Selected", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /**/
                if (file[i].exists()) {
                    String p = file[i].getAbsolutePath().toString();
                    Log.d("App", p);
                    File F = new File(p);
                    Uri u = Uri.fromFile(F);
                    if (u.toString().contains(".3gp") || u.toString().contains(".mpeg") || u.toString().contains(".mp4") || u.toString().contains(".avi") || u.toString().contains(".FLV") || u.toString().contains(".WMP")) {
/*                        if(Build.VERSION.SDK_INT>23)
                        {
                            Intent in=new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(this, AUTHORITY, f));

                            in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            in.setType("video/*");
                            startActivity(in);
                        }
                        else {*/
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setDataAndType(u, "video/*");
                        startActivity(intent1);
                        finish();
                        //}
                    } else {
                        String message = "Sorry.....\n Only Videos Ending with .mp4 or.3gp or .avi are valid here \n Cant Open...";
                        Toast.makeText(VideosActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }


    /*class MyActionModeCallback implements ActionMode.Callback
    {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }*/
}