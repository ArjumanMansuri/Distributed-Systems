import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;

import DcrsApp.DcrsPOA;

public class DcrsImpl extends DcrsPOA {

	private static String user;
	private static int capacity;
	private String otherDept="";
	private static final String FALL="FALL";
	private static final String SUMMER="SUMMER";
	private static final String WINTER="WINTER";

	private static final String COMP="COMP";
	private static final String SOEN="SOEN";
	private static final String INSE="INSE";
	private ORB orb;
	
	public void setOrb(ORB orb) {
		this.orb = orb;
	}

	@Override
	public void shutdown() {
		orb.shutdown(false);
	}
	@Override
	public void setUser(String user) {
		this.user=user;
	}

	@Override
	public String getUser() {
		return this.user;
	}
	
	@Override
	public void setCapacity(int capacity) {
		this.capacity=capacity;
	}

	@Override
	public int getCapacity() {
		return this.capacity;
	}
	
	@Override
	public String addCourse(String courseID, String semester){
		String message="";
		String requestType="Request Type : Add course";
		String requestParameters="Request Parameters :  1.Course Id : "+courseID+" 2.Semester : "+semester;
		String requestStatus;
		String serverResponse;
		ConcurrentHashMap<String, HashMap<String, Course>> courses=new ConcurrentHashMap<>();
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
		try {
			if(!courses.get(semester).containsKey(courseID)){
				synchronized (this) {
			
					Course course = new Course(courseID, capacity, capacity);
					course.setRegisteredStudents(registeredStudents);
					courses.get(semester).put(courseID, course);
				}
					requestStatus="Request completion : success";
					serverResponse="Server Response : "+courseID+" added successfully in "+semester+" with capacity "+capacity;
					
						enterLog(user,user, requestType, requestParameters, requestStatus, serverResponse);
					
						message=courseID+" added successfully in "+semester+" with capacity "+capacity;
				}
				else{
					requestStatus="Request completion : failed";
					serverResponse="Server Response : "+courseID+" is already present in "+semester;
					enterLog(user, user,requestType, requestParameters, requestStatus, serverResponse);
					message=courseID+" is already present in "+semester;
				}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}

	@Override
	public String removeCourse(String courseID, String semester) {
		String message="";
		String requestType;
		String requestParameters="Request Parameters :  1.Course Id : "+courseID+" 2.Semester : "+semester;
		String requestStatus;
		String serverResponse;
		ConcurrentHashMap<String, HashMap<String, Course>> courses=new ConcurrentHashMap<>();
		ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();

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
		
		try{
			if(courses.get(semester).containsKey(courseID)){
				synchronized (this) {
				ArrayList<String> registeredStudents = courses.get(semester).get(courseID).getRegisteredStudents();
				if(registeredStudents.size()>0){
					for (int i = 0; i < registeredStudents.size(); i++) {
						String studentId=registeredStudents.get(i);
				if(user.substring(0, 4).equals(studentId.substring(0,4))){
					students.get(studentId).get(semester).remove(courseID);
				}
				else{
					if(user.contains(COMP)){
						if(studentId.contains(SOEN)){
							String response=COMPServer.sendMessage(2222,courseID+" "+semester+" "+studentId+" remove");
						}
						else if(studentId.contains(INSE)){
							String response=COMPServer.sendMessage(3333,courseID+" "+semester+" "+studentId+" remove");
						}
				}
					else if(user.contains(SOEN)){
						if(studentId.contains(COMP)){
							String response=SOENServer.sendMessage(1111,courseID+" "+semester+" "+studentId+" remove");
						}
						else if(studentId.contains(INSE)){
							String response=SOENServer.sendMessage(3333,courseID+" "+semester+" "+studentId+" remove");
						}
				}
					else if(user.contains(INSE)){
						if(studentId.contains(COMP)){
							String response=INSEServer.sendMessage(1111,courseID+" "+semester+" "+studentId+" remove");
						}
						else if(studentId.contains(SOEN)){
							String response=INSEServer.sendMessage(2222,courseID+" "+semester+" "+studentId+" remove");
						}
				}
			}
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
			message= courseID+" removed successfully from "+semester;
		}
		else{
			requestType="Request Type : Remove course";
			requestStatus="Request completion : failed";
			serverResponse="Server Response : "+courseID+" is not present in "+semester;
			enterLog(user, user,requestType, requestParameters, requestStatus, serverResponse);
			message= courseID+" is not present in "+semester;
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
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
	public String listCourseAvailability(String semester) {

		ArrayList<String> courseAvailability=new ArrayList<>();
		ConcurrentHashMap<String, HashMap<String, Course>> courses=new ConcurrentHashMap<>();
		
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
		try {
			enterLog(user, user, requestType, requestParameters, requestStatus, serverResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String message="";
		if(courses.size()==0){
			message = message+"No courses are available in "+semester+"\n";
		}
		else{
			message = message + semester+" - ";
			for(int i=0;i<courseAvailability.size();i++){
				if(i==courseAvailability.size()-1){
					message = message+courseAvailability.get(i)+".\n";
				}
				else{
					message = message+courseAvailability.get(i)+", ";
				}
			}
		}
		
	return message;
	}

	@Override
	public String enrolCourse(String studentID, String courseID, String semester) {

		String requestType="Request Type : Enroll in a course";
		String requestParameters="Request Parameters : 1.Student Id : "+studentID+" 2.Course Id : "+courseID+" 3.Semester : "+semester;
		String requestStatus;
		String serverResponse;
		String studentType=studentID.substring(0, 4);
		ConcurrentHashMap<String, HashMap<String, Course>> courses=new ConcurrentHashMap<>();
		ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();
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
				serverResponse="Server Response : You are already registered for "+courseID+" in Fall term";
				try {
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "You are already registered for "+courseID+" in Fall term";
				
			}
			else if(students.get(studentID).get(WINTER).contains(courseID)){
				
				requestStatus="Request completion : failed";
				serverResponse="Server Response : You are already registered for "+courseID+" in Winter term";
				try {
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "You are already registered for "+courseID+" in Winter term";
				
			}
			else{
			
				requestStatus="Request completion : failed";
				serverResponse="Server Response : You are already registered for "+courseID+" in Summer term";
				try {
					enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "You are already registered for "+courseID+" in Summer term";
			}
		}
		// if student is already registered in 3 courses for the requested term
		if(students.get(studentID).get(semester).size()==3){
			
			requestStatus="Request completion : failed";
			serverResponse="Server Response : You are already registered for 3 courses in "+semester;
			try {
				enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
						try {
							enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return "Registered in "+courseID;
					}
					else{
						requestStatus="Request completion : failed";
						serverResponse="Server Response : "+courseID+" is full in "+semester;
						try {
							enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return courseID+" is full in "+semester;
						}
					}
				}
				else{
					//updating student log file
					requestStatus="Request completion : failed";
					serverResponse="Server Response : "+courseID+" is not available in "+semester;
					try {
						enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					try {
						enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "You are already registered in 2 out of department courses";
				}
				else{
					// contact corresponding server
					if(studentID.contains(COMP)){
						synchronized (this) {
							if(courseID.contains(SOEN)){
								otherDept=SOEN;
							String response=COMPServer.sendMessage(2222,courseID+" "+semester+" "+studentID+" enroll");
							try {
								return reactToResponse(students,response,courseID,semester,studentID);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else if(courseID.contains(INSE)){
							otherDept=INSE;
							String response=COMPServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" enroll");
							try {
								return reactToResponse(students,response,courseID,semester,studentID);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						}
					}
					else if(studentID.contains(SOEN)){
						synchronized (this) {
						if(courseID.contains(COMP)){
							otherDept=COMP;
							String response=SOENServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" enroll");
							try {
								return reactToResponse(students,response,courseID,semester,studentID);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if(courseID.contains(INSE)){
							otherDept=INSE;
							String response=SOENServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" enroll");
							try {
								return reactToResponse(students,response,courseID,semester,studentID);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						}
					}
					else if(studentID.contains(INSE)){
						synchronized (this) {
						if(courseID.contains(COMP)){
							otherDept=COMP;
							String response=INSEServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" enroll");
							try {
								return reactToResponse(students,response,courseID,semester,studentID);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else if(courseID.contains(SOEN)){
							otherDept=SOEN;
							String response=INSEServer.sendMessage(2222,courseID+" "+semester+" "+studentID+" enroll");
							try {
								return reactToResponse(students,response,courseID,semester,studentID);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					}
				}
			
			return "";
		}
		
	}

	private String reactToResponse(ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students,String response, String courseId,String semester,String studentId) throws IOException{
		if(response.trim().contains("Dropped")){
			String requestType="Request Type : Drop a course";
			String requestParameters="Request Parameters : 1.Student Id : "+studentId+" 2.Course Id : "+courseId;
			students.get(studentId).get(semester).remove(courseId);
			String requestStatus="Request completion : success";
			String serverResponse="Server Response : You have dropped "+courseId+" from "+semester;
			enterLog(user,studentId, requestType, requestParameters, requestStatus, serverResponse);
			return "Dropped "+courseId+" from "+semester;
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
	public String getClassSchedule(String studentID) {
		String studentType=studentID.substring(0, 4);
		ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();
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
		try {
			enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serverResponse.substring(serverResponse.indexOf("["));
	}

	@Override
	public String dropCourse(String studentID, String courseID) {
		String requestType="Request Type : Drop a course";
		String requestParameters="Request Parameters : 1.Student Id : "+studentID+" 2.Course Id : "+courseID;
		String requestStatus;
		String serverResponse;
		String studentType=studentID.substring(0, 4);
		ConcurrentHashMap<String, HashMap<String, Course>> courses=new ConcurrentHashMap<>();
		ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();
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
			try {
				enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "You are not registered in "+courseID;
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
					try {
						enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					try {
						enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					try {
						enterLog(user,studentID, requestType, requestParameters, requestStatus, serverResponse);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
						try {
							return reactToResponse(students,response,courseID,semester,studentID);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(courseID.contains(INSE)){
						otherDept=INSE;
						String response=COMPServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" drop");
						try {
							return reactToResponse(students,response,courseID,semester,studentID);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					}
				}
				else if(studentID.contains(SOEN)){
					synchronized (this) {
					if(courseID.contains(COMP)){
						otherDept=COMP;
						String response=SOENServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" drop");
						try {
							return reactToResponse(students,response,courseID,semester,studentID);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(courseID.contains(INSE)){
						otherDept=INSE;
						String response=SOENServer.sendMessage(3333,courseID+" "+semester+" "+studentID+" drop");
						try {
							return reactToResponse(students,response,courseID,semester,studentID);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					}
				}
				else if(studentID.contains(INSE)){
					synchronized (this) {
					if(courseID.contains(COMP)){
						otherDept=COMP;
						String response=INSEServer.sendMessage(1111,courseID+" "+semester+" "+studentID+" drop");
						try {
							return reactToResponse(students,response,courseID,semester,studentID);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(courseID.contains(SOEN)){
						otherDept=SOEN;
						String response=INSEServer.sendMessage(2222,courseID+" "+semester+" "+studentID+" drop");
						try {
							return reactToResponse(students,response,courseID,semester,studentID);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				}
			}
		}
		return courseID;
	}

	@Override
	public String swapCourse(String studentId, String newCourseId, String oldCourseId) {
		// TODO Auto-generated method stub
		String client = "Student";
		String requestStatus = "Failed";
		String response ;
		synchronized (this) {
			response = dropCourse(studentId, oldCourseId);
		
			String semester=null;
			
			if(response.contains("dropped") || response.contains("Dropped")){
				if(response.contains("Summer") || response.contains(SUMMER)){
					semester=SUMMER;
				}
				else if(response.contains("Winter") || response.contains(WINTER)){
					semester=WINTER;
				}
				else if(response.contains("Fall") || response.contains(FALL)){
					semester=FALL;
				}
				response = enrolCourse(studentId, newCourseId, semester);
				if(response.contains("Registered")){
					requestStatus = "Success";
				}
				else{
					enrolCourse(studentId, oldCourseId, semester);
					requestStatus = "Failed";
				}
			}
		}
			DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date=new Date();
			try {
			if(user.charAt(4)=='A' && studentId.charAt(4)=='S'){
				client = "Advisor";
				FileWriter advisorFile;
				
				advisorFile = new FileWriter(user+".txt",true);
				synchronized (this) {
				BufferedWriter advisorBufferedWriter=new BufferedWriter(advisorFile);
				PrintWriter advisorPw=new PrintWriter(advisorBufferedWriter);
				advisorPw.println("Action performed : Swap course");
				advisorPw.println("Date and time : "+dateFormat.format(date));
				advisorPw.println("Request Status : "+requestStatus);
				advisorPw.println("");
				advisorPw.flush();
				advisorPw.close();
				}
			}
			FileWriter studentFile;
			
			studentFile = new FileWriter(studentId+".txt",true);
		
		BufferedWriter studentBufferedWriter=new BufferedWriter(studentFile);
		PrintWriter studentPw=new PrintWriter(studentBufferedWriter);
		synchronized (this) {
		studentPw.println("Action performed : Swap course");
		studentPw.println("Date and time : "+dateFormat.format(date));
		studentPw.println("Request Status : "+requestStatus);
		studentPw.println("");
		studentPw.flush();
		studentPw.close();
		}
		FileWriter serverFile;
		
		serverFile = new FileWriter(user.substring(0,4)+"Server.txt",true);
	
	BufferedWriter serverBufferedWriter=new BufferedWriter(serverFile);
	PrintWriter serverPw=new PrintWriter(serverBufferedWriter);
	synchronized (this) {
	
	serverPw.println("Client : "+client+" ("+user+")");
	serverPw.println("Action performed : Swap course");
	serverPw.println("Date and time : "+dateFormat.format(date));
	serverPw.println("Request Status : "+requestStatus);
	serverPw.println("");
	serverPw.flush();
	serverPw.close();
	}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return response;
		
	}

	
	@Override
	public int checkRegistration(String id) {
		ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();
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

	@Override
	public String registerNewUser(String userType) {
		String newId=null;
		try{
		if(userType.charAt(4)=='S'){
		ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();
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
					
						file.createNewFile();
						synchronized (this) {
						serverPw.println("Student registered successfully with student id : "+newId);
						serverPw.println("Date and time : "+dateFormat.format(date));
						serverPw.println("");
						serverPw.flush();
						serverPw.close();
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
			synchronized (this) {
				file.createNewFile();
				serverPw.println("Advisor registered successfully with advisor id : "+newId);
				serverPw.println("Date and time : "+dateFormat.format(date));
				serverPw.println("");
				serverPw.flush();
				serverPw.close();
			}
			} 
		}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return newId;
				
	}

	private void enterLog(String userId,String studentId,String requestType,String requestParameters,String requestStatus,String serverResponse) throws IOException{
		synchronized (this) {
		DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date=new Date();
		if(requestType.equals("CourseRemoved")){
			FileWriter studentFile=new FileWriter(studentId+".txt",true);
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
	}
	private void enterOtherDeptLog(FileWriter otherDeptServerFile,String studentId,String requestType,String requestParameters,String requestStatus,String serverResponse){
		synchronized(this){
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
}
