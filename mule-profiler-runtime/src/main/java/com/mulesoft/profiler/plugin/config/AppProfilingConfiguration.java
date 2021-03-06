/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.config;

import com.mulesoft.profiler.plugin.filter.NotificationFilter;


public interface AppProfilingConfiguration {
  boolean enableSampler();

  boolean enableMetrics();

  MetricsConfiguration metricsConfiguration();

  SamplerConfiguration samplerConfiguration();

  NotificationFilter notificationFilter();

  int bufferSize();
}
