package System1;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.ws.Endpoint;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DCRS.FrontEnd;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class student_inse{
	static List<String> list11 = new ArrayList<>();
	static List<String> list12 = new ArrayList<>();
	static List<String> list13 = new ArrayList<>();
	static HashMap<String,List<String>> student_inse_fall = new HashMap<>();
	static HashMap<String,List<String>> student_inse_winter = new HashMap<>();
	static HashMap<String,List<String>> student_inse_summer = new HashMap<>();
	
	static HashMap<String, HashMap<String,List<String>>> std_inse = new HashMap<>();
		
	static List<String> students_in = new ArrayList<>();		
	static List<String> advisor_in = new ArrayList<>();

	public student_inse(){
		list11.add("SOENS1000");
		student_inse_fall.put("INSE6261",list11);
		std_inse.put("Fall", student_inse_fall);
		
		list12.add("INSES1001");
		student_inse_winter.put("INSE2361", list12);
		std_inse.put("Winter", student_inse_winter);
		
		list13.add("INSES1002");
		student_inse_summer.put("INSE6401", list13);
		std_inse.put("Summer", student_inse_summer);
				
		students_in.add("INSES1000");
		students_in.add("INSES1001");
		students_in.add("INSES1002");
				
		advisor_in.add("INSEA1000");
		advisor_in.add("INSEA1001");
		advisor_in.add("INSEA1002");
	}
}

class CourseDetail_inse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	static int capacity;
	static HashMap<String,Integer> inse_fall = new HashMap<>();
	static HashMap<String,Integer> inse_winter = new HashMap<>();
	static HashMap<String,Integer> inse_summer = new HashMap<>();
	static HashMap<String, HashMap<String,Integer>> hash_inse = new HashMap<>();

	public CourseDetail_inse(){
		capacity = 3;
		
		inse_fall.put("INSE6261",capacity);
		hash_inse.put("Fall", inse_fall);
		
		inse_winter.put("INSE2361",capacity);
		hash_inse.put("Winter", inse_winter);
		
		inse_summer.put("INSE6401",capacity);		
		hash_inse.put("Summer", inse_summer);
	}
	static void set(int c){
		capacity = c;
	}
}

