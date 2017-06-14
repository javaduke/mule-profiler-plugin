/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.mulesoft.profiler.plugin.utils.ListStatistics;
import org.mule.api.AnnotatedObject;
import org.mule.api.MuleContext;
import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.api.context.notification.ServerNotification;
import org.mule.api.context.notification.ServerNotificationListener;
import org.mule.context.notification.MessageProcessorNotification;
import org.mule.context.notification.NotificationException;
import org.mule.context.notification.ServerNotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MuleAppProfiler {


  private final static Logger LOGGER = LoggerFactory.getLogger(MuleAppProfiler.class);

  private RingBuffer<ProfilerEvent> ringBuffer;
  private Disruptor<ProfilerEvent> disruptor;

  private String appName;

  private AlertDataHandler alertDataHandler;
  private MetricsDataHandler metricsDataHandler;


  public MuleAppProfiler(MuleContext muleContext, String appName) {
    this.appName = appName;
    initDisruptor();
    registerNotifications(muleContext);
    Log4JDataHandlerImpl log4JDataHandler = new Log4JDataHandlerImpl();
    this.alertDataHandler = log4JDataHandler;
    this.metricsDataHandler = log4JDataHandler;
  }


  private void registerNotifications(MuleContext muleContext) {
    final ServerNotificationManager notificationManager = muleContext.getNotificationManager();
    if (!notificationManager.isNotificationDynamic()) {
      notificationManager.setNotificationDynamic(true);
    }
    registerNotificationType(notificationManager, MessageProcessorNotificationListener.class, MessageProcessorNotification.class);
    //Init listeners
    ProfilerMessageProcessorNotificationListener profillerMessageProcessorNotificationListener = new ProfilerMessageProcessorNotificationListener();
    //register listeners
    try {
      muleContext.registerListener(profillerMessageProcessorNotificationListener);
    } catch (NotificationException e) {
      e.printStackTrace();
    }
  }

  protected final void registerNotificationType(final ServerNotificationManager notificationManager,
                                                @SuppressWarnings("rawtypes") final Class<? extends ServerNotificationListener> listenerType,
                                                final Class<? extends ServerNotification> notificationType) {
    @SuppressWarnings("rawtypes") final Map<Class<? extends ServerNotificationListener>, Set<Class<? extends ServerNotification>>> mapping = notificationManager.getInterfaceToTypes();
    if (!mapping.containsKey(listenerType)) {
      notificationManager.addInterfaceToType(listenerType, notificationType);
    }
  }

  private void initDisruptor() {
    // Executor that will be used to construct new threads for consumers
    Executor executor = Executors.newCachedThreadPool();

    // The factory for the event
    ProfilerEventFactory factory = new ProfilerEventFactory();

    // Specify the size of the ring buffer, must be power of 2.
    int bufferSize = 1024;

    // Construct the Disruptor

    disruptor = new Disruptor<>(factory, bufferSize, executor);

    // Connect the handler
    disruptor.handleEventsWith(new ProfilerEventHandler());

    // Start the Disruptor, starts all threads running
    disruptor.start();

    // Get the ring buffer from the Disruptor to be used for publishing.
    ringBuffer = disruptor.getRingBuffer();
  }

  public void stop() {
    disruptor.shutdown();
  }

  private class ProfilerMessageProcessorNotificationListener implements MessageProcessorNotificationListener<MessageProcessorNotification> {

    private ProfilerMessageProcessorNotificationListener() {
    }

    public void onNotification(MessageProcessorNotification notification) {
      final ProfilerEventData profilerEventData = new ProfilerEventData();
      profilerEventData.setPath(notification.getProcessorPath());
      profilerEventData.setEventType(notification.getType());
      profilerEventData.setAction(notification.getAction());
      profilerEventData.setAppName(appName);
      profilerEventData.setEventId(notification.getSource().getId());
      profilerEventData.setStartTime(notification.getTimestamp());

      if (notification.getProcessor() instanceof AnnotatedObject) {
        final Collection<Object> values = ((AnnotatedObject) notification.getProcessor()).getAnnotations().values();
        if (values.isEmpty()) {
          profilerEventData.setDocName(String.valueOf(values.iterator().next()));
        }
      }

      ringBuffer.publishEvent(new EventTranslator<ProfilerEvent>() {
        @Override
        public void translateTo(ProfilerEvent profilerEvent, long l) {
          profilerEvent.set(profilerEventData);
        }
      });
    }
  }

  public class ProfilerEventHandler implements EventHandler<ProfilerEvent> {

    final Map<String, ProfilerEvent> currentEvent = new HashMap<>();
    final Map<String, ListStatistics> metrics = new HashMap<>();
    long lastMetricDump = System.currentTimeMillis();

    public void onEvent(ProfilerEvent event, long sequence, boolean endOfBatch) {
      if (event.get() == null) {
        return;
      }
      if (event.get().getAction() == MessageProcessorNotification.MESSAGE_PROCESSOR_PRE_INVOKE) {
        currentEvent.put(event.get().getEventId(), event);
      } else if (event.get().getAction() == MessageProcessorNotification.MESSAGE_PROCESSOR_POST_INVOKE) {
        ProfilerEvent profilerEvent = currentEvent.get(event.get().getEventId());
        if (profilerEvent != null) {
          ProfilerEventData current = event.get();
          ProfilerEventData previous = profilerEvent.get();
          long takenTime = current.getTime() - previous.getTime();

          if (Boolean.getBoolean("com.mulesoft.profiler.alerts.disabled")) {
            if (takenTime > Long.getLong("com.mulesoft.profiler.threshold", 1000)) {
              final AlertEventData alertEventData = new AlertEventData(previous.getTime(), current.getTime(), current.getTime() - previous.getTime(), current.getPath(), current.getAppName(), current.getDocName());
              alertDataHandler.handle(alertEventData);
            }
          }

          if (Boolean.getBoolean("com.mulesoft.profiler.metrics.enabled")) {
            ListStatistics metricsEventData = metrics.get(event.get().getPath());
            if (metricsEventData == null) {
              metricsEventData = new ListStatistics();
              metrics.put(event.get().getPath(), metricsEventData);
            }
            metricsEventData.addValue(takenTime);
            if ((System.currentTimeMillis() - lastMetricDump) > Long.getLong("com.mulesoft.profiler.metrics.interval", 1000)) {
              lastMetricDump = System.currentTimeMillis();
              Set<Map.Entry<String, ListStatistics>> values = metrics.entrySet();
              ArrayList<MetricsEventData> eventData = new ArrayList<>();
              for (Map.Entry<String, ListStatistics> value : values) {
                eventData.add(new MetricsEventData(value.getKey(), value.getValue()));
              }
              metricsDataHandler.handle(eventData);
            }
          }

          currentEvent.remove(event.get().getEventId());
        }
      }
    }
  }

}



