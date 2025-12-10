package com.poly.realstate.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    // ---------------------------------------------------------------------
    // PUBLIC : convertit un URI → vrai chemin (ou copie dans cache)
    // ---------------------------------------------------------------------
    public static String getPath(Context context, Uri uri) {

        // Pour Android 11+ : utiliser une copie locale dans cache
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return copyFileToInternalStorage(context, uri);
        }

        String filePath = null;

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // External Storage
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return "/sdcard/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (id != null && id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                Uri contentUri = Uri.parse("content://downloads/public_downloads");
                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{ split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (gallerie)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Google Photos → copie dans cache
            if (isGooglePhotosUri(uri))
                return copyFileToInternalStorage(context, uri);

            return getDataColumn(context, uri, null, null);
        }
        // File direct
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        // fallback → copie
        return copyFileToInternalStorage(context, uri);
    }

    // ---------------------------------------------------------------------
    // LECTURE DE CHEMIN DIRECT
    // ---------------------------------------------------------------------
    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(
                    uri, projection, selection, selectionArgs, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                String value = cursor.getString(index);
                if (value != null) return value;
            }
        } catch (Exception ignored) {}
        finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // ---------------------------------------------------------------------
    // COPIE DANS LE CACHE (Android 10+)
    // ---------------------------------------------------------------------
    private static String copyFileToInternalStorage(Context context, Uri uri) {

        ContentResolver resolver = context.getContentResolver();
        String fileName = "temp_file";

        // Récupère le nom original
        Cursor cursor = resolver.query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                );
            }
        } catch (Exception ignored) {}
        finally {
            if (cursor != null) cursor.close();
        }

        File output = new File(context.getCacheDir(), fileName);

        try {
            InputStream in = resolver.openInputStream(uri);
            FileOutputStream out = new FileOutputStream(output);

            byte[] buffer = new byte[1024];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            out.close();

            return output.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------------------------------------------------------------------
    // Helpers URI
    // ---------------------------------------------------------------------
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
