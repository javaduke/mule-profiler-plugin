# MuleProfiler

This plugin will dump the message processors info that takes more time than a specified threshold

## Configuration properties
 
  | Property                          | Default       | Description  |
  | --------------------------        |:-------------:| -----:|
  | -M-Dcom.mulesoft.profiler.enabled | false         | Enable the plugin |
  | -M-Dom.mulesoft.profiler.apps     | "" | The list of app names separated by , to profile. If nothing is specified then all the apps are profiled |
  | -M-Dcom.mulesoft.profiler.threshold | 1000 | If a MP takes more time than this it will dump |
  | -M-Dcom.mulesoft.profiler.metrics.enabled | false | Enables metrics on the plugin. Collects total time and hit count of each MP |
  | -M-Dcom.mulesoft.profiler.metrics.interval | 1000 | Metrics dump interval |
