# controllers Package

This package contains the classes responsible for handling the fxml views,
which are: BrowserController, ChooseBaseIPController, ChooseServerController, LoaderController.

## 1. BrowserController

This controller is used to handle the BrowserView.fxml file, which in turn has 5 possible actions being:

1. Navigate to previous directory.

    This action can be done by pressing the **Back** button.
    
    Action is handled by the `onBackButtonClicked` function.

2. Navigate into a child directory.

    This action can be done by double pressing any row representing a directory.
    
    Action is handled by the `onRowDoubleClick` function.

3. Delete a child file.

    This action can be done by selecting any given row followed by pressing pressing the **Delete** button.
    
    Action is handled by the `onDeleteButtonClicked` function.

4. 

 