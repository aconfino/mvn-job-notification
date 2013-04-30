package com.confino.gav.notifier;

import hudson.EnvVars;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;

public class NotificationService {

	public static boolean notify(String endpoints, Boolean mvnOnly, Boolean successfulOnly, EnvVars env, String status) {
		if(sendNotification(status, successfulOnly)){
			ProjectInfo projectInfo = MavenUtils.getProjectInfo(new File(env.get("WORKSPACE")));
			if (mvnOnly == true && projectInfo == null){
				return true;
			}
			String json = generateJSON(projectInfo, mvnOnly, status, env);
			String[] endpointArray = generateEndpoints(endpoints);
			for (int i = 0; i < endpointArray.length; i++){
				sendJson(endpointArray[i], json);
			}	
		}
		return true;
	}
	
	protected static String generateJSON(ProjectInfo projectInfo, Boolean mvnOnly, String status, EnvVars env){
		Map<String, String> map = new HashMap<String, String>();
		if (projectInfo != null){
			map = addProjectInfo(map, projectInfo);
		}
		map = addJobInfo(map, env, status);
		return createJSON(map);
		
	}
	
	protected static boolean sendNotification(String status, Boolean successfulOnly){
		if (successfulOnly == false){
			return true;
		}
		if (status.equals("SUCCESS")){
			return true;
		}
		return false;
	}
	
	protected static boolean isMavenProject(ProjectInfo projectInfo){
		if (projectInfo == null){
			return false;
		}
		return true;
	}
	
	protected static Map addJobInfo(Map map, EnvVars env, String status){
		map.put("jobName", env.get("JOB_NAME"));
		map.put("buildNumber", env.get("BUILD_NUMBER"));
		map.put("jenkinsUrl", env.get("JENKINS_URL"));
		map.put("jobUrl", env.get("JOB_URL"));
		map.put("buildUrl", env.get("BUILD_URL"));
		map.put("workspace", env.get("WORKSPACE"));
		map.put("jobStatus", status);
		return map;
	}
	
	protected static Map addProjectInfo(Map map, ProjectInfo projectInfo){
		map.put("groupId", projectInfo.getGroupId());
		map.put("artifactId", projectInfo.getArtifactId());
		map.put("version", projectInfo.getVersion());
		return map;
	}

	@SuppressWarnings("unchecked")
	protected static String createJSON(Map<String, String> map) {
		JSONObject obj = new JSONObject();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			obj.put(entry.getKey(), entry.getValue());
		}
		return obj.toJSONString();
	}
	
	protected static String[] generateEndpoints(String endpoints){
		return endpoints.split(" ");
	}

	protected static String sendJson(String endpoint, String body) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(endpoint);
		StringEntity input = null;
		String response = null;
		HttpResponse httpResponse = null;
		try {
			input = new StringEntity(body);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			httpResponse = httpClient.execute(postRequest);
			response = IOUtils.toString(new InputStreamReader((httpResponse.getEntity().getContent())));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			httpClient.getConnectionManager().shutdown();
		}
		return response;
	}

}
