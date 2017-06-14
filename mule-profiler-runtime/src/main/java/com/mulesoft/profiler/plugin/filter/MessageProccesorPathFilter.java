/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.filter;

import org.mule.context.notification.MessageProcessorNotification;

import java.util.List;


public class MessageProccesorPathFilter implements NotificationFilter {

  private List<String> paths;

  public MessageProccesorPathFilter(List<String> paths) {
    this.paths = paths;
  }


  @Override
  public boolean acceptsNotifications(MessageProcessorNotification notification) {

    return paths.contains(notification.getProcessorPath());
  }
}
