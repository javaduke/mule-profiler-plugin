/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mulesoft.profiler.plugin.utils;

import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

public interface Statistics extends Serializable, StatisticalSummary, Comparable<Statistics> {
  double[] getConfidenceIntervalAt(double var1);

  double getMeanErrorAt(double var1);

  boolean isDifferent(Statistics var1, double var2);

  int compareTo(Statistics var1);

  int compareTo(Statistics var1, double var2);

  double getMax();

  double getMin();

  double getMean();

  long getN();

  double getSum();

  double getStandardDeviation();

  double getVariance();

  double getPercentile(double var1);

  int[] getHistogram(double[] var1);
}
