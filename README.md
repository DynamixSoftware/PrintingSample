## PrintingSample
This document describes integration with PrintHand, a mobile printing application for Android devices.

**Purpose:** Integration with PrintHand to provide printing capabilities to 3rd party applications.

Depending on the application, its content and other requirements, there are several integration options available.

**Ways to integrate with PrintHand:**

* via Share Intent;
* via PrintHand Intent API;
* via PrintHand Printing SDK;
* using a mix of the said approaches.

PrintingSample application shows all the listed integration options in action.

**Project requirements: Android Studio 2.2.2, gradle plugin 2.2.2, Android build tools 23.0.3, Android support v4 library version 23.2.1, Android v7 appcompat library.**

## Integration via [Share Intent](https://github.com/DynamixSoftware/PrintingSample/blob/master/printingSample/src/main/java/com/dynamixsoftware/printingsample/samples/ShareIntentSample.java)
This is the easiest way to integrate with PrintHand based on the standard Android Share Intent. With Share Intent one can send data to the PrintHand (or to any other app capable of receiving the content type specified when sending the intent).

**Requirements:**

* PrintHand app needs to be installed on the device

Many Android apps (i.e. Gallery) already have sharing mechanism implemented. Without any knowledge of PrintHand such apps can use Share Intent to send their content to be printed.

**Features:**

* Share an image with ACTION_VIEW
* Share an image with ACTION_SEND
* Share multiple images
* Share an image with return to your app
* Share a file
* Share a web page
* Share a web page as a string
* Activate a licence (licence code is needed)
* Activate a licence with return to your app (licence code is needed)

**Example:**
* 3rd party application (i.e. Gallery) sends data with the Share Intent specifying a data type and an URI
This action is typically triggered by tapping on a custom Share button or PrintHand icon in the share action provider (commonly known as Share menu) located in the action bar.
Share action provider launches PrintHand app by default in 2 cases:
    * if the content type sent with the Share Intent can be handled by the PrintHand app only;
    * if the Share Intent specifies the PrintHand’s package name.
* PrintHand app launches and shows the print preview screen with the content being rendered
Most content, including office document files (.doc/.docx, .xls/.xlsx, .ppt/.pptx), needs to be rendered by PrintHand using its own rendering library, even when the original application has its own rendering capabilities. Thus there is a chance that the content will be rendered slightly different by PrintHand compared to the original 3rd party application.
* The user selects a printer (if it has not been configured previously) and adjusts the printing options such as paper size, page margins, color options, etc. and finally hits Print button to print
Printer configuration steps are skipped when the user attempts to print on a printer which has already been configured. 

**Recap:** As you can see, this is the easiest and fastest option to integrate with PrintHand since it handles rendering and all the control over the printout on its own. 

**Our tip:** We recommend sticking to this option if you would like to add printing capabilities to your app with almost zero effort required.

## Integration via [PrintHand Intent API](https://github.com/DynamixSoftware/PrintingSample/blob/master/printingSample/src/main/java/com/dynamixsoftware/printingsample/samples/IntentAPISample.java)
With this integration option you can get better control over the rendering process and printing options. The main idea is that the 3rd party application would be capable of altering the printing options directly using it’s own UI elements. Therefore it can render the content specifically for the specific printer model, and give the user the options to adjust settings.

**Requirements:**
* PrintHand app needs to be installed on the device
* PrintHand Intent API service should be running on the device. 
However, instead of invoking it via a single Share Intent as in the first "fire and forget" approach, there will be series of calls to PrintHand service:
    * Getting default printer info
    * Printer discovering
    * Changing printer settings
    * Supplying printing content for the specific printer
    * Supplying pre-rendered content for the specific printer
    * Printing and getting confirmation (only to the point of data transmission to the printer)
* 3rd party app implements user interface for printing pre-rendered content (in case it does not use the PrintHand’s UI). PrintHand is still fully responsible for printer detection, configuration and driver downloads, providing its own User Interface for it when needed (if such interface is not implemented in the 3rd party app).
* 3rd party app pre-renders content for the print preview (in case it does not use the PrintHand’s UI and preview capabilities). Adjusting options may result in re-rendering the content, which is completely handled by the 3rd party application. The application provides its own preview of the content before printing, However, PrintHand's preview can be requested as well. Yet, since all the pages are pre-rendered using a set of options on the application site, PrintHand's preview will have only limited control over many options.

