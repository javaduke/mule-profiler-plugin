/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


public class MetricsEventData {

  private String path;
  private long enlapsedTime = 0;
  private long counts = 0;
  private String type = "Metric";

  public MetricsEventData(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public long getEnlapsedTime() {
    return enlapsedTime;
  }

  public long getCounts() {
    return counts;
  }

  public void consumed(long enlapsedTime) {
    this.enlapsedTime = this.enlapsedTime + enlapsedTime;
  }

  public void hit() {
    this.counts = counts + 1;
  }
}