class Check_inse{
	CourseDetail_inse cdc = new CourseDetail_inse();
	student_inse sdc = new student_inse();
	Set s;
	public synchronized Set checkReply(String sem,String courseID,String sid,String msg){
		if(msg.equals("checkcourse")) {
			boolean contains = false;
			if(CourseDetail_inse.inse_fall.containsKey(courseID))
				contains = true;
			if(CourseDetail_inse.inse_winter.containsKey(courseID))
				contains = true;
			if(CourseDetail_inse.inse_summer.containsKey(courseID))
				contains = true;			
			if(contains) 
				return new Set(sem,courseID,sid,"yes");
			else 
				return new Set(sem,courseID,sid,"no");
		}
		if(msg.equals("checkOld")) {
			String semester = "";
			boolean contains = false;
			if(student_inse.student_inse_fall.containsKey(courseID))
				semester = "Fall";
			if(student_inse.student_inse_winter.containsKey(courseID))
				semester = "Winter";
			if(student_inse.student_inse_summer.containsKey(courseID))
				semester = "Summer";
			if(semester.equals("Fall")) {
				for(Map.Entry<String, List<String>>map : student_inse.student_inse_fall.entrySet()) {
					List<String> ls = map.getValue();
					if(ls.contains(sid))
						contains = true;
				}
			}
			if(semester.equals("Winter")) {
				for(Map.Entry<String, List<String>>map : student_inse.student_inse_winter.entrySet()) {
					List<String> ls = map.getValue();
					if(ls.contains(sid))
						contains = true;
				}
			}
			if(semester.equals("Summer")) {
				for(Map.Entry<String, List<String>>map : student_inse.student_inse_summer.entrySet()) {
					List<String> ls = map.getValue();
					if(ls.contains(sid))
						contains = true;
				}
			}
			if(contains) return new Set(sem,courseID,sid,semester);
			else return new Set(sem,courseID,sid,"no");
		}
		
		if(msg.equals("schedule")){
			String value ="a";
			String st = new String("1");
			String st1 = new String("2");
			String st2 = new String("3");
			String[] str_array2 = {"ASC","SAD","ASD"};
			for(Map.Entry<String, HashMap<String, List<String>>> entry : student_inse.std_inse.entrySet()){
				HashMap<String, List<String>> hash = entry.getValue();
				for(Map.Entry<String, List<String>> entry1 : hash.entrySet()){
					List<String> str = entry1.getValue();
					if(str.contains(sid)){
						System.out.println("list"+str);
						if(entry.getKey().equals("Fall")){
							if(st.equals("1"))
								st = "f"+entry1.getKey()+",";
							else
								st = st + "f"+entry1.getKey()+",";
							str_array2[0] = entry.getKey();
						}
						if(entry.getKey().equals("Winter")){
							if(st1.equals("2"))
								st1 = "w"+entry1.getKey()+",";
							else
								st1 = st1 + "w" +entry1.getKey()+",";
							str_array2[1] = entry.getKey();
						}
						if(entry.getKey().equals("Summer")){
							if(st2.equals("3"))
								st2 = "s"+entry1.getKey()+",";
							else
								st2 = st2 + "s"+entry1.getKey()+",";
							str_array2[2] = entry.getKey();
						}
					}	
				}
			}
			if(str_array2[0].equals("Fall")){
				st = str_array2[0].toString()+"," + st;
			}
			if(str_array2[1].equals("Winter")){
				st1 = str_array2[1].toString()+"," + st1;
			}
			if(str_array2[2].equals("Summer"))
				st2 = str_array2[2].toString()+"," + st2;
			if(!st.equals("1")){
				if(value.equals("a"))
					value = st;
				else
					value+=st;
				System.out.println("st"+st);
				System.out.println("value"+value);
			}
			System.out.println(!st1.equals("2"));
			if(!st1.equals("2")){
				if(value.equals("a"))
					value = st1;
				else
					value+=st1;
			}
			if(!st2.equals("3")){
				if(value.equals("a"))
					value = st2;
				else
					value+=st2;
			}
			if(value.equals("a"))
				s = new Set(sem, courseID, sid, "empty");
			else{
				System.out.println(value);
				s = new Set(sem, courseID, sid, value);
			}
			return s;
		}
		if(msg.equals("drop")){
			if(student_inse.student_inse_fall.containsKey(courseID)){
				List<String> lst = student_inse.student_inse_fall.get(courseID);
				lst.remove(sid);
				student_inse.student_inse_fall.replace(courseID, student_inse.list11, lst);
				Integer i = CourseDetail_inse.inse_fall.get(courseID);
				CourseDetail_inse.inse_fall.replace(courseID, i, (i+1));
				return new Set(sem, courseID, sid, "dropped");
			}
			if(student_inse.student_inse_winter.containsKey(courseID)){
				List<String> lst = student_inse.student_inse_winter.get(courseID);
				lst.remove(sid);
				student_inse.student_inse_winter.replace(courseID, student_inse.list12, lst);
				Integer i = CourseDetail_inse.inse_fall.get(courseID);
				CourseDetail_inse.inse_winter.replace(courseID, i, (i+1));
				return new Set(sem, courseID, sid, "dropped");
			}
			if(student_inse.student_inse_summer.containsKey(courseID)){
				List<String> lst = student_inse.student_inse_summer.get(courseID);
				lst.remove(sid);
				student_inse.student_inse_summer.replace(courseID, student_inse.list13, lst);
				Integer i = CourseDetail_inse.inse_summer.get(courseID);
				CourseDetail_inse.inse_summer.replace(courseID, i, (i+1));
				return new Set(sem, courseID, sid, "dropped");
			}
			return new Set(sem, courseID, sid, "notexists");
		}
		if(msg.equals("list")){
			StringBuilder builder = new StringBuilder();
			ArrayList<String> ar = new ArrayList<>();
			if(sem.equals("Fall")){
				for(Map.Entry<String, Integer> map : CourseDetail_inse.inse_fall.entrySet()){
					String str = map.getKey() + " " + map.getValue() + ",";
					ar.add(str);
				}					
				System.out.println(ar);
			}
			if(sem.equals("Winter")){
				for(Map.Entry<String, Integer> map : CourseDetail_inse.inse_winter.entrySet()){
					String str = map.getKey() + " " + map.getValue() + ",";
					ar.add(str);
				}
				System.out.println(ar);
			}
			if(sem.equals("Summer")){
				for(Map.Entry<String, Integer> map : CourseDetail_inse.inse_summer.entrySet()){
					String str = map.getKey() + " " + map.getValue() + ",";
					ar.add(str);
				}
				System.out.println(ar);
			}
			if(ar.isEmpty())
				return new Set(sem, "course", "sid", "empty");
			for(String s : ar){
				builder.append(s);
			}
			s = new Set(sem, "course", "sid", builder.toString());
			return s;
		}
		if(msg.equals("seats")){
			int i = 0;
			int j = 0;
			if(sem.equals("Fall")){
				for(Map.Entry<String, List<String>> map1:student_inse.student_inse_fall.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						j++;
				}
			}
			if(sem.equals("Winter")){
				System.out.println(student_inse.student_inse_winter);
				for(Map.Entry<String, List<String>> map1:student_inse.student_inse_winter.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						j++;
				}
			}
			if(sem.equals("Summer")){
				for(Map.Entry<String, List<String>> map1:student_inse.student_inse_summer.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						j++;
				}
			}
			for(Map.Entry<String, List<String>> map1:student_inse.student_inse_fall.entrySet()){
				List<String> l = map1.getValue();
				if(l.contains(sid))
					i++;
			}
			System.out.println(student_inse.student_inse_winter);
			for(Map.Entry<String, List<String>> map1:student_inse.student_inse_winter.entrySet()){
				List<String> l = map1.getValue();
				if(l.contains(sid))
					i++;
			}
			for(Map.Entry<String, List<String>> map1:student_inse.student_inse_summer.entrySet()){
				List<String> l = map1.getValue();
				if(l.contains(sid))
					i++;
			}
			System.out.println("SEATS IN INSE"+i);
			s = new Set(sem, courseID, sid, Integer.toString(i)+","+Integer.toString(j));
			return s;
		}
		if(msg.equals("enroll")){
			int i = 0; 
			int	j = 1000;
			List<String> ls = new ArrayList<>();
			HashMap<String, Integer> hm = CourseDetail_inse.hash_inse.get(sem);
			if(!hm.containsKey(courseID))
				return new Set(sem, courseID, sid, "notexists");
			j = hm.get(courseID);
			if(j == 0){
				return new Set(sem, courseID, sid, "full");
			}
			if(courseID.substring(0,4).equals("INSE")){
				if(student_inse.student_inse_fall.get(courseID) != null){
					List<String> lst = student_inse.student_inse_fall.get(courseID);
					if(lst.contains(sid)){
						return new Set(sem, courseID, sid, "taken");
					}
				}
				if(student_inse.student_inse_winter.get(courseID) != null){
					List<String> lst = student_inse.student_inse_winter.get(courseID);
					if(lst.contains(sid)){
						return new Set(sem, courseID, sid, "taken");
					}
				}
				if(student_inse.student_inse_summer.get(courseID) != null){
					List<String> lst = student_inse.student_inse_summer.get(courseID);
					if(lst.contains(sid)){
						return new Set(sem, courseID, sid, "taken");
					}
				}
			}
			for(Map.Entry<String, HashMap<String,List<String>>> map: student_inse.std_inse.entrySet()){
				HashMap<String, List<String>> hash = map.getValue();
				for(Map.Entry<String, List<String>> map1:hash.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						i++;
				}
			}
			for(Map.Entry<String, HashMap<String,List<String>>> map: student_inse.std_inse.entrySet()){
				HashMap<String, List<String>> hash = map.getValue();
				for(Map.Entry<String, List<String>> map1:hash.entrySet()){
					List<String> l = map1.getValue();
					
					if(l.contains(sid)){
						if(map1.getKey().equals(courseID))
							return new Set(sem, courseID, sid, "taken");
					}
				}
			}
			if(i == 2){
				return new Set(sem, courseID, sid, "cant");
			}
			if(i < 2 && j != 0){
				if(courseID.substring(0,4).equals("INSE")){
					if(sem.equals("Fall")){
						if(!student_inse.student_inse_fall.containsKey(courseID) && CourseDetail_inse.inse_fall.containsKey(courseID))
						{
							ls.add(sid);
							student_inse.student_inse_fall.put(courseID, ls);
						}
						else if(student_inse.student_inse_fall.containsValue(sid)){
							return new Set(sem, courseID, sid, "taken");
						}
						else{
							ls = student_inse.student_inse_fall.get(courseID);
							ls.add(sid);
							student_inse.student_inse_fall.replace(courseID, student_inse.list11, ls);
						}
						CourseDetail_inse.inse_fall.replace(courseID, j, j-1);
						return new Set(sem, courseID, sid, "enroll");
					}
					if(sem.equals("Winter")){
						if(!student_inse.student_inse_winter.containsKey(courseID) && CourseDetail_inse.inse_winter.containsKey(courseID))
						{
							ls.add(sid);
							student_inse.student_inse_winter.put(courseID, ls);
						}
						else if(student_inse.student_inse_winter.containsValue(sid)){
							return new Set(sem, courseID, sid, "taken");
						}
						else{
							ls = student_inse.student_inse_winter.get(courseID);
							ls.add(sid);
							student_inse.student_inse_winter.replace(courseID, student_soen.list12, ls);
						}
						CourseDetail_inse.inse_winter.replace(courseID, j, j-1);
						return new Set(sem, courseID, sid, "enroll");
					}
					if(sem.equals("Summer")){
						if(!student_inse.student_inse_summer.containsKey(courseID) && CourseDetail_inse.inse_summer.containsKey(courseID))
						{
							ls.add(sid);
							student_inse.student_inse_summer.put(courseID, ls);
						}
						else if(student_inse.student_inse_summer.containsValue(sid)){
							return new Set(sem, courseID, sid, "taken");
						}
						else{
							ls = student_inse.student_inse_summer.get(courseID);
							ls.add(sid);
							student_inse.student_inse_summer.replace(courseID, student_soen.list13, ls);
						}
						CourseDetail_inse.inse_summer.replace(courseID, j, j-1);
						return new Set(sem, courseID, sid, "enroll");
					}
				}
			}
		}
		return new Set(sem, courseID, sid, "na");
	}
}

