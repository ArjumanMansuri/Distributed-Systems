package System1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import System1.Server1;
import System1.Server2;
import System1.Server3;

public class RM1 {
	private static HashMap<Long,String> holdBackQueue = new HashMap<>();
	private static HashMap<Long,String> processingQueue = new HashMap<>();
	private static int responseInt = 0;
	private static String response = "";
	private static long expectedMsg = 1; 
	static File file;
	
	public static Server1 server1;
	public static Server2 server2;
	public static Server3 server3;
	
	public  static String sendMessage(String data) {
		DatagramSocket aSocket = null;
		
		try {
			aSocket = new DatagramSocket();
			byte[] message = data.toString().getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, message.length, aHost, 2222);
			aSocket.send(request);
			System.out.println("Request message sent from the client to server0 with port number  + 1012 +  is: "
					+ new String(request.getData()));
			
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
		return new String("12");
	}
	
	public  static void receive() throws IOException {
		
		// Which port should we listen to
		
				int port = 5000;
				// Which address
				String group = "225.4.5.6";
				// Create the socket and bind it to port 'port'.
				MulticastSocket s = new MulticastSocket(port);
				// join the multicast group
				s.joinGroup(InetAddress.getByName(group));
				// Now the socket is set up and we are ready to receive packets
				// Create a DatagramPacket and do a receive
				while(true) {
					byte buf[] = new byte[1024];
					DatagramPacket pack = new DatagramPacket(buf, buf.length);
					s.receive(pack);
					// Finally, let us do something useful with the data we just received,
					// like print it on stdout :-)
					System.out.println("Received data from: " + pack.getAddress().toString() +
							    ":" + pack.getPort() + " with length: " +
							    pack.getLength());
					System.out.write(pack.getData(),0,pack.getLength());
					
					String request = new String(pack.getData());
					String[] requestArray = request.split(",");
					
					long msgNum = Long.parseLong(requestArray[requestArray.length-1].trim());
						holdBackQueue.put(msgNum, request);
						
						FileWriter serverFile=new FileWriter("RM1Log.txt",true);
						BufferedWriter serverBufferedWriter=new BufferedWriter(serverFile);
						PrintWriter serverPw=new PrintWriter(serverBufferedWriter);
						serverPw.println(request);
						serverPw.close();
						
						if(msgNum==expectedMsg) {
							processingQueue.put(msgNum, holdBackQueue.get(msgNum));
							expectedMsg++;
						}
						else {
							System.out.println("Received duplicate message"+msgNum);
						}
						System.out.println(processingQueue);
						String response = processRequest(processingQueue.get(expectedMsg-1));
						Runnable task1 = () -> {
							sendMessage(response+","+processingQueue.get(expectedMsg-1));
						};
						Thread thread = new Thread(task1);
						thread.start();
					}
		
			
			
		}
		
	private static String processRequest(String request) {
		// TODO Auto-generated method stub
		
		String[] requestArray = request.split(",");
		System.out.println("length of array ="+requestArray.length);
		
		// for checkRegistration()
		if(requestArray[1].equals("checkRegistration")) {
			String id = requestArray[2].trim();
			String deptType=requestArray[2].trim().substring(0, 4);
			if(deptType.equals("COMP")){
				System.out.println("send to comp server");
				System.out.println("ID:"+id);
				responseInt=server1.checkRegistration(id);
			}
			else if(deptType.equals("SOEN")){
				responseInt=server2.checkRegistration(id);
			}
			else {
				responseInt=server3.checkRegistration(id);
			}
		
			response = Integer.toString(responseInt);
			System.out.println();
			
		}
		
		//getClassSchedule
		if(requestArray[1].equals("getClassSchedule")) {
			String id = requestArray[2].trim();
			String deptType=requestArray[2].trim().substring(0, 4);
			if(deptType.equals("COMP")){
				System.out.println("send to comp server");
				System.out.println("ID:"+id);
				response=server1.getClassSchedule(id);
			}
			else if(deptType.equals("SOEN")){
				response=server2.getClassSchedule(id);
			}
			else {
				response=server3.getClassSchedule(id);
			}
		}
		return response;
	}

	public static void main(String[] args) throws IOException {
		server1 = new Server1();
		server3 = new Server3();
			server2 = new Server2();
		
			file= new File("RM1Log.txt");
			file.createNewFile();
		
			
		System.out.println("RM1 is Ready & waiting for client...");
		Runnable task1 = () -> {
			try {
				receive();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread thread = new Thread(task1);
		thread.start();
	}
	
	public static void receiveFromFE() {
		DatagramSocket aSocket = null;
		ArrayList<Integer> errorReportRM1 = new ArrayList<>();
		ArrayList<Integer> errorReportRM2 = new ArrayList<>();
		ArrayList<Integer> errorReportRM3 = new ArrayList<>();
		
		try {
			int port = 5001;
			// Which address
			String group = "225.4.5.6";
			System.out.println("Server 5001 Started............");
			while (true) {
			
			byte[] buffer = new byte[1000];
			DatagramPacket reply=null;
			
			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				aSocket.receive(request);
				// for handling software failure starts
				String rm = request.getData().toString().split(",")[1].trim();
				int msgNum = Integer.parseInt(request.getData().toString().split(",")[0].trim());
				
				if(rm.equals("RM1"))
					errorReportRM1.add(msgNum);
				else if(rm.equals("RM2"))
					errorReportRM2.add(msgNum);
				else if(rm.equals("RM3"))
					errorReportRM3.add(msgNum);
				
			if(errorReportRM1.size()==3) {
					if(errorReportRM1.get(1)-errorReportRM1.get(0)==1 && errorReportRM1.get(2)-errorReportRM1.get(1)==1)
						System.out.println("System error occured in RM1.. needs to be replaced");
					else
						errorReportRM1.remove(0);
				}
				if(errorReportRM2.size()==3) {
					if(errorReportRM2.get(1)-errorReportRM2.get(0)==1 && errorReportRM2.get(2)-errorReportRM2.get(1)==1)
						System.out.println("System error occured in RM2.. needs to be replaced");
					else
						errorReportRM2.remove(0);
				}
				if(errorReportRM3.size()==3) {
					if(errorReportRM3.get(1)-errorReportRM3.get(0)==1 && errorReportRM3.get(2)-errorReportRM3.get(1)==1)
						System.out.println("System error occured in RM3.. needs to be replaced");
					else
						errorReportRM3.remove(0);
				}
			}
				// for handling software failure ends
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
