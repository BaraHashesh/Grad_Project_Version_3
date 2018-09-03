# client.models.connection Package

This package is responsible for storing the source code of any class
responsible for managing a TCP or UDP connection.

## 1. BroadCastSender

This class is responsible for handling a UDP connection, which is used
in broadcast mode in order to discover nearby storage devices.

    Note: This class implements the Runnable interface so it 
    does not hinder the progress of the over all application

## 2. BrowsingClient

This class is responsible for handling a TCP connection, which is used
to perform delete actions, as well as getting files meta data to be displayed to the user.

## 3. Download Client

This class is responsible for handling a TCP connection, which is used
to fetch files & folders from the storage device.

    Note: This class implements the Runnable interface so it 
    does not hinder the progress of the over all application
    
## 4. Upload Client

This class is responsible for handling a TCP connection, which is used
to send files & folders to the storage device.

    Note: This class implements the Runnable interface so it 
    does not hinder the progress of the over all application

## 5. Client Connection Holder

This class is used to store the connection socket as well as the input & output streams of the client.