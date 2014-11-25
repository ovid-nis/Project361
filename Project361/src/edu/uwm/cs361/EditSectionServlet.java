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
		
		doGet(req, resp);
	}

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

		_resp.getWriter().println("<div class=\"layout background-style\">"
				+ "<div class=\"page-after-banner\">"
				+ "<form id='ccf' method='POST' action='editSection'>"
				+ (_req.getParameter("submit") != null ? "<div id='title-create-staff'>Successfully Saved</div>" : "")
				);
	}
	
	private void endForm() throws IOException {
		
		_resp.getWriter().println( ""
										+ "<input class='submit' name='submit' type='submit' value='Submit' />"
										+ "</div></form></div>"
										+ "</div>"
										);
	}

	private void displayStaff() throws IOException {
		
		String html = "<br><div id=\"sub\">Select a staff to assign: </div>"
				+ "<select id='staff' name='staff' class='staff-select'>";
		
		List<Staff> staffList = ds.getAllStaff();
		
		for(Staff staffer:staffList) {
			
			html += "<option value='"+staffer.getName()+"'>"+staffer.getName()+"</option>";
		}
		
		html += "</select>";
		
		_resp.getWriter().println(html);
	}

	private void diplayCourses() throws IOException {
		
		List<Course> myCourses = ds.getAllCourses();
		
		String html = "<div id=\"sub\">Select a course for assignment: </div>"
						+"<select id='section' name='section' class='staff-select'>";
		
		for (Course course : myCourses) {
			
			List<Section> sections = ds.getSection("courseid=='"+course.getID()+"'");
			
			html += "<option disabled> CS " + course.getNumber() + "</option>";
			
			for (Section section : sections) {
				
				html += "<option value='"+section.getID()+"'>"
						+ section.getType() + " " + section.getSection()
						+ "</option>";
			}
		}
		
		html += "</select>";
		
		_resp.getWriter().println(html);
	}
	
	private void handleSubmit() {
		
		if(_req.getParameter("submit") != null) {
			
			ds.editSection(_req.getParameter("section"), _req.getParameter("staff"));
		}
	}
}