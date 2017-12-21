package com.example.okutech.imageproject;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by chikara on 12/19/17.
 */

public class ImageProject extends Application {
    @SuppressLint("StaticFieldLeak")
    private static ImageProject imageProject;
    Context context;
    public static final String TAG = ImageProject.class.getName();
    public static final boolean UPLOAD_VIA_MOBILE_NETWORK = true;
    //Enables upload via mobile network


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

    public static synchronized ImageProject getInstance() {
        if (imageProject != null)
            return imageProject;
        else
            return new ImageProject();
    }

    public ImageProject() {
        super();
    }

    /*
    *  method to get application context
    */
    public Context getContext() {
        return context;
    }

}
