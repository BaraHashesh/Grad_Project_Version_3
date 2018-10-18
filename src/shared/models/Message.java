package shared.models;

import shared.Constants;

/**
 * Message object is used to exchange simple messages between server and client
 */
public class Message {
    private int messageType;
    private String messageInfo;

    /**
     * Constructor for Message object
     */
    public Message() {
    }

    /**
     * Get method for messageInfo
     *
     * @return Extra information about the message
     */
    public String getMessageInfo() {
        return messageInfo;
    }

    /**
     * Method used for checking if current message is a download message
     *
     * @return A boolean that indicates if the message is a download message or not
     */
    public boolean isDownloadMessage() {
        return this.messageType == Constants.getInstance().DOWNLOAD_MESSAGE;
    }

    /**
     * Method used for checking if current message is an upload message
     *
     * @return a boolean that indicates if the message is an upload message or not
     */
    public boolean isUploadMessage() {
        return this.messageType == Constants.getInstance().UPLOAD_MESSAGE;
    }

    /**
     * Method used for checking if current message is a delete message
     *
     * @return a boolean that indicates if the message is a delete message or not
     */
    public boolean isDeleteMessage() {
        return this.messageType == Constants.getInstance().DELETE_MESSAGE;
    }

    /**
     * Method used for checking if current message is a browse message
     *
     * @return a boolean that indicates if the message is a download browse or not
     */
    public boolean isBrowseMessage() {
        return this.messageType == Constants.getInstance().BROWSE_MESSAGE;
    }

    /**
     * Method used for checking if current message is an error message
     *
     * @return a boolean that indicates if the message is an error message or not
     */
    public boolean isErrorMessage() {
        return this.messageType == Constants.getInstance().ERROR_MESSAGE;
    }

    /**
     * Method used for checking if current message is a success message
     *
     * @return a boolean that indicates if the message is a success message or not
     */
    public boolean isSuccessMessage() {
        return this.messageType == Constants.getInstance().SUCCESS_MESSAGE;
    }

    /**
     * Method used for checking if current message is a file info message
     *
     * @return a boolean that indicates if the message is a file info message or not
     */
    @SuppressWarnings("unused")
    public boolean isFileInfoMessage() {
        return this.messageType == Constants.getInstance().FILE_INFO_MESSAGE;
    }

    /**
     * Method used for checking if current message is a stream end message
     *
     * @return a boolean that indicates if the message is a stream end message or not
     */
    public boolean isStreamEndMessage() {
        return this.messageType == Constants.getInstance().STREAM_END_MESSAGE;
    }

    /**
     * Method used for checking if current message is an update message
     *
     * @return a boolean that indicates if the message is an update message or not
     */
    public boolean isUpdateMessage() {
        return this.messageType == Constants.getInstance().UPDATE_MESSAGE;
    }

    /**
     * Method used to create a download message
     *
     * @param info Some extra info for the message (usually file path)
     */
    public void createDownloadMessage(String info) {
        this.messageType = Constants.getInstance().DOWNLOAD_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create an upload message
     *
     * @param info Some extra info for the message (usually where to save file)
     */
    public void createUploadMessage(String info) {
        this.messageType = Constants.getInstance().UPLOAD_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create a delete message
     *
     * @param info Some extra info for the message (usually file path)
     */
    public void createDeleteMessage(String info) {
        this.messageType = Constants.getInstance().DELETE_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create a browse message
     *
     * @param info Some extra info for the message (usually file path)
     */
    public void createBrowseMessage(String info) {
        this.messageType = Constants.getInstance().BROWSE_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create a success message
     *
     * @param info Some extra info for the message (usually empty)
     */
    public void createSuccessMessage(String info) {
        this.messageType = Constants.getInstance().SUCCESS_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * method used to create an error message
     *
     * @param info Some extra info for the message (usually empty)
     */
    public void createErrorMessage(String info) {
        this.messageType = Constants.getInstance().ERROR_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * method used to create a file info message
     *
     * @param info Some extra info for the message (usually a BasicFileData object)
     */
    public void createFileInfoMessage(String info) {
        this.messageType = Constants.getInstance().FILE_INFO_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * method used to create a stream end message
     *
     * @param info Some extra info for the message (usually empty)
     */
    public void createStreamEndMessage(String info) {
        this.messageType = Constants.getInstance().STREAM_END_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * method used to create an update message
     *
     * @param info Some extra info for the message (usually path to update)
     */
    public void createUpdateMessage(String info) {
        this.messageType = Constants.getInstance().UPDATE_MESSAGE;
        this.messageInfo = info;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", messageInfo='" + messageInfo + '\'' +
                '}';
    }
}
