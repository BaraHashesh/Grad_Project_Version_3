package Mutex;

public class MutexHandler {

	private static MutexHandler mutexHandler= new MutexHandler();
	private static boolean Working=false;
	private static MutexList mutexList = new MutexList(); 
	public static final String DELETEREQUEST = "DELETE";
	public static final String DOWNLOADREQUEST = "DOWNLOAD";
	public static final String UPLOADREQUEST = "UPLOAD";
	
	public static MutexHandler getinstance() {
		return mutexHandler;
	}
	
	
	/**
	 * this method takes a request and file then check if request can be executed without
	 * collision with other process. It takes three types of requests {"DELETE","DOWNLOAD","UPLOAD"}
	 * 
	 * @param path 		path includes both path and name of given file (path+"/"+file Name)
	 * @param request	it takes one of the values {"DELETE","DOWNLOAD","UPLOAD"}
	 * @return			will return a message stating the availability of request, if available then 
	 * 					return "yes" else will return a message for each scenario 
	 * 					if request is unknown type then it will returns "Unknown Request"
	 */
	public String isAvailable(String path, String request) {
		return mutexList.isAvailable(path, request);
	}

	/**
	 * this method will add lock the given file according to request
	 * it accepts the following types of requests {"DELETE","DOWNLOAD","UPLOAD"}
	 * @param path 		path includes both path and name of given file (path+"/"+file Name)
	 * @param request	it takes one of the values {"DELETE","DOWNLOAD","UPLOAD"}
	 */
	public void addLock(String path, String request) {
		mutexList.addLock(path, request);
	}
	
	
	/**
	 * this method will release a given mutex
	 * @param path 		path includes both path and name of given file (path+"/"+file Name)
	 * @param request	it takes one of the values {"DELETE","DOWNLOAD","UPLOAD"}
	 */
	public void releaseLock(String path, String request) {
		mutexList.releaseLock(path, request);
	}
	
	
	/**
	 * this method will check if handler is busy or not
	 * @return return true if busy, false if not
	 */
	public boolean isWorking() {
		return Working;
	}
	
	/**
	 * this method will declare handler as busy
	 */
	public void startWorking() {
		Working=true;
	}
	
	/**
	 * this method will declare handler as free
	 */
	public void finishedWorking() {
		Working=false;
	}
	
}
