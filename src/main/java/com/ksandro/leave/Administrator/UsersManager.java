package com.ksandro.leave.Administrator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.ksandro.leave.entity.UserLeave;
import com.ksandro.leave.entity.UserRoles;
import com.ksandro.leave.entity.Users;

@Service
public class UsersManager {
	
	final static Logger log = Logger.getLogger(UsersManager.class.getName());
	
	@PersistenceContext
	EntityManager entityManager;
	
	
	public List<Users> loadUsers() {
		log.info("Method loadUsers -> START");
		String query = "select u from Users u";
		List<Users>  users = entityManager.createQuery(query).getResultList();
	
		log.info("Method loadUsers -> End");
		return users;
	}
	
	@Transactional
	public Users createUser(Users user) throws Exception{
		log.info("Method createUser -> START");
		if(user == null || user.getId() == null) {
			throw new Exception("user is null");
		}
		Users existingUser = entityManager.find(Users.class,user.getId());
		if(existingUser != null) {
			throw new Exception("User id already exists");
		}
		
		if(user != null && user.getUserName() != null) {
			throw new Exception("User name not valorized");
		}
		
		if(user != null && user.getUserPassword() != null) {
			throw new Exception("Users password not valorized");
		}
		Users users = new Users();
		users.setId(user.getId());
		users.setUserName(user.getUserName());
		users.setUserLastName(user.getUserLastName());
		users.setEmail(user.getEmail());
		users.setUserPassword(user.getUserPassword());
		//encoding the password for security
	    String encodedPassword = encodePassword(users.getUserPassword());
		users.setUserPassword(encodedPassword);
		users.setInsertDate(new Date());
		entityManager.persist(users);
		UserRoles role = user.getUserRole();
		role.setUserID(user.getId());
		setUserRole(role);
		log.info("Method createUser -> END");
		return user;
	}
	@Transactional
	private void setUserRole (UserRoles role) throws Exception {
		log.info("Method setUserRole -> START");
		try {
		UserRoles userRoles = new UserRoles();
		userRoles.setUserID(role.getUserID());
		userRoles.setRoleName(role.getRoleName());
		entityManager.persist(userRoles);
		setUserLeave(role.getUserID());
		}catch(Exception e) {
			throw new Exception("Error inserting the user role.Contact administrator");
		}
	}
	@Transactional
	private void setUserLeave(String userId) throws Exception{
		log.info("Method setUserLeave -> START");
		try {
		UserLeave userLeave = new UserLeave();
		userLeave.setUserId(userId);
		userLeave.setInsertDate(new Date());
		userLeave.setLeaveDays(20);
		entityManager.persist(userLeave);
		}catch(Exception e) {
			throw new Exception("Error inserting userLeave.Contact administrator");
		}
		
	}
	
	public String encodePassword(String value) throws Exception {		
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (Exception ex) {
			throw new Exception("encodeSHA256 error ", ex);
		}

		byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
		byte[] retBase64 = Base64.getEncoder().encode(hash);

		String encoded = new String(retBase64);
		return encoded;
	}

}
