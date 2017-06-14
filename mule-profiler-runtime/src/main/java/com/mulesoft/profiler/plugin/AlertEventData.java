/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


import java.io.Serializable;

public class AlertEventData implements Serializable {

  private Long startTime;
  private Long endTime;
  private Long duration;
  private String path;
  private String appName;
  private String docName;
  private String type = "Alert";
  private String classname;

  public AlertEventData(Long startTime, Long endTime, Long duration, String path, String appName, String docName, String classname) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = duration;
    this.path = path;
    this.appName = appName;
    this.docName = docName;
    this.classname = classname;
  }

  public String getClassname() {
    return classname;
  }

  public String getDocName() {
    return docName;
  }

  public Long getStartTime() {
    return startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public Long getDuration() {
    return duration;
  }

  public String getPath() {
    return path;
  }

  public String getAppName() {
    return appName;
  }

  public String getType() {
    return type;
  }
}
