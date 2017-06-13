/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


import com.mulesoft.mule.plugin.MulePlugin;
import com.mulesoft.mule.plugin.processor.deployment.DeploymentListenerProvider;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.launcher.DeploymentListener;

import java.io.File;

public class ProfilerPlugin implements MulePlugin, DeploymentListenerProvider {

  private ProfilerDeploymentListenerService profilerDeploymentListenerService;

  @Override
  public void setWorkingDirectory(File file) {

  }

  @Override
  public boolean isDisabledOnEnvironment() {
    return !Boolean.getBoolean("com.mulesoft.profiler.enabled");
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
    profilerDeploymentListenerService = new ProfilerDeploymentListenerService();
  }

  @Override
  public void start() throws MuleException {
    System.out.println("[PROFILER] Profiler is ON starting profiler plugin.");
  }

  @Override
  public void stop() throws MuleException {

  }


}
