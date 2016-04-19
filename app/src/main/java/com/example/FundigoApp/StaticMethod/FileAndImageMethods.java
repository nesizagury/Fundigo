package com.example.FundigoApp.StaticMethod;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileAndImageMethods {

    public static String getCustomerPhoneNumFromFile(Context context) {
        String number = "";
        String myData = "";
        try {
            File myExternalFile = new File (Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS), "verify.txt");
            FileInputStream fis = new FileInputStream (myExternalFile);
            DataInputStream in = new DataInputStream (fis);
            BufferedReader br =
                    new BufferedReader (new InputStreamReader (in));
            String strLine;
            while ((strLine = br.readLine ()) != null) {
                myData = myData + strLine;
            }
            in.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }

        if (myData != null) {
            if (myData.contains ("isFundigo")) {
                String[] parts = myData.split (" ");
                number = parts[0];
            } else
                number = myData;

        }
        return number;
    }

    public static Bitmap getImageFromDevice(Intent data, Context context) {
        Uri selectedImage = data.getData ();
        ParcelFileDescriptor parcelFileDescriptor =
                null;
        try {
            parcelFileDescriptor = context.getContentResolver ().openFileDescriptor (selectedImage, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        }
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor ();
        Bitmap image = BitmapFactory.decodeFileDescriptor (fileDescriptor);
        try {
            parcelFileDescriptor.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        Matrix matrix = new Matrix ();
        int angleToRotate = getOrientation (selectedImage, context);
        matrix.postRotate (angleToRotate);
        Bitmap rotatedBitmap = Bitmap.createBitmap (image,
                                                           0,
                                                           0,
                                                           image.getWidth (),
                                                           image.getHeight (),
                                                           matrix,
                                                           true);
        return rotatedBitmap;
    }

    private static int getOrientation(Uri selectedImage, Context context) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = context.getContentResolver ().query (selectedImage, projection, null, null, null);
        if (cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex (MediaStore.Images.Media.ORIENTATION);
            if (cursor.moveToFirst ()) {
                orientation = cursor.isNull (orientationColumnIndex) ? 0 : cursor.getInt (orientationColumnIndex);
            }
            cursor.close ();
        }
        return orientation;
    }

    public static ImageLoader getImageLoader(Context context) {

        ImageLoader imageLoader = null;
        DisplayImageOptions options = null;

        options = new DisplayImageOptions.Builder ()
                          .cacheOnDisk (true)
                          .cacheInMemory (true)
                          .bitmapConfig (Bitmap.Config.RGB_565)
                          .imageScaleType (ImageScaleType.EXACTLY)
                          .resetViewBeforeLoading (true)
                          .build ();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder (context)
                                                  .defaultDisplayImageOptions (options)
                                                  .threadPriority (Thread.MAX_PRIORITY)
                                                  .threadPoolSize (4)
                                                  .memoryCache (new WeakMemoryCache ())
                                                  .denyCacheImageMultipleSizesInMemory ()
                                                  .build ();
        imageLoader = ImageLoader.getInstance ();
        imageLoader.init (config);

        return imageLoader;
    }

}
