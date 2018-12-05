package server.interfaceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bean.Course;
import server.interfaces.DCRSInterface;
import server.servers.COMPServer;
import server.servers.INSEServer;
import server.servers.SOENServer;

public class DCRSImpl extends UnicastRemoteObject implements DCRSInterface {

	private static String user;
	private static int capacity;
	private String otherDept="";
	private static final String FALL="FALL";
	private static final String SUMMER="SUMMER";
	private static final String WINTER="WINTER";

	private static final String COMP="COMP";
	private static final String SOEN="SOEN";
	private static final String INSE="INSE";
	
	@Override
	public void setUser(String user) throws RemoteException {
		this.user=user;
	}

	@Override
	public String getUser() throws RemoteException {
		return this.user;
	}

	@Override
	public void setCapacity(int capacity) throws RemoteException {
		this.capacity=capacity;
	}

	@Override
	public int getCapacity() throws RemoteException {
		return this.capacity;
	}

	public DCRSImpl() throws RemoteException {
		super();
	}

	
	public String addCourse(String courseID, String semester) throws IOException {
		
		String requestType="Request Type : Add course";
		String requestParameters="Request Parameters :  1.Course Id : "+courseID+" 2.Semester : "+semester;
		String requestStatus;
		String serverResponse;
		HashMap<String, HashMap<String, Course>> courses=new HashMap<>();
		ArrayList<String> registeredStudents=new ArrayList<>();
		if(user.contains(COMP)){
			courses=COMPServer.courses;
			}
		else if(user.contains(SOEN)){
			courses=SOENServer.courses;
			}
		else {
			courses=INSEServer.courses;
			}
	
			if(!courses.get(semester).containsKey(courseID)){
					Course course = new Course(courseID, capacity, capacity);
					course.setRegisteredStudents(registeredStudents);
					courses.get(semester).put(courseID, course);
					requestStatus="Request completion : success";
					serverResponse="Server Response : "+courseID+" added successfully in "+semester;
					enterLog(user,user, requestType, requestParameters, requestStatus, serverResponse);
					return courseID+" added successfully in "+semester;
				}
				else{
					requestStatus="Request completion : failed";
					serverResponse="Server Response : "+courseID+" is already present in "+semester;
					enterLog(user, user,requestType, requestParameters, requestStatus, serverResponse);
					return courseID+" is already present in "+semester;
				}
	}

	@Override
	public String removeCourse(String courseID, String semester) throws IOException {
		
		String requestType;
		String requestParameters="Request Parameters :  1.Course Id : "+courseID+" 2.Semester : "+semester;
		String requestStatus;
		String serverResponse;
		HashMap<String, HashMap<String, Course>> courses=new HashMap<>();
		HashMap<String, HashMap<String, ArrayList<String>>> students=new HashMap<>();
		if(user.contains(COMP)){
			courses=COMPServer.courses;
			students=COMPServer.students;
		}
		else if(user.contains(SOEN)){
			courses=SOENServer.courses;
			students=SOENServer.students;
		}
		else {
			courses=INSEServer.courses;
			students=INSEServer.students;
		}
		
			if(courses.get(semester).containsKey(courseID)){
				synchronized (this) {
				ArrayList<String> registeredStudents = courses.get(semester).get(courseID).getRegisteredStudents();
				if(registeredStudents.size()>0){
					for (int i = 0; i < registeredStudents.size(); i++) {
						String studentId=registeredStudents.get(i);
						students.get(studentId).get(semester).remove(courseID);
						requestType="CourseRemoved";
						enterLog(user, studentId, requestType, courseID, semester, "");
						}
					}
				courses.get(semester).remove(courseID);
				}
			requestType="Request Type : Remove course";
			requestStatus="Request completion : success";
			serverResponse="Server Response : "+courseID+" removed successfully from "+semester;
			enterLog(user,user, requestType, requestParameters, requestStatus, serverResponse);
			return courseID+" removed successfully from "+semester;
		}
		else{
			requestType="Request Type : Remove course";
			requestStatus="Request completion : failed";
			serverResponse="Server Response : "+courseID+" is not present in "+semester;
			enterLog(user, user,requestType, requestParameters, requestStatus, serverResponse);
			return courseID+" is not present in "+semester;
		}
	}
	
