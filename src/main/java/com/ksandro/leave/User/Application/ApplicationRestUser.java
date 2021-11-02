package com.ksandro.leave.User.Application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ksandro.leave.entity.Application;
import com.ksandro.leave.entity.Users;


@RestController
@RequestMapping("app/user")
public class ApplicationRestUser {
	
	@Autowired
	ApplicationManagerUser applicationManager;

	final static Logger log = Logger.getLogger(ApplicationRestUser.class.getName());
	
	@RequestMapping("loadUserApplications")
	@POST
	public List<Application> loadApplicationsByUserId() throws Exception {
		// user id to be taken from session user
		log.info("RestMethod loadApplicationsById() -> START.");
		List<Application> appList = applicationManager.loadApplicationsByUserId();
		if (appList == null) {
			throw new Exception("Error while loading users list");
		}
		
		log.info("RestMethod loadApplicationsById() -> END.");
		return appList;
	}
	
	@RequestMapping("createApplication")
	@POST
	public ResponseEntity<?> createApplication(@FormParam("startDate") String startDate, @FormParam("endDate") String endDate) throws Exception{
		log.info("RestMethod createApplication() -> START.");
		String startSourceDate = startDate;
		String endSourceDate = endDate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date myStartDate = format.parse(startSourceDate);
		Date myEndDate = format.parse(endSourceDate);
		
		
		Application app = applicationManager.createApplication(myStartDate,myEndDate);
		if(app == null) {
			throw new Exception("Error creating application.Contact Admin");
		}
		log.info("RestMethod createApplication() -> END.");
		return ResponseEntity.ok(app);
	}
	
	@RequestMapping("loadById")
	@POST
	public ResponseEntity<?> loadById(@FormParam("idApplication") Integer idApplication) throws Exception {
		log.info("RestMethod loadById() -> START.");
		
		Application app = applicationManager.loadApplicationById(idApplication);
		if (app == null) {
			throw new Exception("Application not found for id " + idApplication);
		}
		
		log.info("RestMethod loadById() -> END.");
		return ResponseEntity.ok(app);
	}
	
	@RequestMapping("loadUserDays")
	@POST
	public ResponseEntity<?> loadUserDays(@FormParam("userId") String userId) throws Exception {
		log.info("RestMethod loadUserDays() -> START.");
		
		Integer daysRemaining = applicationManager.getLeaveDaysByUserId(userId);
		if (daysRemaining == null) {
			throw new Exception("loadUserDays exception");
		}
		
		log.info("RestMethod loadUserDays() -> END.");
		return ResponseEntity.ok(daysRemaining);
	}
	
	@RequestMapping("deleteById")
	@POST
	public ResponseEntity<?> deleteById(@FormParam("idApplication") Integer idApplication) throws Exception {
		log.info("RestMethod loadById() -> START.");
		
		Application app = applicationManager.deleteById(idApplication);
		if (app == null) {
			throw new Exception("Application not found for id " + idApplication);
		}
		
		log.info("RestMethod loadById() -> END.");
		return ResponseEntity.ok(app);
	}
	
	@POST
	@RequestMapping("listaAppFilter")
	public ResponseEntity<?> listaAppFilter(@FormParam("username") String username, @FormParam("days") String days) throws Exception {
		log.info("Rest method listaAccessiSettings START");
		log.info("Username: " + username);
		log.info("days: " + days);
		List<Application> result = applicationManager.getApplicationList(username, days);
		log.info("Rest method listaAppFilter END");
		return ResponseEntity.ok(result);
	}
}
