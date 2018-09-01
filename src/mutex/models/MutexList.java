package mutex.models;

import java.util.HashMap;

public class MutexList {

    public static final String DELETEREQUEST = "DELETE";
    public static final String DOWNLOADREQUEST = "DOWNLOAD";
    public static final String UPLOADREQUEST = "UPLOAD";
    private HashMap<String, Mutex> downloadMap = new HashMap<String, Mutex>();
    private HashMap<String, Mutex> uploadMap = new HashMap<String, Mutex>();
    private HashMap<String, Mutex> deleteMap = new HashMap<String, Mutex>();

    /**
     * this method will add mutex lock the given file according to request
     * it accepts the following types of requests {"DELETE","DOWNLOAD","UPLOAD"}
     * if lock already exits then number of the users inside mutex will increase by 1
     *
     * @param path    path includes both path and name of given file (path+"/"+file Name)
     * @param request it takes one of the values {"DELETE","DOWNLOAD","UPLOAD"}
     */
    public void addLock(String path, String request) {

        switch (request) {
            case "DOWNLOAD":
                if (isDownloading(path)) {
                    downloadMap.get(path).increaseNumberOfUsers();
                } else {
                    downloadMap.put(path, new Mutex(path));
                }
                break;
            case "UPLOAD":
                if (isUploading(path)) {
                    uploadMap.get(path).increaseNumberOfUsers();
                } else {
                    uploadMap.put(path, new Mutex(path));
                }

                break;
            case "DELETE":
                if (isDeleting(path)) {
                    deleteMap.get(path).increaseNumberOfUsers();
                } else {
                    deleteMap.put(path, new Mutex(path));
                }

                break;
        }

    }

    /**
     * this method will release given mutex, if only one user was using it then it will
     * remove mutex from map else it will decrease number of users by 1
     *
     * @param path    path includes both path and name of given file (path+"/"+file Name)
     * @param request it takes one of the values {"DELETE","DOWNLOAD","UPLOAD"}
     */
    public void releaseLock(String path, String request) {

        switch (request) {
            case "DOWNLOAD":
                if (isDownloading(path)) {
                    downloadMap.get(path).decreaseNumberOfUsers();
                    if (!(downloadMap.get(path).isBeingUsed())) {
                        downloadMap.remove(path);
                    }
                }

                break;
            case "UPLOAD":
                if (isUploading(path)) {
                    uploadMap.get(path).decreaseNumberOfUsers();
                    if (!(uploadMap.get(path).isBeingUsed())) {
                        uploadMap.remove(path);
                    }
                }

                break;
            case "DELETE":
                if (isDeleting(path)) {
                    deleteMap.get(path).decreaseNumberOfUsers();
                    if (!(deleteMap.get(path).isBeingUsed())) {
                        deleteMap.remove(path);
                    }
                }

                break;
        }
    }

    /**
     * this method takes a request and file then check if request can be executed without
     * collision with other process. It takes three types of requests {"DELETE","DOWNLOAD","UPLOAD"}
     *
     * @param path    path includes both path and name of given file (path+"/"+file Name)
     * @param request it takes one of the values {"DELETE","DOWNLOAD","UPLOAD"}
     * @return will return a message stating the availability of request, if available then
     * return "yes" else will return a message for each scenario
     * if request is unknown type then it will returns "Unknown Request"
     */
    public String isAvailable(String path, String request) {

        switch (request) {
            case "DOWNLOAD":
                return isDownloadAvailable(path);
            case "UPLOAD":
                return isUploadAvailable(path);
            case "DELETE":
                return isDeleteAvailable(path);
        }

        return "Unknown Reqest";
    }

    /**
     * this method checks if the given file can be downloaded without collision
     * method will return string "yes" if delete is available otherwise will return a message
     * that describes the issue
     *
     * @param path includes both path and name of given file (path+"/"+file Name)
     * @return will return a message stating the availability of download, if available then
     * return "yes" else will return a message for each scenario
     */
    public String isDownloadAvailable(String path) {

        if (isUploading(path)) {
            return "Can't download file because this file is being uploaded or replaced by another process";
        } else if (isDeleting(path)) {
            return "Can't download file because this file is being deleted by another process";
        }

        return "yes";
    }

    /**
     * this method checks if the given file can be uploaded without collision
     * method will return string "yes" if delete is available otherwise will return a message
     * that describes the issue
     *
     * @param path includes both path and name of given file (path+"/"+file Name)
     * @return will return a message stating the availability of upload, if available
     * return "yes" else will return a message for each scenario
     */
    public String isUploadAvailable(String path) {

        if (isUploading(path)) {
            return "Can't upload file because another file with same name is being uploaded by another process";
        } else if (isDeleting(path)) {
            return "Can't upload file because another file with same name is being deleted by another process";
        } else if (isDownloading(path)) {
            return "Can't upload file because another file with same name is being downloaded by another process";
        }

        return "yes";
    }

    /**
     * this method checks if the given file can be deleted without collision
     * method will return string "yes" if delete is available otherwise will return a message
     * that describes the issue
     *
     * @param path includes both path and name of given file (path+"/"+file Name)
     * @return will return a message stating the availability of delete, if available then
     * return "yes" else will return a message for each scenario
     */
    public String isDeleteAvailable(String path) {

        if (isUploading(path)) {
            return "Can't delete file because file is being uploaded by another process";
        } else if (isDeleting(path)) {
            return "Can't delete file because this file is being deleted by another process";
        } else if (isDownloading(path)) {
            return "Can't delete file because this file is being downloaded by another process";
        }

        return "yes";
    }

    /**
     * this method check if given file is being downloaded by another process
     *
     * @param path includes both path and name of given file (path+"/"+file Name)
     * @return return true if file is being downloaded or false if not
     */
    public boolean isDownloading(String path) {

        if (downloadMap.containsKey(path))
            return true;

        return false;
    }

    /**
     * this method check if given file is being uploaded by another process
     *
     * @param path includes both path and name of given file (path+"/"+file Name)
     * @return return true if file is being uploaded or false if not
     */
    public boolean isUploading(String path) {
        if (uploadMap.containsKey(path))
            return true;

        return false;
    }

    /**
     * this method check if given file is being deleted by another process
     *
     * @param path includes both path and name of given file (path+"/"+file Name)
     * @return return true if file is being deleted or false if not
     */
    public boolean isDeleting(String path) {
        if (deleteMap.containsKey(path))
            return true;

        return false;
    }
}
