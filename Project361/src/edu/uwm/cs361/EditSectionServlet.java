package edu.uwm.cs361;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class EditSectionServlet extends HttpServlet {

	ProjectServlet page = new ProjectServlet();
	
	DatastoreServ ds = new DatastoreServ();
	
	HttpServletRequest _req;
	
	HttpServletResponse _resp;
	
	List<String> _errors;
	
	String sectionid;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		_req = req;
		
		_resp = resp;
		
		_errors = new ArrayList<String>();
		
		page.checkLogin(req, resp);
		
		displayForm();
		
		handleSubmit();
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String section = req.getParameter("section");
		
		if(section != null){
				//data.deleteSection(section);
		}
		doGet(req, resp);
	}

	/**
	 * Displays the main page content, generating a list of sections and staff options
	 * @throws IOException
	 */
	private void displayForm() throws IOException {

		page.banner(_req, _resp);
		
		startForm();
		
		diplayCourses();
		
		displayStaff();
		
		endForm();
		
		page.menu(_req, _resp);

	}

	/**
	 * Print initial form html code
	 * @throws IOException
	 */
	private void startForm() throws IOException {

		_resp.getWriter().println("<div class=\"layout\">"
				+ "<div class=\"page-after-banner\">"
				+ "<form id='ccf' method='POST' action='editSection'>"
				+			"<div id=\"title-create-staff\">"
				+				"Edit Section"
				+			"</div>"
				+ (_req.getParameter("submitSect") != null ? "<ul class='success'><li>Successfully Saved</li></ul>": "")
				);
	}
	
	/**
	 * Print endind form html code
	 * 
	 * @throws IOException
	 */
	private void endForm() throws IOException {
		
		_resp.getWriter().println( "<input class='submit-section' type='submit' name = 'submitSect' value='Assign Instructor' />");
		_resp.getWriter().println("</div></form></div>");
		_resp.getWriter().println("</div>");
	}

	/**
	 * Displays the staff as a dropdown
	 * 
	 * @throws IOException
	 */
	private void displayStaff() throws IOException {
		
		String html = "<br><div id=\"sub\">Select a staff to assign: </div>"
				+ "<select required id='staff' name='staff' class='staff-select staff-view-margin'>";
		
		List<Staff> staffList = ds.getAllStaff();
		html += "<option selected disabled>"+ "Select staff" +"</option>";
		
		if(page.username.equals("admin@uwm.edu")){
			html += "<option disabled>Instructor's</option>";		
			for(Staff user:staffList){
				if(!user.getPermissions().equals("TA"))
					html += "<option>" + user.getName() + "</option>";
			}
		}
		if(page.username.equals("admin@uwm.edu"))
			html += "<option disabled>TA's</option>";
		for(Staff user:staffList){
			if(user.getPermissions().equals("TA"))
				html += "<option>" + user.getName() + "</option>";
		}
		
		
		html += "</select>";
		
		_resp.getWriter().println(html);
	}

	/**
	 *  Displays courses as a dropdown
	 *  
	 * @throws IOException
	 */
	private void diplayCourses() throws IOException {
		
		
		String html = "<div id=\"sub\">Select a course for assignment: </div>"
				+"<select id='section' name='section' class='staff-select' required>";

		html += "<option selected disabled>"+ "Select section" +"</option>";

		//if admin - show all
		
		if (page.getUsername().equals("admin@uwm.edu"))
		{
			List<Course> myCourses = ds.getAllCourses();
			
			for (Course course : myCourses) {
				
				List<Section> sections = ds.getSection("courseid=='"+course.getID()+"'");
				
				html += "<option disabled> CS " + course.getNumber() + "</option>";
				
				for (Section section : sections) {

					html += "<option value='"+course.getNumber() + " " + section.getID()+"'>"
							+ "CS " + course.getNumber() + " - "
							+ section.getType() + " " + section.getSection()
							+ "</option>";
				}
			}
		}
		
		//if instructor - show those teaching
		
		else {
			 
			Staff staff = page.getCurrentUser();
			List<Section> sections = staff.getSectionsTaught();
	
				
			for (Section section : sections) {
				
				String course = section.getCourseid();
				course = course.replace("COMPSCI", "");
				course = course.substring(1);
				System.out.println("Course: " + course);

				html += "<option value='"+ course + " " + section.getID()+"'>"
						+ "CS " + course + " - "
						+ section.getType() + " " + section.getSection()
						+ "</option>";
			}
		}
		
		
		
		html += "</select>";
		
		_resp.getWriter().println(html);
	}
	
	/**
	 * Checks if submit has been clicked, edits the section if so
	 */
	private void handleSubmit() {
		
		if(_req.getParameter("staff") != null && _req.getParameter("section") != null) {
			
			String section = _req.getParameter("section");
			
			Section toUpdateSection = ds.getSectionByName(section);
			Staff toUpdateStaff= ds.getStaff(_req.getParameter("staff"));
			
			System.out.println("Editing: " + section + "\n" + ds.getStaff(_req.getParameter("staff")));
			
			ds.editSectionsStaff(toUpdateSection, toUpdateStaff);
		}
	}
}
