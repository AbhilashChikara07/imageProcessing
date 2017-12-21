package com.example.okutech.imageproject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import org.json.JSONObject;


/**
 * <h1>UploadStatusBroadcastReceiver</h1>
 * Listen for file upload status and updates the FileUploadDBHelper & SoundUploadDBHelper
 *
 * @author Gufran Khurshid
 * @version 1.0
 * @since 2016-11-26
 */

public class UploadStatusBroadcastReceiver extends UploadServiceBroadcastReceiver {

    static public final String UPLOAD_TASK_BROADCAST_SUCCESS_ACTION = BuildConfig.APPLICATION_ID + ".uploadtask.broadcast.success.action";
    static public final String UPLOAD_TASK_BROADCAST_FAIL_ACTION = BuildConfig.APPLICATION_ID + ".uploadtask.broadcast.fail.action";
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        super.onReceive(context, intent);
    }

    @Override
    public void onProgress(UploadInfo uploadInfo) {
        //  Log.d(TAG, "onProgress " + uploadInfo.getUploadId() + "  " + uploadInfo.getProgressPercent() + " %");
    }

    @Override
    public void onCancelled(UploadInfo uploadInfo) {
        //   Log.d(BroFirst.TAG, uploadInfo.getUploadId() + "   onCancelled");
    }

    @Override
    public void onError(UploadInfo uploadInfo, Exception exception) {
        //   Log.d(BroFirst.TAG, uploadInfo.getUploadId() + "   onError");
        broadcastFailure();
    }

    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
        try {
            String response = new String(serverResponse.getBody());
            //           Log.d(BroFirst.TAG, "Server Response " + response);
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("success")) {
                String fileURL = jsonObject.getString("file_url");
                Log.e("fileURL",""+fileURL);
                if (!TextUtils.isEmpty(fileURL) && !fileURL.equalsIgnoreCase("NULL")) {
                    // dont delete the local file  as we are deleting internal files on END Site
                    // imageUploadManager.deleteLocalFile(uploadInfo.getUploadId());
                } else {
                    broadcastFailure();
                }
            } else {
                broadcastFailure();
            }
        } catch (Exception e) {
            e.printStackTrace();
            broadcastFailure();
        }
    }

    //broadcast the success of file upload
    private void broadcastSuccess(long imageUploadTaskRowId, String url) {
        Intent broadcastSuccessIntent = new Intent();
        broadcastSuccessIntent.setAction(UPLOAD_TASK_BROADCAST_SUCCESS_ACTION);
        broadcastSuccessIntent.putExtra("UPLOAD_TASK_ID", imageUploadTaskRowId);
        broadcastSuccessIntent.putExtra("UPLOAD_URL", url);
        context.sendBroadcast(broadcastSuccessIntent);
    }

    private void broadcastFailure() {
        Intent broadcastFailureIntent = new Intent();
        broadcastFailureIntent.setAction(UPLOAD_TASK_BROADCAST_FAIL_ACTION);
        context.sendBroadcast(broadcastFailureIntent);
    }
}