class Send3 implements Runnable{
	Thread t;
	int port;
	Set s = new Set();
	String message;
	byte[] buffer1;
	byte[] buffer2;
	public Send3(int p,String sem,String courseID,String sid,String message) {
		port = p;
		this.message = message;
		s = new Set(sem,courseID,sid,message);
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		DatagramSocket socket = null;
		try{
			buffer1 = new byte[1000];
			socket = new DatagramSocket();
			InetAddress host = InetAddress.getByName("localhost");
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			ObjectOutputStream outobj = new ObjectOutputStream(outstream);
			outobj.writeObject(s);
			buffer1 = outstream.toByteArray();
			DatagramPacket packet = new DatagramPacket(buffer1, buffer1.length, host, port);
			socket.send(packet);
			
			buffer2 = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer2, buffer2.length);
			socket.receive(reply);
			ByteArrayInputStream in = new ByteArrayInputStream(buffer2);
			ObjectInputStream objin = new ObjectInputStream(in);
			s = (Set)objin.readObject();
			setMsg(s.message); 
		}
		catch(SocketException e){e.printStackTrace();}
		catch(IOException e){e.printStackTrace();} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if(socket != null)	socket.close();
		}
	}
	public void setMsg(String msg){
		this.message = msg;
	}
	public String getMsg(){
		return message;
	}
}

class Receive3 implements Runnable{
	Thread t;
	Check_inse c;
	public Receive3() {
		c = new Check_inse();
		t = new Thread(this);
		t.start();
	}
	@Override
	public void run() {
		DatagramSocket socket = null;
		try{
			socket = new DatagramSocket(3967);
			byte[] buffer;
			byte[] buffer1;
			System.out.println("server 3 is running on port 3967...");
			while(true){
				buffer = new byte[20000];
				buffer1 = new byte[20000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream objin = new ObjectInputStream(in);
				try {
					Set s1 = (Set)objin.readObject();
					s1 = c.checkReply(s1.getSem(),s1.getCourseID(),s1.getId(),s1.getMessage());
					System.out.println("message:"+s1.message);
					ByteArrayOutputStream ob = new ByteArrayOutputStream();
					ObjectOutputStream oo = new ObjectOutputStream(ob);
					oo.writeObject(s1);
					buffer1 = ob.toByteArray();
				}
				catch (Exception e) {
				}
				//reply back to client
				DatagramPacket reply = new DatagramPacket(buffer1, buffer1.length, packet.getAddress(), packet.getPort());
				socket.send(reply);
			}
			
		}
		catch(SocketException e){}
		catch (IOException e) {	}
		finally {
			if(socket != null)	socket.close();
		}
	}
	
}
public class Server3 {
	
	static BufferedWriter bw = null;
	static FileWriter fw = null;
	static CourseDetail_inse cdc;
	static student_inse sdc;
	static String msg;
	String date;
	
	public Server3() {
		FrontEnd task = new FrontEnd();
		
		cdc = new CourseDetail_inse();
		sdc = new student_inse();	
		
		System.out.println("INSE server is Ready & waiting for client...");

		new Receive3();
	}
	
