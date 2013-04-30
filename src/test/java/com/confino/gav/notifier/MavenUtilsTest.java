package com.confino.gav.notifier;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class MavenUtilsTest {
	
	String workspace = "src/test/resources";
	File pom = new File(workspace + "/pom.xml");
	File effectivePom = new File(workspace + "/effective-pom.xml");
	File effectivePomParse = new File(workspace + "/effective-pom-parse.xml");

	@Test
	public void getProjectInfoTest(){
		// TODO
	}
	
	@Test
	public void effectivePomTest(){
		assertTrue(pom.exists());
		if (effectivePom.exists()){
			FileUtils.deleteQuietly(effectivePom);
		}
		effectivePom = MavenUtils.generateEffectivePom(new File(workspace));
		assertTrue(effectivePom.exists());
	}
	
	@Test
	public void parseProjectInfoTest(){
		assertTrue(effectivePomParse.exists());
		ProjectInfo projectInfo = MavenUtils.parseProjectInfo(effectivePomParse);
		assertTrue(projectInfo.getGroupId().equals("com.ltracker"));
		assertTrue(projectInfo.getArtifactId().equals("app"));
		assertTrue(projectInfo.getVersion().equals("1.0.0-BUILD-SNAPSHOT"));
	}
	
	@Test
	public void parsePomTest() throws IOException{
		assertTrue(effectivePomParse.exists());
		String string = FileUtils.readFileToString(effectivePomParse);
		ProjectInfo projectInfo = MavenUtils.parsePom(string);
		assertTrue(projectInfo.getGroupId().equals("com.ltracker"));
		assertTrue(projectInfo.getArtifactId().equals("app"));
		assertTrue(projectInfo.getVersion().equals("1.0.0-BUILD-SNAPSHOT"));
	}
	
}