**Features:**
* Start Intent API service
* Check Premium licence
* Activate licence online
* Set callback
* Setup printer
* Change options
* Get current printer
* Print an image
* Print a file
* Show file preview
* Print with the 3rd party app’s rendering
* Print with the 3rd party app’s rendering without PrintHand’s UI
* Print a test image with PrintHand’s rendering without PrintHand’s UI
* Change image options
* Print a test file with PrintHand’s rendering without PrintHand’s UI
* Print a protected test file with PrintHand’s rendering without PrintHand’s UI
* Change file options

**Example:**
* The user selects some content to be printed in the 3rd party app
* The user detects the printer, sets it up, evaluates the preview (either using the 3rd party app’s UI or the PrintHand’s UI)
* 3rd party application uses its own UI to send a series of calls to the PrintHand in order to make PrintHand discover a printer, alter the printer’s settings, render content for printing and finally send it to the printer
*PrintHand app works in the background (if the 3rd party app uses its own UI instead of the PrintHand’s UI) and responds to the series of calls with corresponding actions requested or interacts with the user explicitly to finally print the content

**Recap:** This integration scenario is similar in a way to printing documents from Microsoft Office suite on Windows where the application (i.e. Microsoft Word) controls print preview and options, while actual printing is done through a series of calls to print driver. This method is useful for integrating with applications working with complex content such as office documents, maps, etc.

## Integration with [PrintHand Printing SDK](https://github.com/DynamixSoftware/PrintingSample/blob/master/printingSample/src/main/java/com/dynamixsoftware/printingsample/samples/ServiceSample.java)
When sticking to this option, 3rd party application can take full control of printing process calling PrintHand Printing SDK directly. The service is running in the background, does not directly interact with users and doesn't have any UI elements. This is the same service PrintHand application is calling when providing printing functionality.
**Requirements:**
* PrintHand Printing SDK service should be running on the device
* PrintHand app or standalone PrintHand service app needs to be installed on the device
* 3rd party application implements UI for the following features:
    * Printer detection
    * Printer configuration
    * Driver selection and download
    * Content preview and rendering
    * Printing and confirmation (only to the point of data transmission to the printer)

For the most part, UI for the said features is implemented by the PrintHand application. However, in this case the 3rd party app should re-create the UI for them using its own visual elements and styles.

Although these low-level functions are provided by the Printing Service via low-level API implemented in the Printing SDK, there's substantial knowledge about printing and corresponding logic that needs to be provided by the 3rd party application.

For example, when there is no specific driver available for the printer, it needs to switch to generic one (which may work just fine) or offer alternatives such as Shared Printing using the PrintHand client for Windows or MacOS.

**Features:**
* Start Printing SDK service
* Set licence
* Init current and recent printers
* Get current printer
* Get Recent printers list
* Discover Wi-Fi printers
* Discover Bluetooth printers
* Discover Google Cloud printers
* Discover SMB printers
* Discover USB printers
* Find driver
* Get drivers list
* Setup recent printer
* Setup discovered printer
* Change printer option
* Print test page
 
**Example:**
* The user selects some content to be printed in the 3rd party app
* The user detects the printer, sets it up, evaluates the preview using the 3rd party app’s UI
* 3rd party application uses its own UI to discover a printer, alter the printer’s settings, render content for printing and finally send it to the PrintHand Printing SDK service
* PrintHand Printing SDK service runs in the background and responds to the series of calls with corresponding actions requested from the 3rd party app’s UI to finally print out the content

**Recap:** While providing greatest flexibility, this approach also brings significant complexity since the 3rd party app is responsible for the UI, rendering and setup. The data is supplied by the 3rd party app as binary stream to the PrintHand Printing service directly. 3rd party application and the PrintHand Printing service run within the same process.

**Our tip:** This option is designed for those who do not want/need the PrintHand app to be installed on the target device or its UI to be invoked due to various reasons (e.g., branding). It is recommended for applications requiring full control of the printing process and the UI.

For more details please visit our web page [For developers](https://printhand.com/integration.php) at [printhand.com](https://printhand.com/).
