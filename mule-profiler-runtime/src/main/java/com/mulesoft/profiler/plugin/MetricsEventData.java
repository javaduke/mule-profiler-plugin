/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


import com.mulesoft.profiler.plugin.utils.ListStatistics;

import java.util.HashMap;
import java.util.Map;

public class MetricsEventData {

  private String path;
  private String type = "Metric";
  private ListStatistics listStatistics;


  public MetricsEventData(String path, ListStatistics listStatistics) {
    this.path = path;
    this.listStatistics = listStatistics;
  }

  public String getPath() {
    return path;
  }

  public String getType() {
    return type;
  }

  public long getN() {
    return listStatistics.getN();
  }

  public double getMin() {
    return listStatistics.getMin();
  }

  public double getMax() {
    return listStatistics.getMax();
  }

  public double getMean() {
    return listStatistics.getMean();
  }

  public double getSume() {
    return listStatistics.getSum();
  }

  public double getStdDesviation() {
    return listStatistics.getStandardDeviation();
  }

  public double getVariance() {
    return listStatistics.getVariance();
  }

  public Map<String, Double> getDistribution() {
    HashMap<String, Double> result = new HashMap<>();
    result.put("100", listStatistics.getPercentile(100));
    result.put("80", listStatistics.getPercentile(80));
    result.put("60", listStatistics.getPercentile(60));
    result.put("40", listStatistics.getPercentile(20));
    result.put("20", listStatistics.getPercentile(10));
    result.put("0", listStatistics.getPercentile(0));
    return result;
  }
}
