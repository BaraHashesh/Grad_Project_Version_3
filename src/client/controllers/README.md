# client.controllers Package

This package contains the classes responsible for handling the fxml views,
which are: BrowserController, ChooseBaseIPController, ChooseServerController, LoaderController.

## 1. BrowserController

This controller is used to handle the `BrowserView.fxml` file, which in turn has 7 possible actions being:

1. Navigate to previous directory.

    This action can be done by pressing the **Back** button.
    
    Action is handled by the `onBackButtonClicked` function.

2. Navigate into a child directory.

    This action can be done by double pressing any row representing a directory.
    
    Action is handled by the `onRowDoubleClick` function.

3. Delete a child file.

    This action can be done by selecting any given row followed by pressing the **Delete** button.
    
    Action is handled by the `onDeleteButtonClicked` function.

4. Upload a file. 

    This action can be done by selecting a file using the fx FileChooser object, which appears
    when the **Upload File** button is pressed.
    
    Action is handled by the `onUploadFileButtonClicked` function.

5. Upload a Folder. 

    This action can be done by selecting a folder using the fx DirectoryChooser object, which appears 
    when the **Upload Folder** button is pressed.
    
    Action is handled by the `onUploadFolderButtonClicked` function.
    
6. Download a file/folder.

    This action can be done by selecting any given row followed pressing the **Download** button,
    which will cause an fx DirectoryChooser view to appear to choose where to save the file/folder .
    
    Action is handled by the `onDownloadButtonClicked` function.

7. Refresh the view.

    This action can be done by pressing the **Refresh** button.

    Action is handled by the `onRefreshButtonClicked` function. 
    
# 2. ChooseBaseIPController

This controller is used to handle the `ChooseBaseIPView.fxml` file, which in turn has 2 possible actions being:

1. Search for server.

    This action can be done by entering an IP to the text field, followed by pressing the **Search** button.
    
    Action is handled by the `onSearchButtonClicked` function.
    
2. Close the application.
   
    This action can be done by pressing the **Cancel** button.
       
    Action is handled by the `onCancelButtonClicked` function.

## 3. ChooseServerController

This controller is used to handle the `ChooseServerView.fxml` file, which in turn has 3 possible actions being:

1. Select a server.

    This by choosing a row, followed by pressing the **Select** button.
    
    Action is handled by the `onSelectButtonClicked` function.
    
2. Close the application.
   
    This action can be done by pressing the **Cancel** button.
       
    Action is handled by the `onCancelButtonClicked` function.
    
3. Refresh the view.
    
    This action can be done by pressing the **Refresh** button.

    Action is handled by the `onRefreshButtonClicked` function.

## 4. Estimation Controller 
 
This controller is used to handle the `EstimationView.fxml` file, this controller does not provide the
user with direct actions, but rather is auto-updated by an external thread (controller for the controller), 
this for out application being the `EstimationUpdated.java` class from the `client.models.controllers` package.
 
## 5. Loader Controller

This controller is used to handle the `LoaderView.fxml` file, this controller does not provide the
user with direct actions.