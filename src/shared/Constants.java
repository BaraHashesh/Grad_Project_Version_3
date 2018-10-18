package shared;

import java.util.regex.Pattern;

/**
 * Constants is used to store some constant values
 */
public class Constants {

    public static Constants instance = new Constants();

    /**
     * Get method for instance
     * @return an instance of the {@link Constants} class
     */
    public static Constants getInstance() {
        return instance;
    }

    /*
        File related constants
         */
    public final String BACKWARD_DASH = "/";
    public final String FILE_NAME = "TEMP_FILE_";
    public final String FORWARD_DASH = "\\";
    public final String DOUBLE_FORWARD_DASH = "\\\\";
    public final int BUFFER_SIZE = 1024 * 64;
    /*
    Message related Constants
     */
    public final int DOWNLOAD_MESSAGE = 0;
    public final int UPLOAD_MESSAGE = 1;
    public final int DELETE_MESSAGE = 2;
    public final int BROWSE_MESSAGE = 3;
    public final int ERROR_MESSAGE = 4;
    public final int SUCCESS_MESSAGE = 5;
    public final int FILE_INFO_MESSAGE = 6;
    public final int STREAM_END_MESSAGE = 7;
    public final int UPDATE_MESSAGE = 8;
    /*
    Time related Constants
     */
    public final double UPDATE_RATE = 1;
    public final double WAIT_PERIOD = 2;
    /*
    Regex expressions
     */
    public final Pattern IPV4_REGEX = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
    );
    /*
    Socket ports related constants
     */
    public final int TCP_PORT = 8_888;
    public final int UDP_DISCOVERY_PORT = 9_999;
    /*
    Key Store constants
     */
    public final String KEYSTORE_PASSWORD = "password";
    public final String KEYSTORE_TYPE = "JKS";
    public final String KEY_MANAGER_FACTORY_ALGORITHM = "PKIX";
    public final String TRUSTED_MANAGER_FACTORY_ALGORITHM = "PKIX";
}
