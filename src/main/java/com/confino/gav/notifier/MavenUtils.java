package com.confino.gav.notifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MavenUtils {
	
	public static ProjectInfo getProjectInfo(File workspace){
		File effectivePom = generateEffectivePom(workspace);
		return parseProjectInfo(effectivePom);
	}
	
	protected static File generateEffectivePom(File workspace){
		File pomFile = new File(workspace.getAbsolutePath() + "/pom.xml");
		if(!pomFile.exists()){ 
			System.out.println("pom.xml does not exist in " + workspace.getAbsolutePath());
			System.out.println("Are you using the default Jenkins workspace?");
			return null;
		}
		HashMap<String,String> env = new HashMap<String,String>();
		env.putAll(System.getenv());
		File outputFile = new File(workspace.getAbsolutePath() + "/effective-pom.xml");
		List<String> args = new ArrayList<String>();
		args.add("-f");
		args.add(pomFile.getAbsolutePath());
		args.add("help:effective-pom");
		args.add("-Doutput=" + outputFile.getAbsolutePath());
		executeCommand("C:/tools/apache-maven-3.0.5/bin/mvn.bat", args, env);
		return outputFile;
	}
	
	protected static void executeCommand(String commandName, List<String> args, HashMap<String,String> env){
		CommandLine cmdLine = new CommandLine(commandName);
		cmdLine.addArguments(args.toArray(new String[0]));
		DefaultExecutor executor = new DefaultExecutor();
		try {
			executor.execute(cmdLine, env);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static ProjectInfo parseProjectInfo(File effectivePom){
		if(effectivePom == null || !effectivePom.exists()){
			return null;
		}
		ProjectInfo projectInfo = null;
		try {
			String pom = FileUtils.readFileToString(effectivePom);
			projectInfo = parsePom(pom);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projectInfo;
	}
	
	protected static ProjectInfo parsePom(String pom){
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		ProjectInfo projectInfo = new ProjectInfo();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(pom.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				projectInfo = parseNode(node, projectInfo);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projectInfo;
	}
	
	protected static ProjectInfo parseNode(Node node, ProjectInfo projectInfo){
		if(node.getNodeType() == Node.ELEMENT_NODE){
			Element element = (Element) node;
			if (node.getNodeName().equals("groupId")){
				projectInfo.setGroupId(element.getTextContent().trim());
			}
			if (node.getNodeName().equals("artifactId")){
				projectInfo.setArtifactId(element.getTextContent().trim());
			}
			if (node.getNodeName().equals("version")){
				projectInfo.setVersion(element.getTextContent().trim());
			}
		}		
		return projectInfo;
	}
	

}
