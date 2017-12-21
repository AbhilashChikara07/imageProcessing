package com.example.okutech.imageproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

/**
 * <h1>FileUploadManager</h1>
 * Manages all the behaviour needs to schedule and upload the files
 *
 * @author Gufran Khurshid
 * @version 1.0
 * @since 2016-11-26
 */
public class FileUploadManager implements UploadWorker {

    Context context;
    private boolean AUTO_DELETE_FILES_AFTER_SUCCESSFULL_UPLOAD = false;
    private int MAX_RETRIES = 2;

    public FileUploadManager(Context context) {
        this.context = context;
    }

    /*
    * Need to make sure the file extension is proper
    * .mp3 will be redirected to  Audio Server URL http://mdev.broex.net/media/upload/audio
    * else will be directed to Media Server  URL https://mdev.broex.net/media/upload/secimg
    * */
    @Override
    public String enqueueTask(FileUploadTask fileUploadTask) throws FileNotFoundException {
        String uploadID = "";
        boolean isSuitableNetwork = checkSuitableNetwork();
        if (isSuitableNetwork) {
            try {
                Utill.clearFileUploadNotifications(context);//clear old notifications
                MultipartUploadRequest req = new MultipartUploadRequest(context, fileUploadTask.getMediaServerURL().getServerURL())
                        .addFileToUpload(fileUploadTask.getFile().getAbsolutePath(), getUploadKeyBasedOnFileType(fileUploadTask))
                        .setAutoDeleteFilesAfterSuccessfulUpload(AUTO_DELETE_FILES_AFTER_SUCCESSFULL_UPLOAD)
                        .setNotificationConfig(getNotificationConfig(fileUploadTask.getFile().getName()))
                        .setUsesFixedLengthStreamingMode(true)
                        .setMaxRetries(MAX_RETRIES);
                uploadID = req.startUpload();
            } catch (FileNotFoundException exc) {
                throw exc;
            } catch (IllegalArgumentException exc) {
                Log.e(ImageProject.TAG, exc.getMessage());
            } catch (MalformedURLException exc) {
                Log.e(ImageProject.TAG, exc.getMessage());
            } catch (Exception e) {
                Log.e(ImageProject.TAG, e.getMessage());
            }
        }
        return uploadID;
    }


    private boolean checkSuitableNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConnected = false;
        boolean isMobileDataConnected = false;

        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        isWifiConnected = (wifi != null && wifi.isConnectedOrConnecting());

        if (ImageProject.UPLOAD_VIA_MOBILE_NETWORK == true) {
            NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            isMobileDataConnected = (mobile != null && mobile.isConnectedOrConnecting());
        } else {
            isMobileDataConnected = false;
        }

        if (isWifiConnected == true || isMobileDataConnected == true) {
            return true;
        } else {
            return false;
        }
    }

    private String getUploadKeyBasedOnFileType(FileUploadTask fileUploadTask) {
        if (fileUploadTask.getMediaServerURL().getServerURL().equals(FileUploadTask.MediaServerURL.AUDIO_SERVER_URL.getServerURL())) {
            return "audio_upload";
        } else {
            return "image.jpeg";
        }
    }

    protected UploadNotificationConfig getNotificationConfig(String fileName) {
        UploadNotificationConfig config = new UploadNotificationConfig();
        config.setRingToneEnabled(false);
        config.setAutoClearOnSuccess(true);
        config.setClearOnAction(true);
        config.setTitle(fileName + "");
        config.setErrorMessage(context.getString(R.string.file_upload_error));
        //config.setInProgressMessage(context.getString(R.string.file_upload_uploading));
        config.setIcon(R.mipmap.upload_sync);
        return config;
    }

}
