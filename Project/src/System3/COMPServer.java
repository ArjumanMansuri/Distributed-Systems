package System3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


public class COMPServer {
	public static ConcurrentHashMap<String, HashMap<String, Course>> courses=new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();
	public static ArrayList<String> advisors=new ArrayList<>();
	
	private static final String FALL="FALL";
	private static final String SUMMER="SUMMER";
	private static final String WINTER="WINTER";
	
	
	static {
		//'courses' hash-map contains all courses offered by COMP department
		
		//'fallCourses' hash-map contains all courses offered by COMP department in Fall
		HashMap<String, Course> fallCourses=new HashMap<>();
		//'winterCourses' hash-map contains all courses offered by COMP department in Winter
		HashMap<String, Course> winterCourses=new HashMap<>();
		//'summerCourses' hash-map contains all courses offered by COMP department in Summer
		HashMap<String, Course> summerCourses=new HashMap<>();
		ArrayList<String> registeredStudents=new ArrayList<>();
		
		Course comp6231=new Course("COMP6231", 60, 60);
		comp6231.setRegisteredStudents(registeredStudents);
		Course comp7231=new Course("COMP7231", 60, 60);
		comp7231.setRegisteredStudents(registeredStudents);
		Course comp8231=new Course("COMP8231", 60, 60);
		comp8231.setRegisteredStudents(registeredStudents);
		
		Course comp6441=new Course("COMP6441", 60, 60);
		comp6441.setRegisteredStudents(registeredStudents);
		Course comp7441=new Course("COMP7441", 60, 60);
		comp7441.setRegisteredStudents(registeredStudents);
		Course comp8441=new Course("COMP8441", 60, 60);
		comp8441.setRegisteredStudents(registeredStudents);
		
		
		Course comp6578=new Course("COMP6578", 60, 60);
		comp6578.setRegisteredStudents(registeredStudents);
		Course comp7578=new Course("COMP7578", 60, 60);
		comp7578.setRegisteredStudents(registeredStudents);
		Course comp8578=new Course("COMP8578", 60, 60);
		comp8578.setRegisteredStudents(registeredStudents);
		
		fallCourses.put(comp6231.getCourseName(), comp6231);
		fallCourses.put(comp7231.getCourseName(), comp7231);
		fallCourses.put(comp8231.getCourseName(), comp8231);
		
		winterCourses.put(comp6441.getCourseName(), comp6441);
		winterCourses.put(comp7441.getCourseName(), comp7441);
		winterCourses.put(comp8441.getCourseName(), comp8441);
		
		summerCourses.put(comp6578.getCourseName(), comp6578);
		summerCourses.put(comp7578.getCourseName(), comp7578);
		summerCourses.put(comp8578.getCourseName(), comp8578);
		
		courses.put(FALL, fallCourses);
		courses.put(WINTER, winterCourses);
		courses.put(SUMMER, summerCourses);
		
	}
	
