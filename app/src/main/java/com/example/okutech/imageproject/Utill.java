package com.example.okutech.imageproject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Description
 *
 * @author Abhilash Chikara
 * @version 1.0
 * @since 5/1/17
 */

public class Utill {

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getWaterMarkedImage(Context context, Bitmap bmp) {
        Bitmap bitmap = null;
        try {
            bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bitmap);
            Bitmap placeHolder = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            canvas.drawColor(Color.TRANSPARENT);
            if (bitmap.getWidth() < bitmap.getHeight()) {
                placeHolder = scaleUp(placeHolder, (float) (bitmap.getHeight() * 0.3), true);
                canvas.drawBitmap(placeHolder, (bitmap.getWidth() - placeHolder.getWidth()) / 2,
                        (bitmap.getHeight() - placeHolder.getHeight()) / 2, null);
            } else {
                placeHolder = scaleUp(placeHolder, (float) (bitmap.getHeight() * 0.6), true);
                canvas.drawBitmap(placeHolder, (bitmap.getWidth() - placeHolder.getWidth()) / 2,
                        (bitmap.getHeight() - placeHolder.getHeight()) / 2, null);
            }
            return bitmap;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap scaleUp(Bitmap realImage, float maxImageSize,
                                 boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