	private ArrayList<String> formatString(String courses){
		ArrayList<String> formattedCourses=new ArrayList<>();
		String[] soenArr=courses.split(" ");
		for(int i=0;i<soenArr.length-1;i=i+2){
			formattedCourses.add(soenArr[i]+" "+soenArr[i+1]);
		}
		return formattedCourses;
	}
	
	@Override
	public ArrayList<String> listCourseAvailability(String semester) throws IOException {
		
		ArrayList<String> courseAvailability=new ArrayList<>();
		HashMap<String, HashMap<String, Course>> courses=new HashMap<>();
		
		if(user.contains(COMP)){
			courses=COMPServer.courses;
			otherDept="SOEN INSE";
			// contact other servers for their course availability
			String soenCourses = COMPServer.sendMessage(2222, semester);
			courseAvailability.addAll(formatString(soenCourses));
			
			String inseCourses = COMPServer.sendMessage(3333, semester);
			courseAvailability.addAll(formatString(inseCourses));
			
		}
		else if(user.contains(SOEN)){
			courses=SOENServer.courses;
			otherDept="COMP INSE";
			// contact other servers for their course availability
			String compCourses = SOENServer.sendMessage(1111, semester);
			courseAvailability.addAll(formatString(compCourses));
			
			String inseCourses = SOENServer.sendMessage(3333, semester);
			courseAvailability.addAll(formatString(inseCourses));
		}
		else {
			courses=INSEServer.courses;
			otherDept="SOEN COMP";
			// contact other servers for their course availability
			String compCourses = INSEServer.sendMessage(1111, semester);
			courseAvailability.addAll(formatString(compCourses));
			
			String soenCourses = INSEServer.sendMessage(2222, semester);
			courseAvailability.addAll(formatString(soenCourses));
		}
		HashMap<String, Course> semCourses=courses.get(semester);
		for(Map.Entry<String,Course> row:semCourses.entrySet()){
			courseAvailability.add(row.getKey()+" "+row.getValue().getVacantSeats());
		}
		String requestType="Request Type : List Course Availability";
		String requestParameters="Request Parameters : Semester : "+semester;
		String requestStatus="Request Status : Success";
		String serverResponse="Server Response : "+courseAvailability.toString();
		enterLog(user, user, requestType, requestParameters, requestStatus, serverResponse);
	return courseAvailability;
	}

