package com.dynamixsoftware.printingsample;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FilesUtils {

    static final String FILE_PNG = "test_page.png";
    static final String FILE_DOC = "What is PrintHand.doc";
    static final String FILE_PDF = "What is PrintHand.pdf";

    static void extractFilesFromAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        File dir = getFilesDir(context);
        for (String filename : new String[]{FILE_PNG, FILE_DOC, FILE_PDF})
            extractFileFromAssets(assetManager, dir, filename);
    }

    private static void extractFileFromAssets(AssetManager assetManager, File dir, String filename) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            try {
                inputStream = assetManager.open(filename);
                File outFile = new File(dir, filename);
                if (outFile.exists())
                    outFile.delete();
                outFile.createNewFile();
                outputStream = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1)
                    outputStream.write(buffer, 0, read);
                outputStream.flush();
            } finally {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String getFilePath(Context context, String filename) {
        File file = getFile(context, filename);
        return file.exists() ? file.getAbsolutePath() : null;
    }

    static File getFile(Context context, String filename) {
        return new File(getFilesDir(context), filename);
    }

    private static File getFilesDir(Context context) {
        return context.getExternalCacheDir();
    }
}
