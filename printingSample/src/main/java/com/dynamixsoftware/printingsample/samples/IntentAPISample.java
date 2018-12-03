package com.dynamixsoftware.printingsample.samples;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.dynamixsoftware.intentapi.IDocument;
import com.dynamixsoftware.intentapi.IJob;
import com.dynamixsoftware.intentapi.IPrintCallback;
import com.dynamixsoftware.intentapi.IPrinterContext;
import com.dynamixsoftware.intentapi.IPrinterInfo;
import com.dynamixsoftware.intentapi.IServiceCallback;
import com.dynamixsoftware.intentapi.ISetLicenseCallback;
import com.dynamixsoftware.intentapi.IntentAPI;
import com.dynamixsoftware.intentapi.PrintHandOption;
import com.dynamixsoftware.intentapi.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

public class IntentAPISample {

    private static String TAG = "IntentAPISample";

    private IntentAPI intentAPI;
    private Activity activity;

    private boolean connected = false;

    private IServiceCallback serviceCallback = new IServiceCallback.Stub() {

        @Override
        public void onServiceDisconnected() {
            connected = false;
            Log.d(TAG, "Service disconnected");
        }

        @Override
        public void onServiceConnected() {
            connected = true;
            Log.d(TAG, "Service connected");
        }

        @Override
        public void onFileOpen(int progress, int finished) {
            Log.d(TAG, "onFileOpen progress " + progress + "; finished " + (finished == 1));
        }

        @Override
        public void onLibraryDownload(int progress) throws RemoteException {
            Log.d(TAG, "onLibraryDownload progress " + progress);
        }

        @Override
        public boolean onRenderLibraryCheck(boolean renderLibrary, boolean fontLibrary) throws RemoteException {
            Log.d(TAG, "onRenderLibraryCheck render library " + renderLibrary + "; fonts library " + fontLibrary);
            return true;
        }

        @Override
        public String onPasswordRequired() throws RemoteException {
            Log.d(TAG, "onPasswordRequired");
            return "password";
        }

        @Override
        public void onError(Result result) throws RemoteException {
            Log.d(TAG, "error, Result " + result
                    + "; Result type " + result.getType());
        }

    };

    private IPrintCallback printCallback = new IPrintCallback.Stub() {

        @Override
        public void startingPrintJob() throws RemoteException {
            Log.d(TAG, "startingPrintJob");
        }

        @Override
        public void start() throws RemoteException {
            Log.d(TAG, "start");
        }

        @Override
        public void sendingPage(int pageNum, int progress) throws RemoteException {
            Log.d(TAG, "sendingPage number " + pageNum + ", progress " + progress);
        }

        @Override
        public void preparePage(int pageNum) throws RemoteException {
            Log.d(TAG, "preparePage number " + pageNum);
        }

        @Override
        public boolean needCancel() throws RemoteException {
            Log.d(TAG, "needCancel");
            // If you need to cancel printing send true
            return false;
        }

        @Override
        public void finishingPrintJob() throws RemoteException {
            Log.d(TAG, "needCancel");
        }

        @Override
        public void finish(Result result, int pagesPrinted) throws RemoteException {
            Log.d(TAG, "finish, Result " + result
                    + "; Result type " + result.getType()
                    + "; Result message " + result.getType().getMessage()
                    + "; pages printed " + pagesPrinted);
        }
    };

    private ISetLicenseCallback setLicenseCallback = new ISetLicenseCallback.Stub() {

        @Override
        public void start() throws RemoteException {
            Log.d(TAG, "activateOnline start");
        }

        @Override
        public void serverCheck() throws RemoteException {
            Log.d(TAG, "activateOnline serverCheck");
        }

        @Override
        public void finish(Result arg0) throws RemoteException {
            Log.d(TAG, "activateOnline finish " + arg0);
        }

    };

    public IntentAPISample(Activity activity) {
        this.activity = activity;
    }

    public void createIntentAPIWithActivity() {
        intentAPI = new IntentAPI(activity);
    }

    public void createIntentAPIWithApplication() {
        intentAPI = new IntentAPI(activity.getApplicationContext());
    }

