package com.ksandro.leave.User.Application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ksandro.leave.Administrator.UsersManager;
import com.ksandro.leave.entity.Application;
import com.ksandro.leave.entity.UserLeave;
import com.ksandro.leave.entity.Users;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class ApplicationManagerUser {

	
final static Logger log = Logger.getLogger(ApplicationManagerUser.class.getName());
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Transactional
	public Application createApplication(Date startDate,Date endDate) throws Exception{
		//getting userId from sessionUser
		String userId = "testUser1";
		// check if probation time 90 days 
		String query = "select leave from UserLeave leave where leave.userId = : userId "
				+ "AND leave.insertDate < SUBDATE(CURDATE(),90) "
				+ "AND leave.deleteDate is null";
		Query q = entityManager.createQuery(query);
		q.setParameter("userId", userId);
		UserLeave userLeave = (UserLeave) q.getSingleResult();
		if(userLeave == null) {
			throw new Exception("Probation time not pased");
		}
		
		Application application = new Application();
		application.setInsertDate(new Date());
		application.setLeaveStartDate(startDate);
		application.setLeaveEndDate(endDate);
		application.setUserId(userId);
		application.setAppStatus("PENDING");
		entityManager.persist(application);
		return application;
		
	}
	
	public List<Application> loadApplicationsByUserId() {
		log.info("Method getAllUsers -> START");
		String userId = "Test";
		String query = "select app from Application app where app.userId = :userId";
		Query q = entityManager.createQuery(query);
		q.setParameter("userId", userId);
		List<Application>  application = q.getResultList();
	
		log.info("Method getAllUsers -> End");
		return application;
	}
	
	public Integer getLeaveDaysByUserId(String userId) {
		int days = 0;
		String query = "select leave from UserLeave leave where leave.userId = : userId AND leave.deleteDate is null";
		Query q = entityManager.createQuery(query);
		q.setParameter("userId", userId);
		UserLeave userLeave = (UserLeave) q.getSingleResult();
		if(userLeave != null) {
			days = userLeave.getLeaveDays();
		}
		return days;
	}
	
	public Application loadApplicationById(Integer id) throws Exception{
		log.info("[ApplicationId]: " + id);
		
		if(id == null) {
			throw new Exception("Application id not validated");
		}
		
		Application application = entityManager.find(Application.class, id);
		if(application == null) {
			throw new Exception("Application not found");
		}
		
		
		return application;
	}
	
	public List<Application> getApplicationList(String username, String days) throws Exception {
		log.info("Method : getApplicationList START");
		log.info("username" + username);
		log.info("days" + days);
		List<Application> ApplicationList = new ArrayList<Application>();


		String query = "SELECT a FROM Application a "
				+ "WHERE a.userId = :username ";

		if (Integer.valueOf(days) != 0) {
			query += " and a.insertDate > SUBDATE(CURDATE(), :days) ";
		}

		query += " ORDER BY a.insertDate DESC ";

		Query q = this.entityManager.createQuery(query).setParameter("username", username);

		if (Integer.valueOf(days) != 0) {
			q.setParameter("days", Integer.valueOf(days));
		}
		ApplicationList = q.getResultList();
		log.info("Method : getApplicationList END");
		return ApplicationList;
	}
	
	public Application deleteById(Integer id) throws Exception {
		Application app = loadApplicationById(id);
		app.setDeleteDate(new Date());
		//also to set user deleted
		return app;
	}
}
