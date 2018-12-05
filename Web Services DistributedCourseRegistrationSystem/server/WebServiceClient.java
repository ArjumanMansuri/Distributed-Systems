package server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class WebServiceClient {
	
	private static final String FALL="FALL";
	private static final String SUMMER="SUMMER";
	private static final String WINTER="WINTER";
	
	private static final String COMP="COMP";
	private static final String SOEN="SOEN";
	private static final String INSE="INSE";
	
	public static void main(String[] args) throws MalformedURLException {
		URL compUrl = new URL("http://localhost:8080/comp?wsdl");
		URL soenUrl = new URL("http://localhost:8082/soen?wsdl");
		URL inseUrl = new URL("http://localhost:8081/inse?wsdl");
		
		QName qName = new QName("http://server/", "DcrsWebServiceImplService");
		
		Service compService = Service.create(compUrl, qName);
		Service soenService = Service.create(soenUrl, qName);
		Service inseService = Service.create(inseUrl, qName);
		
		DcrsWebService dcrsInterface = null;
		
		try {
			
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
				
				Scanner sc=new Scanner(System.in);
				user = sc.nextInt();
				
				switch(user){
				case 1: System.out.println("Enter your student id");
				        String studentId=sc.next().trim().toUpperCase();
				      
				        if(studentId.contains(COMP)){
				        	dcrsInterface = compService.getPort(DcrsWebService.class);
				        }
				        else if(studentId.contains(SOEN)){
				        	dcrsInterface = soenService.getPort(DcrsWebService.class);
				        }
				        else if(studentId.contains(INSE)){
				        	dcrsInterface = inseService.getPort(DcrsWebService.class);
				        }
				        
				        dcrsInterface.setUser(studentId);
				        if(!studentId.contains("COMPS") && !studentId.contains("SOENS") && !studentId.contains("INSES")){
			        		System.out.println("Invalid student id\n");
				        	break;
			        	}
				        int result=dcrsInterface.checkRegistration(studentId);
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
								String courseId=sc.next().trim().toUpperCase();
								if(!courseId.contains(COMP) && !courseId.contains(SOEN) && !courseId.contains(INSE) ){
									System.out.println("Invalid course id\n");
									break;
								}
								System.out.println("Enter the semester you want to register for (e.g. Fall, Winter or Summer):");
								String semester=sc.next().trim().toUpperCase();
								if(!semester.equals(FALL) && !semester.equals(WINTER) && !semester.equals(SUMMER)){
									System.out.println("Invalid semester\n");
									break;
								}
								String message=dcrsInterface.enrolCourse(studentId, courseId, semester);
								System.out.println(message+"\n");
								break;
						case 2:	String classSchedule=dcrsInterface.getClassSchedule(studentId);
					
								System.out.println(classSchedule+"\n");
								break;
					
						case 3: System.out.println("Enter the course id of the course you want to drop :");
								String dropCourseId=sc.next().trim().toUpperCase();
								if(!dropCourseId.contains(COMP) && !dropCourseId.contains(SOEN) && !dropCourseId.contains(INSE) ){
									System.out.println("Invalid course id\n");
									break;
								}
								String dropMessage=dcrsInterface.dropCourse(studentId, dropCourseId);
								System.out.println(dropMessage+"\n");
								break;
						case 4: System.out.println("Enter the course id of the course you want to drop :");
								String oldCourseId=sc.next().trim().toUpperCase();
								if(!oldCourseId.contains(COMP) && !oldCourseId.contains(SOEN) && !oldCourseId.contains(INSE) ){
									System.out.println("Invalid course id\n");
									break;
								}
								System.out.println("Enter the course id of the course you want to register for :");
								String newCourseId=sc.next().trim().toUpperCase();
								if(!newCourseId.contains(COMP) && !newCourseId.contains(SOEN) && !newCourseId.contains(INSE) ){
									System.out.println("Invalid course id\n");
									break;
								}
								String swapMessage = dcrsInterface.swapCourse(studentId, newCourseId, oldCourseId);
								System.out.println(swapMessage);
								
						case 5: break;
						default : System.out.println("Invalid option\n");
						
						}
				       }while(option!=5);
				    }	
		        break;
				case 2: System.out.println("Enter your advisor id");
        		String advisorId=sc.next().trim().toUpperCase();
	      
        		if(advisorId.contains(COMP)){
		        	dcrsInterface = compService.getPort(DcrsWebService.class);
		        }
		        else if(advisorId.contains(SOEN)){
		        	dcrsInterface = soenService.getPort(DcrsWebService.class);
		        }
		        else if(advisorId.contains(INSE)){
		        	dcrsInterface = inseService.getPort(DcrsWebService.class);
		        }
        		
		        dcrsInterface.setUser(advisorId);
		        if(!advisorId.contains("COMPA") && !advisorId.contains("SOENA") && !advisorId.contains("INSEA")){
	        		System.out.println("Invalid advisor id\n");
		        	break;
	        	}
	        
		        int advisorResult=dcrsInterface.checkRegistration(advisorId);
		        if(advisorResult==0){
		        	System.out.println("You are not registered.\n");
		        	user=4;
		        }
		        if(user==2){
		        	int advisorOption;
		        	do{
					System.out.println("Following are the actions you can perform:");
					System.out.println("1. Enrol Course");
					System.out.println("2. Get Class Schedule");
					System.out.println("3. Drop Course");
					System.out.println("4. Swap Course");
					System.out.println("5. Add Course");
					System.out.println("6. Remove Course");
					System.out.println("7. List Course Availability");
					System.out.println("8. Exit");
					advisorOption=sc.nextInt();
					switch(advisorOption){
					case 1: System.out.println("Enter the student id you want to register for :");
							String advisorStudentId=sc.next().trim().toUpperCase();
							if(advisorId.contains(COMP) && (advisorStudentId.contains("SOENS") || advisorStudentId.contains("INSES"))){
				        		System.out.println("You cannot enrol course for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(SOEN) && (advisorStudentId.contains(COMP) || advisorStudentId.contains(INSE))){
				        		System.out.println("You cannot enrol course for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(INSE) && (advisorStudentId.contains(SOEN) || advisorStudentId.contains(COMP))){
				        		System.out.println("You cannot enrol course for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							if(!advisorStudentId.contains("COMPS") && !advisorStudentId.contains("SOENS") && !advisorStudentId.contains("INSES")){
				        		System.out.println("Invalid student id\n");
					        	break;
				        	}
							 result=dcrsInterface.checkRegistration(advisorStudentId);
						     if(result==0){
						        System.out.println("Student is not registered.\n");
						        break;
						    }
							System.out.println("Enter the course id of the course you want to register for :");
							String courseId=sc.next().trim().toUpperCase();
							if(!courseId.contains(COMP) && !courseId.contains(SOEN) && !courseId.contains(INSE) ){
								System.out.println("Invalid course id\n");
								break;
							}
							System.out.println("Enter the semester you want to register for (e.g. Fall, Winter or Summer):");
							String semester=sc.next().trim().toUpperCase();
							if(!semester.equalsIgnoreCase(FALL) && !semester.equalsIgnoreCase(WINTER) && !semester.equalsIgnoreCase(SUMMER)){
								System.out.println("Invalid semester\n");
								break;
							}
							String message=dcrsInterface.enrolCourse(advisorStudentId, courseId, semester);
							System.out.println(message+"\n");
							break;
					
					case 2: System.out.println("Enter the student id you want the class schedule for :");
							advisorStudentId=sc.next().trim().toUpperCase();
						
							if(advisorId.contains(COMP) && (advisorStudentId.contains(SOEN) || advisorStudentId.contains(INSE))){
				        		System.out.println("You cannot get schedule for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(SOEN) && (advisorStudentId.contains(COMP) || advisorStudentId.contains(INSE))){
				        		System.out.println("You cannot get schedule for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(INSE) && (advisorStudentId.contains(SOEN) || advisorStudentId.contains(COMP))){
				        		System.out.println("You cannot get schedule for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							if(!advisorStudentId.contains("COMPS") && !advisorStudentId.contains("SOENS") && !advisorStudentId.contains("INSES")){
				        		System.out.println("Invalid student id\n");
					        	break;
				        	}
							 result=dcrsInterface.checkRegistration(advisorStudentId);
						        if(result==0){
						        	System.out.println("Student is not registered.\n");
						        	break;
						        }
							String classSchedule=dcrsInterface.getClassSchedule(advisorStudentId);
				
							System.out.println(classSchedule+"\n");
							break;
					
					case 3: System.out.println("Enter the student id you want to drop course for :");
							advisorStudentId=sc.next().trim().toUpperCase();
						
							if(advisorId.contains(COMP) && (advisorStudentId.contains(SOEN) || advisorStudentId.contains(INSE))){
				        		System.out.println("You cannot drop course for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(SOEN) && (advisorStudentId.contains(COMP) || advisorStudentId.contains(INSE))){
				        		System.out.println("You cannot drop course for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(INSE) && (advisorStudentId.contains(SOEN) || advisorStudentId.contains(COMP))){
				        		System.out.println("You cannot drop course for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							if(!advisorStudentId.contains("COMPS") && !advisorStudentId.contains("SOENS") && !advisorStudentId.contains("INSES")){
				        		System.out.println("Invalid student id\n");
					        	break;
				        	}
							 result=dcrsInterface.checkRegistration(advisorStudentId);
						        if(result==0){
						        	System.out.println("Student is not registered.\n");
						        	break;
						        }
							System.out.println("Enter the course id of the course you want to drop :");
							String dropCourseId=sc.next().trim().toUpperCase();
							if(!dropCourseId.contains(COMP) && !dropCourseId.contains(SOEN) && !dropCourseId.contains(INSE) ){
								System.out.println("Invalid course id\n");
								break;
							}
							String dropMessage=dcrsInterface.dropCourse(advisorStudentId, dropCourseId);
							System.out.println(dropMessage+"\n");
							break;
					
					case 4: System.out.println("Enter the student id you want to swap courses for :");
							advisorStudentId=sc.next().trim().toUpperCase();
							if(advisorId.contains(COMP) && (advisorStudentId.contains(SOEN) || advisorStudentId.contains(INSE))){
				        		System.out.println("You cannot swap courses for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(SOEN) && (advisorStudentId.contains(COMP) || advisorStudentId.contains(INSE))){
				        		System.out.println("You cannot swap courses for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							else if(advisorId.contains(INSE) && (advisorStudentId.contains(SOEN) || advisorStudentId.contains(COMP))){
				        		System.out.println("You cannot swap courses for a "+advisorStudentId.subSequence(0, 4)+" student.\n");
					        	break;
				        	}
							
							System.out.println("Enter the course id of the course you want to drop :");
							String oldCourseId=sc.next().trim().toUpperCase();
							if(!oldCourseId.contains(COMP) && !oldCourseId.contains(SOEN) && !oldCourseId.contains(INSE) ){
								System.out.println("Invalid course id\n");
								break;
							}
							System.out.println("Enter the course id of the course you want to register for :");
							String newCourseId=sc.next().trim().toUpperCase();
							if(!newCourseId.contains(COMP) && !newCourseId.contains(SOEN) && !newCourseId.contains(INSE) ){
								System.out.println("Invalid course id\n");
								break;
							}
							String swapMessage = dcrsInterface.swapCourse(advisorStudentId, newCourseId, oldCourseId);
							System.out.println(swapMessage);
							
					case 5: System.out.println("Enter course id of the course you want to add :");
							String addCourseId=sc.next().toUpperCase();
							if(!addCourseId.contains(COMP) && !addCourseId.contains(SOEN) && !addCourseId.contains(INSE) ){
								System.out.println("Invalid course id\n");
								break;
							}
							if(!addCourseId.contains(advisorId.substring(0,4))){
								System.out.println("You cannot add "+addCourseId.substring(0, 4)+" course\n");
								break;
							}
							System.out.println("Enter semester you want add the course in (e.g. Fall, Winter or Summer):");
							String addCourseSem=sc.next().toUpperCase();
							if(!addCourseSem.equals(FALL) && !addCourseSem.equals(WINTER) && !addCourseSem.equals(SUMMER)){
								System.out.println("Invalid semester\n");
								break;
							}
							System.out.println("Enter course capacity :");
							dcrsInterface.setCapacity(sc.nextInt());
							String response=dcrsInterface.addCourse(addCourseId, addCourseSem);
							System.out.println(response+"\n");
							break;
							
					case 6: System.out.println("Enter course id of the course you want to remove :");
							dropCourseId=sc.next().toUpperCase();
							if(!dropCourseId.contains(advisorId.substring(0, 4))){
								System.out.println("You cannot remove "+dropCourseId.substring(0, 4)+" course\n");
								break;
							}
							System.out.println("Enter semester you want to remove the course from (e.g. Fall, Winter or Summer):");
							String dropCourseSem=sc.next().toUpperCase();
							if(!dropCourseSem.equals(FALL) && !dropCourseSem.equals(WINTER) && !dropCourseSem.equals(SUMMER)){
								System.out.println("Invalid semester\n");
								break;
							}
							response=dcrsInterface.removeCourse(dropCourseId, dropCourseSem);
							System.out.println(response);
							break;
							
					case 7: System.out.println("Enter the semester whose course avalilabilty you want to list (e.g. Fall, Winter or Summer)");
							semester=sc.next().toUpperCase();
							if(!semester.equalsIgnoreCase(FALL) && !semester.equalsIgnoreCase(WINTER) && !semester.equalsIgnoreCase(SUMMER)){
								System.out.println("Invalid semester\n");
								break;
							}
							String courses=dcrsInterface.listCourseAvailability(semester);
							System.out.println(courses);
					case 8 : break;
					default: System.out.println("Invalid Option\n");
					}
		          }while(advisorOption!=8);	
		        }
			
			break;
			
				case 3: System.out.println("Select your stream:");
				System.out.println("1.COMP 2.SOEN 3.INSE");
				int branch=sc.nextInt();
				String strBranch=null;
				if(branch==1) strBranch="COMPS";
				else if(branch==2) strBranch="SOENS";
				else if(branch==3) strBranch="INSES";
				
				if(strBranch.contains(COMP)){
		        	dcrsInterface = compService.getPort(DcrsWebService.class);
		        }
		        else if(strBranch.contains(SOEN)){
		        	dcrsInterface = soenService.getPort(DcrsWebService.class);
		        }
		        else if(strBranch.contains(INSE)){
		        	dcrsInterface = inseService.getPort(DcrsWebService.class);
		        }
				
		 		String newId=dcrsInterface.registerNewUser(strBranch);
				System.out.println("Student registered successfully with student id : "+newId+"\n");
				break;
				
		case 4: System.out.println("Select your stream:");
				System.out.println("1.COMP 2.SOEN 3.INSE");
				int advisorBranch=sc.nextInt();
				String strBranch1=null;
				if(advisorBranch==1) strBranch1="COMPA";
				else if(advisorBranch==2) strBranch1="SOENA";
				else if(advisorBranch==3) strBranch1="INSEA";
				
				if(strBranch1.contains(COMP)){
		        	dcrsInterface = compService.getPort(DcrsWebService.class);
		        }
		        else if(strBranch1.contains(SOEN)){
		        	dcrsInterface = soenService.getPort(DcrsWebService.class);
		        }
		        else if(strBranch1.contains(INSE)){
		        	dcrsInterface = inseService.getPort(DcrsWebService.class);
		        }
				
		 		String newAdvisorId=dcrsInterface.registerNewUser(strBranch1);
				System.out.println("Advisor registered successfully with advisor id : "+newAdvisorId+"\n");
				break;
		
		case 5: break;
		default: System.out.println("Invalid Option\n");
		}
		
		
	}while(user!=5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