	static{
		try {
			FileWriter serverFile=new FileWriter("COMPServer.txt",true);
			BufferedWriter serverBufferedWriter=new BufferedWriter(serverFile);
			PrintWriter serverPw=new PrintWriter(serverBufferedWriter);
			DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
		for (int i = 1000; i < 1003; i++) {
			
		
		String studentId="COMPS"+i;
		ArrayList<String> fallCourses=new ArrayList<>();
		//'winterCourses' array-list
		ArrayList<String> winterCourses=new ArrayList<>();
		//'summerCourses' array-list contains
		ArrayList<String> summerCourses=new ArrayList<>();
		HashMap<String, ArrayList<String>> enrolledCourses=new HashMap<>();
		enrolledCourses.put(FALL, fallCourses);
		enrolledCourses.put(WINTER, winterCourses);
		enrolledCourses.put(SUMMER, summerCourses);
		
		COMPServer.students.put(studentId, enrolledCourses);
		
		//Logging message in server text file
		serverPw.println("Student registered successfully with student id : "+studentId);
		Date date=new Date();
		serverPw.println("Date and time : "+dateFormat.format(date));
		serverPw.println("");
		// Creating the log file
		File file = new File(studentId+".txt");
		file.createNewFile();
		}
		serverPw.flush();
		serverPw.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	
	static{
		try {
			FileWriter serverFile=new FileWriter("COMPServer.txt",true);
			BufferedWriter serverBufferedWriter=new BufferedWriter(serverFile);
			PrintWriter serverPw=new PrintWriter(serverBufferedWriter);
			DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (int i = 1000; i < 1003; i++) {
			
				
			String advisorId="COMPA"+i;
		
		COMPServer.advisors.add(advisorId);
		//Logging message in server text file
		serverPw.println("Advisor registered successfully with advisor id : "+advisorId);
		Date date=new Date();
		serverPw.println("Date and time : "+dateFormat.format(date));
		serverPw.println("");
		// Creating the log file
		File file = new File(advisorId+".txt");
		
			file.createNewFile();
		
		}
		serverPw.flush();
		serverPw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws AlreadyBoundException, IOException {
		

			File file = new File("COMPServer.txt");
			file.createNewFile();
			Runnable task = () -> {
				receive();
			};
			Thread thread = new Thread(task);
			thread.start();
			
			System.out.println("COMP Server ready and waiting ...");
			
			// wait for invocations from clients
			
		
		
	}
	public static String sendMessage(int serverPort,String data) {
		DatagramSocket aSocket = null;
		DatagramPacket reply = null;
		try {
			aSocket = new DatagramSocket();
			byte[] message = data.toString().getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, message.length, aHost, serverPort);
			aSocket.send(request);
			System.out.println("Request message sent from the client to server with port number " + serverPort + " is: "
					+ new String(request.getData()));
			
			byte[] buffer = new byte[1000];
			reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);
			System.out.println("Reply received from the server with port number " + serverPort + " is: "
					+ new String(reply.getData()));
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return new String(reply.getData());
	}

	public static void receive() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(1111);
			System.out.println("Server 1111 Started............");
			while (true) {
			
			byte[] buffer = new byte[1000];
			byte[] bufferSen1 = new byte[1000];
			byte[] bufferSen2 = new byte[1000];
			DatagramPacket reply=null;
			
			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				aSocket.receive(request);
				
				String data=(new String(request.getData())).trim();
				
				String response="";
				if(data.equals(FALL) ||  data.equals(WINTER) || data.equals(SUMMER)){
					System.out.println("Req received by soen server..."+data);
					HashMap<String, Course> semCourses=courses.get(data);
					for(Map.Entry<String,Course> row:semCourses.entrySet()){
						response=response+row.getKey()+" "+row.getValue().getVacantSeats()+" ";
						System.out.println("Response : "+response);
					}
					bufferSen1=response.getBytes();
					 reply= new DatagramPacket(bufferSen1, bufferSen1.length, request.getAddress(), request.getPort());
				}
				else if(data.contains("COMP")){
					
					String[] dataArr=data.split(" ");
					String courseId=dataArr[0];
					String semester=dataArr[1];
					String studentId=dataArr[2];
					String method=dataArr[3];
					
					if(method.equals("enroll")){
					if(courses.get(semester).containsKey(courseId)){
					int vacantSeats=courses.get(semester).get(courseId).getVacantSeats();
					if(vacantSeats>0){
						//Updating vacant seats number of the course
						courses.get(semester).get(courseId).setVacantSeats(vacantSeats-1);
						courses.get(semester).get(courseId).getRegisteredStudents().add(studentId);
						response="Registered";
					}
					else{
						response="Full";
					}
					
				}
				else{
					response="NotPresent";
					}
				}
				else if(method.equals("drop")){
					int vacantSeats=courses.get(semester).get(courseId).getVacantSeats();
					//Updating vacant seats number of the course
					courses.get(semester).get(courseId).setVacantSeats(vacantSeats+1);
					courses.get(semester).get(courseId).getRegisteredStudents().remove(studentId);
					response="Dropped from "+semester;
					}
				else if(method.equals("remove")){
					students.get(studentId).get(semester).remove(courseId);
					response="Removed";
					}
					bufferSen2=response.getBytes();
					reply = new DatagramPacket(bufferSen2, bufferSen2.length, request.getAddress(), request.getPort());
			}
			aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
}
