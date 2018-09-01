package Mutex;

public class Mutex {

	private String file;
	private int numberOfUsers=0;
	
	public Mutex(String file) {
		this.file = file;
		increaseNumberOfUsers();
	}


	public String getFile() {
		return file;
	}
	
	/**
	 * this method will increase the number of the users using mutex upon call
	 */
	public void increaseNumberOfUsers() {
		numberOfUsers++;
	}
	
	/**
	 * this method will decrease the number of the users using mutex upon call
	 */
	public void decreaseNumberOfUsers() {
		numberOfUsers--;
	}
	
	/**
	 * method checks if this mutex is being used by any users
	 * @return if mutex is being used then will return true, else will return false
	 */
	public boolean isBeingUsed() {
		if(numberOfUsers==0)
			return false;
		return true;
	}
	
}
