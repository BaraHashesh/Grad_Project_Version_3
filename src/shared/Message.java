package shared;

/**
 * Message object is used to exchange simple messages between server and client
 */
public class Message {
    private final static int DOWNLOAD_MESSAGE = 0;
    private final static int UPLOAD_MESSAGE = 1;
    private final static int DELETE_MESSAGE = 2;
    private final static int BROWSE_MESSAGE = 3;
    private final static int ERROR_MESSAGE = 4;
    private final static int SUCCESS_MESSAGE = 5;

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
        return this.messageType == Message.DOWNLOAD_MESSAGE;
    }

    /**
     * Method used for checking if current message is an upload message
     *
     * @return a boolean that indicates if the message is an upload message or not
     */
    public boolean isUploadMessage() {
        return this.messageType == Message.UPLOAD_MESSAGE;
    }

    /**
     * Method used for checking if current message is a delete message
     *
     * @return a boolean that indicates if the message is a delete message or not
     */
    public boolean isDeleteMessage() {
        return this.messageType == Message.DELETE_MESSAGE;
    }

    /**
     * Method used for checking if current message is a browse message
     *
     * @return a boolean that indicates if the message is a download browse or not
     */
    public boolean isBrowseMessage() {
        return this.messageType == Message.BROWSE_MESSAGE;
    }

    /**
     * Method used for checking if current message is an error message
     *
     * @return a boolean that indicates if the message is an error message or not
     */
    public boolean isErrorMessage() {
        return this.messageType == Message.ERROR_MESSAGE;
    }

    /**
     * Method used for checking if current message is a success message
     *
     * @return a boolean that indicates if the message is a success message or not
     */
    public boolean isSuccessMessage() {
        return this.messageType == Message.SUCCESS_MESSAGE;
    }

    /**
     * Method used to create a download message
     *
     * @param info Some extra info for the message (usually file path)
     */
    public void createDownloadMessage(String info) {
        this.messageType = Message.DOWNLOAD_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create an upload message
     *
     * @param info Some extra info for the message (usually where to save file)
     */
    public void createUploadMessage(String info) {
        this.messageType = Message.UPLOAD_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create a delete message
     *
     * @param info Some extra info for the message (usually file path)
     */
    public void createDeleteMessage(String info) {
        this.messageType = Message.DELETE_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create a browse message
     *
     * @param info Some extra info for the message (usually file path)
     */
    public void createBrowseMessage(String info) {
        this.messageType = Message.BROWSE_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * Method used to create a success message
     *
     * @param info Some extra info for the message (usually empty)
     */
    public void createSuccessMessage(String info) {
        this.messageType = Message.SUCCESS_MESSAGE;
        this.messageInfo = info;
    }

    /**
     * method used to create an error message
     *
     * @param info Some extra info for the message (usually empty)
     */
    public void createErrorMessage(String info) {
        this.messageType = Message.ERROR_MESSAGE;
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
