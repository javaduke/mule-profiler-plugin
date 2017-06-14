/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

import static org.apache.logging.log4j.core.layout.PatternLayout.createLayout;

public class Log4JDataHandlerImpl implements AlertDataHandler, MetricsDataHandler {

  private final static Logger LOGGER = LoggerFactory.getLogger(Log4JDataHandlerImpl.class);

  public static String PATTERN_LAYOUT = "%m%n";

  private String className = this.getClass().getName();
  private String loggerName = className + "." + "logger";
  private String appenderName = className + "." + "appender";
  private String contextName = className + "." + "context";
  private ObjectMapper objectMapper;
  public int daysTrigger = 1;
  public int mbTrigger = 100;
  private org.apache.logging.log4j.core.Logger internalLogger;

  public Log4JDataHandlerImpl() {
    initializeLog();
  }

  public void initializeLog() {

    final LoggerContext logContext = new LoggerContext(contextName);
    final Configuration logConfiguration = logContext.getConfiguration();
    final Layout<? extends Serializable> layout = createLayout(PATTERN_LAYOUT, null, null, null, true, true, null, null);
    final String dayTrigger = TimeUnit.DAYS.toMillis(this.daysTrigger) + "";
    final String sizeTrigger = (this.mbTrigger * 1024 * 1024) + "";
    final TimeBasedTriggeringPolicy timeBasedTriggeringPolicy = TimeBasedTriggeringPolicy.createPolicy(dayTrigger, "true");
    final SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = SizeBasedTriggeringPolicy.createPolicy(sizeTrigger);
    final CompositeTriggeringPolicy policy = CompositeTriggeringPolicy.createPolicy(timeBasedTriggeringPolicy, sizeBasedTriggeringPolicy);
    final DefaultRolloverStrategy strategy = DefaultRolloverStrategy.createStrategy("7", "1", "7",
            Deflater.DEFAULT_COMPRESSION + "", logConfiguration);
    final String fileName = getLogFileName();
    final String filePattern = getFilePattern();
    final Appender appender = RollingRandomAccessFileAppender.createAppender(fileName, filePattern, "true",
            this.appenderName, "true", "",
            policy, strategy, layout, null, "false", null, null, logConfiguration);
    appender.start();
    final AppenderRef[] ref = new AppenderRef[]{};
    final LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.INFO, this.loggerName, "false", ref, null, null, null);
    loggerConfig.addAppender(appender, null, null);
    logConfiguration.addLogger(this.loggerName, loggerConfig);
    this.internalLogger = logContext.getLogger(this.loggerName);
    this.objectMapper = new ObjectMapper();
  }


  private String getFilePattern() {
    return System.getProperty("mule.home") + File.separator + "logs" + File.separator + "profiler_events-%d{yyyy-dd-MM}-%i.log";
  }

  private String getLogFileName() {
    return System.getProperty("mule.home") + File.separator + "logs" + File.separator + "profiler_event.log";
  }

  @Override
  public void handle(AlertEventData data) {
    String message;
    try {
      message = objectMapper.writeValueAsString(data);
      internalLogger.log(Level.INFO, message);
    } catch (JsonProcessingException e) {
      LOGGER.error("Error while writing json", e);
    }
  }

  @Override
  public void handle(ArrayList<MetricsEventData> data) {
    String message;
    try {
      message = objectMapper.writeValueAsString(data);
      internalLogger.log(Level.INFO, message);
    } catch (JsonProcessingException e) {
      LOGGER.error("Error while writing json", e);
    }
  }
}
