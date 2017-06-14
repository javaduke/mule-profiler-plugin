/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;


import java.io.Serializable;

public class ProfilerEventData implements Serializable{

  private Long startTime;
  private String path;
  private String appName;
  private String eventType;
  private String eventId;
  private int action;
  private String docName;


  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public ProfilerEventData() {
  }

  public Long getTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }


  public void setAction(int action) {
    this.action = action;
  }

  public int getAction() {
    return action;
  }

  @Override
  public String toString() {
    return "ProfilerEventData{" +
            "startTime=" + startTime +
            ", path='" + path + '\'' +
            ", appName='" + appName + '\'' +
            ", eventType='" + eventType + '\'' +
            ", eventId='" + eventId + '\'' +
            ", action=" + action +
            '}';
  }

  public void setDocName(String docName) {
    this.docName = docName;
  }

  public String getDocName() {
    return docName;
  }
}
