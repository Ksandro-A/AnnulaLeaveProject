package com.ksandro.leave.Authentication;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ksandro.leave.Administrator.UsersManager;
import com.ksandro.leave.entity.Users;
import com.ksandro.leave.entity.pojo.UserPojo;

@Service
public class AuthenticationManager {
	
	final static Logger log = Logger.getLogger(AuthenticationManager.class.getName());
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	MailManager mailManager;
	
	@Autowired
	UsersManager usersManager;
	
	@Transactional
	public UserPojo login(String userId, String password, String enviroment) throws Exception {

		if (enviroment == null || enviroment.equals("")) {
			throw new Exception("ApplicationName non valorized");
		}

		if ((userId == null || userId.equals("")) || (password == null)) {
			throw new Exception("Login failed");
		}

		String query = "Select a from Users a where a.id = :pUsername and a.deleteDate is null";
		Query q = entityManager.createQuery(query);
		q.setParameter("pUsername", userId);

		Users user;
		try {
			user = (Users) q.getSingleResult();
		} catch (Exception ex) {
			throw new Exception("Login failed");
		}

		if (user == null) {
			throw new Exception("Login failed");
		}

		if (user.getUserPassword() == null) {
			throw new Exception("Login failed");
		}

		//To do controll if blocked
		
		UserPojo userPojo = new UserPojo();
		userPojo.setName(user.getUserName());
		userPojo.setLastName(user.getUserLastName());

		
			boolean checkPassword = user.getUserPassword().equals(usersManager.encodePassword(password));
		if(checkPassword == false) {
			throw new Exception("User name or password not valid");
		}

		return userPojo;
	}
	
	@Transactional
	public Users changePassword(String userid, String oldPassword, String newPassword) throws Exception {
		log.info("Method changePassword -> START");
		if (userid == null || userid.equals("")) {
			throw new Exception("Userid is NULL");
		}

		if (oldPassword == null || oldPassword.equals("")) {
			throw new Exception("oldPassword is NULL");
		}

		if (newPassword == null || newPassword.equals("")) {
			throw new Exception("newPassword is NULL");
		}

		
		Users user = entityManager.find(Users.class, userid);
		if (user == null) {
			throw new Exception("user not valid");
		}

		
		boolean checkPassword =  user.getUserPassword().equals(usersManager.encodePassword(oldPassword));
		
		if(checkPassword) {
			throw new Exception("Old password not correct");
		}

		entityManager.merge(user);
		String bodyText = "Password changed succesfully. If this wasnt your request use reset password";
		try {
		mailManager.sendSimpleMessage(user.getEmail(), userid, bodyText);
		}catch (Exception e) {
			//log.severe("Error sending change password confirmation e-mail");
			throw new Exception("MAIL");
		}
		log.info("Method changePassword -> END");
		return user;
	}
	
	@Transactional
	public UserPojo resetPassword(String userid, String email) throws Exception {

		if (userid == null || userid.equals("")) {
			throw new Exception("Userid is NULL");
		}

		if (email == null || email.equals("")) {
			throw new Exception("email is NULL");
		}

		//search for user
		Users user = this.entityManager.find(Users.class, userid);
		if (user == null) {
			throw new Exception("User not found ");
		}

		if (user.getEmail().equals("")) {
			log.info("resetPassword, email not valorized: " + userid);
			throw new Exception("Atention email not valorized. Contact administrator");
		}
		
		if(user.getEmail().compareToIgnoreCase(email) != 0) {
			throw new Exception("Mail not correct");
		}


		
		String newPassword = getRandomNumber() + "";

		//Setting the visible password to send via mail
		UserPojo userPojo = new UserPojo(user.getUserName(),user.getUserLastName(),user.getEmail(),newPassword);
		String encodedPass = usersManager.encodePassword(newPassword);
		user.setUserPassword(encodedPass);
		this.entityManager.merge(user);

		return userPojo;

	}
	
	private int getRandomNumber() {
		log.info("Method getRandomNumber() -> INIZIO.");
		Random rnd = new Random();
		int number = rnd.nextInt(90000) + 10000;
		log.info("Method getRandomNumber() -> Fine.");
		return number;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isUserAdministrator(String userId) throws Exception {

		String query = " SELECT users "
		+ "FROM UserRoles roles, Users users "
		+ "WHERE users.id = :userId "
		+ "AND users.id = roles.userID "
		+ "AND roles.roleName IN ('administrator','adm') ";

		List<Users> resultList = entityManager.createQuery(query).setParameter("userId", userId).getResultList();

		if (resultList == null || resultList.size() < 1) {
		return false;
		}

		return true;
		}
	@SuppressWarnings("unchecked")
	public boolean isUserUser(String userId) throws Exception {

		String query = " SELECT users "
		+ "FROM UserRoles roles, Users users "
		+ "WHERE users.id = :userId "
		+ "AND users.id = roles.userID "
		+ "AND roles.roleName IN ('user','client') ";

		List<Users> resultList = entityManager.createQuery(query).setParameter("userId", userId).getResultList();

		if (resultList == null || resultList.size() < 1) {
		return false;
		}

		return true;
		}
}
