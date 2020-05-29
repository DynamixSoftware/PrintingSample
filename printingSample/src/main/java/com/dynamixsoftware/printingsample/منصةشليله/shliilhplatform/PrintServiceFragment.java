https://shliilhplatform.com.sa
package com.dynamixsoftware.printingsample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dynamixsoftware.printingsdk.DriverHandleEntry;
import com.dynamixsoftware.printingsdk.DriversSearchEntry;
import com.dynamixsoftware.printingsdk.IDiscoverCloudListener;
import com.dynamixsoftware.printingsdk.IDiscoverListener;
import com.dynamixsoftware.printingsdk.IDiscoverSmb;
import com.dynamixsoftware.printingsdk.IDiscoverSmbListener;
import com.dynamixsoftware.printingsdk.IFindDriversListener;
import com.dynamixsoftware.printingsdk.IGetDriversListener;
import com.dynamixsoftware.printingsdk.IPage;
import com.dynamixsoftware.printingsdk.IPrintListener;
import com.dynamixsoftware.printingsdk.IServiceCallback;
import com.dynamixsoftware.printingsdk.ISetLicenseCallback;
import com.dynamixsoftware.printingsdk.ISetupPrinterListener;
import com.dynamixsoftware.printingsdk.Printer;
import com.dynamixsoftware.printingsdk.PrinterOption;
import com.dynamixsoftware.printingsdk.PrinterOptionValue;
import com.dynamixsoftware.printingsdk.PrintingSdk;
import com.dynamixsoftware.printingsdk.Result;
import com.dynamixsoftware.printingsdk.ResultType;
import com.dynamixsoftware.printingsdk.SmbFile;
import com.dynamixsoftware.printingsdk.TransportType;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrintServiceFragment extends Fragment implements View.OnClickListener {

    private PrintingSdk printingSdk;

    private final List<Printer> discoveredPrinters = new ArrayList<>();
    private final List<DriversSearchEntry> driversSearchEntries = new ArrayList<>();
    private final List<DriverHandleEntry> driverHandleEntries = new ArrayList<>();

    private IDiscoverSmb discoverSmb;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        printingSdk = new PrintingSdk(context);
        printingSdk.startService(new IServiceCallback() {
            @Override
            public void onServiceConnected() {
                Toast.makeText(context.getApplicationContext(), "Service connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected() {
                Toast.makeText(context.getApplicationContext(), "Service disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        printingSdk.stopService();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_print_service, container, false);
        root.findViewById(R.id.set_license).setOnClickListener(this);
        root.findViewById(R.id.init_current_and_recent_printers).setOnClickListener(this);
        root.findViewById(R.id.get_current_printer).setOnClickListener(this);
        root.findViewById(R.id.get_recent_printers).setOnClickListener(this);
        root.findViewById(R.id.discover_wifi).setOnClickListener(this);
        root.findViewById(R.id.discover_bluetooth).setOnClickListener(this);
        root.findViewById(R.id.discover_google_cloud).setOnClickListener(this);
        root.findViewById(R.id.discover_smb).setOnClickListener(this);
        root.findViewById(R.id.discover_usb).setOnClickListener(this);
        root.findViewById(R.id.find_driver).setOnClickListener(this);
        root.findViewById(R.id.get_drivers).setOnClickListener(this);
        root.findViewById(R.id.setup_recent_printer).setOnClickListener(this);
        root.findViewById(R.id.setup_discovered_printer).setOnClickListener(this);
        root.findViewById(R.id.change_options).setOnClickListener(this);
        root.findViewById(R.id.print_image).setOnClickListener(this);
        return root;
    }


    @Override
    public void onClick(View v) {
        final Context appContext = requireContext().getApplicationContext();
        switch (v.getId()) {
            case R.id.set_license:
                printingSdk.setLicense("YOUR_LICENSE_HERE", new ISetLicenseCallback.Stub() {
                    @Override
                    public void start() {
                        Toast.makeText(appContext, "set license start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void serverCheck() {
                        Toast.makeText(appContext, "set license check server", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void finish(Result result) {
                        Toast.makeText(appContext, "set license finish " + (result == Result.OK ? "ok" : "error"), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.init_current_and_recent_printers:
                try {
                    printingSdk.initRecentPrinters(new ISetupPrinterListener.Stub() {
                        @Override
                        public void start() {
                            toastInMainThread(appContext, "ISetupPrinterListener start");
                        }

                        @Override
                        public void libraryPackInstallationProcess(int arg0) {
                            toastInMainThread(appContext, "ISetupPrinterListener libraryPackInstallationProcess " + arg0 + " %");
                        }

                        @Override
                        public void finish(Result arg0) {
                            toastInMainThread(appContext, "ISetupPrinterListener finish " + arg0.name());
                            if (arg0.getType().equals(ResultType.ERROR_LIBRARY_PACK_NOT_INSTALLED)) {
                                // printingSdk.setup should be called with forceInstall = true to download required drivers
                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.get_current_printer:
                try {
                    Printer currentPrinter = printingSdk.getCurrentPrinter();
                    showDialog(getString(R.string.success), "Current printer:\n" + (currentPrinter != null ? currentPrinter.getName() : "null"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.get_recent_printers:
                try {
                    List<Printer> recentPrinters = printingSdk.getRecentPrintersList();
                    String message = "";
                    for (Printer printer : recentPrinters)
                        message += printer.getName() + "\n";
                    if (message.length() == 0)
                        message = "No recent printers";
                    showDialog(getString(R.string.success), message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.discover_wifi:
                try {
                    printingSdk.startDiscoverWiFi(new IDiscoverListener.Stub() {
                        @Override
                        public void start() {
                            toastInMainThread(appContext, "IDiscoverListener start");
                        }

                        @Override
                        public void printerFound(List<Printer> arg0) {
                            toastInMainThread(appContext, "IDiscoverListener printerFound");
                            discoveredPrinters.clear();
                            discoveredPrinters.addAll(arg0);
                        }

                        @Override
                        public void finish(Result arg0) {
                            toastInMainThread(appContext, "IDiscoverListener finish " + arg0.name());
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.discover_bluetooth:
                try {
                    printingSdk.startDiscoverBluetooth(new IDiscoverListener.Stub() {
                        @Override
                        public void start() {
                            toastInMainThread(appContext, "IDiscoverListener start");
                        }

                        @Override
                        public void printerFound(List<Printer> arg0) {
                            toastInMainThread(appContext, "IDiscoverListener printerFound");
                            discoveredPrinters.clear();
                            discoveredPrinters.addAll(arg0);
                        }

                        @Override
                        public void finish(Result arg0) {
                            toastInMainThread(appContext, "IDiscoverListener finish " + arg0.name());
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.discover_google_cloud:
                try {
                    printingSdk.startDiscoverCloud("YOUR_GOOGLE_ACCOUNT_NAME", new IDiscoverCloudListener.Stub() {

                        @Override
                        public void start() {
                            toastInMainThread(appContext, "IDiscoverCloudListener start");
                        }

                        @Override
                        public void showAuthorization(Intent arg0) {
                            // Launch Intent arg0 to show authorization activity
                        }

                        @Override
                        public void printerFound(List<Printer> arg0) {
                            toastInMainThread(appContext, "IDiscoverCloudListener printerFound");
                            discoveredPrinters.clear();
                            discoveredPrinters.addAll(arg0);
                        }

                        @Override
                        public void finish(Result arg0) {
                            toastInMainThread(appContext, "IDiscoverCloudListener finish " + arg0.name());
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.discover_smb:
                try {
                    discoverSmb = printingSdk.startDiscoverSmb(new IDiscoverSmbListener.Stub() {
                        @Override
                        public void start() {
                            toastInMainThread(appContext, "IDiscoverSmbListener start");
                        }

                        @Override
                        public void smbFilesFound(List<SmbFile> arg0) {
                            // Show list of SMB files. This listener is used for navigation.
                            // You should call discoverSmbControl.move(arg0) to change location.
                        }

                        @Override
                        public void showAuthorization() {
                            // You have to ask user for authorization credentials and call discoverSmbControl.login(arg0, arg1);
                        }

                        @Override
                        public void printerFound(List<Printer> arg0) {
                            toastInMainThread(appContext, "IDiscoverSmbListener printerFound");
                            discoveredPrinters.clear();
                            discoveredPrinters.addAll(arg0);
                        }

                        @Override
                        public void finish(Result arg0) {
                            toastInMainThread(appContext, "IDiscoverSmbListener finish " + arg0.name());
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.discover_usb:
                try {
                    printingSdk.startDiscoverUSB(new IDiscoverListener.Stub() {
                        @Override
                        public void start() {
                            toastInMainThread(appContext, "IDiscoverListener start");
                        }

                        @Override
                        public void printerFound(List<Printer> arg0) {
                            toastInMainThread(appContext, "IDiscoverListener printerFound");
                            discoveredPrinters.clear();
                            discoveredPrinters.addAll(arg0);
                        }

                        @Override
                        public void finish(Result arg0) {
                            toastInMainThread(appContext, "IDiscoverListener finish " + arg0.name());
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.find_driver:
                if (!discoveredPrinters.isEmpty()) {
                    Printer printer = discoveredPrinters.get(0);
                    try {
                        printingSdk.findDrivers(printer, new IFindDriversListener.Stub() {
                            @Override
                            public void start() {
                                toastInMainThread(appContext, "IFindDriversListener start");
                            }

                            @Override
                            public void finish(List<DriversSearchEntry> arg0) {
                                toastInMainThread(appContext, "IFindDriversListener finish; Found "
                                        + arg0.size() + " drivers entries;" +
                                        ((arg0.size() == 0) ? "" : ""));
                                driversSearchEntries.clear();
                                driversSearchEntries.addAll(arg0);
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else
                    showDialog(getString(R.string.error), "Discover printers first");
                break;
            case R.id.get_drivers:
                if (!discoveredPrinters.isEmpty()) {
                    Printer printer = discoveredPrinters.get(0);
                    TransportType transportType = printer.getTransportTypes().get(0);
                    if (transportType != null) {
                        try {
                            printingSdk.getDriversList(printer, transportType, new IGetDriversListener.Stub() {
                                @Override
                                public void start() {
                                    toastInMainThread(appContext, "IGetDriversListener start");
                                }

                                @Override
                                public void finish(List<DriverHandleEntry> arg0) {
                                    toastInMainThread(appContext, "IGetDriversListener finish");
                                    driverHandleEntries.clear();
                                    driverHandleEntries.addAll(arg0);
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    showDialog(getString(R.string.error), "Discover printers first");
                break;
            case R.id.setup_recent_printer:
                try {
                    List<Printer> printerList = printingSdk.getRecentPrintersList();
                    if (!printerList.isEmpty())
                        printingSdk.setup(printerList.get(0), true, new ISetupPrinterListener.Stub() {
                            @Override
                            public void start() {
                                toastInMainThread(appContext, "ISetupPrinterListener start");
                            }

                            @Override
                            public void libraryPackInstallationProcess(int arg0) {
                                toastInMainThread(appContext, "ISetupPrinterListener libraryPackInstallationProcess " + arg0 + " %");
                            }

                            @Override
                            public void finish(Result arg0) {
                                toastInMainThread(appContext, "ISetupPrinterListener finish " + arg0.name());
                                if (arg0.getType().equals(ResultType.ERROR_LIBRARY_PACK_NOT_INSTALLED)) {
                                    // printingSdk.setup should be called with forceInstall = true to download required drivers
                                }
                            }
                        });
                    else
                        showDialog(getString(R.string.error), "No recent printers");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.setup_discovered_printer:
                if (!discoveredPrinters.isEmpty()) {
                    if (!driversSearchEntries.isEmpty()) {
                        Printer printer = discoveredPrinters.get(0);
                        DriversSearchEntry driversSearchEntry = driversSearchEntries.get(0);
                        try {
                            printingSdk.setup(printer, driversSearchEntry.getDriverHandlesList().get(0), driversSearchEntry.getTransportType(), false, new ISetupPrinterListener.Stub() {
                                @Override
                                public void start() {
                                    toastInMainThread(appContext, "ISetupPrinterListener start");
                                }

                                @Override
                                public void libraryPackInstallationProcess(int arg0) {
                                    toastInMainThread(appContext, "ISetupPrinterListener libraryPackInstallationProcess " + arg0 + " %");
                                }

                                @Override
                                public void finish(Result arg0) {
                                    toastInMainThread(appContext, "ISetupPrinterListener finish " + arg0.name());
                                    if (arg0.getType().equals(ResultType.ERROR_LIBRARY_PACK_NOT_INSTALLED)) {
                                        // printingSdk.setup should be called with forceInstall = true to download required drivers
                                    }
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else
                        showDialog(getString(R.string.error), "Find driver first");
                } else
                    showDialog(getString(R.string.error), "Discover printers first");
                break;
            case R.id.change_options:
                try {
                    Printer currentPrinter = printingSdk.getCurrentPrinter();
                    if (currentPrinter != null) {
                        List<PrinterOption> options = currentPrinter.getOptions();
                        if (options.size() > 0) {
                            Random random = new Random();
                            PrinterOption option = options.get(random.nextInt(options.size()));
                            PrinterOptionValue currentValue = option.getOptionValue();
                            List<PrinterOptionValue> valuesList = option.getOptionValueList();
                            PrinterOptionValue newValue = valuesList.get(random.nextInt(valuesList.size()));
                            printingSdk.setCurrentPrinterOptionValue(option, newValue);
                            Toast.makeText(requireContext().getApplicationContext(), "option " + option.getName() + " changed from " + currentValue + " to " + newValue, Toast.LENGTH_LONG).show();
                        }
                    } else
                        showDialog(getString(R.string.error), "Setup printer first");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.print_image:
                try {
                    if (printingSdk.getCurrentPrinter() != null) {
                        List<IPage> pages = new ArrayList<>();
                        pages.add(new IPage() {
                            @Override
                            public Bitmap getBitmapFragment(Rect fragment) {
                                Printer printer = null;
                                try {
                                    printer = printingSdk.getCurrentPrinter();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
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
                                            int xDpi = printer.getContext().getHResolution();
                                            int yDpi = printer.getContext().getVResolution();
                                            // in dots
                                            int paperWidth = printer.getContext().getPaperWidth() * xDpi / 72;
                                            int paperHeight = printer.getContext().getPaperHeight() * yDpi / 72;
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
                                } else
                                    return null;
                            }
                        });
                        try {
                            printingSdk.print(pages, 1, new IPrintListener.Stub() {
                                @Override
                                public void startingPrintJob() {
                                    toastInMainThread(appContext, "IPrintListener startingPrintJob");
                                }

                                @Override
                                public void start() {
                                    toastInMainThread(appContext, "IPrintListener start");
                                }

                                @Override
                                public void sendingPage(int arg0, int arg1) {
                                    toastInMainThread(appContext, "IPrintListener sendingPage " + arg0 + "; progress " + arg1 + "%");
                                }

                                @Override
                                public void preparePage(int arg0) {
                                    toastInMainThread(appContext, "IPrintListener preparePage " + arg0);
                                }

                                @Override
                                public boolean needCancel() {
                                    toastInMainThread(appContext, "IPrintListener needCancel");
                                    // Return false if cancel needed.
                                    return false;
                                }

                                @Override
                                public void finishingPrintJob() {
                                    toastInMainThread(appContext, "IPrintListener finishingPrintJob");

                                }

                                @Override
                                public void finish(Result arg0, int arg1, int arg2) {
                                    toastInMainThread(appContext, "IPrintListener finish Result " + arg0 +
                                            "; Result type " + arg0.getType() +
                                            "; Total pages " + arg1 +
                                            "; Pages sent " + arg2);
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else
                        showDialog(getString(R.string.error), "You must setup printer before print");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void toastInMainThread(final Context appContext, final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
