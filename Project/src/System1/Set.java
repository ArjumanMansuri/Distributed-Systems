//package DCRS;
package System1;
import java.io.Serializable;

class Set implements Serializable{
/**
 * 
 */
	private static final long serialVersionUID = 1L;
	String courseID;
	String sem;
	String sid;
	String message = "none";
	public Set() {
		// TODO Auto-generated constructor stub
	}
	public Set(String sem,String message){
		this.sem = sem;
		this.message = message;
	}
	public Set(String sem,String courseID,String sid,String message){
		this.sem = sem;
		this.courseID = courseID;
		this.sid = sid;
		this.message = message;
	}
	//not needed

	public void setMessage(String msg){
		this.message = msg;
	}

	public String getMessage(){
		return message;
	}
	public void setCourseID(String courseID){
		this.courseID = courseID;
	}
	
	public void setId(String id){
		this.sid = id;
	}
	public String getCourseID(){
		return courseID;
	}
	
	public String getId(){
		return sid;
	}
	public String getSem(){
		return sem;
	}
}
