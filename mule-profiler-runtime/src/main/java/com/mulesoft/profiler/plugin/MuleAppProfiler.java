/*
 * (c) 2003-2015 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.profiler.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.mule.api.MuleContext;
import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.api.context.notification.ServerNotification;
import org.mule.api.context.notification.ServerNotificationListener;
import org.mule.context.notification.MessageProcessorNotification;
import org.mule.context.notification.NotificationException;
import org.mule.context.notification.ServerNotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

import static org.apache.logging.log4j.core.layout.PatternLayout.*;


public class MuleAppProfiler {

  public static String MULE_HOME_PLACEHOLDER = "$MULE_HOME";
  public static String PATTERN_LAYOUT = "%m%n";

  private String className = this.getClass().getName();
  private String loggerName = className + "." + "logger";
  private String appenderName = className + "." + "appender";
  private String contextName = className + "." + "context";


  private final static Logger LOGGER = LoggerFactory.getLogger(MuleAppProfiler.class);

  private RingBuffer<ProfilerEvent> ringBuffer;
  private Disruptor<ProfilerEvent> disruptor;

  private String appName;

  //Logger data
  private Configuration logConfiguration;
  private LoggerConfig loggerConfig;
  private Appender appender;
  private LoggerContext logContext;
  private ObjectMapper objectMapper;
  public int daysTrigger = 1;
  public int mbTrigger = 100;
  private org.apache.logging.log4j.core.Logger internalLogger;

  public MuleAppProfiler(MuleContext muleContext, String appName) {
    this.appName = appName;
    initDisruptor();
    registerNotifications(muleContext);
    initializeLog();
  }


  public void initializeLog() {

    this.logContext = new LoggerContext(appName);
    this.logConfiguration = logContext.getConfiguration();
    Layout<? extends Serializable> layout = createLayout(PATTERN_LAYOUT, null, null, null, true, true, null, null);
    String dayTrigger = TimeUnit.DAYS.toMillis(this.daysTrigger) + "";
    String sizeTrigger = (this.mbTrigger * 1024 * 1024) + "";
    TimeBasedTriggeringPolicy timeBasedTriggeringPolicy;
    timeBasedTriggeringPolicy = TimeBasedTriggeringPolicy.createPolicy(dayTrigger, "true");
    SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = SizeBasedTriggeringPolicy.createPolicy(sizeTrigger);
    CompositeTriggeringPolicy policy = CompositeTriggeringPolicy.createPolicy(timeBasedTriggeringPolicy, sizeBasedTriggeringPolicy);
    DefaultRolloverStrategy strategy = DefaultRolloverStrategy.createStrategy("7", "1", "7",
            Deflater.DEFAULT_COMPRESSION + "", this.logConfiguration);
    String fileName = getLogFileName();
    String filePattern = getFilePattern();
    this.appender = RollingRandomAccessFileAppender.createAppender(fileName, filePattern, "true",
            this.appenderName, "true", "",
            policy, strategy, layout, null, "false", null, null, this.logConfiguration);
    this.appender.start();
    AppenderRef[] ref = new AppenderRef[]{};
    this.loggerConfig = LoggerConfig.createLogger("false", Level.INFO, this.loggerName, "false", ref, null, null, null);
    this.loggerConfig.addAppender(this.appender, null, null);
    this.logConfiguration.addLogger(this.loggerName, this.loggerConfig);
    this.internalLogger = this.logContext.getLogger(this.loggerName);
    this.objectMapper = new ObjectMapper();
  }

  private String getFilePattern() {
    return System.getProperty("mule.home") + File.separator + "logs" + File.separator + "profiler_events-%d{yyyy-dd-MM}-%i.log";
  }

  private String getLogFileName() {
    return System.getProperty("mule.home") + File.separator + "logs" + File.separator + "profiler_event.log";
  }


  private void registerNotifications(MuleContext muleContext) {
    final ServerNotificationManager notificationManager = muleContext.getNotificationManager();
    if (!notificationManager.isNotificationDynamic()) {
      notificationManager.setNotificationDynamic(true);
    }
    registerNotificationType(notificationManager, MessageProcessorNotificationListener.class, MessageProcessorNotification.class);
    //Init listeners
    DebuggerMessageProcessorNotificationListener debuggerMessageProcessorNotificationListener = new DebuggerMessageProcessorNotificationListener();
    //register listeners
    try {
      muleContext.registerListener(debuggerMessageProcessorNotificationListener);
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

  private class DebuggerMessageProcessorNotificationListener implements MessageProcessorNotificationListener<MessageProcessorNotification> {

    private DebuggerMessageProcessorNotificationListener() {
    }

    public void onNotification(MessageProcessorNotification notification) {
      final ProfilerEventData profilerEventData = new ProfilerEventData();
      profilerEventData.setPath(notification.getProcessorPath());
      profilerEventData.setEventType(notification.getType());
      profilerEventData.setAction(notification.getAction());
      profilerEventData.setAppName(appName);
      profilerEventData.setEventId(notification.getSource().getId());
      profilerEventData.setStartTime(notification.getTimestamp());
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
          if (takenTime > Long.getLong("com.mulesoft.profiler.threshold", 1000)) {
            String message;
            try {
              final AlertEventData alertEventData = new AlertEventData(previous.getTime(), current.getTime(), current.getTime() - previous.getTime(), current.getPath(), current.getAppName());
              message = objectMapper.writeValueAsString(alertEventData);
              internalLogger.log(Level.INFO, message);
            } catch (JsonProcessingException e) {
              LOGGER.error("There was an error logging the object.", e);
            }
          }
          currentEvent.remove(event.get().getEventId());
        }
      }
    }
  }

}



