package com.example.okutech.imageproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /*
    This project is for image operation like ->
    1- Save image in a gallery on a folder.
    2- Save image in a private mode.
    3- Delete image from gallery after transfer image in gallery.
    4- Compress image.
    5- Download image from url and save it in a gallery folder or private folder.
    5- By using surfaceView make our own camera - this will done letter.
    * */


    private int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageHolder;
    private File imageFilePath;
    private ImageOperationUtill imageOperationUtill = new ImageOperationUtill(this);
    private Long startScreenTime;
    private File folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageHolder = (ImageView) findViewById(R.id.captured_photo);
        Button capturedImageButton = (Button) findViewById(R.id.photo_button);

        capturedImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScreenTime = Calendar.getInstance().getTimeInMillis();
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUrlForOutput());
                startActivityForResult(photoCaptureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    private Uri getFileUrlForOutput() {
        folder = new File(Environment.getExternalStorageDirectory(), "AbhilashImage");// make folder with AbhilashImage
        if (!folder.mkdir()) {// mkdir is used to create directory in an external directory.
            if (!folder.exists()) {
                Snackbar.make(getCurrentFocus(), "Unable to create folder", Snackbar.LENGTH_SHORT).show();
            }
        }
        imageFilePath = new File(folder, String.valueOf(System.currentTimeMillis()) + ".jpg");
        return Uri.fromFile(imageFilePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            imageHolder.setImageBitmap((Bitmap) data.getExtras().get("data"));
            new changeImageRotationAndCompress().execute();
        }
    }

    /*
    * This class is used for
    * 1- Rotate image rotation.
    * 2- Transfer file to internal.
    * 3- Compress file.
    * */

    class changeImageRotationAndCompress extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            imageOperationUtill.rotateImageOrientation("");
            //then transfer file
            File transferredFile = transferFile(imageFilePath);

            if (transferredFile != null) {
                //now compress the image
                imageOperationUtill.compressImage(transferredFile);
                return transferredFile.getName();
            } else {
                return "";
            }
        }
    }

    public File transferFile(File file) {
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            File transferredFileTo = new File(file.getName());// have the original name as the name in internal memory
            inChannel = new FileInputStream(file).getChannel();
            fos = openFileOutput(transferredFileTo.getName(), MODE_PRIVATE);
            outChannel = fos.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            return transferredFileTo;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null && fos != null) {
                    inChannel.close();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //ultimately delete the file from gallery
                deleteLatestFile();
                deleteFileFromTempFolder(file);
            }
        }
        return null;
    }

    private void deleteLatestFile() {
        String[] projection = {
                BaseColumns._ID, MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_TAKEN};
        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderClause = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC  limit 5 ";// get all the latest 5 images
        c = getContentResolver().query(u, projection, null, null, orderClause);

        if ((c != null) && (c.moveToFirst())) {
            do {
                if (Long.parseLong(c.getString(3)) > startScreenTime) {
                    ContentResolver cr = getContentResolver();
                    cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            BaseColumns._ID + "=" + c.getString(0), null);
                }
            }
            while (c.moveToNext());
        }
    }

    private void deleteFileFromTempFolder(File file) {
        if (folder.isDirectory() && file != null && file.exists()) {
            file.delete();
        }
    }
}
