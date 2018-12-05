package server;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Test {
	public static void main(String[] args) {
		// *** add course
		// *** remove course
		// *** list course availability
		
		// *** enroll course
		// drop course
		// *** get class schedule
		// *** swap course 
		try {
			URL compUrl = new URL("http://localhost:8080/comp?wsdl");
			URL soenUrl = new URL("http://localhost:8082/soen?wsdl");
			URL inseUrl = new URL("http://localhost:8081/inse?wsdl");
			
			QName qName = new QName("http://server/", "DcrsWebServiceImplService");
			
			Service compService = Service.create(compUrl, qName);
			Service soenService = Service.create(soenUrl, qName);
			Service inseService = Service.create(inseUrl, qName);
			
			DcrsWebService compInterface = compService.getPort(DcrsWebService.class);
			DcrsWebService soenInterface = soenService.getPort(DcrsWebService.class);
			DcrsWebService inseInterface = inseService.getPort(DcrsWebService.class);
		
			// adding INSE6230 course
			Runnable task0 = () -> {
				inseInterface.setUser("INSEA1000");
				inseInterface.setCapacity(1);
				System.out.println("Adding INSE6230 course: "+inseInterface.addCourse("INSE6230", "WINTER"));
				System.out.println();
			};
			Thread thread0 = new Thread(task0);
			
			// check course availability in winter
			Runnable task6 = () -> {
				inseInterface.setUser("INSEA1000");
				System.out.println("Checking course availability in winter: "+inseInterface.listCourseAvailability("WINTER"));
				System.out.println();
			};
			Thread thread6 = new Thread(task6);
			
			// Enrolling COMPS1000 in INSE6230 and then listing class schedule
			Runnable task1 = () -> {
				compInterface.setUser("COMPS1000");
				System.out.println("Enrolling COMPS1000 in INSE6230: "+compInterface.enrolCourse("COMPS1000", "INSE6230", "WINTER"));
				System.out.println("Listing class schedule for COMPS1000: "+compInterface.getClassSchedule("COMPS1000"));
				System.out.println();
			};
			Thread thread1 = new Thread(task1);
			
			// Enrolling SOENS1000 in INSE6230 and then listing class schedule
			Runnable task2 = () -> {
				soenInterface.setUser("SOENS1000");
				System.out.println("Enrolling SOENS1000 in INSE6230: "+soenInterface.enrolCourse("SOENS1000", "INSE6230", "WINTER"));
				System.out.println("Listing class schedule for SOENS1000: "+soenInterface.getClassSchedule("SOENS1000"));
				System.out.println();
			};
			Thread thread2 = new Thread(task2);
			
			// Swapping for COMPS1000 (INSE6230 to COMP6441)
			Runnable task3 = () -> {
				compInterface.setUser("COMPS1000");
				System.out.println("Swapping for COMPS1000 (INSE6230 to COMP6441): "+compInterface.swapCourse("COMPS1000", "COMP6441", "INSE6230"));
				System.out.println();
			};
			Thread thread3 = new Thread(task3);
			
			// Swapping for SOENS1000 (INSE6230 to COMP6441)
			Runnable task4 = () -> {
				soenInterface.setUser("SOENS1000");
				System.out.println("Swapping for SOENS1000 (INSE6230 to COMP6441): "+soenInterface.swapCourse("SOENS1000", "COMP6441", "INSE6230"));
				System.out.println();
			};
			Thread thread4 = new Thread(task4);
			
			// removing INSE6230 course
			Runnable task5 = () -> {
				inseInterface.setUser("INSEA1000");
				System.out.println("Removing INSE6230 course: "+inseInterface.removeCourse("INSE6230", "WINTER"));
				System.out.println();
			};
			Thread thread5 = new Thread(task5);
			
			// checking class schedule of SOENS1000
			Runnable task7 = () -> {
				soenInterface.setUser("SOENS1000");
				System.out.println("Listing class schedule for SOENS1000: "+soenInterface.getClassSchedule("SOENS1000"));
				System.out.println();
			};
			Thread thread7 = new Thread(task7);
			
			// checking class schedule of COMPS1000
			Runnable task8 = () -> {
				compInterface.setUser("COMPS1000");
				System.out.println("Listing class schedule for COMPS1000: "+compInterface.getClassSchedule("COMPS1000"));
				System.out.println();
			};
			Thread thread8 = new Thread(task8);
			
			// drop course COMP6441 for SOENS1000
			Runnable task9 = () -> {
				soenInterface.setUser("SOENS1000");
				System.out.println("Dropping course COMP6441 for SOENS1000: "+soenInterface.dropCourse("SOENS1000","COMP6441"));
				System.out.println();
			};
			Thread thread9 = new Thread(task9);
			
			// drop course COMP6441 for COMPS1000
			Runnable task10 = () -> {
				compInterface.setUser("COMPS1000");
				System.out.println("Dropping course COMP6441 for COMPS1000: "+compInterface.dropCourse("COMPS1000","COMP6441"));
				System.out.println();
			};
			Thread thread10 = new Thread(task10);
			
			System.out.println("Thread 0 started");
			thread0.start();
			thread0.join();
			
			System.out.println("Thread 6 started");
			thread6.start();
			thread6.join();
			
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
			thread5.join();
			
			System.out.println("Thread 7 started");
			thread7.start();
			System.out.println("Thread 8 started");
			thread8.start();
			
			thread7.join();
			thread8.join();
			
			System.out.println("Thread 9 started");
			thread9.start();
			System.out.println("Thread 10 started");
			thread10.start();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
