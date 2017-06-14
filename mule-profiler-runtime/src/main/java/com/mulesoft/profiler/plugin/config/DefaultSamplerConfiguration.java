/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.config;


public class DefaultSamplerConfiguration implements SamplerConfiguration {

  @Override
  public long threshold() {
    return Long.getLong("com.mulesoft.profiler.threshold", 1000);
  }

}
