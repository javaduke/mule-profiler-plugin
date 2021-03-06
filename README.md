# MuleProfiler

This plugins monitors/profile the execution of a flow. It allows to generate alerts when it throughput is bigger than a threshold and also dump some execution metrics.
The user can filter what app and also what MP wants to be monitored

The profiler info is being dropped inside the logs folder

* profiler_metrics.json
* profiler_event.log

## Configuration properties
 
  | Property                          | Default       | Description  |
  | --------------------------        |:-------------:| -----:|
  | -M-Dcom.mulesoft.profiler.enabled | false         | Enable the plugin |
  | -M-Dcom.mulesoft.profiler.alerts.disabled | false         | Disable threshold alerts |
  | -M-Dcom.mulesoft.profiler.mp.path     | "" | The list of Message Processor paths separated by , to profile. If nothing is specified then all the mps are profiled |
  | -M-Dcom.mulesoft.profiler.mp.class     | "" | The list of Message Processor Class Names separated by , to profile. If nothing is specified then all the mps are profiled |
  | -M-Dcom.mulesoft.profiler.apps     | "" | The list of app names separated by , to profile. If nothing is specified then all the apps are profiled |
  | -M-Dcom.mulesoft.profiler.threshold | 1000 | If a MP takes more time than this it will dump |
  | -M-Dcom.mulesoft.profiler.metrics.enabled | false | Enables metrics on the plugin. Collects total time and hit count of each MP |
  | -M-Dcom.mulesoft.profiler.metrics.interval | 1000 | Metrics dump interval |
  | -M-Dcom.mulesoft.profiler.ringbuffersize | 4096 | The size of the buffer of the events that needs to be dispatched to the profiler.  |
  
## Instalation

Just download the zip from the releases and drop it into the plugins directory

## Problems

If too many events are being dropped try increasing the ringbuffersize or try to filter the mp that are more interested.
