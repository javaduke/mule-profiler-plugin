/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;


public class DefaultMetricsDataHandler implements MetricsDataHandler {

  private final static Logger LOGGER = LoggerFactory.getLogger(DefaultMetricsDataHandler.class);

  private ObjectMapper objectMapper;
  private File metricsFile;

  public DefaultMetricsDataHandler() {
    objectMapper = new ObjectMapper();
    metricsFile = new File(metricsFileName());
  }

  private String metricsFileName() {
    return System.getProperty("mule.home") + File.separator + "logs" + File.separator + "profiler_metrics.json";
  }

  @Override
  public void close() {
  }

  @Override
  public void handle(ArrayList<MetricsEventData> data) {
    String message;
    try {
      message = objectMapper.writeValueAsString(data);
      try (Writer writer = new BufferedWriter(new FileWriter(metricsFile))) {
        writer.write(message);
      }
    } catch (Exception e) {
      LOGGER.error("Error while writing json", e);
    }
  }
}
