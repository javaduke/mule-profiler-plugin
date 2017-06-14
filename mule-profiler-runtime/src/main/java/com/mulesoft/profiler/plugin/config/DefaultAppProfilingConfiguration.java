/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.config;


import com.mulesoft.profiler.plugin.filter.DefaultNotificationFilter;
import com.mulesoft.profiler.plugin.filter.MessageProcessorClassFilter;
import com.mulesoft.profiler.plugin.filter.MessageProccesorPathFilter;
import com.mulesoft.profiler.plugin.filter.NotificationFilter;
import com.mulesoft.profiler.plugin.filter.OrFilter;
import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultAppProfilingConfiguration implements AppProfilingConfiguration {

  private final static Logger LOGGER = LoggerFactory.getLogger(DefaultAppProfilingConfiguration.class);

  public static final String PROFILER_MP_PATH = "com.mulesoft.profiler.mp.path";
  public static final String MP_CLASS = "com.mulesoft.profiler.mp.class";
  public static final String METRICS_ENABLED = "com.mulesoft.profiler.metrics.enabled";
  public static final String ALERTS_DISABLED = "com.mulesoft.profiler.alerts.disabled";
  private MuleContext context;

  public DefaultAppProfilingConfiguration(String appName, MuleContext context) {
    this.context = context;
  }

  @Override
  public boolean enableSampler() {
    return !Boolean.getBoolean(ALERTS_DISABLED);
  }


  @Override
  public boolean enableMetrics() {
    return Boolean.getBoolean(METRICS_ENABLED);
  }


  @Override
  public MetricsConfiguration metricsConfiguration() {
    return new DefaultMetricsConfiguration();
  }

  @Override
  public SamplerConfiguration samplerConfiguration() {
    return new DefaultSamplerConfiguration();
  }

  @Override
  public NotificationFilter notificationFilter() {
    String paths = System.getProperty(PROFILER_MP_PATH);
    String classes = System.getProperty(MP_CLASS);

    List<NotificationFilter> filterList = new ArrayList<>();
    if (!StringUtils.isBlank(paths)) {
      filterList.add(new MessageProccesorPathFilter(Arrays.asList(paths.split(","))));
    }

    if (!StringUtils.isBlank(classes)) {
      List<String> validClassNames = Arrays.asList(classes.split(","));
      List<Class<?>> validClasses = new ArrayList<>();
      for (String validClassName : validClassNames) {
        try {
          validClasses.add(context.getExecutionClassLoader().loadClass(validClassName));
        } catch (ClassNotFoundException e) {
          try {
            validClasses.add(this.getClass().getClassLoader().loadClass(validClassName));
          } catch (ClassNotFoundException e1) {
            LOGGER.error("Ignoring class " + validClassName + " as it can not be loaded.", e1);
          }
        }
      }
      filterList.add(new MessageProcessorClassFilter(validClasses));
    }

    if (filterList.isEmpty()) {
      return new DefaultNotificationFilter();
    } else if (filterList.size() == 1) {
      return filterList.get(0);
    } else {
      return new OrFilter(filterList);
    }

  }

  @Override
  public int bufferSize() {
    return Integer.getInteger("com.mulesoft.profiler.ringbuffersize", 4096);
  }

}
