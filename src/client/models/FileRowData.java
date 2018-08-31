package client.models;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import shared.Constants;
import shared.Methods;
import shared.models.BasicFileData;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileRowData extends BasicFileData {

    /**
     * Constructor for the FileRowData object
     *
     * @param file File object to be changed (transformed)
     */
    public FileRowData(File file) {
        super(file);
    }

    /**
     * Method used to get the size of the file
     *
     * @return the size of file in bytes, K-bytes, M-bytes
     */
    public String getSizeInfo() {
        /*
        Check if file is a directory
         */
        if (isDirectory())
            return "";

        else {
            Object[] sizeInfo = Methods.getInstance().reduceSize((double) super.getSize());

            return Methods.getInstance().round((double) sizeInfo[0], 2) + " " + sizeInfo[1];
        }
    }

    /**
     * Method used to extract the extension of the file
     *
     * @return The extension of the file (EXE, PDF, ...)
     */
    private String getExtension() {
        String extension = "";

        int i = getPath().lastIndexOf('.');
        int p = getPath().lastIndexOf(Constants.BACKWARD_DASH);

        if (i > p && i > 0) {
            extension = getPath().substring(i + 1);
        }

        return extension;
    }

    /**
     * Method used to get an icon used in the GUI in displaying the file information
     *
     * @return An icon that represents the file
     */
    public ImageView getIcon() {
        try {
            /*
            Check if file is a directory
             */
            if (isDirectory()) {
                return new ImageView(new Image(getClass()
                        .getResource("/client/resources/images/folder.png").openStream()));
            }

            String extension = this.getExtension();

            /*
            Check if file doesn't have an extension
             */
            if (extension.compareTo("") == 0) {
                return new ImageView(new Image(getClass()
                        .getResource("/client/resources/images/file.png").openStream()));
            }

            /*
            Construct an icon for the file
             */
            File file = File.createTempFile(Constants.FILE_NAME, "." + extension);

            /*
            Get the system icon
             */
            ImageIcon swingImageIcon = (ImageIcon) FileSystemView
                    .getFileSystemView().getSystemIcon(file);

            java.awt.Image awtImage = swingImageIcon.getImage();

            /*
            Cast icon into a BufferedImage object
             */
            BufferedImage bufferedImage = new BufferedImage(
                    awtImage.getWidth(null), awtImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(awtImage, 0, 0, null);
            graphics.dispose();

            /*
            Construct an FX-image
             */
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

            file.deleteOnExit();

            return new ImageView(image);
        } catch (Exception e) {
            e.printStackTrace();

            /*
            In case of failure in generating the file icon return a stock file image
             */
            try {
                return new ImageView(new Image(getClass()
                        .getResource("/client/resources/images/file.png").openStream()));

            } catch (Exception ignored) {
                return null;
            }
        }
    }

    /**
     * Method used to obtain the system type of the file
     *
     * @return The type of the file according to the OS
     */
    public String getType() {
        /*
        Check if file is a directory
         */
        if (isDirectory())
            return "File Folder";

        String extension = this.getExtension();

        /*
        Check if file doesn't have an Extension
         */
        if (extension.compareTo("") == 0)
            return "File";

        else {
            try {
                File file = File.createTempFile(Constants.FILE_NAME, "." + extension);

                String type = FileSystemView.getFileSystemView().getSystemTypeDescription(file);

                file.deleteOnExit();

                if (type == null) {
                    type = extension.toUpperCase() + " File";
                }

                return type;
            } catch (Exception e) {
                e.printStackTrace();
                return "File";
            }
        }
    }

    /**
     * Method used to modify the format of the date
     *
     * @return The reformatted date for the last edit done on the file
     */
    public String getModifiedDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(this.getLastModified());
    }

    /**
     * Method used to extract the file name from the file path
     *
     * @return The name of the file
     */
    public String getName() {
        int i = getPath().lastIndexOf(Constants.BACKWARD_DASH);

        return getPath().substring(i + 1);
    }

    /**
     * Method used to extract the parent of the file from the file path
     *
     * @return The path for the parent of the file
     */
    public String getParent() {
        int i = getPath().lastIndexOf(Constants.BACKWARD_DASH);

        return getPath().substring(0, i);
    }

    /**
     * Method used to obtain parent directory of the file
     *
     * @return The path for the directory at which the parent exists
     */
    public String getPreviousDirectory() {
        String parent = getParent();

        int i = parent.lastIndexOf(Constants.BACKWARD_DASH);

        return parent.substring(0, i + 1);
    }
}
