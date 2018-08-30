package shared;

import java.io.File;

/**
 * ObjectParser class is used to cast objects, that have no direct relationship between them
 * from a given model to another [No Inheritance](File -> BasicFileData)
 */
public class ObjectParser {
    private static ObjectParser instance = new ObjectParser();

    /**
     * Empty constructor
     */
    private ObjectParser() {
    }

    /**
     * Get method for Instance
     *
     * @return An instance of the ObjectParser object
     */
    public static ObjectParser getInstance() {
        return instance;
    }

    /**
     * Method used to convert File objects into BasicFileData objects
     *
     * @param fileList Is a list of File objects
     * @return A list of BasicFileData objects
     */
    public BasicFileData[] fileToBasicFileData(File... fileList) {
        BasicFileData[] newList = new BasicFileData[fileList.length];

        /*
        for loop used to iterate over file objects
         */
        for (int i = 0; i < fileList.length; i++)
            newList[i] = new BasicFileData(fileList[i]);

        return newList;
    }
}
