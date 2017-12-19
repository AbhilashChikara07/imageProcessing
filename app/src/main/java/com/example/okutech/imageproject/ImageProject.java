package com.example.okutech.imageproject;

import android.app.Application;
import android.content.Context;

/**
 * Created by chikara on 12/19/17.
 */

public class ImageProject extends Application {
    ImageProject imageProject;
    Context context;
    public static final String TAG = ImageProject.class.getName();

    public synchronized ImageProject getInstance() {
        if (imageProject != null)
            return imageProject;
        else
            return new ImageProject();
    }

    public ImageProject() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        imageProject = this;
        context = getApplicationContext();
    }

    public static void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

}