	@Override
	public String enrolCourse(String studentID, String courseID, String semester) throws IOException{
		
		String requestType="Request Type : Enroll in a course";
		String requestParameters="Request Parameters : 1.Student Id : "+studentID+" 2.Course Id : "+courseID+" 3.Semester : "+semester;
		String requestStatus;
		String serverResponse;
		String studentType=studentID.substring(0, 4);
		HashMap<String, HashMap<String, Course>> courses=new HashMap<>();
		HashMap<String, HashMap<String, ArrayList<String>>> students=new HashMap<>();
		if(studentType.equals(COMP)){
			courses=COMPServer.courses;
			students=COMPServer.students;
		}
		else if(studentType.equals(SOEN)){
			courses=SOENServer.courses;
			students=SOENServer.students;
		}
		else {
			courses=INSEServer.courses;
			students=INSEServer.students;
		}
		
		// Check if student is  already registered or not
		if(students.get(studentID).get(FALL).contains(courseID) || students.get(studentID).get(WINTER).contains(courseID) || students.get(studentID).get(SUMMER).contains(courseID)){
			// Check if student is registered, then in which term is he/she registered
			if(students.get(studentID).get(FALL).contains(courseID)){
				
				requestStatus="Request completion : failed";
				serverResponse="Server Response : You are already registered for this course in Fall term";
				enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
				return "You are already registered for this course in Fall term";
				
			}
			else if(students.get(studentID).get(WINTER).contains(courseID)){
				
				requestStatus="Request completion : failed";
				serverResponse="Server Response : You are already registered for this course in Winter term";
				enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
				return "You are already registered for this course in Winter term";
				
			}
			else{
			
				requestStatus="Request completion : failed";
				serverResponse="Server Response : You are already registered for this course in Summer term";
				enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
				return "You are already registered for this course in Summer term";
			}
		}
		// if student is already registered in 3 courses for the requested term
		if(students.get(studentID).get(semester).size()==3){
			
			requestStatus="Request completion : failed";
			serverResponse="Server Response : You are already registered for 3 courses in "+semester;
			enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
			return "You are already registered for 3 courses in "+semester;
			
		}
		// Check if course is available on server or not
		if(studentID.substring(0, 4).equals(courseID.substring(0,4))){
			if(courses.get(semester).containsKey(courseID)){
			
				synchronized (this) {
					int vacantSeats=courses.get(semester).get(courseID).getVacantSeats();
					if(vacantSeats>0){
						//Adding course to student's list of registered courses
						students.get(studentID).get(semester).add(courseID);
						//Updating vacant seats number of the course
						courses.get(semester).get(courseID).setVacantSeats(vacantSeats-1);
						courses.get(semester).get(courseID).getRegisteredStudents().add(studentID);
						requestStatus="Request completion : success";
						serverResponse="Server Response : You are registered in "+courseID+" in "+semester;
						enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
						return "Registered in "+courseID;
					}
					else{
						requestStatus="Request completion : failed";
						serverResponse="Server Response : "+courseID+" is full in "+semester;
						enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
						return courseID+" is full in "+semester;
						}
					}
				}
				else{
					//updating student log file
					requestStatus="Request completion : failed";
					serverResponse="Server Response : "+courseID+" is not available in "+semester;
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					return courseID+" is not available in "+semester;
					}
				}
		else{
			Iterator it=students.get(studentID).entrySet().iterator();
			int otherCourseCount=0;
			while(it.hasNext()){
				Map.Entry pair=(Map.Entry)it.next();
					ArrayList<String> studentCourses=(ArrayList<String>) pair.getValue();
					for (int i=0;i<studentCourses.size();i++){
						if(!studentCourses.get(i).contains(studentType)){
							otherCourseCount++;
						}
					}
				}
				if(otherCourseCount==2){
					requestStatus="Request completion : failed";
					serverResponse="Server Response : You are already registered in 2 out of department courses";
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					return "You are already registered in 2 out of department courses";
				}
				else{
					// contact corresponding server
					if(studentID.contains(COMP)){
						synchronized (this) {
							if(courseID.contains(SOEN)){
								otherDept=SOEN;
							String response=COMPServer.sendMessage(2222,courseID+" "+semester+" "+studentID+" enroll");
							return reactToResponse(students,response,courseID,semester,studentID);
						}else if(courseID.contains(INSE)){
							otherDept=INSE;
							String response=COMPServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" enroll");
							return reactToResponse(students,response,courseID,semester,studentID);
						}
						}
					}
					else if(studentID.contains(SOEN)){
						synchronized (this) {
						if(courseID.contains(COMP)){
							otherDept=COMP;
							String response=SOENServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" enroll");
							return reactToResponse(students,response,courseID,semester,studentID);
						}
						else if(courseID.contains(INSE)){
							otherDept=INSE;
							String response=SOENServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" enroll");
							return reactToResponse(students,response,courseID,semester,studentID);
						}
						}
					}
					else if(studentID.contains(INSE)){
						synchronized (this) {
						if(courseID.contains(COMP)){
							otherDept=COMP;
							String response=INSEServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" enroll");
							return reactToResponse(students,response,courseID,semester,studentID);
						}
						else if(courseID.contains(SOEN)){
							otherDept=SOEN;
							String response=INSEServer.sendMessage(2222,courseID+" "+semester+" "+studentID+" enroll");
							return reactToResponse(students,response,courseID,semester,studentID);
						}
					}
					}
				}
			
			return "";
		}
		
	}
	
	private String reactToResponse(HashMap<String, HashMap<String, ArrayList<String>>> students,String response, String courseId,String semester,String studentId) throws IOException{
		if(response.trim().equals("Dropped")){
			String requestType="Request Type : Drop a course";
			String requestParameters="Request Parameters : 1.Student Id : "+studentId+" 2.Course Id : "+courseId;
			students.get(studentId).get(semester).remove(courseId);
			String requestStatus="Request completion : success";
			String serverResponse="Server Response : You have dropped "+courseId+" from "+semester;
			enterLog(user,studentId, requestType, requestParameters, requestStatus, serverResponse);
			return "Dropped "+courseId;
		}
		else{
			String requestType="Request Type : Enroll in a course";
			String requestParameters="Request Parameters : 1.Student Id : "+studentId+" 2.Course Id : "+courseId+" 3.Semester : "+semester;
			if(response.trim().equals("Registered")){
				students.get(studentId).get(semester).add(courseId);
				String requestStatus="Request completion : success";
				String serverResponse="Server Response : You are registered in "+courseId+" in "+semester;
				enterLog(user,studentId, requestType, requestParameters, requestStatus, serverResponse);
				return "Registered in "+courseId;
			}
			else if(response.trim().equals("Full")){
				String requestStatus="Request completion : failed";
				String serverResponse="Server Response : "+courseId+" is full in "+semester;
				enterLog(user,studentId, requestType, requestParameters, requestStatus, serverResponse);
				return courseId+" is full in "+semester;
			}
			else{
				String requestStatus="Request completion : failed";
				String serverResponse="Server Response : "+courseId+" is not available in "+semester;
				enterLog(user,studentId, requestType, requestParameters, requestStatus, serverResponse);
				return courseId+" is not available in "+semester;
			}
		}
	}
	
	@Override
	public HashMap<String, ArrayList<String>> getClassSchedule(String studentID) throws IOException {
		
		String studentType=studentID.substring(0, 4);
		HashMap<String, HashMap<String, ArrayList<String>>> students=new HashMap<>();
		String requestType="Request Type : Get Class Schedule";
		String requestParameters="Request Parameters : 1.Student Id : "+studentID;
		String requestStatus="Request completion : success";
		String serverResponse="Server Response : [";
		if(studentType.equals(COMP)){
			students=COMPServer.students;
		}
		else if(studentType.equals(SOEN)){
			students=SOENServer.students;
		}
		else {
			students=INSEServer.students;
		}
		HashMap<String, ArrayList<String>> schedule=students.get(studentID);
		int count=0;
		for (Map.Entry<String, ArrayList<String>> row : schedule.entrySet()) {
			serverResponse=serverResponse+row.getKey()+":{ ";
			for (String course : row.getValue()) {
				serverResponse=serverResponse+course+" ";
			}
			count++;
			if(count==3){
			serverResponse=serverResponse+"}";
			}else{
				serverResponse=serverResponse+"},";
			}
		}
		serverResponse=serverResponse+"]";
		enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
		return schedule;
	}

	@Override
	public String dropCourse(String studentID, String courseID) throws IOException {
		
		String requestType="Request Type : Drop a course";
		String requestParameters="Request Parameters : 1.Student Id : "+studentID+" 2.Course Id : "+courseID;
		String requestStatus;
		String serverResponse;
		String studentType=studentID.substring(0, 4);
		HashMap<String, HashMap<String, Course>> courses=new HashMap<>();
		HashMap<String, HashMap<String, ArrayList<String>>> students=new HashMap<>();
		if(studentType.equals(COMP)){
			courses=COMPServer.courses;
			students=COMPServer.students;
		}
		else if(studentType.equals(SOEN)){
			courses=SOENServer.courses;
			students=SOENServer.students;
		}
		else {
			courses=INSEServer.courses;
			students=INSEServer.students;
		}
		
		if(!students.get(studentID).get(FALL).contains(courseID) && !students.get(studentID).get(WINTER).contains(courseID) && !students.get(studentID).get(SUMMER).contains(courseID)){
			requestStatus="Request completion : failed";
			serverResponse="Server Response : You are not registered in this course";
			enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
			return "You are not registered in this course";
		}
		else{
			if(studentType.equals(courseID.substring(0,4))){
				if(students.get(studentID).get(FALL).contains(courseID)){
					synchronized(this){
					students.get(studentID).get(FALL).remove(courseID);
					courses.get(FALL).get(courseID).setVacantSeats(courses.get(FALL).get(courseID).getVacantSeats()+1);
					courses.get(FALL).get(courseID).getRegisteredStudents().remove(studentID);
					}
					requestStatus="Request completion : success";
					serverResponse="Server Response : You have successfully dropped "+courseID+" from Fall term";
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					return "You have successfully dropped "+courseID+" from Fall term";
				}
				else if(students.get(studentID).get(WINTER).contains(courseID)){
					synchronized(this){
					students.get(studentID).get(WINTER).remove(courseID);
					courses.get(WINTER).get(courseID).setVacantSeats(courses.get(WINTER).get(courseID).getVacantSeats()+1);
					courses.get(WINTER).get(courseID).getRegisteredStudents().remove(studentID);
					}
					requestStatus="Request completion : success";
					serverResponse="Server Response : You have successfully dropped "+courseID+" from Winter term";
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					return "You have successfully dropped "+courseID+" from Winter term";
				}
				else if(students.get(studentID).get(SUMMER).contains(courseID)){
					synchronized(this){
					students.get(studentID).get(SUMMER).remove(courseID);
					courses.get(SUMMER).get(courseID).setVacantSeats(courses.get(SUMMER).get(courseID).getVacantSeats()+1);
					courses.get(SUMMER).get(courseID).getRegisteredStudents().remove(studentID);
					}
					requestStatus="Request completion : success";
					serverResponse="Server Response : You have successfully dropped "+courseID+" from Summer term";
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					return "You have successfully dropped "+courseID+" from Summer term";
				}
			}
			// contact soen or inse server to remove course
			else{
				String semester=null;
				if(students.get(studentID).get(FALL).contains(courseID)){
					semester=FALL;
				}
				else if(students.get(studentID).get(WINTER).contains(courseID)){
					semester=WINTER;
				}
				else if(students.get(studentID).get(SUMMER).contains(courseID)){
					semester=SUMMER;
				}
				
				// contact corresponding server
				if(studentID.contains(COMP)){
					synchronized (this) {
						if(courseID.contains(SOEN)){
						otherDept=SOEN;
						String response=COMPServer.sendMessage(2222,courseID+" "+semester+" "+studentID+" drop");
						return reactToResponse(students,response,courseID,semester,studentID);
					}else if(courseID.contains(INSE)){
						otherDept=INSE;
						String response=COMPServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" drop");
						return reactToResponse(students,response,courseID,semester,studentID);
					}
					}
				}
				else if(studentID.contains(SOEN)){
					synchronized (this) {
					if(courseID.contains(COMP)){
						otherDept=COMP;
						String response=SOENServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" drop");
						return reactToResponse(students,response,courseID,semester,studentID);
					}
					else if(courseID.contains(INSE)){
						otherDept=INSE;
						String response=SOENServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" drop");
						return reactToResponse(students,response,courseID,semester,studentID);
					}
					}
				}
				else if(studentID.contains(INSE)){
					synchronized (this) {
					if(courseID.contains(COMP)){
						otherDept=COMP;
						String response=INSEServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" drop");
						return reactToResponse(students,response,courseID,semester,studentID);
					}
					else if(courseID.contains(SOEN)){
						otherDept=SOEN;
						String response=INSEServer.sendMessage(2222,courseID+" "+semester+" "+studentID+" drop");
						return reactToResponse(students,response,courseID,semester,studentID);
					}
				}
				}
			}
		}
		return courseID;
	}

