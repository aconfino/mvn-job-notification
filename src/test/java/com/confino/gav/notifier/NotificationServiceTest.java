package com.confino.gav.notifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class NotificationServiceTest {
	
	private Map<String,String> map = new HashMap<String,String>();
	
	@Before
	public void setup(){
		map.put("groupId", "com.confino");
		map.put("artifactId", "gav-notifier");
		map.put("version", "1.0-SNAPSHOT");
	}

	@Test
	public void addJobInfoTest(){
		// TODO
	}
	
	public void addProjectInfoTest(){
		// TODO
	}
	
	@Test
	public void createJSONTest(){
		String string = NotificationService.createJSON(map);
		String validJSON = "{\"groupId\":\"com.confino\",\"artifactId\":\"gav-notifier\",\"version\":\"1.0-SNAPSHOT\"}";
		assert(string.equals(validJSON));
	}
	
	@Test
	public void generateEndpoints(){
		String endpoints1 = "http://localhost:8000/test http://www.google.com/test";
		String [] endpointsArray1  = NotificationService.generateEndpoints(endpoints1);
		assertTrue(endpointsArray1.length == 2);
		String endpoints2 = "http://localhost:8000/test";
		String [] endpointsArray2  = NotificationService.generateEndpoints(endpoints2);
		assertTrue(endpointsArray2[0].equals(endpoints2));
	}
	
	@Test
	public void generateJSONTest(){
		// TODO
	}
	
	@Test
	public void isMavenProjectTest(){
		ProjectInfo projectInfo = new ProjectInfo();
		assertTrue(NotificationService.isMavenProject(projectInfo));
		assertFalse(NotificationService.isMavenProject(null));
	}
	
	@Test
	public void notifyTest(){
		// TODO
	}
	
	@Test
	public void sendJsonTest(){
		// TODO - finish me
		String endpoint = "http://localhost:8000/test";
		String body ="{\"groupId\":\"com.confino\",\"artifactId\":\"gav-notifier\",\"version\":\"1.0-SNAPSHOT\"}";
		String response = NotificationService.sendJson(endpoint, body);
		System.out.println(response);
	}
	
	@Test
	public void sendNotificationTest(){
		assertTrue(NotificationService.sendNotification("SUCCESS", true));
		assertFalse(NotificationService.sendNotification("FAILED", true));	
		assertTrue(NotificationService.sendNotification("FAILED", false));
		assertTrue(NotificationService.sendNotification("UNSTABLE", false));
	}



}
