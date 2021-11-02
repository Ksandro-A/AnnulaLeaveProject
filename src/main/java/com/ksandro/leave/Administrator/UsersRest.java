package com.ksandro.leave.Administrator;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ksandro.leave.entity.Users;

import javax.ws.rs.core.Response;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@RestController
@RequestMapping("users")
public class UsersRest {
	
	final static Logger log = Logger.getLogger(UsersRest.class.getName());
	
	@Autowired
	UsersManager usersManager;
	
	@RequestMapping("hi")
	@GET
	@Produces("text/html")
	 public String index() {
        return "Spring Boot POC Welcomes You!";
    }
	
	@RequestMapping("loadUsers")
	@POST
	public ResponseEntity<?> loadUsers() throws Exception {
		log.info("RestMethod loadList() -> START.");
		List<Users> usersList = usersManager.loadUsers();
		if (usersList == null) {
			throw new Exception("Error while loading users list");
		}
		
		log.info("RestMethod loadList() -> FINE.");
		return ResponseEntity.ok(usersList);
	}
	
	@RequestMapping("create")
	@POST
	@ResponseBody
	public ResponseEntity<?> createUser(@RequestBody Users user) throws Exception {
		log.info("RestMethod loadList() -> START.");
		Users users = usersManager.createUser(user);
		if (users == null) {
			throw new Exception("Error while creating user");
		}
		
		log.info("RestMethod loadList() -> FINE.");
		return ResponseEntity.ok(users);
	}

}
