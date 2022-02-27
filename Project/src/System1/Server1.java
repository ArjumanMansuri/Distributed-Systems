package System1;

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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DCRS.FrontEnd;

class student_comp{
	static List<String> list11 = new ArrayList<>();
	static List<String> list12 = new ArrayList<>();
	static List<String> list13 = new ArrayList<>();
	static HashMap<String,List<String>> student_comp_fall = new HashMap<>();
	static HashMap<String,List<String>> student_comp_winter = new HashMap<>();
	static HashMap<String,List<String>> student_comp_summer = new HashMap<>();
	
	static HashMap<String, HashMap<String,List<String>>> std_comp = new HashMap<>();
		
	static List<String> students_co = new ArrayList<>();
		
	static List<String> advisor_co = new ArrayList<>();

	public student_comp(){
		list11.add("COMPS1000");
	//	list11.add("COMPS1001");
		student_comp_fall.put("COMP6231",list11);
		std_comp.put("Fall", student_comp_fall);
		
		list12.add("COMPS1001");
		student_comp_winter.put("COMP6281", list12);
		std_comp.put("Winter", student_comp_winter);
		
		list13.add("COMPS1002");
		student_comp_summer.put("COMP6461", list13);
		std_comp.put("Summer", student_comp_summer);
				
		students_co.add("COMPS1000");
		students_co.add("COMPS1001");
		students_co.add("COMPS1002");
				
		advisor_co.add("COMPA1000");
		advisor_co.add("COMPA1001");
		advisor_co.add("COMPA1002");
	}
}

class CourseDetail_comp implements Serializable{
	
	private static final long serialVersionUID = 1L;
	static int capacity;
	static HashMap<String,Integer> comp_fall = new HashMap<>();
	static HashMap<String,Integer> comp_winter = new HashMap<>();
	static HashMap<String,Integer> comp_summer = new HashMap<>();
	static HashMap<String, HashMap<String,Integer>> hash_comp = new HashMap<>();
	
