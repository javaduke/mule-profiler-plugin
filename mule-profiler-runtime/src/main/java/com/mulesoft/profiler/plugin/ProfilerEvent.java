/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


public class ProfilerEvent {

  private ProfilerEventData data;

  public ProfilerEvent() {
  }

  public void set(ProfilerEventData data) {
    this.data = data;
  }


  public ProfilerEventData get() {
    return data;
  }
}
