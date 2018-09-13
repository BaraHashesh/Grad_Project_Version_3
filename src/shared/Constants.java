package shared;

import java.util.regex.Pattern;

/**
 * Constants is used to store some constant values
 */
public class Constants {
    /*
    File related constants
     */
    static final String FORWARD_DASH = "\\";
    public static final String BACKWARD_DASH = "/";
    static final String DOUBLE_FORWARD_DASH = "\\\\";
    public static final String FILE_NAME = "TEMP_FILE_";
    static final int BUFFER_SIZE = 1024 * 64;

    /*
    Message related Constants
     */
    public final static int DOWNLOAD_MESSAGE = 0;
    public final static int UPLOAD_MESSAGE = 1;
    public final static int DELETE_MESSAGE = 2;
    public final static int BROWSE_MESSAGE = 3;
    public final static int ERROR_MESSAGE = 4;
    public final static int SUCCESS_MESSAGE = 5;
    public final static int FILE_INFO_MESSAGE = 6;
    public final static int STREAM_END_MESSAGE = 7;

    /*
    Time related Constants
     */
    public static final double UPDATE_RATE = 1;
    public static final double WAIT_PERIOD = 2;

    /*
    Socket ports related constants
     */
    static final int TCP_CONNECTION_PORT = 8_888;
    public static final int UDP_UPDATE_PORT = 9_999;
    public static final int UDP_DISCOVERY_PORT = 11_111;

    /*
    Regex expressions
     */
    public final static Pattern IPV4_REGEX = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
    );
}
