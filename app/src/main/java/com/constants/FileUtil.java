package com.constants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.OutputStream;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static Uri createFile(ContentResolver contentResolver, String fileName, String mimeType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri uri = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), contentValues);
            } else {
                uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating file", e);
            e.printStackTrace();
        }

        return uri;
    }

    public static void writeFile(ContentResolver contentResolver, Uri uri, byte[] data) {
        try {
            OutputStream outputStream = contentResolver.openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error writing to file", e);
            e.printStackTrace();
        }
    }


}
