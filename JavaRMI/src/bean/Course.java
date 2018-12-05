package bean;

import java.util.ArrayList;

public class Course {

	private String courseName;
	private int maxCapacity;
	private int vacantSeats;
	private ArrayList<String> registeredStudents;
	
	public ArrayList<String> getRegisteredStudents() {
		return registeredStudents;
	}

	public void setRegisteredStudents(ArrayList<String> registeredStudents) {
		this.registeredStudents = registeredStudents;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	

	public Course(String courseName, int maxCapacity, int vacantSeats) {
		super();
		this.courseName = courseName;
		this.maxCapacity = maxCapacity;
		this.vacantSeats = vacantSeats;
	}

	public int getVacantSeats() {
		return vacantSeats;
	}

	public void setVacantSeats(int vacantSeats) {
		this.vacantSeats = vacantSeats;
	}

	
	
}
