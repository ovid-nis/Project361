package edu.uwm.cs361;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class Staff {
	
	@Persistent
	private String email;
	@Persistent
	private String name;
	@Persistent//(mappedBy = "instructor")
	private ArrayList<Key> sectionsTaught;
	@Persistent
	private ArrayList<String> officeHours;
	@Persistent
	private String permissions;
	@Persistent
	private String password;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String officeLocation;
	@Persistent
	private String officePhone;
	@Persistent
	private String homeAddress;
	@Persistent
	private String homePhone;
	@Persistent
	private ArrayList<String> skills;
	
	/**
	 * Staff constructor
	 * 
	 * @param email String - staff email address
	 * @param name String - staff full name
	 * @param secs ArrayList<Section> - sections taught
	 * @param staffType String - Instructor / TA
	 * @param pass String - password
	 */
	public Staff(String email, String name, ArrayList<Section> secs, String staffType, String pass){	
		this.email = email;
		this.name = name;
		//officeHours = of;
		permissions = staffType;
		password = pass;
		
		officeLocation = "";
		officePhone = "";
		homeAddress = "";
		homePhone = "";

		if (secs != null)
		{
			for(Section s : secs){
				sectionsTaught.add(s.getKey());
			}
		}
		
		key = KeyFactory.createKey(Staff.class.getSimpleName(), name);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(this);
		pm.close();
	}
	
	/**
	 * 
	 * @return REturns Staffs Key for use in datastore
	 */
	public Key getKey() {
		return key;
	}
	/**
	 * 
	 * @return Returns array list of all the staff's office hours
	 */
	public ArrayList<String> getOfficeHours(){
		if(officeHours.isEmpty() || officeHours == null){
			ArrayList<String> ss = new ArrayList<String>();
			ss.add("");
			return null;
		}
		return officeHours;
	}
	/**
	 * pushes argument to officeHours
	 * @param hours
	 */
	public void addOfficeHours(String hours){
		officeHours.add(hours);
	}
	/**
	 * removes argument from officeHours if it exists
	 * @param hours
	 */
	public void removeOfficeHours(String hours){
		officeHours.remove(hours);
	}
	/**
	 * tries to change an existing element of officeHours
	 * does nothing if oldHours does not exist in officeHours
	 * @param oldHours
	 * @param newHours
	 */
	public void modifyOfficeHours(String oldHours, String newHours){
		if(officeHours.indexOf(oldHours) >= 0)
			officeHours.set(officeHours.indexOf(oldHours), newHours);
	}
	/**
	 * empties officeHours
	 */
	public void removeOfficeHoursAll(){
		officeHours.clear();
	}
	/**
	 * getOfficeLoc
	 * 
	 * @return String containing office location
	 */
	public String getOfficeLoc() {
		return officeLocation;
	}
	
	/**
	 * sets office location to the provided parameter
	 * @param officeLoc New office location
	 */
	public void setOfficeLoc(String officeLoc) {
		this.officeLocation = officeLoc;
	}
	
	/**
	 * getOfficePhone
	 * 
	 * @return String containing office phone
	 */
	public String getOfficePhone() {
		return officePhone;
	}
	
	/**
	 * sets office phone number to phone param
	 * 
	 * @param phone New phone number
	 */
	public void setOfficePhone(String phone) {
		this.officePhone = phone;
	}
	
	/**
	 * getHomeAddress
	 * 
	 * @return String containing home address
	 */
	public String getHomeAddress() {
		return homeAddress;
	}
	
	/**
	 * sets home address to the address param
	 * @param address New address
	 */
	public void setHomeAddress(String address) {
		this.homeAddress = address;
	}
	
	/**
	 * getHomePhone
	 * 
	 * @return String containing home phone
	 */
	public String getHomePhone() {
		return homePhone;
	}
	
	/**
	 * sets home phone number the the provided number
	 * 
	 * @param phone New phone number
	 */
	public void setHomePhone(String phone) {
		this.homePhone = phone;	
	}
	
	/**
	 * getEmail
	 * 
	 * @return String containing email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * setEmail - sets staffs email to the email provided
	 * 
	 * @param email New email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * getName 
	 * 
	 * @return String containing staff's full name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * sets staff name to the provided name
	 * 
	 * @param name New name 
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * getSectionsTaught
	 * 
	 * @return ArrayList of sections taught by staff
	 */
	public ArrayList<Section> getSectionsTaught() {
		ArrayList<Section> ss = new ArrayList<Section>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		for(Key k : sectionsTaught){
			
			if(pm.getObjectById(Section.class, k) instanceof Section){
				ss.add((Section) pm.getObjectById(Section.class, k));
			}
		}
		pm.close();
		return ss;
	}
	
	/**
	 * sets the staffs sections taught, to the passed array list
	 * 
	 * @param sectionsTaught Array list with new sections taught
	 */
	public void setSectionsTaught(ArrayList<Section> st) {
		sectionsTaught.clear();
		for(Section s : st){
			sectionsTaught.add(s.getKey());
		}
	}
	
	/**
	 * Adds the given section to this staff's list of sections taught
	 * @param s
	 */
	public void addSectionTaught(Section s){
		sectionsTaught.add(s.getKey());
	}
	
	/**
	 * Removes the given section to this staff's list of sections taught
	 * @param s
	 */
	public void removeSectionTaught(Section s){
		sectionsTaught.remove(s.getKey());
	}
	/*
	public String getOfficeHours() {
		return officeHours;
	}
	public void setOfficeHours(String officeHours) {
		this.officeHours = officeHours;
	}
	*/
	
	/**
	 * getPermissions
	 * 
	 * @return returns a string indicating staff type
	 */
	public String getPermissions() {
		return permissions;
	}
	
	/**
	 * sets staff type the the permissions parameter
	 * 
	 * @param permissions String containing new staff type
	 */
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	
	/**
	 * getPassword
	 * 
	 * @return String containing staff's password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * sets staff's password to the newly provided password
	 * 
	 * @param password String containing new password 
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns a list containing this staffs teaching skills/profiniencies
	 * List will be undefined for calls on a non-TA staff
	 * @return
	 */
	public ArrayList<String> getSkills() {
		return skills;
	}
	
	/**
	 * Takes a string containing a skill, adds that skill to this staff
	 * @param skillname
	 */
	public void addSkill(String skillname){
		skills.add(skillname);
	}
	
	/**
	 * Takes a string containing a skill, removes that skill from this Staff's list of skills
	 * @param skillname
	 * @return
	 */
	public boolean removeSkill(String skillname){
		return skills.remove(skillname);
	}
	
	/**
	 * Takes an array list of skills, replaces this staff's skills with the given list
	 * @param newSkills
	 */
	public void setSkills(ArrayList<String> newSkills){
		skills = newSkills;
	}
	
}
