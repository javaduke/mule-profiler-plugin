/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.utils;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.Arrays;

public class ListStatistics extends AbstractStatistics {
  private static final long serialVersionUID = -90642978235578197L;
  private double[] values;
  private int count;

  public ListStatistics() {
    this.values = new double[0];
    this.count = 0;
  }

  public ListStatistics(double[] samples) {
    this();
    double[] var2 = samples;
    int var3 = samples.length;

    for (int var4 = 0; var4 < var3; ++var4) {
      double d = var2[var4];
      this.addValue(d);
    }

  }

  public ListStatistics(long[] samples) {
    this();
    long[] var2 = samples;
    int var3 = samples.length;

    for (int var4 = 0; var4 < var3; ++var4) {
      long l = var2[var4];
      this.addValue((double) l);
    }

  }


  public void addValue(double d) {
    if (this.count >= this.values.length) {
      this.values = Arrays.copyOf(this.values, Math.max(1, this.values.length << 1));
    }

    this.values[this.count] = d;
    ++this.count;
  }

  public double getMax() {
    if (this.count <= 0) {
      return 0.0D / 0.0;
    } else {
      double m = -1.0D / 0.0;

      for (int i = 0; i < this.count; ++i) {
        m = Math.max(m, this.values[i]);
      }

      return m;
    }
  }

  public double getMin() {
    if (this.count <= 0) {
      return 0.0D / 0.0;
    } else {
      double m = 1.0D / 0.0;

      for (int i = 0; i < this.count; ++i) {
        m = Math.min(m, this.values[i]);
      }

      return m;
    }
  }

  public long getN() {
    return (long) this.count;
  }

  public double getSum() {
    if (this.count <= 0) {
      return 0.0D / 0.0;
    } else {
      double s = 0.0D;

      for (int i = 0; i < this.count; ++i) {
        s += this.values[i];
      }

      return s;
    }
  }

  public double getPercentile(double rank) {
    if (this.count == 0) {
      return 0.0D / 0.0;
    } else if (rank == 0.0D) {
      return this.getMin();
    } else {
      this.values = Arrays.copyOf(this.values, this.count);
      Percentile p = new Percentile();
      return p.evaluate(this.values, rank);
    }
  }

  public int[] getHistogram(double[] levels) {
    if (levels.length < 2) {
      throw new IllegalArgumentException("Expected more than two levels");
    } else {
      double[] vs = Arrays.copyOf(this.values, this.count);
      Arrays.sort(vs);
      int[] result = new int[levels.length - 1];
      int c = 0;
      double[] var5 = vs;
      int var6 = vs.length;

      for (int var7 = 0; var7 < var6; ++var7) {
        double v = var5[var7];

        while (levels[c] > v || v >= levels[c + 1]) {
          ++c;
          if (c > levels.length - 2) {
            return result;
          }
        }

        ++result[c];
      }

      return result;
    }
  }

  public double getVariance() {
    if (this.count <= 1) {
      return 0.0D / 0.0;
    } else {
      double v = 0.0D;
      double m = this.getMean();

      for (int i = 0; i < this.count; ++i) {
        v += Math.pow(this.values[i] - m, 2.0D);
      }

      return v / (double) (this.count - 1);
    }
  }
}
