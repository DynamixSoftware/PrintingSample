# PrintingSample
This document described integration with PrintHand, a mobile printing application for Android devices. The purpose of integrating with PrintHand is to provide printing capabilities to applications. There are different ways to integrate with PrintHand depending on the application, its content and other requirements. Sample application shows possible integration options.<br/>
Project was built with Android Studio 1.4.<br/>
# Integration via intent SHARE
This is the easiest way to integrate with PrintHand. It's based on the standard Android intent SHARE which will trigger PrintHand among other applications. PrintHand app needs to be installed on the device. Many Android apps (Gallery, etc.) already have sharing mechanism, and without any knowledge of PrintHand can print their content.<br/>
External application renders its content, saves it as temporary file on the device and fires the intent SHARE typically by clicking on the Share button. In response, all the apps including PrintHand provide menu choices for the user to select. To bypass all the choices and only trigger PrintHand the external application needs to fire the proprietary intent PRINTHAND expecting the same reaction as for SHARE.<br/>
Original application supplies its content as a path to the temporary file. The format of the file is driven by the application itself, not PrintHand. Some content such as Gallery images (.JPEG) is already pre-rendered and does not require any further rendering before preview and eventually printing. Most content however, including office document files (.DOC, .XLS, .PPT) needs to be rendered separately by PrintHand, even when the original application has its own rendering capabilities. It's possible the content will be rendered slightly different by PrintHand compared to the original application.<br/>
PrintHand would do printer detection and configuration, if needed for example when no printer are configured yet, or a new printer is needed. Although the user does not start PrintHand manually, it will go through the same configuration experience including Printer Setup Wizard if needed. Printer configuration steps are skipped next time when the user wants to print to the same printer.<br/>
The user will see the preview of the content, will be able to adjust and fully control settings (page margins, color options, etc.) depending on target printer capabilities, select pages (one, all, any) and finally print.<br/>
This is the easiest and fastest option to integrate with PrintHand. However, in many cases the content needs to be rendered twice which may create fidelity and compatibility issues.<br/>
# Integration via Intent API<br/>
To get better control of the rendering process and printing options, the external application can integrate with PrintHand via its intent API. The main idea is that the application would be aware of the target printer, its capabilities and options. Therefore it can render the content specifically for the printer model, and give the user the options to adjust settings.<br/>
PrintHand application still needs to be installed on the device, but instead of calling it via a single intent SHARE as in "fire and forget" approach, there will be series of calls to PrintHand service:<br/>
<ul>
* Getting default printer info<br/>
* Printer discovering<br/>
* Changing printer settings<br/>
* Supplying printing content for the specific printer<br/>
* Supplying pre-rendered content for the specific printer<br/>
* Printing and getting confirmation (only to the point of data transmission to the printer) 
</ul>
<br/>
The external application would need to provide its user interface for the printing pre-rendered content. Adjusting options may result in re-rendering the content, which is completely handled by the application. The application provides its own preview of the content before printing, however PrintHand's preview can be requested too. Since all the pages are pre-rendered using a set of options on the application site, PrintHand's preview will have only limited control over many options.<br/>
While the external application has much greater control over printing options and preview, PrintHand is fully responsible for printer detection, configuration, driver downloads when needed providing its own User Interface for it. This integration scenario is similar in a way to printing documents from Microsoft Office suite on Windows where the application (i.e. Microsoft Word) controls print preview and options, while actual printing is done through a series of calls to print driver.<br/>
This method is useful for integrating with applications working with complex content such as office documents, maps, etc.<br/>
# Direct Integration with PrintHand Library<br/>
Application can take full control of printing process calling PrintHand library directly. In this scenario PrintHand application does not need to be present on the device, but the library must be included in the target application. The service is running on the background, doesn't directly interact with users and doesn't have any UI elements. This is the same service PrintHand application is calling when providing printing functionality.<br/>
While relying on the Printing Service and using the library, target application is fully responsible for the following form the User Interface standpoint.<br/>
<ul>
* Printer detection<br/>
* Printer configuration<br/>
* Driver selection and download<br/>
* Content preview and rendering<br/>
* Printing and confirmation<br/>
</ul>
For the most part, these UI functions are implemented by PrintHand application, and the app must re-create them, possibly with different visual elements and styles. Although the low-level functions are provided by Printing Service via low-level API implemented in the library, there's substantial knowledge about printing and corresponding logic that need to be provided by the target application. For example, when there's no exact match for the printer driver, it needs to switch to generic one (which may work just fine) or offer alternatives such as Shared Printing.<br/>
The main advantage of this approach is the PrintHand application doesn't need to be installed on the device, and only the Printing Service need to be there, invisible for the user. The data is supplied as binary stream directly. Target application code and PrintHand library work within the same process (the library is embedded into the appliction).<br/>
While providing greater flexibility, this approach also brings significant complexity. It's recommended for applications requiring full control of the printing process.<br/>
Visit [printhand.com](http://printhand.com/integration.php) for more information.