    public void startService() {
        if (intentAPI == null) {
            Log.d(TAG, "intentAPI is null");
            return;
        }
        if (!connected) {
            try {
                intentAPI.runService(serviceCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkService() {
        return intentAPI != null && connected;
    }

    public void setServiceCallback() throws RemoteException {
        if (intentAPI == null) {
            Log.d(TAG, "intentAPI is null");
            return;
        }
        intentAPI.setServiceCallback(serviceCallback);
    }

    public void setPrintCallback() throws RemoteException {
        if (checkService()) {
            intentAPI.setPrintCallback(printCallback);
        } else {
            serviceStopped();
        }
    }

    public void checkPremium() throws RemoteException {
        if (checkService()) {
            Log.d(TAG, "checkPremium " + intentAPI.checkPremium());
        } else {
            serviceStopped();
        }
    }

    public void print(Uri uri, String contentType, String description) {
        if (intentAPI == null) {
            Log.d(TAG, "intentAPI is null");
            return;
        }
        intentAPI.print(uri, contentType, description);
    }

    public void setupCurrentPrinter() {
        if (intentAPI == null) {
            Log.d(TAG, "intentAPI is null");
            return;
        }
        intentAPI.setupCurrentPrinter();
    }

    public IPrinterInfo getCurrentPrinter() throws RemoteException {
        if (checkService()) {
            return intentAPI.getCurrentPrinter();
        } else {
            serviceStopped();
            return null;
        }
    }

    public void changeOptions() {
        if (intentAPI == null) {
            Log.d(TAG, "intentAPI is null");
            return;
        }
        intentAPI.changePrinterOptions();
    }

    public void setLicense() {
        if (checkService()) {
            // set activation key here
            intentAPI.setLicense("", setLicenseCallback);
        } else {
            serviceStopped();
        }
    }

    private void serviceStopped() {
        Log.d(TAG, "Service is not running");
    }

    /***
     * Prints test page with small image.
     * @throws RemoteException
     */
    public void printIJob() throws RemoteException {
        IJob.Stub job = new IJob.Stub() {

            @Override
            public Bitmap renderPageFragment(int num, Rect fragment)
                    throws RemoteException {
                IPrinterInfo printer = getCurrentPrinter();
                if (printer != null) {
                    Bitmap bitmap = Bitmap.createBitmap(fragment.width(), fragment.height(), Config.ARGB_8888);

                    AssetManager am = activity.getAssets();
                    for (int i = 0; i < 3; i++)
                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            options.inDither = false;
                            if (i > 0) {
                                options.inSampleSize = 1 << i;
                            }
                            Bitmap imageBMP = BitmapFactory.decodeStream(am.open("test_page.png"), null, options);

                            Paint p = new Paint();

                            int imageWidth = 0;
                            int imageHeight = 0;

                            if (imageBMP != null) {
                                imageWidth = imageBMP.getWidth();
                                imageHeight = imageBMP.getHeight();
                            }

                            int xDpi = printer.getPrinterContext().getHResolution();
                            int yDpi = printer.getPrinterContext().getVResolution();

                            // in dots
                            int paperWidth = printer.getPrinterContext().getPaperWidth() * xDpi / 72;
                            int paperHeight = printer.getPrinterContext().getPaperHeight() * yDpi / 72;

                            float aspectH = (float) imageHeight / (float) paperHeight;

                            float aspectW = (float) imageWidth / (float) paperWidth;

                            RectF dst = new RectF(0, 0, fragment.width() * aspectW, fragment.height() * aspectH);

                            float sLeft = 0;
                            float sTop = fragment.top * aspectH;
                            float sRight = imageWidth;
                            float sBottom = fragment.top * aspectH + fragment.bottom * aspectH;

                            RectF source = new RectF(sLeft, sTop, sRight, sBottom);

                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawColor(Color.WHITE);

                            // move image to actual printing area
                            dst.offsetTo(dst.left - fragment.left, dst.top - fragment.top);
                            Matrix matrix = new Matrix();
                            matrix.setRectToRect(source, dst, Matrix.ScaleToFit.FILL);
                            canvas.drawBitmap(imageBMP, matrix, p);

                            break;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            break;
                        } catch (OutOfMemoryError ex) {
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            continue;
                        }

                    return bitmap;
                } else {
                    return null;
                }
            }

            @Override
            public int getTotalPages() throws RemoteException {
                return 1;
            }
        };

        if (checkService()) {
            intentAPI.print(job, 1);
        } else {
            serviceStopped();
        }
    }

    public void printIDoc() throws RemoteException {
        IDocument.Stub document = new IDocument.Stub() {

            private int thumbnailWidth;
            private int thumbnailHeight;

            @Override
            public Bitmap renderPageFragment(int arg0, Rect fragment)
                    throws RemoteException {
                IPrinterInfo printer = getCurrentPrinter();
                if (printer != null) {
                    Bitmap bitmap = Bitmap.createBitmap(fragment.width(), fragment.height(), Config.ARGB_8888);

                    AssetManager am = activity.getAssets();
                    for (int i = 0; i < 3; i++)
                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            options.inDither = false;
                            if (i > 0) {
                                options.inSampleSize = 1 << i;
                            }
                            Bitmap imageBMP = BitmapFactory.decodeStream(am.open("test_page.png"), null, options);

                            Paint p = new Paint();

                            int imageWidth = 0;
                            int imageHeight = 0;

                            if (imageBMP != null) {
                                imageWidth = imageBMP.getWidth();
                                imageHeight = imageBMP.getHeight();
                            }

                            int xDpi = printer.getPrinterContext().getHResolution();
                            int yDpi = printer.getPrinterContext().getVResolution();

                            // in dots
                            int paperWidth = printer.getPrinterContext().getPaperWidth() * xDpi / 72;
                            int paperHeight = printer.getPrinterContext().getPaperHeight() * yDpi / 72;

                            float aspectH = (float) imageHeight / (float) paperHeight;
                            float aspectW = (float) imageWidth / (float) paperWidth;

                            aspectH = aspectH > 1 ? 1 / aspectH : aspectH;
                            aspectW = aspectW > 1 ? 1 / aspectW : aspectW;

                            RectF dst = new RectF(0, 0, fragment.width() * aspectW, fragment.height() * aspectH);

                            float sLeft = 0;
                            float sTop = fragment.top * aspectH;
                            float sRight = imageWidth;
                            float sBottom = fragment.top * aspectH + fragment.bottom * aspectH;

                            RectF source = new RectF(sLeft, sTop, sRight, sBottom);

                            Canvas canvas = new Canvas(bitmap);
                            canvas.drawColor(Color.WHITE);

                            // move image to actual printing area
                            dst.offsetTo(dst.left - fragment.left, dst.top - fragment.top);
                            Matrix matrix = new Matrix();
                            matrix.setRectToRect(source, dst, Matrix.ScaleToFit.FILL);
                            canvas.drawBitmap(imageBMP, matrix, p);

                            break;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            break;
                        } catch (OutOfMemoryError ex) {
                            if (bitmap != null) {
                                bitmap.recycle();
                                bitmap = null;
                            }
                            continue;
                        }

                    return bitmap;
                } else {
                    return null;
                }
            }

            @Override
            public void initDeviceContext(IPrinterContext printerContext, int thumbnailWidth, int thumbnailHeight)
                    throws RemoteException {
                Log.d(TAG, "initDeviceContext");
                this.thumbnailWidth = thumbnailWidth;
                this.thumbnailHeight = thumbnailHeight;
            }

            @Override
            public int getTotalPages() throws RemoteException {
                return 1;
            }

            @Override
            public String getDescription() throws RemoteException {
                return "PrintHand test page";
            }

            @Override
            public Bitmap getPageThumbnail(int arg0) throws RemoteException {
                Bitmap bitmap = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight, Config.ARGB_8888);

                AssetManager am = activity.getAssets();
                for (int i = 0; i < 3; i++)
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        options.inDither = false;
                        if (i > 0) {
                            options.inSampleSize = 1 << i;
                        }
                        Bitmap imageBMP = BitmapFactory.decodeStream(am.open("test_page.png"), null, options);

                        Paint p = new Paint();

                        int imageWidth = 0;
                        int imageHeight = 0;

                        if (imageBMP != null) {
                            imageWidth = imageBMP.getWidth();
                            imageHeight = imageBMP.getHeight();
                        }

                        // default
                        int xDpi = 300;
                        int yDpi = 300;
                        int paperWidth = 2481;
                        int paperHeight = 3507;

                        IPrinterInfo printer = getCurrentPrinter();
                        if (printer != null) {
                            xDpi = printer.getPrinterContext().getHResolution();
                            yDpi = printer.getPrinterContext().getVResolution();

                            // in dots
                            paperWidth = printer.getPrinterContext().getPaperWidth() * xDpi / 72;
                            paperHeight = printer.getPrinterContext().getPaperHeight() * yDpi / 72;
                        }

                        float aspectW = (float) imageWidth / (float) paperWidth;
                        float aspectH = (float) imageHeight / (float) paperHeight;

                        RectF dst = new RectF(0, 0, thumbnailWidth * aspectW, thumbnailHeight * aspectH);

                        float sLeft = 0;
                        float sTop = 0;
                        float sRight = imageWidth;
                        float sBottom = imageHeight;

                        RectF source = new RectF(sLeft, sTop, sRight, sBottom);

                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(Color.WHITE);

                        Matrix matrix = new Matrix();
                        matrix.setRectToRect(source, dst, Matrix.ScaleToFit.FILL);
                        canvas.drawBitmap(imageBMP, matrix, p);

                        break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        break;
                    } catch (OutOfMemoryError ex) {
                        if (bitmap != null) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                        continue;
                    }

                return bitmap;
            }
        };

        if (checkService()) {
            intentAPI.print(document);
        } else {
            serviceStopped();
        }
    }

