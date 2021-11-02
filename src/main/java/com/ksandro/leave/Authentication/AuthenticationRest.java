package com.ksandro.leave.Authentication;

import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ksandro.leave.entity.Users;
import com.ksandro.leave.entity.pojo.UserPojo;

@RestController
@RequestMapping("account")
public class AuthenticationRest {
	
	final static Logger log = Logger.getLogger(AuthenticationRest.class.getName());
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	MailManager mailManager;
	
	@POST
	@RequestMapping("login")
	public ResponseEntity<?> login(@FormParam("userId") String userId, @FormParam("password") String password, @FormParam("enviroment") String enviroment) throws Exception{
		
		UserPojo user = authenticationManager.login(userId,password,enviroment);
		
		switch (enviroment) {
		case "SUPERVISOR":
		case "FINANCE":
		case "ADMINISTRATOR":
			if(!authenticationManager.isUserAdministrator(userId)) {
				throw new Exception("User not authorized in admin area");
			}
			break;
		case "USER":
			if(!authenticationManager.isUserUser(userId)) {
				throw new Exception("User not authorized in users area");
			}
			break;
		default:
			throw new Exception("Environmnet from problem");
		}
			// to do same logic for SUPERVISOR, FINANCE
		
		return ResponseEntity.ok(user);
	}
	
	@POST
	@RequestMapping("changePassword")
	public ResponseEntity<?> changePassword(@FormParam("userId") String userId, @FormParam("oldPassword") String oldPassword, @FormParam("newPassword") String newPassword) throws Exception {
		log.info(String.format("changePassword [UserId] %s", userId));
		String response = "SUCCESS";
		Users user = authenticationManager.changePassword(userId, oldPassword, newPassword);
		if(user == null) {
			response = "FAIL";
		}

		return ResponseEntity.ok(response);
	}

	@POST
	@RequestMapping("resetPassword")
	public ResponseEntity<?> resetPassword(@FormParam("userId") String userId, @FormParam("email") String email) throws Exception {
		log.info("resetPassword [userid] " + userId + " [email] " + email);

		UserPojo user = authenticationManager.resetPassword(userId, email);
		if(user == null) {
			throw new Exception("Error while reseting password");
		}
		String bodyText= mailManager.getBodyResetPassword(user.getName(), user.getLastName(), user.getMail());
		mailManager.sendSimpleMessage(user.getMail(), "Password Reset", bodyText);
		return ResponseEntity.ok("Success");
	}
}
