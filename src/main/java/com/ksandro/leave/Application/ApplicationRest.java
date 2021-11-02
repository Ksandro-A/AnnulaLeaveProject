package com.ksandro.leave.Application;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ksandro.leave.entity.Application;

@Produces("application/json")
@RestController
@RequestMapping("app/admin")
public class ApplicationRest {
	
	@Autowired
	ApplicationManager applicationManager;
	

	final static Logger log = Logger.getLogger(ApplicationRest.class.getName());
	
	@RequestMapping("loadUserApplications")
	@POST
	public ResponseEntity<?> loadApplicationsList() throws Exception {
		// user id to be taken from session user
		log.info("RestMethod loadList() -> Start.");
		List<Application> appList = applicationManager.loadApplications();
		if (appList == null) {
			throw new Exception("Error while loading users list");
		}
		
		log.info("RestMethod loadList() -> END.");
		return ResponseEntity.ok(appList);
	}
	
	@RequestMapping("loadById")
	@POST
	public ResponseEntity<?> loadById(@FormParam("idApplication") Integer idApplication) throws Exception {
		log.info("RestMethod loadById() -> START.");
		
		if(idApplication == null) {
			throw new Exception("Application id not valorized");
		}
		
		Application app = applicationManager.loadApplicationById(idApplication);
		if (app == null) {
			throw new Exception("Application not found for id " + idApplication);
		}
		
		log.info("RestMethod loadById() -> END.");
		return ResponseEntity.ok(app);
	}
	
	@RequestMapping("saveChangeStatus")
	@POST
	@ResponseBody
	public ResponseEntity<?> saveApplicationChangeStatus(@RequestBody Application application) throws Exception {
		log.info("RestMethod saveApplicationChangeStatus() -> Start.");
		log.info("applicationid " + application.getId());
		log.info("applicationS " + application.getAppStatus());
		
		application = applicationManager.changeStateApplication(application);
		if (application == null) {
			throw new Exception("Error while saving application.");
		}
		
		log.info("RestMethod savePropostaCambioStato() -> FINE.");
		return ResponseEntity.ok(application);
	}
}
