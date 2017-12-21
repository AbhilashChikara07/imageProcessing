package com.example.okutech.imageproject;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * <h1>UploadWorker</h1>
 * Defines the methods required by a image upload worker
 *
 * @author Gufran Khurshid
 * @version 1.0
 * @since 2016-11-26
 */

public interface UploadWorker {

    public String enqueueTask(FileUploadTask fileUploadTask) throws FileNotFoundException;
    
}
