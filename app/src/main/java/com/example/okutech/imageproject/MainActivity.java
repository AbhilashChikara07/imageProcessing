package com.example.okutech.imageproject;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    private ImageView mShowImage;
    private Button mTakePhoto;
    private final int CAMERA_PERMISSION = 1;
    private final int READ_EXTRA_PERMISSION = 2;
    private final int WRITE_EXTRA_PERMISSION = 3;

    private File capturedImageFile = null;
    private Uri capturedImageUri = null;
    private File mFolderPath;

    private File imageFilePath;
    private ImageOperationUtil imageOperationUtil = new ImageOperationUtil(this);
    private Long startScreenTime;
    private File folder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowImage = (ImageView) findViewById(R.id.showImage);
        mTakePhoto = (Button) findViewById(R.id.takePhoto);
        mTakePhoto.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION);
                } else {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTRA_PERMISSION);
                    } else {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    WRITE_EXTRA_PERMISSION);
                        } else {

                            startCameraIntent();

                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTRA_PERMISSION);
            } else {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTRA_PERMISSION);
                } else {

                    startCameraIntent();

                }
            }
        }
        if (requestCode == READ_EXTRA_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTRA_PERMISSION);
            } else {

                startCameraIntent();

            }
        }
    }


    private void startCameraIntent() {
        startScreenTime = Calendar.getInstance().getTimeInMillis();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        setsFilePath(getTempFileString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Glide.with(this).load(capturedImageUri).into(mShowImage);
            new changeImageRotationAndCompress().execute();
        }
    }

    public void setsFilePath(String value) {
        capturedImageFile = null;
        capturedImageUri = null;
        if (!value.equalsIgnoreCase("")) {
            capturedImageFile = new File(value);

        /*
        * This used to get uri.
        * URI used to pass in camera intent.
        * URI also help to save image with unique name at same folder always.
        * */

            capturedImageUri = FileProvider.getUriForFile(
                    this,
                    this.getApplicationContext()
                            .getPackageName() + ".provider", capturedImageFile);

        }
    }

    private String getTempFileString() {
        mFolderPath = new File(Environment.getExternalStorageDirectory(), "FolderName");

        /*
        * mkdirs() used to create folder.
        * hare "FolderName" is a name of folder which can be anything.
        * */

        if (mFolderPath != null) {
            if (!mFolderPath.mkdirs()) {
                if (!mFolderPath.exists()) {
                    Log.d(ImageProject.TAG, "Unable to create BroFirst temporary folder");
                }
            }
        }

        /*
        * Always return a different file path.
        * We always return different file path by using "String.valueOf(System.currentTimeMillis())".
        * ".jpg" is used for image. We can use another extension .png also.
        * */

        return new File(mFolderPath, String.valueOf(System.currentTimeMillis()) + ".jpg").getPath();
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
            imageOperationUtil.rotateImageOrientation("");
            //then transfer file
            File transferredFile = transferFile(imageFilePath);

            if (transferredFile != null) {
                //now compress the image
                imageOperationUtil.compressImage(transferredFile);
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