    public void printWithoutUI(String filename) throws RemoteException, IOException {
        if (checkService()) {
            AssetManager assetMgr = activity.getAssets();
            InputStream in = assetMgr.open(filename);
            OutputStream out = null;

            File f = new File(activity.getExternalFilesDir(null), filename);

            out = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

            if (filename.endsWith(".png")) {
                intentAPI.print("PrintingSample", "image/png", Uri.fromFile(f));
            } else if (filename.endsWith(".doc")) {
                intentAPI.print("PrintingSample", "application/ms-word", Uri.fromFile(f));
            } else if (filename.endsWith(".pdf")) {
                intentAPI.print("PrintingSample", "application/pdf", Uri.fromFile(f));
            }
        } else {
            serviceStopped();
        }
    }

    public void changeImageOptions() throws RemoteException {
        if (checkService()) {
            Random random = new Random();
            List<PrintHandOption> imagesOptions = intentAPI.getImagesOptions();
            for (PrintHandOption phOption : imagesOptions) {
                Log.d(TAG, "Current option " + phOption.getName() + " value is " + phOption.getValue());
                List<String> valuesList = phOption.getValuesList();
                phOption.setValue(valuesList.get(random.nextInt(valuesList.size())));
                Log.d(TAG, "Changed option " + phOption.getName() + " value is " + phOption.getValue());
            }
            intentAPI.setImagesOptions(imagesOptions);
        } else {
            serviceStopped();
        }
    }

    public void changeFileOptions() throws RemoteException {
        if (checkService()) {
            Random random = new Random();
            List<PrintHandOption> fileOptions = intentAPI.getFilesOptions();
            for (PrintHandOption phOption : fileOptions) {
                Log.d(TAG, "Current option " + phOption.getName() + " value is " + phOption.getValue());
                List<String> valuesList = phOption.getValuesList();
                phOption.setValue(valuesList.get(random.nextInt(valuesList.size())));
                Log.d(TAG, "Changed option " + phOption.getName() + " value is " + phOption.getValue());
            }
            intentAPI.setFilesOptions(fileOptions);
        } else {
            serviceStopped();
        }
    }

    public void showFilesPreview(Uri uri, String mimeType, int pageNumber) {
        if (intentAPI == null) {
            Log.d(TAG, "intentAPI is null");
            return;
        }
        intentAPI.showFilePreview(uri, mimeType, pageNumber);
    }

}
