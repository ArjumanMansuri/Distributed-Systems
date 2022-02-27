package DCRS;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
	import org.omg.CosNaming.NamingContextExtHelper;

public class Client1 {
	
//		private static final String FALL="FALL";
//		private static final String SUMMER="SUMMER";
//		private static final String WINTER="WINTER";
//		
//		private static final String COMP="COMP";
//		private static final String SOEN="SOEN";
//		private static final String INSE="INSE";
	
		private static Scanner sc;
		static int ftype = 0;

		public static void main(String[] args) throws Exception {
			ORB orb = ORB.init(args, null);
			//-ORBInitialPort 1050 -ORBInitialHost localhost
			org.omg.CORBA.Object objRef;
			do {
				String msg = "",msg1="";	
				System.out.println("Select type of failure:\n1.Software Failure\n2.Crash Failure\n");
				sc = new Scanner(System.in);
				ftype = sc.nextInt();
				msg+=ftype;
				objRef = orb.resolve_initial_references("NameService");
				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
				computeTaskInterface dcrsInterface = null;
				
				int user=0;
				System.out.println("*************************************************");
				System.out.println("Welcome to Distributed Course Registration System");
				System.out.println("*************************************************");
				do{
					System.out.println("Select an user:");
					System.out.println("1. Student");
					System.out.println("2. Advisor");
					System.out.println("******** If you are not registered ********");
					System.out.println("3. Student Registration");
					System.out.println("4. Advisor Registration");
					System.out.println("5. Exit");
					
				user = sc.nextInt();
					
				switch(user){
						case 1: System.out.println("Enter your student id");
						        String studentId=sc.next().trim().toUpperCase();
						      
						        dcrsInterface = (computeTaskInterface) computeTaskInterfaceHelper.narrow(ncRef.resolve_str("FE"));
						        
						        
				//		        dcrsInterface.setUser(studentId);
						       //user added
						       
						        if(!studentId.contains("COMPS") && !studentId.contains("SOENS") && !studentId.contains("INSES")){
					        		System.out.println("Invalid student id\n");
						        	break;
					        	}
						        
						        msg += ",checkRegistration";
						        msg+=","+studentId;
						        int result=dcrsInterface.checkRegistration(msg);
						        msg = "";
						        System.out.println(result);
						        
						        if(result==0){
						        	System.out.println("You are not registered.\n");
						        	user=4;
						        }  
						        if(user==1){
							        
						        	int option=0;							        
						        	do{
								       
						        		System.out.println("Following are the actions you can perform:");
								        System.out.println("1. Enrol Course");
										System.out.println("2. Get Class Schedule");
										System.out.println("3. Drop Course");
										System.out.println("4. Swap Course");
										System.out.println("5. Exit\n");
									
										option=sc.nextInt();
										switch(option){
											case 1:	System.out.println("Enter the course id you want to register for :");
//													String courseId=sc.next().trim().toUpperCase();
//								//different implementations
//													if(!courseId.contains(COMP) && !courseId.contains(SOEN) && !courseId.contains(INSE) ){
//														System.out.println("Invalid course id\n");
//														break;
//													}
//													System.out.println("Enter the semester you want to register for (e.g. Fall, Winter or Summer):");
//													String semester=sc.next().trim().toUpperCase();
//													if(!semester.equals("FALL") && !semester.equals("WINTER") && !semester.equals("SUMMER")){
//														System.out.println("Invalid semester\n");
//														break;
//													}
//													msg += ftype + ",enrollCourse" + ","+studentId + "," + courseId + "," + semester;
//													String message=dcrsInterface.enrollCourse(studentId, courseId, semester);
//													System.out.println(message+"\n");
													msg = "";
//											break;
											case 2:	msg += ftype + ",getClassSchedule" + ","+studentId;
													String classSchedule=dcrsInterface.getClassSchedule(msg);											
													System.out.println(classSchedule+"\n");
													msg = "";
													
											break;
										}							        
							        }while(option!=5);
						        }
						  }
						        
				}while(user!=5);
			
			
			}while(ftype  != 3);
		
		}

}
