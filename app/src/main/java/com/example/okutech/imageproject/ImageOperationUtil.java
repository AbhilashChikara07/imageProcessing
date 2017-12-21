package com.example.okutech.imageproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

/**
 * Description
 *
 * @author Abhilash Chikara
 * @version 1.0
 * @since 5/1/17
 */

public class ImageOperationUtil {

    private Context context;

    public ImageOperationUtil(Context context) {
        this.context = context;
    }

    public void rotateImageOrientation(String photoPath) {

        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        Bitmap bitmap = null;
        Bitmap rotatedBitMap = null;
        boolean isPhotoUnchanged = false;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoPath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(photoPath, options);

            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitMap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitMap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitMap = rotateImage(bitmap, 270);
                    break;
                default:
                    rotatedBitMap = bitmap;
                    isPhotoUnchanged = true;
                    break;
            }
            if (isPhotoUnchanged == false && rotatedBitMap != null) {
                FileOutputStream baos = new FileOutputStream(photoPath);
                rotatedBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            if (rotatedBitMap != null) {
                rotatedBitMap.recycle();
                rotatedBitMap = null;
            }
        }
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    public void compressImage(File file) {

        try {

            ExifInterface exifInterfaceBefore = new ExifInterface(context.getFilesDir()
                    + file.getAbsolutePath());

            //-------------------save exif data
            String aperture = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_APERTURE);
            String dateTime = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_DATETIME);
            String exposureTime = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            String flash = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_FLASH);
            String focalLength = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            String gpsAltitude = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
            String gpsAltitudeRef = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
            String gpsDateStamp = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            String gpsLatitude = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String gpsLatitudeRef = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String gpsLongitude = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String gpsLongitudeRef = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String gpsProcessingMethod = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
            String gpsTimestamp = exifInterfaceBefore.
                    getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            Integer imageLength = exifInterfaceBefore.
                    getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            Integer imageWidth = exifInterfaceBefore.
                    getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);

            String iso = exifInterfaceBefore.getAttribute(ExifInterface.TAG_ISO);
            String make = exifInterfaceBefore.getAttribute(ExifInterface.TAG_MAKE);
            String model = exifInterfaceBefore.getAttribute(ExifInterface.TAG_MODEL);
            Integer orientation = exifInterfaceBefore.
                    getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
            Integer whiteBalance = exifInterfaceBefore.
                    getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, 0);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(context.getFilesDir() + file.getAbsolutePath(),
                    options);

            options.inSampleSize = Utill.
                    calculateInSampleSize(options, 800, 1000);

            options.inJustDecodeBounds = false;

            Bitmap org = BitmapFactory.decodeFile(context.getFilesDir()
                    + file.getAbsolutePath(), options);

            org = Utill.getWaterMarkedImage(context, org);

            FileOutputStream fos = new FileOutputStream(context.getFilesDir()
                    + file.getAbsolutePath());
            assert org != null;
            org.compress(Bitmap.CompressFormat.JPEG, 80, fos);

            //---------------restore exif data
            ExifInterface exifInterfaceAfter = new ExifInterface(context.getFilesDir()
                    + file.getAbsolutePath());
            exifInterfaceAfter.setAttribute(ExifInterface.TAG_ORIENTATION,
                    orientation.toString());
            if (aperture != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_APERTURE, aperture);
            if (dateTime != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
            if (exposureTime != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_EXPOSURE_TIME, exposureTime);
            if (flash != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_FLASH, flash);
            if (focalLength != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_FOCAL_LENGTH, focalLength);
            if (gpsAltitude != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, gpsAltitude);
            if (gpsAltitudeRef != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF,
                        gpsAltitudeRef);
            if (gpsDateStamp != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, gpsDateStamp);
            if (gpsLatitude != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_LATITUDE, gpsLatitude);
            if (gpsLatitudeRef != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
                        gpsLatitudeRef);
            if (gpsLongitude != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                        gpsLongitude);
            if (gpsLongitudeRef != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
                        gpsLongitudeRef);
            if (gpsProcessingMethod != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD,
                        gpsProcessingMethod);
            if (gpsTimestamp != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, gpsTimestamp);
            if (imageLength != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_IMAGE_LENGTH,
                        imageLength.toString());
            if (imageWidth != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_IMAGE_WIDTH,
                        imageWidth.toString());
            if (iso != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_ISO, iso);
            if (make != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_MAKE, make);
            if (model != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_MODEL, model);
            if (whiteBalance != null)
                exifInterfaceAfter.setAttribute(ExifInterface.TAG_WHITE_BALANCE, whiteBalance.toString());

            exifInterfaceAfter.saveAttributes();

            if (org != null) {
                org.recycle();
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
