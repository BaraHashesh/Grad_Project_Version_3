package shared;

import com.google.gson.Gson;

/**
 * JsonParser class is used to provide a single instance of Gson object to the application
 */
public class JsonParser {
    private static Gson instance = new Gson();

    /**
     * Get method for instance
     *
     * @return An instance of Gson object
     */
    public static Gson getInstance() {
        return instance;
    }
}
