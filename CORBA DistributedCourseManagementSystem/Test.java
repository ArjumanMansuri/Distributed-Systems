import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DcrsApp.Dcrs;
import DcrsApp.DcrsHelper;

public class Test {
	public static void main(String[] args) {
		ORB orb = ORB.init(args, null);
		//-ORBInitialPort 1050 -ORBInitialHost localhost
		org.omg.CORBA.Object objRef;
		
		try {
			objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			final Dcrs dcrsInterfaceComp  = (Dcrs) DcrsHelper.narrow(ncRef.resolve_str("CompServer"));
			final Dcrs dcrsInterfaceSoen = (Dcrs) DcrsHelper.narrow(ncRef.resolve_str("SoenServer"));
			final Dcrs dcrsInterfaceInse = (Dcrs) DcrsHelper.narrow(ncRef.resolve_str("InseServer"));
		
			// adding INSE6230 course
			Runnable task0 = () -> {
				dcrsInterfaceInse.setUser("INSEA1000");
				dcrsInterfaceInse.setCapacity(1);
				System.out.println("Adding INSE6230 course: "+dcrsInterfaceInse.addCourse("INSE6230", "WINTER"));
				System.out.println();
			};
			Thread thread0 = new Thread(task0);
			
			// Enrolling COMPS1000 in INSE6230
			Runnable task1 = () -> {
				dcrsInterfaceComp.setUser("COMPS1000");
				System.out.println("Enrolling COMPS1000 in INSE6230: "+dcrsInterfaceComp.enrolCourse("COMPS1000", "INSE6230", "WINTER"));
				System.out.println();
			};
			Thread thread1 = new Thread(task1);
			
			// Enrolling SOENS1000 in INSE6230
			Runnable task2 = () -> {
				dcrsInterfaceSoen.setUser("SOENS1000");
				System.out.println("Enrolling SOENS1000 in INSE6230: "+dcrsInterfaceSoen.enrolCourse("SOENS1000", "INSE6230", "WINTER"));
				System.out.println();
			};
			Thread thread2 = new Thread(task2);
			
			// Swapping for COMPS1000 (INSE6230 to COMP6441)
			Runnable task3 = () -> {
				dcrsInterfaceComp.setUser("COMPS1000");
				System.out.println("Swapping for COMPS1000 (INSE6230 to COMP6441): "+dcrsInterfaceComp.swapCourse("COMPS1000", "COMP6441", "INSE6230"));
				System.out.println();
			};
			Thread thread3 = new Thread(task3);
			
			// Swapping for SOENS1000 (INSE6230 to COMP6441)
			Runnable task4 = () -> {
				dcrsInterfaceSoen.setUser("SOENS1000");
				System.out.println("Swapping for SOENS1000 (INSE6230 to COMP6441): "+dcrsInterfaceSoen.swapCourse("SOENS1000", "COMP6441", "INSE6230"));
				System.out.println();
			};
			Thread thread4 = new Thread(task4);
			
			// removing INSE6230 course
			Runnable task5 = () -> {
				dcrsInterfaceInse.setUser("INSEA1000");
				System.out.println("Removing INSE6230 course: "+dcrsInterfaceInse.removeCourse("INSE6230", "WINTER"));
				System.out.println();
			};
			Thread thread5 = new Thread(task5);
			
			System.out.println("Thread 0 started");
			thread0.start();
			thread0.join();
			
			System.out.println("Thread 1 started");
			thread1.start();
			System.out.println("Thread 2 started");
			thread2.start();
			
			thread1.join();
			thread2.join();
			
			System.out.println("Thread 3 started");
			thread3.start();
			System.out.println("Thread 4 started");
			thread4.start();
			
			thread3.join();
			thread4.join();
			
			System.out.println("Thread 5 started");
			thread5.start();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
