/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin.filter;

import org.mule.context.notification.MessageProcessorNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class MessageProcessorClassFilter implements NotificationFilter {

  private final static Logger LOGGER = LoggerFactory.getLogger(MessageProcessorClassFilter.class);

  private List<Class<?>> classes;

  public MessageProcessorClassFilter(List<Class<?>> classes) {
    this.classes = classes;
  }

  @Override
  public boolean acceptsNotifications(MessageProcessorNotification notification) {
    for (Class<?> validMP : classes) {
      if (validMP.isInstance(notification.getProcessor())) {
        return true;
      }
    }
    return false;
  }
}
