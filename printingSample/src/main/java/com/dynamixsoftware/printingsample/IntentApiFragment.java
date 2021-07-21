package com.dynamixsoftware.printingsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class IntentApiFragment extends Fragment implements View.OnClickListener {

    private IntentAPI intentApi;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        intentApi = new IntentAPI(getActivity() != null ? getActivity() : context); // some features not worked if initialized without activity
        final Context appContext = context.getApplicationContext();
        try {
            intentApi.runService(new IServiceCallback.Stub() {
                @Override
                public void onServiceDisconnected() {
                    toastInMainThread(appContext, "Service disconnected");
                }

                @Override
                public void onServiceConnected() {
                    toastInMainThread(appContext, "Service connected");
                }

                @Override
                public void onFileOpen(int progress, int finished) {
                    toastInMainThread(appContext, "onFileOpen progress " + progress + "; finished " + (finished == 1));
                }

                @Override
                public void onLibraryDownload(int progress) {
                    toastInMainThread(appContext, "onLibraryDownload progress " + progress);
                }

                @Override
                public boolean onRenderLibraryCheck(boolean renderLibrary, boolean fontLibrary) {
                    toastInMainThread(appContext, "onRenderLibraryCheck render library " + renderLibrary + "; fonts library " + fontLibrary);
                    return true;
                }

                @Override
                public String onPasswordRequired() {
                    toastInMainThread(appContext, "onPasswordRequired");
                    return "password";
                }

                @Override
                public void onError(Result result) {
                    toastInMainThread(appContext, "error, Result " + result + "; Result type " + result.getType());
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            intentApi.setPrintCallback(new IPrintCallback.Stub() {
                @Override
                public void startingPrintJob() {
                    toastInMainThread(appContext, "startingPrintJob");
                }

                @Override
                public void start() {
                    toastInMainThread(appContext, "start");
                }

                @Override
                public void sendingPage(int pageNum, int progress) {
                    toastInMainThread(appContext, "sendingPage number " + pageNum + ", progress " + progress);
                }

                @Override
                public void preparePage(int pageNum) {
                    toastInMainThread(appContext, "preparePage number " + pageNum);
                }

                @Override
                public boolean needCancel() {
                    toastInMainThread(appContext, "needCancel");
                    // If you need to cancel printing send true
                    return false;
                }

                @Override
                public void finishingPrintJob() {
                    toastInMainThread(appContext, "finishingPrintJob");
                }

                @Override
                public void finish(Result result, int pagesPrinted) {
                    toastInMainThread(appContext, "finish, Result " + result + "; Result type " + result.getType() + "; Result message " + result.getType().getMessage() + "; pages printed " + pagesPrinted);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (intentApi != null) {
            intentApi.stopService(null);
            try {
                intentApi.setServiceCallback(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                intentApi.setPrintCallback(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            intentApi = null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_intent_api, container, false);
        root.findViewById(R.id.check_premium).setOnClickListener(this);
        root.findViewById(R.id.activate_online).setOnClickListener(this);
        root.findViewById(R.id.setup_printer).setOnClickListener(this);
        root.findViewById(R.id.change_options).setOnClickListener(this);
        root.findViewById(R.id.get_current_printer).setOnClickListener(this);
        root.findViewById(R.id.print_image).setOnClickListener(this);
        root.findViewById(R.id.print_file).setOnClickListener(this);
        root.findViewById(R.id.show_file_preview).setOnClickListener(this);
        root.findViewById(R.id.print_with_your_rendering).setOnClickListener(this);
        root.findViewById(R.id.print_with_your_rendering_without_ui).setOnClickListener(this);
        root.findViewById(R.id.print_image_with_print_hand_rendering_without_ui).setOnClickListener(this);
        root.findViewById(R.id.change_image_options).setOnClickListener(this);
        root.findViewById(R.id.print_file_with_print_hand_rendering_without_ui).setOnClickListener(this);
        root.findViewById(R.id.print_protected_file_with_print_hand_rendering_without_ui).setOnClickListener(this);
        root.findViewById(R.id.change_files_options).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_premium:
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.check_premium)
                        .setMessage("" + intentApi.checkPremium())
                        .setPositiveButton(R.string.ok, null)
                        .show();
                break;
            case R.id.activate_online:
                final Context appContext = requireContext().getApplicationContext();
                intentApi.setLicense("YOUR_ACTIVATION_KEY", new ISetLicenseCallback.Stub() {
                    @Override
                    public void start() {
                        toastInMainThread(appContext, "activate start");
                    }

                    @Override
                    public void serverCheck() {
                        toastInMainThread(appContext, "activate check server");
                    }

                    @Override
                    public void finish(final Result arg0) {
                        toastInMainThread(appContext, "activate finish " + (arg0 == Result.OK ? "ok" : "error"));
                    }
                });
                break;
            case R.id.setup_printer:
                intentApi.setupCurrentPrinter();
                break;
            case R.id.change_options:
                intentApi.changePrinterOptions();
                break;
            case R.id.get_current_printer:
                try {
                    IPrinterInfo printer = intentApi.getCurrentPrinter();
                    Toast.makeText(requireContext().getApplicationContext(), "current printer " + (printer != null ? printer.getName() : "null"), Toast.LENGTH_LONG).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.print_image:
                intentApi.print(Uri.parse("file://" + FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_PNG)), "image/png", "from printing sample");
                break;
            case R.id.print_file:
                intentApi.print(Uri.parse("file://" + FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_DOC)), "application/msword", "from printing sample");
                break;
            case R.id.show_file_preview:
                intentApi.showFilePreview(Uri.parse("file://" + FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_DOC)), "application/msword", 0);
                break;
            case R.id.print_with_your_rendering:
                try {
                    IDocument.Stub document = new IDocument.Stub() {

                        private int thumbnailWidth;
                        private int thumbnailHeight;

                        @Override
                        public Bitmap renderPageFragment(int arg0, Rect fragment) throws RemoteException {
                            IPrinterInfo printer = intentApi.getCurrentPrinter();
                            if (printer != null) {
                                Bitmap bitmap = Bitmap.createBitmap(fragment.width(), fragment.height(), Bitmap.Config.ARGB_8888);
                                for (int i = 0; i < 3; i++)
                                    try {
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                        options.inDither = false;
                                        if (i > 0) {
                                            options.inSampleSize = 1 << i;
                                        }
                                        Bitmap imageBMP = BitmapFactory.decodeStream(new FileInputStream(FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_PNG)), null, options);
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
                        public void initDeviceContext(IPrinterContext printerContext, int thumbnailWidth, int thumbnailHeight) {
                            this.thumbnailWidth = thumbnailWidth;
                            this.thumbnailHeight = thumbnailHeight;
                        }

                        @Override
                        public int getTotalPages() {
                            return 1;
                        }

                        @Override
                        public String getDescription() {
                            return "PrintHand test page";
                        }

                        @Override
                        public Bitmap getPageThumbnail(int arg0) throws RemoteException {
                            Bitmap bitmap = Bitmap.createBitmap(thumbnailWidth, thumbnailHeight, Bitmap.Config.ARGB_8888);
                            for (int i = 0; i < 3; i++)
                                try {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                    options.inDither = false;
                                    if (i > 0) {
                                        options.inSampleSize = 1 << i;
                                    }
                                    Bitmap imageBMP = BitmapFactory.decodeStream(new FileInputStream(FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_PNG)), null, options);
                                    Paint p = new Paint();
                                    int imageWidth = 0;
                                    int imageHeight = 0;
                                    if (imageBMP != null) {
                                        imageWidth = imageBMP.getWidth();
                                        imageHeight = imageBMP.getHeight();
                                    }
                                    // default
                                    int paperWidth = 2481;
                                    int paperHeight = 3507;
                                    IPrinterInfo printer = intentApi.getCurrentPrinter();
                                    if (printer != null) {
                                        int xDpi = printer.getPrinterContext().getHResolution();
                                        int yDpi = printer.getPrinterContext().getVResolution();
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
                    intentApi.print(document);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.print_with_your_rendering_without_ui:
                try {
                    IJob.Stub job = new IJob.Stub() {
                        @Override
                        public Bitmap renderPageFragment(int num, Rect fragment) throws RemoteException {
                            IPrinterInfo printer = intentApi.getCurrentPrinter();
                            if (printer != null) {
                                Bitmap bitmap = Bitmap.createBitmap(fragment.width(), fragment.height(), Bitmap.Config.ARGB_8888);
                                for (int i = 0; i < 3; i++)
                                    try {
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                        options.inDither = false;
                                        if (i > 0) {
                                            options.inSampleSize = 1 << i;
                                        }
                                        Bitmap imageBMP = BitmapFactory.decodeStream(new FileInputStream(FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_PNG)), null, options);
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
                        public int getTotalPages() {
                            return 1;
                        }
                    };
                    intentApi.print(job, 1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.print_image_with_print_hand_rendering_without_ui:
                try {
                    intentApi.print("PrintingSample", "image/png", Uri.parse("file://" + FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_PNG)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.change_image_options:
                try {
                    List<PrintHandOption> imageOptions = intentApi.getImagesOptions();
                    changeRandomOption(imageOptions);
                    intentApi.setImagesOptions(imageOptions);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.print_file_with_print_hand_rendering_without_ui:
                try {
                    intentApi.print("PrintingSample", "application/ms-word", Uri.parse("file://" + FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_DOC)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.print_protected_file_with_print_hand_rendering_without_ui:
                try {
                    intentApi.print("PrintingSample", "application/pdf", Uri.parse("file://" + FilesUtils.getFilePath(requireContext(), FilesUtils.FILE_PDF)));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.change_files_options:
                try {
                    List<PrintHandOption> fileOptions = intentApi.getFilesOptions();
                    changeRandomOption(fileOptions);
                    intentApi.setFilesOptions(fileOptions);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void toastInMainThread(final Context appContext, final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeRandomOption(List<PrintHandOption> options) {
        if (options.size() > 0) {
            Random random = new Random();
            PrintHandOption option = options.get(random.nextInt(options.size()));
            String currentValue = option.getValue();
            List<String> valuesList = option.getValuesList();
            option.setValue(valuesList.get(random.nextInt(valuesList.size())));
            String newValue = option.getValue();
            Toast.makeText(requireContext().getApplicationContext(), "option " + option.getName() + " changed from " + currentValue + " to " + newValue, Toast.LENGTH_LONG).show();
        }
    }
}
