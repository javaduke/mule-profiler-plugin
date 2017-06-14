# MuleProfiler

This plugins monitors/profile the execution of a flow. It allows to generate alerts when it throughput is bigger than a threshold and also dump some execution metrics.
The user can filter what app and also what MP wants to be monitored

## Configuration properties
 
  | Property                          | Default       | Description  |
  | --------------------------        |:-------------:| -----:|
  | -M-Dcom.mulesoft.profiler.enabled | false         | Enable the plugin |
  | -M-Dcom.mulesoft.profiler.alerts.disabled | false         | Disable threshold alerts |
  | -M-Dcom.mulesoft.profiler.mps     | "" | The list of Message Processor paths separated by , to profile. If nothing is specified then all the mps are profiled |
  | -M-Dcom.mulesoft.profiler.apps     | "" | The list of app names separated by , to profile. If nothing is specified then all the apps are profiled |
  | -M-Dcom.mulesoft.profiler.threshold | 1000 | If a MP takes more time than this it will dump |
  | -M-Dcom.mulesoft.profiler.metrics.enabled | false | Enables metrics on the plugin. Collects total time and hit count of each MP |
  | -M-Dcom.mulesoft.profiler.metrics.interval | 1000 | Metrics dump interval |
