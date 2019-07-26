package com.openogy.classes;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

public class ImageCompression {

   final static float maxHeight = 1280.0f;
    final static float maxWidth = 1280.0f;




    /**
     * Reduces the size of an image without affecting its quality.
     *
     * @param imagePath -Path of an image
     * @return
     */


    public static String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/ImageCompApp/Images");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/" + mImageName);
        return uriString;
    }

    public static void hideKeyboard(Activity context) {
        try {
            if (context == null) return;
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getWindow().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }
}

