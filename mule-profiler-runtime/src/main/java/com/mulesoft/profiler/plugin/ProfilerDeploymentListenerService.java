/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;

import org.mule.api.MuleContext;
import org.mule.module.launcher.AbstractDeploymentListener;
import org.mule.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Attaches debugger to each deployed mule application
 */
public class ProfilerDeploymentListenerService extends AbstractDeploymentListener {

  private Map<String, MuleAppProfiler> handlersByAppName;

  public ProfilerDeploymentListenerService() {
    this.handlersByAppName = new HashMap<>();
  }

  @Override
  public void onDeploymentFailure(String appName, Throwable throwable) {
    stopProfiling(appName);
  }

  @Override
  public void onUndeploymentStart(String appName) {
    stopProfiling(appName);
  }

  @Override
  public void onMuleContextInitialised(String appName, MuleContext muleContext) {
    String property = System.getProperty("com.mulesoft.profiler.apps");
    if (StringUtils.isBlank(property) || Arrays.asList(property.split(",")).contains(appName)) {y
      System.out.println("[PROFILER] Start Profiling " + appName);
      startProfiling(appName, muleContext);
    }else{
      System.out.println("[PROFILER] Ignoring Profiling " + appName);
    }
  }

  private void startProfiling(String appName, MuleContext muleContext) {
    handlersByAppName.put(appName, new MuleAppProfiler(muleContext, appName));
  }

  private void stopProfiling(String appName) {

    MuleAppProfiler muleAppProfiler = handlersByAppName.get(appName);
    if(muleAppProfiler != null){
      muleAppProfiler.stop();
    }
  }

}
