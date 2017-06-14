/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.config;


import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultProfilerConfiguration implements ProfilerConfiguration {

  public static final String PROFILER_ENABLED = "com.mulesoft.profiler.enabled";
  public static final String PROFILER_APPS = "com.mulesoft.profiler.apps";

  @Override
  public boolean isEnabled() {
    return Boolean.getBoolean(PROFILER_ENABLED);
  }

  @Override
  public boolean profileAllApps() {
    return StringUtils.isBlank(getProfilerApps());
  }

  @Override
  public List<String> getAppsToProfile() {
    if (StringUtils.isBlank(getProfilerApps())) {
      return Collections.emptyList();
    } else {
      return Arrays.asList(getProfilerApps().split(","));
    }
  }

  @Override
  public AppProfilingConfiguration getAppProfiler(String appName, MuleContext context) {
    return new DefaultAppProfilingConfiguration(appName, context);
  }

  private String getProfilerApps() {
    return System.getProperty(PROFILER_APPS);
  }

}
