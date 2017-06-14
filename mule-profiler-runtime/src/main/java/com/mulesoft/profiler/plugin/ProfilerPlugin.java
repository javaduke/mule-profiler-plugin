/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


import com.mulesoft.mule.plugin.MulePlugin;
import com.mulesoft.mule.plugin.processor.deployment.DeploymentListenerProvider;
import com.mulesoft.profiler.plugin.config.DefaultProfilerConfiguration;
import com.mulesoft.profiler.plugin.config.ProfilerConfiguration;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.launcher.DeploymentListener;

import java.io.File;

public class ProfilerPlugin implements MulePlugin, DeploymentListenerProvider {

  private ProfilerDeploymentListenerService profilerDeploymentListenerService;
  private File workingDirectory;
  private ProfilerConfiguration configuration;

  @Override
  public void setWorkingDirectory(File workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  @Override
  public boolean isDisabledOnEnvironment() {
    return !configuration.isEnabled();
  }

  @Override
  public DeploymentListener getDeploymentListener() {
    return profilerDeploymentListenerService;
  }

  @Override
  public void dispose() {

  }

  @Override
  public void initialise() throws InitialisationException {
    //TODO load configuration from a config metricsFile in the /conf directory

    configuration = new DefaultProfilerConfiguration();
    profilerDeploymentListenerService = new ProfilerDeploymentListenerService(configuration);
  }

  @Override
  public void start() throws MuleException {
    System.out.println("[PROFILER] Profiler is ON starting profiler plugin.");
  }

  @Override
  public void stop() throws MuleException {

  }


}
