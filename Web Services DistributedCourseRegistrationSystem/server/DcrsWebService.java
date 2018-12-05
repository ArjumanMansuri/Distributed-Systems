package server;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface DcrsWebService {
	// Advisor Functions
	public String addCourse(String courseID,String semester) ;
	
	public String removeCourse(String courseID,String semester);
	
	public String listCourseAvailability(String semester);
	
	//Student Functions
	public String enrolCourse(String studentID,String courseID,String semester);
	
	public String getClassSchedule(String studentID);
	
	public String dropCourse(String studentID,String courseID);
	
	public String swapCourse(String studentId, String newCourseId, String oldCourseId);
	
	//helper methods
	public int checkRegistration(String studentId);
	
	public String registerNewUser(String studentId);
	
	public void setUser(String user);
	
	public String getUser();
	
	public void setCapacity(int capacity);
	
	public int getCapacity();
	
}
