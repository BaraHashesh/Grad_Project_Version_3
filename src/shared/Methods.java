package shared;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Class used to implement redundant methods
 */
public class Methods {
    private static Methods instance = new Methods();

    /**
     * Empty Constructor
     */
    private Methods() {
    }

    /**
     * Get method for instance
     *
     * @return An instance of the Methods Object
     */
    public static Methods getInstance() {
        return instance;
    }

    /**
     * Method used to delete a given file
     *
     * @param file Is the file to be deleted
     */
    public void deleteFile(File file) {
        try {
            /*
            Check if file Exists
             */
            if (file.exists()) {
                /*
                Check if file is a directory
                 */
                if (file.isDirectory()) {
                    /*
                    For loop to iterate over all child files
                     */
                    for (File childFile : Objects.requireNonNull(file.listFiles())) {
                        deleteFile(childFile);
                    }
                }

                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method is used to calculate the total size of file/folder
     *
     * @param file Is the file/folder to be evaluated
     * @return The total size of the file/folder in bytes
     */
    public long calculateSize(File file) {
        long sum = 0;

        /*
        Check if file is a directory
         */
        if (file.isDirectory()) {

            /*
            For loop to iterate over all child files
             */
            for (File childFile : Objects.requireNonNull(file.listFiles())) {

                /*
                Check if child file is a directory
                 */
                if (childFile.isDirectory())
                    sum += calculateSize(childFile);
                else
                    sum += childFile.length();
            }
        } else {
            sum += file.length();
        }
        return sum;
    }

    /**
     * Method used to reduce the size of the file from bytes to KB/MB/GB...
     *
     * @param size Is the size of the file in bytes
     * @return A list containing the reduced size in first element and the order (KB/MB...) in the second
     */
    public Object[] reduceSize(Double size) {

        String sizeOrder = "Byte";

        /*
        Check if the size of the file can be expressed as KB
         */
        if (size >= 1024.0) {
            size = size / 1024.0;
            sizeOrder = "Kilo-Byte";
        }

        /*
        Check if the size of the file can be expressed as MB
         */
        if (size >= 1024.0) {
            size = size / 1024.0;
            sizeOrder = "Mega-Byte";
        }

        return new Object[]{size, sizeOrder};
    }

    /**
     * Method used to round double/float values for a given number of positions
     *
     * @param value    Is the value to round
     * @param position Is how many significant figures to keep (after dot)
     */
    public double round(double value, int position) {
        value = value * Math.pow(10, position);
        value = (double) Math.round(value);
        value = value / Math.pow(10, position);
        return value;
    }

    /**
     * Method used to reduce a given time
     *
     * @param milliSecond Is the time in milli seconds
     * @return A string representing the time in HH:mm:SS format
     */
    public String reduceTime(long milliSecond) {
        return String.format("%02d hours & %02d minutes & %02d seconds",
                TimeUnit.MILLISECONDS.toHours(milliSecond),

                TimeUnit.MILLISECONDS.toMinutes(milliSecond) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSecond)),

                TimeUnit.MILLISECONDS.toSeconds(milliSecond) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSecond)));
    }

    /**
     * Method used to build the SSLSocketFactory
     *
     * @return An SSLSocketFactory
     * @throws Exception Unable to create factory for any given reason
     */
    public SSLSocketFactory buildFactory() throws Exception {
        String STORETYPE = Constants.getInstance().KEYSTORE_TYPE;
        InputStream KEYSTORE = Methods.class.getResourceAsStream("/KEYSTORE");

        String STORE_PASSWORD = Constants.getInstance().KEYSTORE_PASSWORD;
        String KEY_PASSWORD = Constants.getInstance().KEYSTORE_PASSWORD;

        KeyStore ks = KeyStore.getInstance(STORETYPE);
        ks.load(KEYSTORE, STORE_PASSWORD.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(Constants.getInstance().KEY_MANAGER_FACTORY_ALGORITHM);
        kmf.init(ks, KEY_PASSWORD.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(Constants.getInstance().TRUSTED_MANAGER_FACTORY_ALGORITHM);
        tmf.init(ks);

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }
}
