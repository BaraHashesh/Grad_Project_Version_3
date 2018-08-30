package shared;

import java.util.regex.Pattern;

/**
 * Constants is used to store some constant values
 */
public class Constants {
    /*
    File related constants
     */
    public static final String FORWARD_DASH = "\\";
    public static final String BACKWARD_DASH = "/";
    public static final String DOUBLE_FORWARD_DASH = "\\\\";

    /*
    Message related Constants
     */
    public final static int DOWNLOAD_MESSAGE = 0;
    public final static int UPLOAD_MESSAGE = 1;
    public final static int DELETE_MESSAGE = 2;
    public final static int BROWSE_MESSAGE = 3;
    public final static int ERROR_MESSAGE = 4;
    public final static int SUCCESS_MESSAGE = 5;

    /*
    Time related Constants
     */
    public static final double UPDATE_RATE = 1;
    public static final double WAIT_PERIOD = 2;

    /*
    Socket ports related constants
     */
    public static final int BYTE_PORT = 8_888;
    public static final int STRING_PORT = 9_999;
    public static final int BROAD_CAST_PORT = 11_111;

    /*
    File related Constants
     */
    public static final String FILE_NAME = "TEMP_FILE_";

    /*
    Regex expressions
     */
    public final static Pattern IPV4_REGEX = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
    );
}
