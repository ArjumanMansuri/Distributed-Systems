import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DcrsApp.Dcrs;
import DcrsApp.DcrsHelper;

public class SOENServer {
	public static ConcurrentHashMap<String, HashMap<String, Course>> courses;
	public static ConcurrentHashMap<String, HashMap<String, ArrayList<String>>> students=new ConcurrentHashMap<>();
	public static ArrayList<String> advisors=new ArrayList<>();
	
	private static final String FALL="FALL";
	private static final String SUMMER="SUMMER";
	private static final String WINTER="WINTER";
	
	static {
		//'courses' hash-map contains all courses offered by COMP department
		courses = new ConcurrentHashMap<>();
		//'fallCourses' hash-map contains all courses offered by COMP department in Fall
		HashMap<String, Course> fallCourses=new HashMap<>();
		//'winterCourses' hash-map contains all courses offered by COMP department in Winter
		HashMap<String, Course> winterCourses=new HashMap<>();
		//'summerCourses' hash-map contains all courses offered by COMP department in Summer
		HashMap<String, Course> summerCourses=new HashMap<>();
		ArrayList<String> registeredStudents=new ArrayList<>();
		
		Course soen6231=new Course("SOEN6231", 60, 60);
		soen6231.setRegisteredStudents(registeredStudents);
		Course soen7231=new Course("SOEN7231", 60, 60);
		soen7231.setRegisteredStudents(registeredStudents);
		Course soen8231=new Course("SOEN8231", 60, 60);
		soen8231.setRegisteredStudents(registeredStudents);
		
		Course soen6441=new Course("SOEN6441", 60, 60);
		soen6441.setRegisteredStudents(registeredStudents);
		Course soen7441=new Course("SOEN7441", 60, 60);
		soen7441.setRegisteredStudents(registeredStudents);
		Course soen8441=new Course("SOEN8441", 60, 60);
		soen8441.setRegisteredStudents(registeredStudents);

		
		Course soen6578=new Course("SOEN6578", 60, 60);
		soen6578.setRegisteredStudents(registeredStudents);
		Course soen7578=new Course("SOEN7578", 60, 60);
		soen7578.setRegisteredStudents(registeredStudents);
		Course soen8578=new Course("SOEN8578", 60, 60);
		soen8578.setRegisteredStudents(registeredStudents);
		
		fallCourses.put(soen6231.getCourseName(), soen6231);
		fallCourses.put(soen7231.getCourseName(), soen7231);
		fallCourses.put(soen8231.getCourseName(), soen8231);
		
		winterCourses.put(soen6441.getCourseName(), soen6441);
		winterCourses.put(soen7441.getCourseName(), soen7441);
		winterCourses.put(soen8441.getCourseName(), soen8441);
		
		summerCourses.put(soen6578.getCourseName(), soen6578);
		summerCourses.put(soen7578.getCourseName(), soen7578);
		summerCourses.put(soen8578.getCourseName(), soen8578);
		
		courses.put(FALL, fallCourses);
		courses.put(WINTER, winterCourses);
		courses.put(SUMMER, summerCourses);
		
	}
	
	static{
		try {
			FileWriter serverFile=new FileWriter("SOENServer.txt",true);
			BufferedWriter serverBufferedWriter=new BufferedWriter(serverFile);
			PrintWriter serverPw=new PrintWriter(serverBufferedWriter);
			DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (int i = 1000; i < 1003; i++) {
			
		
		String studentId="SOENS"+i;
		ArrayList<String> fallCourses=new ArrayList<>();
		//'winterCourses' array-list
		ArrayList<String> winterCourses=new ArrayList<>();
		//'summerCourses' array-list contains
		ArrayList<String> summerCourses=new ArrayList<>();
		HashMap<String, ArrayList<String>> enrolledCourses=new HashMap<>();
		enrolledCourses.put(FALL, fallCourses);
		enrolledCourses.put(WINTER, winterCourses);
		enrolledCourses.put(SUMMER, summerCourses);
		
		SOENServer.students.put(studentId, enrolledCourses);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static{
		try {
			FileWriter serverFile=new FileWriter("SOENServer.txt",true);
			BufferedWriter serverBufferedWriter=new BufferedWriter(serverFile);
			PrintWriter serverPw=new PrintWriter(serverBufferedWriter);
			DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (int i = 1000; i < 1003; i++) {
		
			String advisorId="SOENA"+i;
		
		SOENServer.advisors.add(advisorId);
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
	
	public static void main(String[] args) {
		ORB orb = ORB.init(args, null); //-ORBInitialPort 1050 -ORBInitialHost localhost
		POA rootpoa;
		try {
			rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			DcrsImpl dcrsImpl = new DcrsImpl();
			dcrsImpl.setOrb(orb);
			
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(dcrsImpl);
			Dcrs href = DcrsHelper.narrow(ref);
			
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			NameComponent path[] = ncRef.to_name("SoenServer");
			ncRef.rebind(path, href);

			File file = new File("SOENServer.txt");
			file.createNewFile();
			Runnable task = () -> {
				receive();
			};
			Thread thread = new Thread(task);
			thread.start();
			
			System.out.println("SOEN Server ready and waiting ...");
			
			// wait for invocations from clients
			for (;;) {
				orb.run();
			}
		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SOEN Server Exiting ...");
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
			aSocket = new DatagramSocket(2222);
			System.out.println("Server 2222 Started............");
			while (true) {
			
			byte[] bufferRec = new byte[1000];
			byte[] bufferSen1 = new byte[1000];
			byte[] bufferSen2 = new byte[1000];
			DatagramPacket reply =null;
			
			DatagramPacket request = new DatagramPacket(bufferRec, bufferRec.length);
				
				aSocket.receive(request);
				
				String data=(new String(request.getData())).trim();
				
				String response="";
				if(data.equalsIgnoreCase(FALL) ||  data.equalsIgnoreCase(WINTER) || data.equalsIgnoreCase(SUMMER)){
					System.out.println("Req received by soen server..."+data);
					HashMap<String, Course> semCourses=courses.get(data);
					for(Map.Entry<String,Course> row:semCourses.entrySet()){
						response=response+row.getKey()+" "+row.getValue().getVacantSeats()+" ";
						System.out.println("Response : "+response);
					}
					bufferSen1=response.getBytes();
					reply = new DatagramPacket(bufferSen1, bufferSen1.length, request.getAddress(), request.getPort());
				}
				else if(data.contains("SOEN")){
					

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
			}catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

}
