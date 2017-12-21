package com.example.okutech.imageproject;

import java.io.File;


/**
 * <h1>FileUploadTask</h1>
 * Encapsulates the file, upload status, etc .
 *
 * @author Gufran Khurshid
 * @version 1.0
 * @since 2016-11-26
 */
public class FileUploadTask {
    private File file;// local file location
    private String uploadID;
    private UploadStatus uploadStatus;
    private String uploadServerURL;//media url after upload has been done
    private FileStatus fileStatus;
    private long id = -1;// relates row ID of Table
    private MediaServerURL mediaServerURL;// media serve URL where file needs to be uploaded

    public FileUploadTask() {
    }

    public FileUploadTask(File file) {
        this.setFile(file);
    }


    public String getUploadID() {
        return uploadID;
    }

    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getUploadServerURL() {
        return uploadServerURL;
    }

    public void setUploadServerURL(String uploadServerURL) {
        this.uploadServerURL = uploadServerURL;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public MediaServerURL getMediaServerURL() {
        return mediaServerURL;
    }

    public void setMediaServerURL(MediaServerURL mediaServerURL) {
        this.mediaServerURL = mediaServerURL;
    }

    public enum UploadStatus {
        IDLE, IN_PROGRESS, SUCCESS, ERROR, FAILED, CANCELLED,
    }

    public enum FileStatus {
        EXISTS, DELETED
    }

    public enum MediaServerURL {

        IMAGE_SERVER_URL(ImageProject.getInstance().getString(R.string.media_upload_uri)),
        AUDIO_SERVER_URL(ImageProject.getInstance().getContext().getString(R.string.audio_upload_uri));

        private String serverURL = ImageProject.getInstance().getString(R.string.media_upload_uri);

        MediaServerURL(String serverURL) {
            this.serverURL = serverURL;
        }

        public String getServerURL() {
            return serverURL;
        }
    }
}
