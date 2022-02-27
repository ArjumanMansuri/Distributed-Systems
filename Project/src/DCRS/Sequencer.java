package DCRS;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class Sequencer {
	static String data;
	@SuppressWarnings("deprecation")
	public static void sendMessage(String data) throws IOException {
		// Import some needed classes
		// Which port should we send to
		int port = 5000;
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
	public static void receive() {
		DatagramSocket aSocket = null;
		long msg_num = 0;
		
		try {
			aSocket = new DatagramSocket(1010);
			System.out.println("Server 1111 Started............");
			while (true) {
			
			byte[] buffer = new byte[1000];
			DatagramPacket reply=null;
			
			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				aSocket.receive(request);
				msg_num++;
				data=(new String(request.getData())).trim();
				
				data+=","+msg_num;			// appending message number
				Runnable task1 = () -> {
					try {
						sendMessage(data);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
				Thread thread = new Thread(task1);
				thread.start();
//				try {
//					thread.join();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
				
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
	public static void main(String[] args) {
		System.out.println("Seq is Ready & waiting for client...");
		Runnable task1 = () -> {
			receive();
		};
		Thread thread = new Thread(task1);
		thread.start();
//		try {
//			thread.join();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
	}

}