public int checkRegistration(String id){
		HashMap<String, HashMap<String, ArrayList<String>>> students=new HashMap<>();
		ArrayList<String> advisors=new ArrayList<>();
				
		char userType=id.charAt(4);
		String deptType=id.substring(0, 4);
		if(deptType.equals(COMP)){
			if(userType=='S')
				students=COMPServer.students;
			else
				advisors=COMPServer.advisors;
				
		}
		else if(deptType.equals(SOEN)){
			if(userType=='S')
				students=SOENServer.students;
			else
				advisors=SOENServer.advisors;
			}
		else {
			if(userType=='S')
				students=INSEServer.students;
			else
				advisors=INSEServer.advisors;
			}
		if(userType=='S'){
			if (!students.containsKey(id)) {
				return 0;
			}else
				return 1;
		}
		else{
			if (!advisors.contains(id)) {
			return 0;
		}else
			return 1;
		}
	}
	
public String registerNewUser(String userType) throws IOException{
	String newId=null;
	if(userType.charAt(4)=='S'){
	HashMap<String, HashMap<String, ArrayList<String>>> students=new HashMap<>();
	FileWriter serverFile;
	BufferedWriter serverBufferedWriter;
	PrintWriter serverPw;
	DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date=new Date();
	if(userType.contains(COMP)){
		students=COMPServer.students;
		serverFile=new FileWriter("COMPServer.txt",true);
		serverBufferedWriter=new BufferedWriter(serverFile);
		serverPw=new PrintWriter(serverBufferedWriter);
	}
	else if(userType.contains(SOEN)){
		students=SOENServer.students;
		serverFile=new FileWriter("SOENServer.txt",true);
		serverBufferedWriter=new BufferedWriter(serverFile);
		serverPw=new PrintWriter(serverBufferedWriter);
	}
	else {
		students=INSEServer.students;
		serverFile=new FileWriter("INSEServer.txt",true);
		serverBufferedWriter=new BufferedWriter(serverFile);
		serverPw=new PrintWriter(serverBufferedWriter);
	}
		synchronized(this){
				int idPart=students.size()+1000;
				newId=userType+idPart;
				ArrayList<String> fallCourses=new ArrayList<>();
				//'winterCourses' array-list
				ArrayList<String> winterCourses=new ArrayList<>();
				//'summerCourses' array-list
				ArrayList<String> summerCourses=new ArrayList<>();
				HashMap<String, ArrayList<String>> enrolledCourses=new HashMap<>();
				enrolledCourses.put(FALL, fallCourses);
				enrolledCourses.put(WINTER, winterCourses);
				enrolledCourses.put(SUMMER, summerCourses);
				
				students.put(newId, enrolledCourses);
		}
				// Creating the log file
				File file = new File(newId+".txt");
				try {
					file.createNewFile();
					serverPw.println("Student registered successfully with student id : "+newId);
					serverPw.println("Date and time : "+dateFormat.format(date));
					serverPw.println("");
					serverPw.flush();
					serverPw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();	
				}
	}
	else{
		ArrayList<String> advisors=new ArrayList<>();
		FileWriter serverFile;
		BufferedWriter serverBufferedWriter;
		PrintWriter serverPw;
		DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date=new Date();
		if(userType.contains(COMP)){
			advisors=COMPServer.advisors;
			serverFile=new FileWriter("COMPServer.txt",true);
			serverBufferedWriter=new BufferedWriter(serverFile);
			serverPw=new PrintWriter(serverBufferedWriter);
		}
		else if(userType.contains(SOEN)){
			advisors=SOENServer.advisors;
			serverFile=new FileWriter("SOENServer.txt",true);
			serverBufferedWriter=new BufferedWriter(serverFile);
			serverPw=new PrintWriter(serverBufferedWriter);
		}
		else {
			advisors=INSEServer.advisors;
			serverFile=new FileWriter("INSEServer.txt",true);
			serverBufferedWriter=new BufferedWriter(serverFile);
			serverPw=new PrintWriter(serverBufferedWriter);
		}
		synchronized (this) {
		int idPart=advisors.size()+1000;
		newId=userType+idPart;
		advisors.add(newId);
		}
		// Creating the log file
		File file = new File(newId+".txt");
		try {
			file.createNewFile();
			serverPw.println("Advisor registered successfully with advisor id : "+newId);
			serverPw.println("Date and time : "+dateFormat.format(date));
			serverPw.println("");
			serverPw.flush();
			serverPw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	return newId;
			
		
	}
	private void enterLog(String userId,String studentId,String requestType,String requestParameters,String requestStatus,String serverResponse) throws IOException{
		DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date=new Date();
		if(requestType.equals("CourseRemoved")){
			FileWriter studentFile=new FileWriter(userId+".txt",true);
			BufferedWriter studentBufferedWriter=new BufferedWriter(studentFile);
			PrintWriter studentPw=new PrintWriter(studentBufferedWriter);
			studentPw.println("Action performed : "+userId+" removed "+requestParameters+" from "+requestStatus);
			studentPw.println("Date and time : "+dateFormat.format(date));
			studentPw.println("");
			studentPw.flush();
			studentPw.close();
		}
		else{
			
			if(userId.charAt(4)=='A' && studentId.charAt(4)=='S'){
				FileWriter studentFile=new FileWriter(studentId+".txt",true);
				BufferedWriter studentBufferedWriter=new BufferedWriter(studentFile);
				PrintWriter studentPw=new PrintWriter(studentBufferedWriter);
				studentPw.println("Client : Advisor");
				studentPw.println(requestType);
				studentPw.println(requestParameters);
				studentPw.println("Date and time : "+dateFormat.format(date));
				studentPw.println(requestStatus);
				studentPw.println(serverResponse);
				studentPw.println("");
				studentPw.flush();
				studentPw.close();
				
			}
			
		FileWriter studentFile=new FileWriter(userId+".txt",true);
		FileWriter serverFile;
			if(userId.contains(COMP)){
			serverFile=new FileWriter("COMPServer.txt",true);
			}
			else if(userId.contains(SOEN)){
				serverFile=new FileWriter("SOENServer.txt",true);
			}
			else{
				serverFile=new FileWriter("INSEServer.txt",true);
			}
			BufferedWriter studentBufferedWriter=new BufferedWriter(studentFile);
			BufferedWriter serverBufferedWriter=new BufferedWriter(serverFile);
			
			PrintWriter studentPw=new PrintWriter(studentBufferedWriter);
			PrintWriter serverPw=new PrintWriter(serverBufferedWriter);
		
		
		if(user.charAt(4)=='S'){
			studentPw.println("Client : Student");
		}else{
			studentPw.println("Client : Advisor");
		}
		studentPw.println(requestType);
		studentPw.println(requestParameters);
		studentPw.println("Date and time : "+dateFormat.format(date));
		studentPw.println(requestStatus);
		studentPw.println(serverResponse);
		studentPw.println("");
		
		synchronized (this) {
		if(user.charAt(4)=='S'){
			serverPw.println("Client : Student");
		}else{
			serverPw.println("Client : Advisor"+" ("+user+")");
		}
		serverPw.println(requestType);
		serverPw.println(requestParameters);
		serverPw.println("Date and time : "+dateFormat.format(date));
		serverPw.println(requestStatus);
		
		if(serverResponse.contains("You are")){
			serverPw.println(serverResponse.replace("You are", studentId+" is"));
		}
		else if(serverResponse.contains("You have")){
			serverPw.println(serverResponse.replace("You have", studentId+" has"));
		}
		else{
			serverPw.println(serverResponse);
		}
		serverPw.println("");
		// logging action to other department's server if needed
		if(!otherDept.equals("")){
			FileWriter compDeptServerFile=null;
			FileWriter soenDeptServerFile=null;
			FileWriter inseDeptServerFile=null;
			if(otherDept.contains(COMP)){
				compDeptServerFile=new FileWriter("COMPServer.txt",true);
			}
			if(otherDept.contains(SOEN)){
				soenDeptServerFile=new FileWriter("SOENServer.txt",true);
			}
			if(otherDept.contains(INSE)){
				inseDeptServerFile=new FileWriter("INSEServer.txt",true);
			}
			if(compDeptServerFile!=null){
				enterOtherDeptLog(compDeptServerFile, studentId, requestType, requestParameters, requestStatus, serverResponse);
				}
			if(soenDeptServerFile!=null){
				enterOtherDeptLog(soenDeptServerFile, studentId, requestType, requestParameters, requestStatus, serverResponse);
				}
			if(inseDeptServerFile!=null){
				enterOtherDeptLog(inseDeptServerFile, studentId, requestType, requestParameters, requestStatus, serverResponse);
				}
			
		}
		
		}
		studentPw.flush();
		serverPw.flush();
		studentPw.close();
		serverPw.close();
	}
	}
	private void enterOtherDeptLog(FileWriter otherDeptServerFile,String studentId,String requestType,String requestParameters,String requestStatus,String serverResponse){
		DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date=new Date();
		BufferedWriter OtherDeptServerBufferedWriter=new BufferedWriter(otherDeptServerFile);
		PrintWriter otherDeptServerPw=new PrintWriter(OtherDeptServerBufferedWriter);
		
		if(user.charAt(4)=='S'){
			otherDeptServerPw.println("Client : Student");
		}else{
			otherDeptServerPw.println("Client : Advisor"+" ("+user+")");
		}
		otherDeptServerPw.println(requestType);
		otherDeptServerPw.println(requestParameters);
		otherDeptServerPw.println("Date and time : "+dateFormat.format(date));
		otherDeptServerPw.println(requestStatus);
		
		if(serverResponse.contains("You are")){
			otherDeptServerPw.println(serverResponse.replace("You are", studentId+" is"));
		}
		else if(serverResponse.contains("You have")){
			otherDeptServerPw.println(serverResponse.replace("You have", studentId+" has"));
		}
		else{
			otherDeptServerPw.println(serverResponse);
		}
		otherDeptServerPw.println("");
		otherDeptServerPw.flush();
		otherDeptServerPw.close();
	}
}
