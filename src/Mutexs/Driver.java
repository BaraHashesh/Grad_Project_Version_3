import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Mutex.MutexHandler;

public class Driver {

	public static void main(String [] args) throws FileNotFoundException {
		
		File input = new File("resources/input.txt");
		Scanner s = new Scanner(input);
		String path = s.nextLine();
		
		MutexHandler mutexHandler = MutexHandler.getinstance();
		
		while(mutexHandler.isWorking()); // handler is busy
		mutexHandler.startWorking();
		String msg= mutexHandler.isAvailable(path, MutexHandler.DOWNLOADREQUEST);
		if ( msg.compareTo("yes")==0)
		{
			mutexHandler.addLock(path, MutexHandler.DOWNLOADREQUEST);
			mutexHandler.finishedWorking();
			
			/**
			 *  do your thing here
			 */
			
			mutexHandler.releaseLock(path, MutexHandler.DOWNLOADREQUEST);
		}
		else {
			System.out.println("can't serve yor request");
		}
		
		
		s.close();
		
	}
	
}