	public CourseDetail_comp(){
		capacity = 3;
		comp_fall.put("COMP6231",capacity);
		hash_comp.put("Fall", comp_fall);
		
		comp_winter.put("COMP6281",capacity);
		hash_comp.put("Winter", comp_winter);
		
		comp_summer.put("COMP6461",capacity);
		hash_comp.put("Summer", comp_summer);
	}
	static void set(int c){
		capacity = c;
	}
}
class Check_comp{
	CourseDetail_comp cdc = new CourseDetail_comp();
	student_comp sdc = new student_comp();
	Set s;
	public synchronized Set checkReply(String sem,String courseID,String sid,String msg){
		if(msg.equals("checkcourse")) {
			boolean contains = false;
			if(CourseDetail_comp.comp_fall.containsKey(courseID))
				contains = true;
			if(CourseDetail_comp.comp_winter.containsKey(courseID))
				contains = true;
			if(CourseDetail_comp.comp_summer.containsKey(courseID))
				contains = true;
			
			if(contains) 
				return new Set(sem,courseID,sid,"yes");
			else 
				return new Set(sem,courseID,sid,"no");
		}
		if(msg.equals("checkOld")) {
			String semester = "";
			boolean contains = false;
			System.out.println(student_comp.std_comp);
			System.out.println(student_comp.student_comp_fall.containsKey(courseID));
			if(student_comp.student_comp_fall.containsKey(courseID))
				semester = "Fall";
			if(student_comp.student_comp_winter.containsKey(courseID))
				semester = "Winter";
			if(student_comp.student_comp_summer.containsKey(courseID))
				semester = "Summer";
			if(semester.equals("Fall")) {
			//	contains = student_comp.list11.contains(sid);
				for(Map.Entry<String, List<String>>map : student_comp.student_comp_fall.entrySet()) {
					List<String> ls = map.getValue();
					if(ls.contains(sid))
						contains = true;
				}
			}
			if(semester.equals("Winter")) {
				for(Map.Entry<String, List<String>>map : student_comp.student_comp_winter.entrySet()) {
					List<String> ls = map.getValue();
					if(ls.contains(sid))
						contains = true;
				}
			}
			if(semester.equals("Summer")) {
				for(Map.Entry<String, List<String>>map : student_comp.student_comp_summer.entrySet()) {
					List<String> ls = map.getValue();
					if(ls.contains(sid))
						contains = true;
				}
			}
			System.out.println("contains:"+contains);
			if(contains) return new Set(sem,courseID,sid,semester);
			else return new Set(sem,courseID,sid,"no");
		}
		
		if(msg.equals("schedule")){
			String value ="a";
			String st = new String("1");
			String st1 = new String("2");
			String st2 = new String("3");
			String[] str_array2 = {"ASC","SAD","ASD"};
			for(Map.Entry<String, HashMap<String, List<String>>> entry : student_comp.std_comp.entrySet()){
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
							if(st2.equals("s"))
								st2 = "s"+entry1.getKey()+",";
							else
								st2 += "s"+entry1.getKey()+",";
							str_array2[3] = entry.getKey();
						}
					}	
				}
			}
			if(str_array2[0].equals("Fall")){
				st = str_array2[0].toString()+","+st;
			}
			if(str_array2[1].equals("Winter")){
				st1 = str_array2[1].toString()+","+st1;
			}
			if(str_array2[2].equals("Summer"))
				st2 = str_array2[2].toString()+","+st2;
			if(!st.equals("1")){
				if(value.equals("a"))
					value = st;
				else
					value+=st;
			}
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
			if(student_comp.student_comp_fall.containsKey(courseID)){
				List<String> lst = student_comp.student_comp_fall.get(courseID);
				lst.remove(sid);
				student_comp.student_comp_fall.replace(courseID, student_comp.list11, lst);
				Integer i = CourseDetail_comp.comp_fall.get(courseID);
				CourseDetail_comp.comp_fall.replace(courseID, i, (i+1));
				return new Set(sem, courseID, sid, "dropped");
			}
			if(student_comp.student_comp_winter.containsKey(courseID)){
				List<String> lst = student_comp.student_comp_winter.get(courseID);
				lst.remove(sid);
				student_comp.student_comp_winter.replace(courseID, student_comp.list12, lst);
				Integer i = CourseDetail_comp.comp_fall.get(courseID);
				CourseDetail_comp.comp_winter.replace(courseID, i, (i+1));
				return new Set(sem, courseID, sid, "dropped");
			}
			if(student_comp.student_comp_summer.containsKey(courseID)){
				List<String> lst = student_comp.student_comp_summer.get(courseID);
				lst.remove(sid);
				student_comp.student_comp_summer.replace(courseID, student_comp.list13, lst);
				Integer i = CourseDetail_comp.comp_summer.get(courseID);
				CourseDetail_comp.comp_summer.replace(courseID, i, (i+1));
				return new Set(sem, courseID, sid, "dropped");
			}
			return new Set(sem, courseID, sid, "notexists");
		}
		if(msg.equals("list")){
			StringBuilder builder = new StringBuilder();
			ArrayList<String> ar = new ArrayList<>();
			if(sem.equals("Fall")){
				for(Map.Entry<String, Integer> map : CourseDetail_comp.comp_fall.entrySet()){
					String str = map.getKey() + " " + map.getValue()+ ",";
					ar.add(str);
				}								
			}
			if(sem.equals("Winter")){
				for(Map.Entry<String, Integer> map : CourseDetail_comp.comp_winter.entrySet()){
					String str = map.getKey() + " " + map.getValue()+ ",";
					ar.add(str);
				}				
			}
			if(sem.equals("Summer")){
				for(Map.Entry<String, Integer> map : CourseDetail_comp.comp_summer.entrySet()){
					String str = map.getKey() + " " + map.getValue()+ ",";
					ar.add(str);
				}				
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
				for(Map.Entry<String, List<String>> map1:student_comp.student_comp_fall.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						j++;
				}
			}
			if(sem.equals("Winter")){
				for(Map.Entry<String, List<String>> map1:student_comp.student_comp_winter.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						j++;
				}
			}
			if(sem.equals("Summer")){
				for(Map.Entry<String, List<String>> map1:student_comp.student_comp_summer.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						j++;
				}
			}
				for(Map.Entry<String, List<String>> map1:student_comp.student_comp_fall.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						i++;
				}
				for(Map.Entry<String, List<String>> map1:student_comp.student_comp_winter.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						i++;
				}
				for(Map.Entry<String, List<String>> map1:student_comp.student_comp_summer.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						i++;
				}
			s = new Set(sem, courseID, sid, Integer.toString(i)+","+Integer.toString(j));
			return s;
		}
		if(msg.equals("enroll")){
			int i = 0; 
			int	j = 0;
			List<String> ls = new ArrayList<>();
			HashMap<String, Integer> hm = CourseDetail_comp.hash_comp.get(sem);
			if(!hm.containsKey(courseID))
				return new Set(sem, courseID, sid, "notexists");
			j = hm.get(courseID);
			System.out.println("j"+j);
			if(j == 0){
				return new Set(sem, courseID, sid, "full");
			}
			if(courseID.substring(0,4).equals("COMP")){
				if(student_comp.student_comp_fall.get(courseID) != null){
					List<String> lst = student_comp.student_comp_fall.get(courseID);
					if(lst.contains(sid)){
						return new Set(sem, courseID, sid, "taken");
					}
				}
				if(student_comp.student_comp_winter.get(courseID) != null){
					List<String> lst = student_comp.student_comp_winter.get(courseID);
					if(lst.contains(sid)){
						return new Set(sem, courseID, sid, "taken");
					}
				}
				if(student_comp.student_comp_summer.get(courseID) != null){
					List<String> lst = student_comp.student_comp_summer.get(courseID);
					if(lst.contains(sid)){
						return new Set(sem, courseID, sid, "taken");
					}
				}
			}
			for(Map.Entry<String, HashMap<String,List<String>>> map: student_comp.std_comp.entrySet()){
				HashMap<String, List<String>> hash = map.getValue();
				for(Map.Entry<String, List<String>> map1:hash.entrySet()){
					List<String> l = map1.getValue();
					if(l.contains(sid))
						i++;
				}
			}
			for(Map.Entry<String, HashMap<String,List<String>>> map: student_comp.std_comp.entrySet()){
				HashMap<String, List<String>> hash = map.getValue();
				for(Map.Entry<String, List<String>> map1:hash.entrySet()){
					List<String> l = map1.getValue();
					
					if(l.contains(sid)){
						if(map1.getKey().equals(courseID))
							return new Set(sem, courseID, sid, "taken");
					}
				}
			}
			System.out.println("i :"+i);
			if(i == 2){
				return new Set(sem, courseID, sid, "cant");
			}
			if(i < 2){
				if(courseID.substring(0,4).equals("COMP")){
					if(sem.equals("Fall")){
						if(!student_comp.student_comp_fall.containsKey(courseID) && CourseDetail_comp.comp_fall.containsKey(courseID))
						{
							ls.add(sid);
							student_comp.student_comp_fall.put(courseID, ls);
						}
						else{
							ls = student_comp.student_comp_fall.get(courseID);
							ls.add(sid);
							student_comp.student_comp_fall.replace(courseID, student_comp.list11, ls);
						}
						CourseDetail_comp.comp_fall.replace(courseID, j, j-1);
						return new Set(sem, courseID, sid, "enroll");
					}
					if(sem.equals("Winter")){
						if(!student_comp.student_comp_winter.containsKey(courseID) && CourseDetail_comp.comp_winter.containsKey(courseID))
						{
							ls.add(sid);
							student_comp.student_comp_winter.put(courseID, ls);
						}
						else{
							ls = student_comp.student_comp_winter.get(courseID);
							ls.add(sid);
							student_comp.student_comp_winter.replace(courseID, student_comp.list12, ls);
						}
						CourseDetail_comp.comp_fall.replace(courseID, j, j-1);
						return new Set(sem, courseID, sid, "enroll");
					}
					if(sem.equals("Summer")){
						if(student_comp.student_comp_summer.containsValue(sid)){
							return new Set(sem, courseID, sid, "taken");
						}
						else{
							student_comp.list13.add(sid);
							CourseDetail_comp.comp_summer.replace(courseID, (j-1));
							student_comp.student_comp_summer.put(courseID, student_comp.list13);
							return new Set(sem, courseID, sid, "enroll");
						}
					}
				}
			}
		}
		return new Set(sem, courseID, sid, "na");
	}
}

