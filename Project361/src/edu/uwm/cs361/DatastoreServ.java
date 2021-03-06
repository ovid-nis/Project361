package edu.uwm.cs361;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Entity;

import javax.jdo.Query;

public class DatastoreServ {
	
	private DatastoreService ds = null;
	private String adminPassword = "admin";
	
	private PersistenceManager _pm = PMF.get().getPersistenceManager();
	
	/**
	 * 
	 * @return Returns admins password from datastore
	 */
	public String getAdminPassword() {
		return adminPassword;
	}
	
	/**
	 * 
	 * @param password New password to set for admin
	 */
	public void setAdminPassword(String password) {
		adminPassword = password;
	}
	
	/**
	 * Constructor for datastoreserv. No params, MUST call the setters
	 */
	public DatastoreServ() {
		ds = DatastoreServiceFactory.getDatastoreService();
	}
	
	/**
	 * 
	 * @return internal datastore, if needed for queries
	 */
	public DatastoreService getDatastore(){
		return ds;
	}
	
	/**
	 * Creates a staff class within the datastore
	 * @param s New staff
	 */
	public void createStaff(Staff s){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(s);
		}finally{
			pm.close();
		}
	}
	
	/**
	 * Returns a list containing single course. 
	 * @param query Use this to find specific course. See jdo queries
	 * @return Course list. Use list.get(0) for the course being searched for
	 */
	public List<Course> getCourse(String query){
		
		Query q = _pm.newQuery(Course.class);
		
		if(query != null) {
			
			q.setFilter(query);
		}
		
		List<Course> courseList = (List<Course>)q.execute();
		if(courseList != null) Collections.sort(courseList);
		return courseList;
	}
	
	/**
	 * Returns a single course object, based on the course object pased in
	 * @param c
	 * @return
	 */
	public Course getCoursebyCourse(Course c){
		try{
			return getCourse("courseid == '" + c.getID() + "'").get(0);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Returns one element list with section
	 * @param query Ex ("courseid=='"+course.getID()+"'")
	 * @return Return list of section, list.get(0) is result of query
	 */
	@SuppressWarnings("unchecked")
	public List<Section> getSection(String query){
		
		Query q = _pm.newQuery(Section.class);
		
		if(query != null) {
			
			q.setFilter(query);
		}
		
		return (List<Section>) q.execute() ;
	}
	
	/**
	 * REturns a single Section based on a stirng containing course and section information
	 * @param section
	 * @return
	 */
	public Section getSectionByName(String section)
	{
		String secNum = section.substring(section.length() - 3);
		String courseNum = section.substring(0, 3);
		
		Query q = _pm.newQuery(Section.class);
		
		q.setFilter("section=='"+secNum+"'");
		List<Section> sectionList = (List<Section>)q.execute();
		
		for (Section sec : sectionList)
		{

			if (sec.getCourseid().contains(courseNum))
			{
				return sec;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a section object given that same seciton object. If similar enough will return.
	 * @param s
	 * @return
	 */
	public Section getSectionbySection(Section s){
		try{
			return getSection("courseid == '" + s.getCourseid() + "' && sectionid == '" + s.getSection() + "'").get(0);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Gets a list containing all the Course objects from datastore
	 * @return List of all courses
	 */
	@SuppressWarnings("unchecked")
	public List<Course> getAllCourses(){
		
		return getCourse(null) ;
	}
	
	/**
	 * Gets a list containing all the Staff objects from datastore
	 * @return List of all staff
	 */
	public ArrayList<Staff> getAllStaff(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Extent<Staff> e = pm.getExtent(Staff.class);
		ArrayList<Staff> ss = new ArrayList<Staff>();
		for(Staff s : e){
			ss.add(s);
		}
		return ss;
	}
	
	/**
	 * Adds the given course to datastore
	 * 
	 * @param ID Local use id number
	 * @param title Course title
	 * @param number Course number
	 */
	public void addCourse(String ID, String title, String number) {
		
		Course course = new Course();
		
		course.setID(ID);
		course.setTitle(title);
		course.setNumber(number);
		
		_pm.makePersistent(course);
	}
	
	/**
	 * Adds the given course object to the datastore
	 * @param course
	 */
	public void addCourse(Course course){
		_pm.makePersistent(course);
	}
	
	/**
	 * Takes a list of Course objects and adds each to the datastore
	 * @param courses
	 */
	public void addCourseAll(List<Course> courses){
		_pm.makePersistentAll(courses);
	}
	
	/**
	 * Takes a string containing 1 skill and returns a list of Staff objects containing all the TA staff who have that skill
	 * @param skill
	 * @return
	 */
	public ArrayList<Staff> staffBySkill(String skill){
		ArrayList<Staff> ss = getAllStaff();
		ArrayList<Staff> newlist = new ArrayList<Staff>();
		for(Staff s : ss){
			if(s.getSkills().contains(skill)){
				newlist.add(s);
			}
		}
		return newlist;
	}
	
	/**
	 * Adds the section to datastore
	 * 
	 * @param sectionid Locale only section id
	 * @param courseid Section's corresponding courses id
	 * @param units (credits)
	 * @param designation lab/lec/dis
	 * @param hours Meeting hours
	 * @param days Meeting days
	 * @param dates Meeting dates
	 * @param instructor Instructor
	 * @param room Meeting room
	 */
	public void addSection(String sectionid, String courseid, String units,
			String designation, String hours, String days,
			String dates, String secInstructor, String room) {
		String[] temp = designation.split(" ");
		
		Section section = new Section();
		section.setID(sectionid);
		section.setCourseid(courseid);
		section.setDates(dates);
		section.setDays(days);
		section.setHours(hours);
		section.setFallbackInstructor(secInstructor);
		section.setRoom(room);
		section.setType(temp.length == 2 ? temp[0] : "");
		section.setSection(temp.length == 2 ? temp[1] : "");
		section.setUnits(units);
		
		_pm.makePersistent(section);
	}
	
	/**
	 * Adds the given Section object to the datastore
	 * @param section
	 */
	public void addSection(Section section){
		_pm.makePersistent(section);
	}
	
	/**
	 * Takes a list of section objects and adds each one to the datastore
	 * @param sections
	 */
	public void addSectionAll(List<Section> sections){
		_pm.makePersistentAll(sections);
	}

	
	/**
	 * Deletes all courses form datastore
	 */
	public void deleteCourses() {
		
		List<Course> courses = getCourse(null);
		List<Section> sections = getSection(null);
		
		_pm.deletePersistentAll(courses);
		_pm.deletePersistentAll(sections);
	}
	
	/**
	 * Given a staff object, the corresponding Staff object is removed from the datastore
	 * @param stf
	 */
	public void deleteStaff(Staff stf){
		_pm.deletePersistent(stf);
	}
	
	/**
	 * Edit the given section in datastore
	 * 
	 * @param sectionid Section's id
	 * @param staff New instructor to teach section
	 */
//	public void editSection(String sectionid, String staff) {
//		
//		String[] tokens = sectionid.split(" ");
//		
//		List<Course> myCourses = getAllCourses();
//		for (Course course : myCourses) {
//			
//			if(course.getNumber().equals(tokens[0]))
//			{
//				List<Section> sections = course.getSections();
//				for (Section section : sections)
//				{
//					if (section.getSection().equals(tokens[2]))
//					{
//						section.setInstructor(staff);
//						section.edited = true;
//						_pm.makePersistent(section);
//					}
//				}
//				break;
//			}
//		}
//		_pm.close();		
//	}
	
	/**
	 * Takes a Section and a Staff object. Adds the staff to the section 'instructor' field.
	 * and adds the section to the list of the Staff's list of sections taught
	 * 
	 * @param sec
	 * @param stf
	 */
	public void editSectionsStaff(Section sec, Staff stf){
//		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			
			Section s = _pm.getObjectById(Section.class, sec.getKey());
			Staff sf = _pm.getObjectById(Staff.class, stf.getKey());
			
			if(s.getInstructor() != null){
				
				Staff oldsf = _pm.getObjectById(Staff.class, sec.getInstructor().getKey());
				oldsf.removeSectionTaught(s);
			}
			s.setInstructor(sf);
			sf.addSectionTaught(s);
			_pm.makePersistent(s);
			_pm.makePersistent(sf);
			
		}finally{
		}
	}
	
	/**
	 * Updates the staffs contact info in datastore
	 * 
	 * @param toEditEmail Email of staff to edit
	 * @param office Office location
	 * @param officePhone 10 digit phone for office
	 * @param homeAddress home address
	 * @param homePhone 10 digit phone for home
	 */
	public void updateStaffContact(String toEditEmail, String office,
			String officePhone, String homeAddress, String homePhone) {

		Query q = _pm.newQuery(Staff.class);
		q.setFilter("email=='"+toEditEmail+"'");
		
		List<Staff> staffList = (List<Staff>) q.execute(); 
		Staff staff = staffList.get(0);
		
		staff.setOfficeLoc(office);
		staff.setOfficePhone(officePhone);
		staff.setHomeAddress(homeAddress);
		staff.setHomePhone(homePhone);
		
		_pm.makePersistent(staff);
	}
	
	
	/**
	 * Returns a particular staff from datastore
	 * @param staff Staffs full name with space
	 * @return Staff object from ds
	 */
	public Staff getStaff(String staff) {

		Query q = _pm.newQuery(Staff.class);
			
		q.setFilter("name=='"+staff+"'");
		List<Staff> staffList = (List<Staff>)q.execute();
		
		return staffList.get(0);
	}
	
	/**
	 * Update the given staff 
	 * 
	 * @param username Username (email) to update. Cannot change email
	 * @param nameIn New name
	 * @param password New password
	 * @param stafftype New staff type
	 */
	public void updateStaff(String username, String nameIn, String password,
			String stafftype) {
		// username, firstname + lastname, password, stafftype
		Query q = _pm.newQuery(Staff.class);
		q.setFilter("email=='"+username+"'");
		
		List<Staff> staffList = (List<Staff>) q.execute(); 
		Staff staff = staffList.get(0);
		
		staff.setName(nameIn);
		staff.setPassword(password);
		if (stafftype != null && !stafftype.isEmpty())
			staff.setPermissions(stafftype);
		
		_pm.makePersistent(staff);
	}
	
	
}
