package server.interfaces;

import java.io.IOException;
import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

public interface DCRSInterface extends Remote {
	
	// Advisor Functions
	public String addCourse(String courseID,String semester) throws RemoteException, IOException;
	
	public String removeCourse(String courseID,String semester)throws RemoteException, IOException;
	
	public ArrayList<String> listCourseAvailability(String semester)throws RemoteException, IOException;
	
	//Student Functions
	public String enrolCourse(String studentID,String courseID,String semester) throws RemoteException, IOException;
	
	public HashMap<String, ArrayList<String>> getClassSchedule(String studentID)  throws RemoteException, IOException;
	
	public String dropCourse(String studentID,String courseID) throws RemoteException, IOException;
	
	//helper methods
	public int checkRegistration(String studentId) throws RemoteException;
	
	public String registerNewUser(String studentId) throws RemoteException, IOException;
	
	public void setUser(String user) throws RemoteException;
	
	public String getUser() throws RemoteException;
	
	public void setCapacity(int capacity) throws RemoteException;
	
	public int getCapacity() throws RemoteException;
	
	
	
	
}
