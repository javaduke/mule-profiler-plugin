/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.utils;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.inference.TestUtils;


public abstract class AbstractStatistics implements Statistics {
  private static final long serialVersionUID = 1536835581997509117L;

  public AbstractStatistics() {
  }

  public double[] getConfidenceIntervalAt(double confidence) {
    double[] interval = new double[2];
    if(this.getN() <= 2L) {
      interval[0] = interval[1] = 0.0D / 0.0;
      return interval;
    } else {
      TDistribution tDist = new TDistribution((double)(this.getN() - 1L));
      double a = tDist.inverseCumulativeProbability(1.0D - (1.0D - confidence) / 2.0D);
      interval[0] = this.getMean() - a * this.getStandardDeviation() / Math.sqrt((double)this.getN());
      interval[1] = this.getMean() + a * this.getStandardDeviation() / Math.sqrt((double)this.getN());
      return interval;
    }
  }

  public boolean isDifferent(Statistics other, double confidence) {
    return TestUtils.tTest(this, other, 1.0D - confidence);
  }

  public double getMeanErrorAt(double confidence) {
    if(this.getN() <= 2L) {
      return 0.0D / 0.0;
    } else {
      TDistribution tDist = new TDistribution((double)(this.getN() - 1L));
      double a = tDist.inverseCumulativeProbability(1.0D - (1.0D - confidence) / 2.0D);
      return a * this.getStandardDeviation() / Math.sqrt((double)this.getN());
    }
  }

  public String toString() {
    return "N:" + this.getN() + " Mean: " + this.getMean() + " Min: " + this.getMin() + " Max: " + this.getMax() + " StdDev: " + this.getStandardDeviation();
  }

  public double getMean() {
    return this.getN() > 0L?this.getSum() / (double)this.getN():0.0D / 0.0;
  }

  public double getStandardDeviation() {
    return Math.sqrt(this.getVariance());
  }

  public int compareTo(Statistics other, double confidence) {
    if(this.isDifferent(other, confidence)) {
      double t = this.getMean();
      double o = other.getMean();
      return t > o?-1:1;
    } else {
      return 0;
    }
  }

  public int compareTo(Statistics other) {
    return this.compareTo(other, 0.99D);
  }
}
