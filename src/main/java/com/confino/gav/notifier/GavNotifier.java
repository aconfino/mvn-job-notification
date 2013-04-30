package com.confino.gav.notifier;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

@SuppressWarnings("unchecked")
public class GavNotifier extends Notifier {

    private String endPoints;
    private Boolean mvnOnly = false;
    private Boolean successfulOnly = false;
    
    @DataBoundConstructor
    public GavNotifier(String endPoints, Boolean mvnOnly, Boolean successfulOnly) {
    	this.endPoints = endPoints;
    	this.mvnOnly = mvnOnly;
    	this.successfulOnly = successfulOnly;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException  {
    	EnvVars env = build.getEnvironment(listener);
        env.overrideAll(build.getBuildVariables());
        return NotificationService.notify(endPoints, mvnOnly, successfulOnly, env, build.getResult().toString());
    }
    
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

	public String getEndPoints() {
		return endPoints;
	}

	public Boolean getMvnOnly() {
		return mvnOnly;
	}

	public Boolean getSuccessfulOnly() {
		return successfulOnly;
	}



	@Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            super(GavNotifier.class);
        }
        
        @Override
        public String getDisplayName() {
            return "Job Notifier";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> item) {
            return true;
        }
        
        public FormValidation doCheckEndPoints(@QueryParameter String value) {
            if (value == null || value.isEmpty()){
                return FormValidation.error("Value cannot be blank");
            }
            return FormValidation.ok();
        }
        
    }

}