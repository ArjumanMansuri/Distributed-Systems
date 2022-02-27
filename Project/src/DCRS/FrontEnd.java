package DCRS;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


public class FrontEnd extends computeTaskInterfacePOA{
	static String finalResult="3";
	static boolean resultReady=false;
	Thread registrationThread;
	static int failureType;
	
	public static void main(String[] args) {
		ORB orb = ORB.init(args, null);
		try {
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			
			FrontEnd task = new FrontEnd();
			
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(task);
			computeTaskInterface href = computeTaskInterfaceHelper.narrow(ref);
			
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			String name = "FE";
			
			NameComponent[] path = ncRef.to_name(name);
			ncRef.rebind(path, href);
			
			System.out.println("FE is Ready & waiting for client...");
			
			
			orb.run();
		} 
		catch (Exception e) {}
	}
	public synchronized int checkRegistration(String msg) {
		System.out.println("control in check method");
		sendMessage(msg);
		
		FrontEnd ct = new FrontEnd();
		Runnable task1 = () -> {
			ct.receive();
		};
		Thread thread = new Thread(task1);
		thread.start();
		try {
			thread.join();
		} catch (Exception e) {}
		System.out.println("finalResult="+finalResult);
		return Integer.parseInt(finalResult);
	}
	public static void sendMessage(String data) {
		
		failureType = Integer.parseInt(data.split(",")[0]);
		
		System.out.println("in send message");
		DatagramSocket aSocket = null;
		int serverPort = 1010;
		try {
			aSocket = new DatagramSocket();
			byte[] message = data.toString().getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(message, message.length, aHost, serverPort);
			aSocket.send(request);
			System.out.println("Request message sent from the client to sequencer with port number " + serverPort + " is: "
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
	}
	public synchronized void receive() {
		DatagramSocket aSocket = null;
		int counter = 0;
		String[] response = new String[3];
		try {
			aSocket = new DatagramSocket(2222);
			System.out.println("FE Started............");
			while (true) {
			//resultReady=false;
			byte[] buffer = new byte[1000];
			int msgNum = 0;
			
			if(counter == 3) {
				
				counter = 0;
			
				resultReady = true;
				if(failureType==1) {
				if(response[0].equals(response[1]) && response[0].equals(response[2]) )
					finalResult = response[0];
				else if(response[0].equals(response[1])) {
					finalResult = response[0];
					sendMulticastMessageToRMs(String.valueOf(msgNum)+",RM3");
				}
				else if(response[1].equals(response[2])) {
					finalResult = response[1];
					sendMulticastMessageToRMs(String.valueOf(msgNum)+",RM1");
				}
				else if(response[0].equals(response[2])) {
					finalResult = response[0];
					sendMulticastMessageToRMs(String.valueOf(msgNum)+",RM2");
				}
				}
				break;
			}
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				aSocket.receive(request);
				
				String data=(new String(request.getData())).trim();	
				System.out.println(data);
				msgNum = Integer.parseInt(data.split(",")[1]);
				if(!data.isEmpty()) {
					//result += Integer.parseInt(data);
					response[counter%3] = data.split(",")[0];
					counter++;
				}
				
				
							
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	//	System.out.println(result);
	}
	
	@SuppressWarnings("deprecation")
	public static void sendMulticastMessageToRMs(String data) throws IOException {
		// Import some needed classes
		// Which port should we send to
		int port = 5001;
		// Which address
		String group = "225.4.5.6";
		// Which ttl
		int ttl = 1;
		// Create the socket but we don't bind it as we are only going to send data
		MulticastSocket s = new MulticastSocket();
		// Note that we don't have to join the multicast group if we are only
		// sending data and not receiving
		// Fill the buffer with some data
		byte[] buf = data.toString().getBytes();
		// Create a DatagramPacket 
		DatagramPacket pack = new DatagramPacket(buf, buf.length,
							 InetAddress.getByName(group), port);
		// Do a send. Note that send takes a byte for the ttl and not an int.
		s.send(pack,(byte)ttl);
		// And when we have finished sending data close the socket
		s.close();		
	}
	
	@Override
	public String enrollCourse(String msg) {
		return null;
	}
	@Override
	public String getClassSchedule(String msg) {
		System.out.println("control in getClassSchedule method");
		sendMessage(msg);
		
		FrontEnd ct = new FrontEnd();
		Runnable task1 = () -> {
			ct.receive();
		};
		Thread thread = new Thread(task1);
		thread.start();
		try {
			thread.join();
		} catch (Exception e) {}
		System.out.println("finalResult="+finalResult);
		return finalResult;
	}
	@Override
	public String dropCourse(String msg) {
		return null;
	}
	@Override
	public String swapCourse(String msg) {
		return null;
	}
	@Override
	public String addCourse(String msg) {
		return null;
	}
	@Override
	public String removeCourse(String msg) {
		return null;
	}
	@Override
	public String listCourseAvailability(String msg) {
		return null;
	}
	
}