class Send implements Runnable{
	Thread t;
	int port;
	String message;
	String sem;
	String courseID;
	String sid;
	byte[] buffer1;
	byte[] buffer2;
//	Set s = new Set();
	public Send(int p,String sem1,String courseID1,String sid1,String msg) {
		port = p;
		this.message = msg;
		sem = sem1;
		courseID = courseID1;
		sid = sid1;
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run() {
		DatagramSocket socket = null;
		Set s = new Set(sem,courseID,sid,message);
		
		try{
			buffer1 = new byte[1000];
			socket = new DatagramSocket();
			InetAddress host = InetAddress.getByName("localhost");
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			ObjectOutputStream outobj = new ObjectOutputStream(outstream);
			outobj.writeObject(s);
		
			buffer1 = outstream.toByteArray();
			outobj.flush();
			DatagramPacket packet = new DatagramPacket(buffer1, buffer1.length, host, port);
			socket.send(packet);
			buffer2 = new byte[1000];

			//	reply from servers
			DatagramPacket reply = new DatagramPacket(buffer2, buffer2.length);
			socket.receive(reply);
			ByteArrayInputStream in = new ByteArrayInputStream(buffer2);
			ObjectInputStream objin = new ObjectInputStream(in);
			Set s1 = (Set)objin.readObject();	
			setMsg(s1.message);
			System.out.println(Thread.activeCount());
			System.out.println("send"+s1.message);
		}
		catch(SocketException e){}
		catch(IOException e){} 
		catch (ClassNotFoundException e) {
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

class Receive implements Runnable{
	Thread t;
	Check_comp c;
	public Receive() {
		c = new Check_comp();
		t = new Thread(this);
		t.start();
	}
	@Override
	public void run() {
		DatagramSocket socket = null;
		try{
			socket = new DatagramSocket(3965);
			byte[] buffer;
			System.out.println("server 1 is running on port 3965...");
			while(true){
				buffer = new byte[1000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				//reply back to client
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				ObjectInputStream objin = new ObjectInputStream(in);
				try {
					Set s1 = (Set)objin.readObject();
					s1 = c.checkReply(s1.getSem(),s1.getCourseID(),s1.getId(),s1.getMessage());
					ByteArrayOutputStream ob = new ByteArrayOutputStream();
					ObjectOutputStream oo = new ObjectOutputStream(ob);
					oo.writeObject(s1);
					buffer = ob.toByteArray();
				}
				catch (Exception e) {
				}
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
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


public class Server1 {
	
	static BufferedWriter bw = null;
	static FileWriter fw = null;
	static CourseDetail_comp cdc;
	static String msg;
	String date;
	public static FrontEnd task;
	
	public Server1() {
		task = new FrontEnd();
		new student_comp();
		cdc = new CourseDetail_comp();
		
		System.out.println("COMP server is Ready & waiting for client...");

		new Receive();
	}
	public synchronized  int checkRegistration(String id) {
		System.out.println("in check registration..");
		char userType=id.charAt(4);
		
		if(userType=='S'){
			System.out.println(student_comp.students_co);
			if(student_comp.students_co.contains(id))
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else{
			if (!student_comp.advisor_co.contains(id)) {
			return 0;
		}else
			return 1;
		}		
	}
	
	void LogFile(String advisor,String student,String date,String arguments,String method,String ans,String status) {
	
		try {
			fw = new FileWriter("./CompServer/COMPServer.txt",true);
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
		Send s1,s2;
		int a,b,a_total,b_total;
		String arguments = "( "+studentID+", "+newCourseID+", "+oldCourseID + " )";
		if(oldCourseID.substring(0, 4).equals("SOEN")) {
			s1 = new Send(3966,"sem",oldCourseID,studentID,"checkOld");		
			try {
				s1.t.join();
				if(s1.message.equals("no")) {
					LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
					return "no";
				}
				else {
					String sem = s1.message;
					if(newCourseID.substring(0, 4).equals("SOEN")) {
						s1 = new Send(3966,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send(3966,sem,newCourseID,studentID,"seats");
						s2 = new Send(3967,sem,newCourseID,studentID,"seats");
						
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
							s1 = new Send(3966,sem,oldCourseID,studentID,"drop");
							
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send(3966,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
									}
								}
								s1 = new Send(3966,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}
							
						}
					}
					
					if(newCourseID.substring(0, 4).equals("INSE")) {
						s1 = new Send(3967,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send(3966,sem,newCourseID,studentID,"seats");
						s2 = new Send(3967,sem,newCourseID,studentID,"seats");
						
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
							s1 = new Send(3966,sem,oldCourseID,studentID,"drop");
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send(3967,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
									}
								}
								s1 = new Send(3966,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}
							
						}
					}
					if(newCourseID.substring(0, 4).equals("COMP")) {
						boolean contains = false;
						if(student_comp.student_comp_fall.containsKey(newCourseID))
							contains = true;
						if(student_comp.student_comp_winter.containsKey(newCourseID))
							contains = true;
						if(student_comp.student_comp_summer.containsKey(newCourseID))
							contains = true;
						
						if(!contains) {
							LogFile("none",studentID,date,arguments,"swap","Course Not Found","unsuccessful");
							return "notfound";
						}
						int i = 0;
						if(sem.equals("Fall")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_fall.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Winter")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_winter.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Summer")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_summer.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(i >= 3) {
							LogFile("none",studentID,date,arguments,"swap","Limit exceed","unsuccessful");
							return "limit";
						}
						if(i<3) {
							s2 = new Send(3966,sem,oldCourseID,studentID,"drop");
							s2.t.join();
							if(s2.message.equals("dropped")) {
								String res = enroll_comp(studentID, newCourseID, sem);	
								if(res.equals("enroll")) {
								LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
								return res;
								}
								msg = res;
							}
							s2 = new Send(3966,sem,oldCourseID,studentID,"enroll");
							s2.t.join();
							return msg;
						}
					}
				}
			} 
			catch (Exception e) {}
		
		}
		if(oldCourseID.substring(0, 4).equals("INSE")) {
			s1 = new Send(3967,"sem",oldCourseID,studentID,"checkOld");
			try {
				s1.t.join();
				if(s1.message.equals("no")) {
					LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
					return "no";
				}
				else {
					String sem = s1.message;
					if(newCourseID.substring(0, 4).equals("SOEN")) {
						s1 = new Send(3966,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send(3966,sem,newCourseID,studentID,"seats");
						s2 = new Send(3967,sem,newCourseID,studentID,"seats");
						
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
							s1 = new Send(3967,sem,oldCourseID,studentID,"drop");
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send(3966,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
									}
								}
								s1 = new Send(3967,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}
							
						}
					}
					
					if(newCourseID.equals("INSE")) {
						s1 = new Send(3967,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "notfound";
							}
						}
						catch(Exception e) {}
						s1 = new Send(3966,sem,newCourseID,studentID,"seats");
						s2 = new Send(3967,sem,newCourseID,studentID,"seats");
						
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
							s1 = new Send(3967,sem,oldCourseID,studentID,"drop");
							try {
								s1.t.join();
								if(s1.message.equals("dropped")) {
									s2 = new Send(3967,sem,newCourseID,studentID,"enroll");
									s2.t.join();
									msg = s2.message;
									if(msg.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
									}
								}
								s1 = new Send(3967,sem,oldCourseID,studentID,"enroll");
								s1.t.join();
								return msg;
							} catch (Exception e) {	}					
						}
					}
					if(newCourseID.substring(0, 4).equals("COMP")) {
						boolean contains = false;
						if(CourseDetail_comp.comp_fall.containsKey(newCourseID))
							contains = true;
						if(CourseDetail_comp.comp_winter.containsKey(newCourseID))
							contains = true;
						if(CourseDetail_comp.comp_summer.containsKey(newCourseID))
							contains = true;
						
						if(!contains) {
							LogFile("none",studentID,date,arguments,"swap","Course Not Found","unsuccessful");
							return "notfound";
						}
						int i = 0;
						if(sem.equals("Fall")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_fall.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Winter")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_winter.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(sem.equals("Summer")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_summer.entrySet()){
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
							s2 = new Send(3967,sem,oldCourseID,studentID,"drop");
							s2.t.join();
							if(s2.message.equals("dropped")) {
								String res = enroll_comp(studentID, newCourseID, sem); 
								if(res.equals("enroll")) {
								LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
								return res;
								}
								msg = res;
							}
							s2 = new Send(3967,sem,oldCourseID,studentID,"enroll");
							s2.t.join();
							return msg;
						}
					}
				}
			} 
			catch (Exception e) {}
			}
			if(oldCourseID.substring(0,4).equals("COMP")) {
				String semester = "";
				boolean contains = false;
				if(student_comp.student_comp_fall.containsKey(oldCourseID))
					semester = "Fall";
				if(student_comp.student_comp_winter.containsKey(oldCourseID))
					semester = "Winter";
				if(student_comp.student_comp_summer.containsKey(oldCourseID)) {
					System.out.println("sem taken");
					semester = "Summer";
				}
				if(semester.equals("Fall")) {
					List<String> ls = student_comp.student_comp_fall.get(oldCourseID);
					if(ls.contains(studentID)) {
						System.out.println("contains : ");
						contains = true;
					}					
				}
				if(semester.equals("Winter")) {
					List<String> ls = student_comp.student_comp_winter.get(oldCourseID);
					if(ls.contains(studentID)) {
						contains = true;
					}
				}
				if(semester.equals("Summer")) {
					List<String> ls = student_comp.student_comp_summer.get(oldCourseID);
					if(ls.contains(studentID)) {
						contains = true;
					}
				}
				if(!contains) {
					LogFile("none",studentID,date,arguments,"swap","not enrolled in old course","unsuccessful");
					return "notenroll";
				}
				if(contains) {
					if(newCourseID.substring(0, 4).equals("SOEN")) {
						s1 = new Send(3966,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								System.out.println("1st no");
								return "no";
							}
						}
						catch(Exception e) {}
						s1 = new Send(3966,semester,newCourseID,studentID,"seats");
						s2 = new Send(3967,semester,newCourseID,studentID,"seats");
						
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
									s1 = new Send(3966,semester,newCourseID,studentID,"enroll");
									s1.t.join();
									msg = s1.message;
									if(s1.message.equals("enroll")) {
										LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
										return "enroll";
									}
								}
								enroll_comp(studentID, oldCourseID, semester);
								return msg;
							} catch (Exception e) {	}
							
						}
					}
					
					if(newCourseID.substring(0, 4).equals("INSE")) {
						s1 = new Send(3967,"sem",newCourseID,studentID,"checkcourse");
						try {
							s1.t.join();
							if(s1.message.equals("no")) {
								LogFile("none",studentID,date,arguments,"swap","not found","unsuccessful");
								return "no";
							}
						}
						catch(Exception e) {}
						s1 = new Send(3966,semester,newCourseID,studentID,"seats");
						s2 = new Send(3967,semester,newCourseID,studentID,"seats");
						
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
									s1 = new Send(3967,semester,newCourseID,studentID,"enroll");
									s1.t.join();
									msg = s1.message;
									if(s1.message.equals("enroll")) {
										LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
										return "enroll";
									}
								}
								enroll_comp(studentID, oldCourseID, semester);
								return msg;
							} catch (Exception e) {	}
						}
					}
					if(newCourseID.substring(0, 4).equals("COMP")) {
						if(student_comp.student_comp_fall.containsKey(newCourseID))
							contains = true;
						if(student_comp.student_comp_winter.containsKey(newCourseID))
							contains = true;
						if(student_comp.student_comp_summer.containsKey(newCourseID))
							contains = true;
						
						if(!contains) {
							LogFile("none",studentID,date,arguments,"swap","Course Not Found","unsuccessful");
							return "notfound";
						}
						int i = 0;
						if(semester.equals("Fall")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_fall.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(semester.equals("Winter")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_winter.entrySet()){
								List<String> l = map1.getValue();
								if(l.contains(studentID))
									i++;
							}							
						}
						if(semester.equals("Summer")) {
							for(Map.Entry<String, List<String>> map1:student_comp.student_comp_summer.entrySet()){
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
							String res = drop(studentID, oldCourseID); 
							ans = res;
							if(res.equals("dropped")) {
								String s = enroll_comp(studentID, newCourseID, semester);
								if(s.equals("enroll")) {
									LogFile("none",studentID,date,arguments,"swap","enrolled","successful");
									return "enroll";
								}
								msg = s;
							}
							enroll_comp(studentID, oldCourseID, semester);
							return msg;
						}
					}
				}
			}
		return ans;
	}
	
	
	public synchronized String addCourse_comp(String courseID, String sem,int capacity){
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));
		String ans = "invalid";
		String status = "unsuccessful";
		System.out.println(CourseDetail_comp.hash_comp);
		if(CourseDetail_comp.hash_comp.containsKey(sem)){
			if(sem.equals("Fall")){
				if(!CourseDetail_comp.comp_fall.containsKey(courseID)){
					CourseDetail_comp.set(capacity);
					CourseDetail_comp.comp_fall.put(courseID, capacity);
					CourseDetail_comp.hash_comp.put(sem,CourseDetail_comp.comp_fall);
					ans = "done";
					System.out.println(ans);
				}
				else{
					System.out.println("course already exists!");
					ans = "exists";
				}
			}
			if(sem.equals("Winter")){
				if(!CourseDetail_comp.comp_winter.containsKey(courseID)){
					CourseDetail_comp.set(capacity);
					CourseDetail_comp.comp_winter.put(courseID, capacity);
					CourseDetail_comp.hash_comp.put(sem,CourseDetail_comp.comp_winter);
					ans = "done";
				}
				else{
					ans = "exists";
				}
			}
			if(sem.equals("Summer")){
				if(!CourseDetail_comp.comp_summer.containsKey(courseID)){
					CourseDetail_comp.set(capacity);
					CourseDetail_comp.comp_summer.put(courseID, capacity);
					CourseDetail_comp.hash_comp.put(sem,CourseDetail_comp.comp_summer);
					ans = "done";
				}
				else{
					ans = "exists";
				}
			}
		}
		return ans;
	}
	public synchronized String enroll_comp(String studentID, String courseID, String sem){
		String ans = "invalid";
		String status = "unsuccessful";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		List<String> ls = new ArrayList<>();
		Send s1,s2;
		int a_total=0,b_total=0,a = 0,b = 0;
		int i = 0; 
		int	j = 1000;
		if(courseID.substring(0, 4).equals("COMP")){
			HashMap<String, Integer> hm = CourseDetail_comp.hash_comp.get(sem);
			if(!hm.containsKey(courseID)){
				ans = "notexists";				
			}
			j = hm.get(courseID);
			if(j == 0){
				ans = "full";
			}
		}
		
		for(Map.Entry<String, HashMap<String, List<String>>> map:student_comp.std_comp.entrySet()){
			HashMap<String, List<String>> hash = map.getValue();
			for(Map.Entry<String, List<String>> map1:hash.entrySet()){
				List<String> l = map1.getValue();
				if(l.contains(studentID)){
					if(map1.getKey().equals(courseID))
						return "taken";
				}
			}
		}
		for(Map.Entry<String, HashMap<String,List<String>>> map: student_comp.std_comp.entrySet()){
			HashMap<String, List<String>> hash = map.getValue();
			for(Map.Entry<String, List<String>> map1:hash.entrySet()){
				List<String> l = map1.getValue();
				if(l.contains(studentID))
					i++;
			}
		}
		System.out.println(student_comp.std_comp);
		System.out.println(i);
		if(i==3 ){
			ans = "enrolled";
			return ans;
		}
		else {
			System.out.println("inside condition1");
			System.out.println(!ans.equals("enrolled"));
			if(i<3 && j != 0 && !ans.equals("enrolled")){
				s1 = new Send(3966,sem,courseID,studentID,"seats");
				s2 = new Send(3967,sem,courseID,studentID,"seats");
				
				try{
					
					s1.t.join();
					s2.t.join();
				}
				catch(InterruptedException e){}
				String[] a1 = s1.message.split(",");
				String[] a2 = s2.message.split(",");
				System.out.println("message from SOEN:"+s1.message);
				System.out.println("message from INSE:"+s2.message);
				a_total = Integer.parseInt(a1[0]);
				a = Integer.parseInt(a1[1]);
				b = Integer.parseInt(a2[1]);
				b_total = Integer.parseInt(a2[0]);
				if(courseID.substring(0, 4).equals("SOEN") && a_total+b_total >= 2){
					try {
						
						fw = new FileWriter("./CompServer/COMPServer.txt",true);
						bw = new BufferedWriter(fw);
						bw.write(date);
						bw.write("enroll ");
						bw.write("("+courseID+", "+sem+")");
						if(a_total >=2)
							bw.write("Response from Server2:can't register");
						if(b_total >= 2)
							bw.write("Response from Server3:can't register");
						bw.write("Response from Server2 and Server3:can't register");
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
						s1 = new Send(3966,sem,courseID,studentID,"enroll");
						try {
							s1.t.join();				
							fw = new FileWriter("COMPServer.txt",true);
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
				if(courseID.substring(0, 4).equals("INSE") && a_total+b_total >= 2){
					System.out.println("can not take more courses of other sem");
					try {
						
						fw = new FileWriter("./CompServer/COMPServer.txt",true);
						bw = new BufferedWriter(fw);
						bw.write(date);
						bw.write("enroll ");
						bw.write("("+courseID+", "+sem+")");	
						if(a_total >=2)
							bw.write("Response from Server2:can't register");
						if(b_total >= 2)
							bw.write("Response from Server3:can't register");
						bw.write("Response from Server2 and Server3:can't register");
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
					if(courseID.substring(0, 4).equals("INSE") && a_total+b_total < 2 && a+b < 3){
						s1 = new Send(3967,sem,courseID,studentID,"enroll");
						try {
							s1.t.join();				
							fw = new FileWriter("./CompServer/COMPServer.txt",true);
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
				System.out.println("a+b"+(a+b));
				if(a+b+i<3){
					System.out.println("in comp registration");
					if(courseID.substring(0, 4).equals("COMP")){
					
					if(sem.equals("Fall")){
						if(student_comp.student_comp_fall.containsValue(studentID)){
							ans = "taken";
						}
						else{
							if(CourseDetail_comp.comp_fall.containsKey(courseID) && !student_comp.student_comp_fall.containsKey(courseID)){
								ls.add(studentID);
								student_comp.student_comp_fall.put(courseID, ls);
							}
							else{
								ls = student_comp.student_comp_fall.get(courseID);
								ls.add(studentID);
								student_comp.student_comp_fall.replace(courseID, student_comp.list11, ls);
							}
							CourseDetail_comp.comp_fall.replace(courseID, j, j-1);
							ans = "enroll";
						}
					}
					if(sem.equals("Winter")){
						if(student_comp.student_comp_winter.containsValue(studentID)){
							ans = "taken";
						}
						else{
							if(CourseDetail_comp.comp_winter.containsKey(courseID) && !student_comp.student_comp_winter.containsKey(courseID)){
								ls.add(studentID);
								student_comp.student_comp_winter.put(courseID, ls);
							}
							else{
								ls = student_comp.student_comp_winter.get(courseID);
								ls.add(studentID);
								student_comp.student_comp_winter.replace(courseID, student_comp.list12, ls);
							}
							CourseDetail_comp.comp_winter.replace(courseID, j, j-1);
							ans = "enroll";				
						}
					}
					if(sem.equals("Summer")){
						if(student_comp.student_comp_summer.containsValue(studentID)){
							ans = "taken";
						}
						else{
							if(CourseDetail_comp.comp_summer.containsKey(courseID) && !student_comp.student_comp_summer.containsKey(courseID)){
								ls.add(studentID);
								student_comp.student_comp_summer.put(courseID, ls);
							}
							else{
								ls = student_comp.student_comp_summer.get(courseID);
								ls.add(studentID);
								student_comp.student_comp_summer.replace(courseID, student_comp.list13, ls);
							}
							CourseDetail_comp.comp_summer.replace(courseID, j, j-1);
							ans = "enroll";				
						}
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
			fw = new FileWriter("./CompServer/COMPServer.txt",true);
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
		String status = "";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		if(courseID.substring(0, 4).equals("COMP")){
			if(CourseDetail_comp.hash_comp.containsKey(sem)){			
				if(sem.equals("Fall")){
					if(CourseDetail_comp.comp_fall.containsKey(courseID)){
						CourseDetail_comp.comp_fall.remove(courseID);
						student_comp.student_comp_fall.remove(courseID);
						ans = "done";
					}
					else{
						ans = "cant";
					}
				}
				if(sem.equals("Winter")){
					if(CourseDetail_comp.comp_winter.containsKey(courseID)){
						CourseDetail_comp.comp_winter.remove(courseID);
						student_comp.student_comp_winter.remove(courseID);
						ans = "done";
					}
					else{
						ans = "cant";
					}
				}
				if(sem.equals("Summer")){
					if(CourseDetail_comp.comp_summer.containsKey(courseID)){
						CourseDetail_comp.comp_summer.remove(courseID);
						student_comp.student_comp_summer.remove(courseID);
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
			fw = new FileWriter("./CompServer/COMPServer.txt",true);
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
		String str = "";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		Send s1 = new Send(3966,sem,"course","sid","list");
		Send s2 = new Send(3967,sem,"course","sid","list");
		try{
			s1.t.join();
			s2.t.join();
		}
		catch(InterruptedException e){}
		if(sem.equals("Fall")){
			for(Map.Entry<String, Integer> map : CourseDetail_comp.comp_fall.entrySet()){
				str += map.getKey() + " " + map.getValue()+ ",";
			}
		}
		if(sem.equals("Winter")){
			for(Map.Entry<String, Integer> map : CourseDetail_comp.comp_winter.entrySet()){
				str += map.getKey() + " " + map.getValue()+ ",";
			}
		}
		if(sem.equals("Summer")){
			for(Map.Entry<String, Integer> map : CourseDetail_comp.comp_summer.entrySet()){
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
			
			fw = new FileWriter("./CompServer/COMPServer.txt",true);
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
		Send s1;
		Send s2;
		String status = "";
		String ans = "invalid";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));

		if(courseID.substring(0, 4).equals("SOEN")){
			s1 = new Send(3966, "sem", courseID, studebtID, "drop");
			try {
				s1.t.join();
				ans = s1.message;
			} 
			catch (InterruptedException e) {e.printStackTrace();}	
			
		}
		else if(courseID.substring(0, 4).equals("INSE")){
			s2 = new Send(3967, "sem", courseID, studebtID, "drop");
			try {
				s2.t.join();
			} 
			catch (InterruptedException e) {}
			ans = s2.message;
		}
		else{
			if(student_comp.student_comp_fall.containsKey(courseID)){
				List<String> lst = student_comp.student_comp_fall.get(courseID);
				lst.remove(studebtID);
				student_comp.student_comp_fall.replace(courseID, student_comp.list11, lst);
				Integer i = CourseDetail_comp.comp_fall.get(courseID);
				CourseDetail_comp.comp_fall.replace(courseID, i, (i+1));
				ans = "dropped";
			}
			if(student_comp.student_comp_winter.containsKey(courseID)){
				List<String> lst = student_comp.student_comp_winter.get(courseID);
				lst.remove(studebtID);
				student_comp.student_comp_winter.replace(courseID, student_comp.list12, lst);
				Integer i = CourseDetail_comp.comp_winter.get(courseID);
				CourseDetail_comp.comp_winter.replace(courseID, i, (i+1));
				ans = "dropped";
			}
			if(student_comp.student_comp_summer.containsKey(courseID)){
				List<String> lst = student_comp.student_comp_summer.get(courseID);
				lst.remove(studebtID);
				student_comp.student_comp_summer.replace(courseID, student_comp.list13, lst);
				Integer i = CourseDetail_comp.comp_summer.get(courseID);
				CourseDetail_comp.comp_summer.replace(courseID, i, (i+1));
				ans = "dropped";
			}
		}
		try {
			if(ans.equals("dropped")){
				status = "success";
			}
			else
				status = "unsuccessful";
			fw = new FileWriter("./CompServer/COMPServer.txt",true);
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
		Send s1,s2;
		String value ="a";
		date = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("E dd/MM/yyyy HH:mm:ss"));
		String result = "empty";
		String key="",key1="",key2="";
		LinkedHashSet<String> list1 = new LinkedHashSet<>();
		LinkedHashSet<String> list2 = new LinkedHashSet<>();
		LinkedHashSet<String> list3 = new LinkedHashSet<>();
		HashMap<String, LinkedHashSet<String>> hm = new HashMap<>();
		String[] str_array1 = {"ASC","SAD","ASD"};
		for(Map.Entry<String, HashMap<String, List<String>>> entry : student_comp.std_comp.entrySet()){
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

		s1 = new Send(3966, "sem", "courseID", studentID, "schedule");
		s2 = new Send(3967, "sem", "courseID", studentID, "schedule");
		try {
			s1.t.join();
			s2.t.join();
		} catch (Exception e) {}
		System.out.println("message"+s1.message);
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
			result += "\nSummer:";
			while(itr.hasNext()){
				result += itr.next();
				result += ",";
			}
		}
		try {
			
			fw = new FileWriter("COMPServer.txt",true);
			bw = new BufferedWriter(fw);
			bw.write(date);
			bw.write("GetSchedule ");
			bw.write("("+studentID+") ");
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
			
//			task = new FrontEnd();
//			new student_comp();
//			cdc = new CourseDetail_comp();
//			
//			System.out.println("COMP server is Ready & waiting for client...");
//
//			new Receive();
			
		} 
		catch (Exception e) {}
	}
}
