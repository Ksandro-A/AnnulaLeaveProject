package com.ksandro.leave.Application;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ksandro.leave.Authentication.MailManager;
import com.ksandro.leave.entity.Application;
import com.ksandro.leave.entity.UserLeave;
import com.ksandro.leave.entity.Users;

@Service
public class ApplicationManager {
	
	@Autowired
	MailManager mailManager;

	
final static Logger log = Logger.getLogger(ApplicationManager.class.getName());
	
	@PersistenceContext
	EntityManager entityManager;
	
	// Method to load all applications
	public List<Application> loadApplications() {
		log.info("Method loadApplications -> START");
		String query = "select app from Application app where app.deleteDate is NULL";
		List<Application>  application = entityManager.createQuery(query).getResultList();
		log.info("Method loadApplications -> End");
		return application;
	}
	
	// Method to load application by id
	public Application loadApplicationById(Integer id) throws Exception{
		log.info("Method loadApplicationById -> START");
		log.info("[ApplicationId]: " + id);
		if(id == null) {
			throw new Exception("Application id not validated");
		}
		Application application = entityManager.find(Application.class, id);
		if(application == null) {
			throw new Exception("Application not found");
		}
		log.info("Method loadApplicationById -> End");
		return application;
	}
	
	// Method to approve or decline user application
	@Transactional
	public Application changeStateApplication(Application application) throws Exception{
		log.info("Method changeStateApplication() -> START.");
		
		if (application == null || application.getId() == null) {
			throw new Exception("Application not valorized");
		}
		Date now = new Date();
		application.setUpdateDate(now);
		// getting user from session
		application.setDecisionBy("Test");
		application.setResponseDate(now);
		entityManager.merge(application);
		
		if(application != null && application.getAppStatus() != null && application.getAppStatus().compareToIgnoreCase("declined") == 0) {
			if(application.getReason() == null || (application.getReason() != null && application.getReason().compareTo("") == 0)){
				throw new Exception("Reason must be specified");
			}
		}

		if(application != null && application.getAppStatus() != null && application.getAppStatus().compareToIgnoreCase("declined") != 0) {
			if(application.getLeaveEndDate() != null && application.getLeaveStartDate() != null) {
				int daysRequested = (int) ((application.getLeaveEndDate().getTime() - application.getLeaveStartDate().getTime()) / (1000 * 60 * 60 * 24));
				updateUserLeave(application.getUserId(),daysRequested);
			}
		}
		
		Users user = entityManager.find(Users.class, application.getUserId());
			
		if(user == null) {
			throw new Exception("User not found");
		}
		String bodyText = mailManager.getBodyChangeStatusApplication(user.getUserName(), user.getUserLastName(), application.getAppStatus(), application.getReason());
		mailManager.sendSimpleMessage(user.getEmail(), "Application Response", bodyText);
		log.info("Method changeStateApplication() -> START.");
		return application;
	}
	
	//Method to update the days of the leave
	@Transactional
	private void updateUserLeave(String appUserId,int daysRequested) {
		log.info("Method  updateUserLeave -> START");
		log.info("[appUserId]: " + appUserId);
		log.info("[daysRequested]: " + daysRequested);
		UserLeave userLeave = null;
		if(appUserId != null) {
			userLeave = getUserLeave(appUserId);
		}
		if(userLeave != null && userLeave.getLeaveDays() != null) {
			int daysRemaining = userLeave.getLeaveDays() - daysRequested;
			userLeave.setLeaveDays(daysRemaining);
			entityManager.merge(userLeave);
		}
		log.info("Method  updateUserLeave -> END");
	}
	@Transactional
	public UserLeave getUserLeave(String appUserId) {
		log.info("Method  getUserLeave -> START");
		log.info("[appUserId]: " + appUserId);
		String query = "select leave from UserLeave leave where leave.userId = : userId AND leave.deleteDate is null";
		Query q = entityManager.createQuery(query);
		q.setParameter("userId", appUserId);
		UserLeave userLeave = (UserLeave) q.getSingleResult();
		log.info("Method  getUserLeave -> END");
		return userLeave;
	}
	
	
	
}
