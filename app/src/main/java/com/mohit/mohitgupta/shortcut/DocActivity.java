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
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
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

public class DocActivity extends AppCompatActivity {

    static final int Pick_content = 1;
    Uri uri;
    String path, path2;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mohit.mohitgupta.shortcut.R.layout.activity_doc);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ActionBar a=getSupportActionBar();
        a.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.doc)));
        mAdView = (AdView) findViewById(com.mohit.mohitgupta.shortcut.R.id.adView);
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
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
            intent.setType("application/pdf");
            startActivityForResult(intent, Pick_content);
            return true;
        }
        if (id == com.mohit.mohitgupta.shortcut.R.id.Close) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Pick_content) {
                Uri uri = data.getData();
                path = getPath(this,uri);
                //Toast.makeText(DocActivity.this, path, Toast.LENGTH_LONG).show();
                path2 = uri.getPath().substring(uri.getPath().lastIndexOf('/')+1);
                //  Toast.makeText(getApplicationContext(), path2,Toast.LENGTH_LONG).show();
                moveFile(path,path2);
            }
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                final String[] selectionArgs = new String[] {
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
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
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

   ////Moving File...........

    private void moveFile(String inputPath,String inputFile) {

        InputStream in = null;
        OutputStream out = null;
        String p;
        if(!inputFile.contains(".pdf"))
        {
            p=inputFile+".pdf";
        }
        else
        {
            p=inputFile;
        }

        String outputPath=(android.os.Environment.getExternalStorageDirectory()+ "/Shortcut/Documents/").toString();
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
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


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        File f=new File(inputPath);
        f.delete();

    }



    /* private void copy(File source, File destination) throws IOException {

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

    public String getRealPathFromURI (Uri uri) {
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


/*    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore..Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/

  /*  private void SaveImage(String p) {
        // your sd card
        String sdCard = Environment.getExternalStorageDirectory().toString() + "/Shortcut/Documents";

        // the file to be moved or copied
        File sourceLocation = new File(p);

        // make sure your target location folder exists!
        Random r = new Random();
        Integer counter = r.nextInt(10000);
        File targetLocation = new File(sdCard + "/" + path2);

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
                    Toast.makeText(this, "Saved Document", Toast.LENGTH_SHORT).show();
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
    }*/

   /* private void moveFile(String inputPath, String inputFile) {

        InputStream in = null;
        OutputStream out = null;
        String p;
        if(!inputFile.contains("."))
        {
            p=inputFile+".pdf";
        }
        else
        {
            p=inputFile;
        }
        String outputPath=Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Shortcut/Documents/";
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
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


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

*/

    //Document Library....


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(DocActivity.this, "Press Again to Exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<String> Document_list = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter;
        ListView l;
        l = (ListView) findViewById(com.mohit.mohitgupta.shortcut.R.id.List);

        String path = Environment.getExternalStorageDirectory().toString() + "/Shortcut/Documents";
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
                new ArrayAdapter<String>(this, com.mohit.mohitgupta.shortcut.R.layout.sample_list_view, com.mohit.mohitgupta.shortcut.R.id.DocumentName, Document_list);
        arrayAdapter.notifyDataSetChanged();
        l.setAdapter(arrayAdapter);
        if (arrayAdapter.getCount() == 0) {
            Toast.makeText(DocActivity.this, "Click On Plus button to Add Item", Toast.LENGTH_SHORT).show();
        }
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                adapterView.getChildAt(i).setBackgroundResource(R.drawable.blue);
                adapterView.getChildAt(i).setSelected(true);
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
                                Intent intent = new Intent(DocActivity.this, DocActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.share:
                                Uri u=Uri.fromFile(file[i]);
                                Intent share=new Intent(Intent.ACTION_SEND);
                                share.setType("application/*")
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
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                /**/
                if (file[i].exists()) {
                    String p = file[i].getAbsolutePath().toString();
                    Log.d("App", p);
                    File F = new File(p);
                    Uri u = Uri.fromFile(F);
                    if (u.toString().contains(".pdf")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(u, "application/pdf");
                        startActivity(intent);
                        finish();
                    } else if (u.toString().contains(".txt") || u.toString().contains(".doc") || u.toString().contains(".DOC") || u.toString().contains(".DOCX") || u.toString().contains(".docx")) {
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setDataAndType(u, "text/plain");
                        startActivity(intent1);
                        finish();
                    } else {
                        String message = "Sorry.....\n Only Documents Ending with .pdf or.txt or .doc are valid here \n Cant Open...";
                        Toast.makeText(DocActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }
}
