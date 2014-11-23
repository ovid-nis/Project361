package edu.uwm.cs361;

import java.util.ArrayList;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class DatastoreServ {
	
	private DatastoreService ds = null;
	private String adminPassword = "admin";
	
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String password) {
		adminPassword = password;
	}
	
	public DatastoreServ() {
		ds = DatastoreServiceFactory.getDatastoreService();
	}
	public DatastoreService getDatastore(){
		return ds;
	}
	
	public void createStaff(Staff s){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(s);
		}finally{
			pm.close();
		}
	}
	
	// i don't know what the equivalent to higher order
	// functions in java would be. in CL i'd just write 
	// a with-persistence-manager macro, but that won't
	// do here because no homoiconicity... but i could
	// get a higher order function to work for the same
	// purpose if i had any
	
	public void createCourse(Course c){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(c);
			pm.makePersistentAll(c.getSections());
		}finally{
			pm.close();
		}
	}
	public void createCourse(ArrayList<Course> cs){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			for(Course c : cs){
				pm.makePersistent(c);
				pm.makePersistentAll(c.getSections());
			}
		}finally{
			pm.close();
		}
	}
	public void createSection(Section s){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(s);
		}finally{
			pm.close();
		}
	}
	public void createSection(ArrayList<Section> ss){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistentAll(ss);
		}finally{
			pm.close();
		}
	}
	
	public Course getCourse(String key){
		PersistenceManager pm = PMF.get().getPersistenceManager(); // this line smells
		Course c = pm.getObjectById(Course.class, key);
		pm.close();	// i don't trust java to close pm when the function is done
		return c;  // so that's another two lines
	}
	public Section getSection(String key){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Section s = pm.getObjectById(Section.class, key);
		pm.close();
		return s;
	}
	
	public ArrayList<Course> getAllCourses(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Extent<Course> e = pm.getExtent(Course.class);
		ArrayList<Course> cs = new ArrayList<Course>();
		for(Course c : e){
			cs.add(c);
		}
		return cs;
	}
	public ArrayList<Staff> getAllStaff(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Extent<Staff> e = pm.getExtent(Staff.class);
		ArrayList<Staff> ss = new ArrayList<Staff>();
		for(Staff s : e){
			ss.add(s);
		}
		return ss;
	}
}