	public synchronized  int checkRegistration(String id) {
		//ArrayList<String> advisors=new ArrayList<>();
		char userType=id.charAt(4);
		
		if(userType=='S'){
			if(student_inse.students_in.contains(id))
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else{
			if (!student_inse.advisor_in.contains(id)) {
			return 0;
		}else
			return 1;
		}		
	}
	void LogFile(String advisor,String student,String date,String arguments,String method,String ans,String status) {
		
		try {
			fw = new FileWriter("./InseServer/INSEServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date+", ");
			bw.write(student+", ");
			bw.write(method+", ");
			bw.write(arguments+", ");
			bw.write(ans+", ");
			bw.write(status);
			bw.newLine();
		} catch (Exception e) {}
		finally{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public synchronized String swap(String studentID,String newCourseID,String oldCourseID) {
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));
		String ans = "";
		Send3 s1,s2;
		String arguments = "( "+studentID+", "+newCourseID+", "+oldCourseID + " )";
		int a,b,a_total,b_total;
		if(oldCourseID.substring(0, 4).equals("COMP")) {
			s1 = new Send3(3965,"sem",oldCourseID,studentID,"checkOld");		
			try {
				s1.t.join();
				if(s1.message.equals("no")) {
					LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
					return "no";
				}
				else {
					String sem = s1.message;
					if(newCourseID.substring(0, 4).equals("COMP")) {
						s1 = new Send3(3965,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send3(3965,sem,newCourseID,studentID,"seats");
						s2 = new Send3(3966,sem,newCourseID,studentID,"seats");
						
						try{
							
							s1.t.join();
							s2.t.join();
						}
						catch(InterruptedException e){}
						String[] a1 = s1.message.split(",");
						String[] a2 = s2.message.split(",");
						a_total = Integer.parseInt(a1[0]);
						a = Integer.parseInt(a1[1]);
						b = Integer.parseInt(a2[1]);
						b_total = Integer.parseInt(a2[0]);
						if(a+b >= 3) {
							LogFile("none",studentID,date,arguments,"swap","seats full","unsuccessful");
							return "full";
						}
						if(a+b < 3) {
							s1 = new Send3(3965,sem,oldCourseID,studentID,"drop");
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send3(3965,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
										LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
										return "enroll";
									}
								}
								s1 = new Send3(3965,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}
						}
					}
					
					if(newCourseID.substring(0, 4).equals("SOEN")) {
						s1 = new Send3(3966,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send3(3965,sem,newCourseID,studentID,"seats");
						s2 = new Send3(3966,sem,newCourseID,studentID,"seats");
						
						try{
							
							s1.t.join();
							s2.t.join();
						}
						catch(InterruptedException e){}
						String[] a1 = s1.message.split(",");
						String[] a2 = s2.message.split(",");
						a_total = Integer.parseInt(a1[0]);
						a = Integer.parseInt(a1[1]);
						b = Integer.parseInt(a2[1]);
						b_total = Integer.parseInt(a2[0]);
						if(a+b >= 3) {
							LogFile("none",studentID,date,arguments,"swap","seats full","unsuccessful");
							return "full";
						}
						if(a+b < 3 ) {
							s1 = new Send3(3965,sem,oldCourseID,studentID,"drop");
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send3(3966,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
										LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
										return "enroll";
									}
								}
								s1 = new Send3(3965,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}
						}
					}
					if(newCourseID.substring(0, 4).equals("INSE")) {
						boolean contains = false;
						if(student_inse.student_inse_fall.containsKey(newCourseID))
							contains = true;
						if(student_inse.student_inse_winter.containsKey(newCourseID))
							contains = true;
						if(student_inse.student_inse_summer.containsKey(newCourseID))
							contains = true;
						
						if(!contains) {
							LogFile("none",studentID,date,arguments,"swap","Course Not Found","unsuccessful");
							return "notfound";
						}
						int i = 0;
						if(sem.equals("Fall")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_fall.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Winter")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_winter.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Summer")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_summer.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(i>=3) {
							LogFile("none",studentID,date,arguments,"swap","limit exceed","unsuccessful");
							return "limit";
						}
						if(i<3) {
							s2 = new Send3(3965,sem,oldCourseID,studentID,"drop");
							s2.t.join();
							if(s2.message.equals("dropped")) {
								String res = enroll_inse(studentID, newCourseID, sem);
								if(res.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return res;
								}
								msg = res;
							}
							enroll_inse(studentID, oldCourseID, sem); 
							return msg;
						}
					}
				}
			} 
			catch (Exception e) {}
		
		}
		if(oldCourseID.substring(0, 4).equals("SOEN")) {
			s1 = new Send3(3966,"sem",oldCourseID,studentID,"checkOld");
			try {
				s1.t.join();
				if(s1.message.equals("no")) {
					LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
					return "no";
				}
				else {
					String sem = s1.message;
					if(newCourseID.substring(0, 4).equals("COMP")) {
						s1 = new Send3(3965,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send3(3965,sem,newCourseID,studentID,"seats");
						s2 = new Send3(3966,sem,newCourseID,studentID,"seats");
						
						try{
							
							s1.t.join();
							s2.t.join();
						}
						catch(InterruptedException e){}
						String[] a1 = s1.message.split(",");
						String[] a2 = s2.message.split(",");
						a_total = Integer.parseInt(a1[0]);
						a = Integer.parseInt(a1[1]);
						b = Integer.parseInt(a2[1]);
						b_total = Integer.parseInt(a2[0]);
						if(a+b >= 3) {
							LogFile("none",studentID,date,arguments,"swap","seats full","unsuccessful");
							return "full";
						}
						if(a+b < 3) {
							s1 = new Send3(3966,sem,oldCourseID,studentID,"drop");
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send3(3965,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
									}
								}
								s1 = new Send3(3966,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}
						}
					}
					
					if(newCourseID.equals("SOEN")) {
						s1 = new Send3(3966,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send3(3965,sem,newCourseID,studentID,"seats");
						s2 = new Send3(3966,sem,newCourseID,studentID,"seats");
						
						try{
							
							s1.t.join();
							s2.t.join();
						}
						catch(InterruptedException e){}
						String[] a1 = s1.message.split(",");
						String[] a2 = s2.message.split(",");
						a_total = Integer.parseInt(a1[0]);
						a = Integer.parseInt(a1[1]);
						b = Integer.parseInt(a2[1]);
						b_total = Integer.parseInt(a2[0]);
						if(a+b >= 3) {
							LogFile("none",studentID,date,arguments,"swap","seats full","unsuccessful");
							return "full";
						}
						if(a+b < 3) {
							s1 = new Send3(3966,sem,oldCourseID,studentID,"drop");
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send3(3966,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
									}
								}
								s1 = new Send3(3966,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}
						}
					}
					if(newCourseID.substring(0, 4).equals("INSE")) {
						boolean contains = false;
						if(student_inse.student_inse_fall.containsKey(newCourseID))
							contains = true;
						if(student_inse.student_inse_winter.containsKey(newCourseID))
							contains = true;
						if(student_inse.student_inse_summer.containsKey(newCourseID))
							contains = true;
						
						if(!contains) {
							LogFile("none",studentID,date,arguments,"swap","Course Not Found","unsuccessful");
							return "notfound";
						}
						int i = 0;
						if(sem.equals("Fall")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_fall.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Winter")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_winter.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Summer")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_summer.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(i >=3) {
							LogFile("none",studentID,date,arguments,"swap","limit excced","unsuccessful");
							return "limit";
						}
						if(i<3) {
							s2 = new Send3(3966,sem,oldCourseID,studentID,"drop");
							s2.t.join();
							if(s2.message.equals("dropped")) {
								String res = enroll_inse(studentID, newCourseID, sem); 
								if(res.equals("enroll")) {
								LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
								return res;
								}
								msg = res;
							}
							enroll_inse(studentID, oldCourseID, sem); 
							return msg;
						}
					}
				}
			} 
			catch (Exception e) {}
			}
			if(oldCourseID.substring(0,4).equals("INSE")) {
				String semester = "";
				boolean contains = false;
				if(student_inse.student_inse_fall.containsKey(oldCourseID))
					semester = "Fall";
				if(student_inse.student_inse_winter.containsKey(oldCourseID))
					semester = "Winter";
				if(student_inse.student_inse_summer.containsKey(oldCourseID)) {
					semester = "Summer";
				}
				if(semester.equals("Fall")) {
					List<String> ls = student_inse.student_inse_fall.get(oldCourseID);
					if(ls.contains(studentID))
						contains = true;					
				}
				if(semester.equals("Winter")) {
					List<String> ls = student_inse.student_inse_winter.get(oldCourseID);
					if(ls.contains(studentID))
						contains = true;
				}
				if(semester.equals("Summer")) {
					List<String> ls = student_inse.student_inse_summer.get(oldCourseID);
					if(ls.contains(studentID))
						contains = true;
				}
				if(!contains) {
					LogFile("none",studentID,date,arguments,"swap","not enrolled in old course","unsuccessful");
					return "notenroll";
				}
				if(contains) {
					if(newCourseID.substring(0, 4).equals("COMP")) {
						s1 = new Send3(3965,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send3(3965,semester,newCourseID,studentID,"seats");
						s2 = new Send3(3966,semester,newCourseID,studentID,"seats");
						
						try{
							
							s1.t.join();
							s2.t.join();
						}
						catch(InterruptedException e){}
						String[] a1 = s1.message.split(",");
						String[] a2 = s2.message.split(",");
						a_total = Integer.parseInt(a1[0]);
						a = Integer.parseInt(a1[1]);
						b = Integer.parseInt(a2[1]);
						b_total = Integer.parseInt(a2[0]);
						if(a+b >= 3) {
							LogFile("none",studentID,date,arguments,"swap","seats full","unsuccessful");
							return "full";
						}
						if(a_total + b_total >= 2) {
							LogFile("none",studentID,date,arguments,"swap","Can't take more than 2 courses of other Dept","unsuccessful");
							return "cant";
						}
						if(a+b < 3 && a_total + b_total < 2) {
							String res = drop(studentID, oldCourseID);							
							try {								
								if(res.equals("dropped")) {	
									s1 = new Send3(3965,semester,newCourseID,studentID,"enroll");
									s1.t.join();
									msg = s1.message;
									if(msg.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
									}
								}
								enroll_inse(studentID, oldCourseID, semester);
								return msg;
							} catch (Exception e) {	}
						}
					}
					
					if(newCourseID.substring(0, 4).equals("SOEN")) {
						s1 = new Send3(3965,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send3(3965,semester,newCourseID,studentID,"seats");
						s2 = new Send3(3966,semester,newCourseID,studentID,"seats");
						
						try{
							
							s1.t.join();
							s2.t.join();
						}
						catch(InterruptedException e){}
						String[] a1 = s1.message.split(",");
						String[] a2 = s2.message.split(",");
						a_total = Integer.parseInt(a1[0]);
						a = Integer.parseInt(a1[1]);
						b = Integer.parseInt(a2[1]);
						b_total = Integer.parseInt(a2[0]);
						if(a+b >= 3) {
							LogFile("none",studentID,date,arguments,"swap","seats full","unsuccessful");
							return "full";
						}
						if(a_total + b_total >= 2) {
							LogFile("none",studentID,date,arguments,"swap","Can't take more than 2 courses of other Dept","unsuccessful");
							return "cant";
						}
						if(a+b < 3 && a_total + b_total < 2) {
							String res = drop(studentID, oldCourseID);							
							try {								
								if(res.equals("dropped")) {
									s1 = new Send3(3966,semester,newCourseID,studentID,"enroll");
									s1.t.join();
									msg = s1.message;
									if(s1.message.equals("enroll")) {
										LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
										return "enroll";
									}
								}
								enroll_inse(studentID, oldCourseID, semester);
								return msg;
							} catch (Exception e) {	}							
						}
					}
					if(newCourseID.substring(0, 4).equals("INSE")) {
						if(student_inse.student_inse_fall.containsKey(newCourseID))
							contains = true;
						if(student_inse.student_inse_winter.containsKey(newCourseID))
							contains = true;
						if(student_inse.student_inse_summer.containsKey(newCourseID))
							contains = true;
						
						if(!contains) {
							LogFile("none",studentID,date,arguments,"swap","Course Not Found","unsuccessful");
							return "notfound";
						}
						int i = 0;
						if(semester.equals("Fall")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_fall.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(semester.equals("Winter")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_winter.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(semester.equals("Summer")) {
							for(Map.Entry<String, List<String>> map1:student_inse.student_inse_summer.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(i >= 3) {
							LogFile("none",studentID,date,arguments,"swap","limit excced","unsuccessful");
							return "limit";
						}
						if(i<3) {
							System.out.println(studentID+" "+newCourseID+" "+semester);
							String res = drop(studentID, oldCourseID);
							ans = res;
							if(res.equals("dropped")) {
								System.out.println("done");
								msg = enroll_inse(studentID, newCourseID, semester);
								if(msg.equals("enroll")) {
								LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
								return "enroll";
								}
							}
							enroll_inse(studentID, oldCourseID, semester);
							return msg;
						}
					}
				}
			}		
		return ans;
	}
	
	public synchronized String addCourse_inse(String courseID, String sem,int capacity){
		String ans ="invalid";
		String status = "";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		if(CourseDetail_inse.hash_inse.containsKey(sem)){
			if(sem.equals("Fall")){
				if(!CourseDetail_inse.inse_fall.containsKey(courseID)){
					CourseDetail_inse.set(capacity);
					CourseDetail_inse.inse_fall.put(courseID, capacity);
					CourseDetail_inse.hash_inse.put(sem,CourseDetail_inse.inse_fall);
					ans = "done";
				}
				else{
					ans = "exists";
				}
			}
			if(sem.equals("Winter")){
				if(!CourseDetail_inse.inse_winter.containsKey(courseID)){
					CourseDetail_inse.set(capacity);
					CourseDetail_inse.inse_winter.put(courseID, capacity);
					CourseDetail_inse.hash_inse.put(sem,CourseDetail_inse.inse_winter);
					ans = "done";
					System.out.println(CourseDetail_inse.inse_winter);
				}
				else{
					ans = "exists";
				}
			}if(sem.equals("Summer")){
				if(!CourseDetail_inse.inse_summer.containsKey(courseID)){
					CourseDetail_inse.set(capacity);
					CourseDetail_inse.inse_summer.put(courseID, capacity);
					CourseDetail_inse.hash_inse.put(sem,CourseDetail_inse.inse_summer);
					ans = "done";
				}
				else{
					System.out.println("course already exists!");
					ans = "exists";
				}
			}			
		}
		try {
			if(ans.equals("done"))
				status = "success";
			else
				status = "unsuccessful";
			fw = new FileWriter("./InseServer/INSEServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date);
			bw.write("add Course ");
			bw.write("("+courseID+", "+sem+")");
			bw.write(status+" ");
			bw.write(ans);
			bw.newLine();
		} catch (Exception e) {}
		finally{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ans;
	}
	public synchronized String enroll_inse(String studentID, String courseID, String sem){
		String ans = "invalid";
		String status = "";
		int i = 0; 
		int	j = 1000;
		Send3 s1,s2;
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		List<String> ls = new ArrayList<>();
		int a=0,b=0,a_total,b_total;
		if(courseID.substring(0, 4).equals("INSE")){
			HashMap<String, Integer> hm = CourseDetail_inse.hash_inse.get(sem);
			j = hm.get(courseID);
			if(j == 0){
				return "full";
			}
		}
		
		for(Map.Entry<String, HashMap<String,List<String>>> map: student_inse.std_inse.entrySet()){
			HashMap<String, List<String>> hash = map.getValue();
			for(Map.Entry<String, List<String>> map1:hash.entrySet()){
				List<String> l = map1.getValue();
				if(l.contains(studentID)){
					if(map1.getKey().equals(courseID))
						return "taken";
				}
			}
		}
		for(Map.Entry<String, HashMap<String,List<String>>> map: student_inse.std_inse.entrySet()){
			HashMap<String, List<String>> hash = map.getValue();
			for(Map.Entry<String, List<String>> map1:hash.entrySet()){
				List<String> l = map1.getValue();
				if(l.contains(studentID))
					i++;
			}
		}
		if(i==3){
			ans = "enrolled";
			return ans;
		}
		else if(i<3 && j!=0){
			s1 = new Send3(3965,sem,courseID,studentID,"seats");
			s2 = new Send3(3966,sem,courseID,studentID,"seats");
			try {
				s1.t.join();
				s2.t.join();
			} catch (InterruptedException e) {
			}
			String[] a1 = s1.message.split(",");
			String[] a2 = s2.message.split(",");
			a_total = Integer.parseInt(a1[0]);
			a = Integer.parseInt(a1[1]);
			b = Integer.parseInt(a2[1]);
			System.out.println("courses in soen:"+a);
			b_total = Integer.parseInt(a2[0]);
			System.out.println("courses in inse:"+b);
			if(courseID.substring(0, 4).equals("COMP") && a_total+b_total >= 2){
				System.out.println("can not take more courses of other sem");
				try {
					
					fw = new FileWriter("./InseServer/INSEServer.txt",true);
					bw = new BufferedWriter(fw);
					bw.write(date);
					bw.write("enroll ");
					bw.write("("+courseID+", "+sem+")");			
					bw.write(status+" ");
					bw.write("cant");
					bw.newLine();
				} catch (Exception e) {}
				finally{
					try {
						if (bw != null)
							bw.close();
						if (fw != null)
							fw.close();
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				return "cant";
			}
			
			if(courseID.substring(0, 4).equals("COMP") && a_total+b_total < 2 && a+b < 3){
					s1 = new Send3(3965,sem,courseID,studentID,"enroll");
					try {
						s1.t.join();				
						fw = new FileWriter("./InseServer/INSEServer.txt",true);
						bw = new BufferedWriter(fw);
						bw.write(date);
						bw.write("enroll ");
						bw.write("("+courseID+", "+sem+")");			
						bw.write("success ");
						bw.write(s1.message);
						bw.newLine();
					} catch (Exception e) {}
					finally{
						try {
							if (bw != null)
								bw.close();
							if (fw != null)
								fw.close();
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}
					} 
					System.out.println("message:"+s1.message);
					return s1.message;
				}	
			if(courseID.substring(0, 4).equals("SOEN") && a_total+b_total >= 2){
				System.out.println("can not take more courses of other sem");
				try {
					
					fw = new FileWriter("./InseServer/INSEServer.txt",true);
					bw = new BufferedWriter(fw);
					bw.write(date);
					bw.write("enroll ");
					bw.write("("+courseID+", "+sem+")");			
					bw.write(status+" ");
					bw.write("cant");
					bw.newLine();
				} catch (Exception e) {}
				finally{
					try {
						if (bw != null)
							bw.close();
						if (fw != null)
							fw.close();
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				return "cant";
			}
				if(courseID.substring(0, 4).equals("SOEN") && a_total+b_total < 2 && a+b < 3){
					s1 = new Send3(3966,sem,courseID,studentID,"enroll");
					try {
						s1.t.join();				
						fw = new FileWriter("./InseServer/INSEServer.txt",true);
						bw = new BufferedWriter(fw);
						bw.write(date);
						bw.write("enroll ");
						bw.write("("+courseID+", "+sem+")");			
						bw.write("success ");
						bw.write(s1.message);
						bw.newLine();
					} catch (Exception e) {}
					finally{
						try {
							if (bw != null)
								bw.close();
							if (fw != null)
								fw.close();
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					return s1.message;
				}
			
			if(a+b+i >= 3){
				System.out.println("total"+(a+b+i));
				ans = "register3";
			}
			if(a+b+i<3){
				if(courseID.substring(0, 4).equals("INSE")){
				
				if(sem.equals("Fall")){				
					if(student_inse.student_inse_fall.containsValue(studentID)){
						ans = "taken";
					}
					else{
						if(CourseDetail_inse.inse_fall.containsKey(courseID) && !student_inse.student_inse_fall.containsKey(courseID)){
							ls.add(studentID);
							student_inse.student_inse_fall.put(courseID, ls);
						}
						else{
							ls = student_inse.student_inse_fall.get(courseID);
							ls.add(studentID);
							student_inse.student_inse_fall.replace(courseID, student_inse.list11, ls);
						}
						CourseDetail_inse.inse_fall.replace(courseID, j, j-1);
						ans = "enroll";
					}
				}
				if(sem.equals("Winter")){
					if(student_inse.student_inse_winter.containsValue(studentID)){
						ans = "taken";
					}
					else{
						if(CourseDetail_inse.inse_winter.containsKey(courseID) && !student_inse.student_inse_winter.containsKey(courseID)){
							ls.add(studentID);
							student_inse.student_inse_winter.put(courseID, ls);
						}
						else{
							ls = student_inse.student_inse_winter.get(courseID);
							ls.add(studentID);
							student_inse.student_inse_winter.replace(courseID, student_inse.list12, ls);
						}
						CourseDetail_inse.inse_winter.replace(courseID, j, j-1);
						ans = "enroll";				}
				}
				if(sem.equals("Summer")){
					if(student_inse.student_inse_summer.containsValue(studentID)){
						ans = "taken";
					}
					else{
						if(CourseDetail_inse.inse_summer.containsKey(courseID) && !student_inse.student_inse_summer.containsKey(courseID)){
							ls.add(studentID);
							student_inse.student_inse_summer.put(courseID, ls);
						}
						else{
							ls = student_inse.student_inse_summer.get(courseID);
							ls.add(studentID);
							student_inse.student_inse_summer.replace(courseID, student_inse.list13, ls);
						}
						CourseDetail_inse.inse_summer.replace(courseID, j, j-1);
						ans = "enroll";				
					}
				}
				}
			}
		}
		try {
			if(ans.equals("enroll")){
				status = "success";
			}
			else
				status = "unsuccessful";
			fw = new FileWriter("./InseServer/INSEServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date);
			bw.write("enroll ");
			bw.write("("+studentID + ", "+courseID+", "+sem+")");			
			bw.write(status+" ");
			bw.write(ans);
			bw.newLine();
		} catch (Exception e) {}
		finally{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ans;
	}
	public synchronized String remove(String courseID,String sem){
		String ans = "invalid";
		String status ="";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		if(courseID.substring(0, 4).equals("INSE")){
			if(CourseDetail_inse.hash_inse.containsKey(sem)){
				if(sem.equals("Fall")){
					if(CourseDetail_inse.inse_fall.containsKey(courseID)){
						CourseDetail_inse.inse_fall.remove(courseID);
						student_inse.student_inse_fall.remove(courseID);
						ans = "done";
					}
					else{
						ans = "cant";
					}
				}
				if(sem.equals("Winter")){
					if(CourseDetail_inse.inse_winter.containsKey(courseID)){
						CourseDetail_inse.inse_winter.remove(courseID);
						student_inse.student_inse_winter.remove(courseID);
						ans = "done";
					}
					else{
						ans = "cant";
					}
				}
				if(sem.equals("Summer")){
					if(CourseDetail_inse.inse_summer.containsKey(courseID)){
						CourseDetail_inse.inse_summer.remove(courseID);
						student_inse.student_inse_summer.remove(courseID);
						ans = "done";
					}
					else{
						ans = "cant";
					}
				}
			}
		}
		try {
			if(ans.equals("done")){
				status = "success";
			}
			else
				status = "unsuccessful";
			fw = new FileWriter("./InseServer/INSEServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date);
			bw.write("RemoveCourse ");
			bw.write("("+courseID+", "+sem+")");			
			bw.write(status+" ");
			bw.write(ans);
			bw.newLine();
		} catch (Exception e) {}
		finally{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ans;
	}
	public synchronized String listCourse(String sem){
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));
		System.out.println(CourseDetail_inse.inse_winter);
		Send s1 = new Send(3965,sem,"course","sid","list");
		Send s2 = new Send(3966,sem,"course","sid","list");
		try{
			s1.t.join();
			s2.t.join();
		}
		catch(InterruptedException e){}
		String str = "";
		if(sem.equals("Fall")){
			for(Map.Entry<String, Integer> map : CourseDetail_inse.inse_fall.entrySet()){
				str += map.getKey() + " " + map.getValue()+ ",";
			}								
		}
		if(sem.equals("Winter")){
			for(Map.Entry<String, Integer> map : CourseDetail_inse.inse_winter.entrySet()){
				str += map.getKey() + " " + map.getValue()+ ",";
			}				
		}
		if(sem.equals("Summer")){
			for(Map.Entry<String, Integer> map : CourseDetail_inse.inse_summer.entrySet()){
				str += map.getKey() + " " + map.getValue() + ",";
			}				
		}
		if(!s1.message.equals("empty")){
			str += s1.message;
		}
		if(!s2.message.equals("empty")){
			str += s2.message;
		}
		try {			
			fw = new FileWriter("./InseServer/INSEServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date);
			bw.write("ListCourseAvailability ");
			bw.write("("+sem+")");
			bw.write("success ");
			bw.write(str);
			bw.newLine();
		} catch (Exception e) {}
		finally{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return str;
	}
	public synchronized String drop(String studebtID,String courseID){
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		Send3 s1;
		Send3 s2;
		String ans = "invalid";
		String status = "";
		if(courseID.substring(0, 4).equals("COMP")){
			s1 = new Send3(3965, "sem", courseID, studebtID, "drop");
			try {
				s1.t.join();
			} 
			catch (InterruptedException e) {}
			ans = s1.message;
		}
		else if(courseID.substring(0, 4).equals("SOEN")){
			s2 = new Send3(3966, "sem", courseID, studebtID, "drop");
			try {
				s2.t.join();
			} 
			catch (InterruptedException e) {}
			ans = s2.message;
		}
		else{
			if(student_inse.student_inse_fall.containsKey(courseID)){
				List<String> lst = student_inse.student_inse_fall.get(courseID);
				lst.remove(studebtID);
				student_inse.student_inse_fall.replace(courseID, student_inse.list11, lst);
				Integer i = CourseDetail_inse.inse_fall.get(courseID);
				CourseDetail_inse.inse_fall.replace(courseID, i, (i+1));
				ans = "dropped";
			}
			if(student_inse.student_inse_winter.containsKey(courseID)){
				List<String> lst = student_inse.student_inse_winter.get(courseID);
				lst.remove(studebtID);
				student_inse.student_inse_winter.replace(courseID, student_inse.list12, lst);
				Integer i = CourseDetail_inse.inse_winter.get(courseID);
				CourseDetail_inse.inse_winter.replace(courseID, i, (i+1));
				ans = "dropped";
			}
			if(student_inse.student_inse_summer.containsKey(courseID)){
				List<String> lst = student_inse.student_inse_summer.get(courseID);
				lst.remove(studebtID);
				student_inse.student_inse_summer.replace(courseID, student_inse.list13, lst);
				Integer i = CourseDetail_inse.inse_summer.get(courseID);
				CourseDetail_inse.inse_summer.replace(courseID, i, (i+1));
				ans = "dropped";
			}
		}
		try {
			if(ans.equals("dropped")){
				status = "success";
			}
			else
				status = "unsuccessful";
			fw = new FileWriter("./InseServer/INSEServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date);
			bw.write("DropCourse ");
			bw.write("("+studebtID+", "+courseID+")");			
			bw.write(status+" ");
			bw.write(ans);
			bw.newLine();
		} catch (Exception e) {}
		finally{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ans;
	}
	public synchronized String getClassSchedule(String studentID){
		Send3 s1,s2;
		String value = "a";
		String key="",key1="",key2="";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));
		String result = "empty";
		LinkedHashSet<String> list1 = new LinkedHashSet<>();
		LinkedHashSet<String> list2 = new LinkedHashSet<>();
		LinkedHashSet<String> list3 = new LinkedHashSet<>();
		HashMap<String, LinkedHashSet<String>> hm = new HashMap<>();
		String[] str_array1 = {"ASC","SAD","ASD"};
		for(Map.Entry<String, HashMap<String, List<String>>> entry : student_inse.std_inse.entrySet()){
			HashMap<String, List<String>> hash = entry.getValue();
			for(Map.Entry<String, List<String>> entry1 : hash.entrySet()){
				List<String> str = entry1.getValue();
				if(str.contains(studentID)){
					if(entry.getKey().equals("Fall")){
						list1.add(entry1.getKey());
						str_array1[0] = entry.getKey();
					}
					if(entry.getKey().equals("Winter")){
						list2.add(entry1.getKey());
						str_array1[1] = entry.getKey();
					}
					if(entry.getKey().equals("Summer")){
						list3.add(entry1.getKey());
						str_array1[2] = entry.getKey();
					}
				}	
			}
		}
		System.out.println(list1);
		System.out.println(list2);
		System.out.println(list3);
		s1 = new Send3(3965, "sem", "courseID", studentID, "schedule");
		s2 = new Send3(3966, "sem", "courseID", studentID, "schedule");
		try {
			s1.t.join();
			s2.t.join();
		} catch (Exception e) {}
		if(!s1.message.equals("empty")){
			value = s1.message;
			value = value.substring(0, s1.message.length());
			String[] part = value.split(",");
			for(String s : part){
				if(s.equals("Fall"))
					key = s;
				if(s.equals("Winter"))
					key1 = s;
				if(s.equals("Summer"))
					key2 = s;
				if(s.charAt(0) == 'f')
					list1.add(s.substring(1, s.length()));
				if(s.charAt(0) == 'w')
					list2.add(s.substring(1, s.length()));
				if(s.charAt(0) == 's')
					list3.add(s.substring(1, s.length()));
			}
		}
		if(!s2.message.equals("empty")){
			value = s2.message;
			value = value.substring(0, s2.message.length());
			String[] part = value.split(",");
			for(String s : part){
				if(s.equals("Fall"))
					key = s;
				if(s.equals("Winter"))
					key1 = s;
				if(s.equals("Summer"))
					key2 = s;
				if(s.charAt(0) == 'f')
					list1.add(s.substring(1, s.length()));
				if(s.charAt(0) == 'w')
					list2.add(s.substring(1, s.length()));
				if(s.charAt(0) == 's')
					list3.add(s.substring(1, s.length()));
			}
		}	
		if(key.equals("Fall") || str_array1[0].equals("Fall")){
			Iterator<String> itr = list1.iterator();
			result = "Fall:";
			while(itr.hasNext()){
				result += itr.next();
				result += ",";
			}
		}
			hm.put("Fall", list1);
		if(key1.equals("Winter") || str_array1[1].equals("Winter")){
			Iterator<String> itr = list2.iterator();
			result += "Winter:";
			while(itr.hasNext()){
				result += itr.next();
				result += ",";
			}
		}
		if(key2.equals("Summer") || str_array1[2].equals("Summer")){
			Iterator<String> itr = list3.iterator();
			result += "Summer:";
			while(itr.hasNext()){
				result += itr.next();
				result += ",";
			}
			
		}
		try {			
			fw = new FileWriter("./InseServer/INSEServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date);
			bw.write("GetClassSchedule ");
			bw.write("("+studentID+")");
			bw.write("success ");
			bw.write(value);
			bw.newLine();
		} catch (Exception e) {}
		finally{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception{
		
		
		try {
			
//			FrontEnd task = new FrontEnd();
//			
//			cdc = new CourseDetail_inse();
//			sdc = new student_inse();	
//			
//			System.out.println("INSE server is Ready & waiting for client...");
//
//			new Receive3();
		} 
		catch (Exception e) {}	
	}

}
