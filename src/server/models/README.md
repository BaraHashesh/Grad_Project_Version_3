# server.models package

This package contains classes/helpers that are used by the server.

## 1. DiscoveryReceiver

This class is used to receive discovery packets, and to replay to them.

    Note: This class implements the Runnable interface so to create a special thread
    to handle it's operations, to not hinder the progress of the over all application

## 2. ServerHandler

This class is used to handle each client individually after his/her connection socket
has been established by the driver (main class). 


    Note: This class implements the Runnable interface so to create a special thread
    to handle it's operations, to not hinder the progress of the over all application

## 3. StorageHandler

This class is used to handle operations related to the storage media, such as: Deleting a file/folder,
getting the sub files/folder of a certain folder, sending files from it, and receiving data to it. 

## 4. UpdateSender

This class is used to send notifications that changes have been applied to the server to all the
available clients, as such notifying them to update they'er GUI (browser view).

