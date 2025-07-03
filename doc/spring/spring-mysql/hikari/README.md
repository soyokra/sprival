## 集成使用

## 监控指标

参考文档：https://docs.datadoghq.com/integrations/hikaricp/

```text
hikaricp_connections                         
hikaricp_connections_max  
hikaricp_connections_min           
hikaricp_connections_active        
hikaricp_connections_pending           
hikaricp_connections_idle           
hikaricp_connections_acquire_seconds_count           
hikaricp_connections_acquire_seconds_sum           
hikaricp_connections_acquire_seconds_max    
hikaricp_connections_creation_seconds_count     
hikaricp_connections_creation_seconds_sum     
hikaricp_connections_creation_seconds_max   
hikaricp_connections_usage_seconds_count           
hikaricp_connections_usage_seconds_sum           
hikaricp_connections_usage_seconds_max 
hikaricp_connections_timeout_total    
```

| 指标名称                                        | 类型    | 含义                                                  |
| ------------------------------------------- | ----- | --------------------------------------------------- |
| hikaricp_connections                        | gauge | Number of connections                               |
| hikaricp_connections_max                    | gauge | Max number of connections Shown as connection       |
| hikaricp_connections_min                    | gauge | Min number of connections  Shown as connection      |
| hikaricp_connections_active                 | gauge | Number of active connections                        |
| hikaricp_connections_pending                | gauge | Number of pending connections                       |
| hikaricp_connections_idle                   | gauge | Number of idle connections                          |
| hikaricp_connections_acquire_seconds_count  | count | Count of acquire connection time Shown as second    |
| hikaricp_connections_acquire_seconds_sum    | count | Sum of acquire connection time  Shown as second     |
| hikaricp_connections_acquire_seconds_max    | gauge | Max of acquire connection time  Shown as second     |
| hikaricp_connections_creation_seconds_count | count | Count of creation connection time   Shown as second |
| hikaricp_connections_creation_seconds_sum   | count | Sum of creation connection time   Shown as second   |
| hikaricp_connections_creation_seconds_max   | gauge | Max of creation connection time   Shown as second   |
| hikaricp_connections_usage_seconds_count    | count | Count of usage connection time    Shown as second   |
| hikaricp_connections_usage_seconds_sum      | count | Sum of usage connection time   Shown as second      |
| hikaricp_connections_usage_seconds_max      | gauge | Max of usage connection time    Shown as second     |
| hikaricp_connections_timeout_total          | count | Total number of timeout connections                 |

#### PromQL

连接池基本配置指标

```PromQL
#总连接数
sum(hikaricp_connections) by (job)

#最大连接数
sum(hikaricp_connections_max) by (job)

#最小空闲连接数
sum(hikaricp_connections_min) by (job)

#活跃连接数
sum(hikaricp_connections_active) by (job)

#等待获取连接的线程数
sum(hikaricp_connections_pending) by (job)

#空闲的连接数
sum(hikaricp_connections_idle) by (job)

# 监控连接池利用率
avg(hikaricp_connections_active / hikaricp_connections_max * 100) by (job)
```

连接获取性能指标

```PromQL
# 获取连接操作的总次数
hikaricp_connections_acquire_seconds_count

# 有获取连接操作的总耗时（秒）
hikaricp_connections_acquire_seconds_sum

# 获取连接操作的最大耗时（秒）
hikaricp_connections_acquire_seconds_max

# 连接平均获取时长（秒）
avg(rate(hikaricp_connections_acquire_seconds_sum[5m]) / rate(hikaricp_connections_acquire_seconds_count[5m])) by (job)

# 最大获取时长（秒）
avg(hikaricp_connections_acquire_seconds_max) by (job)
```

连接创建性能

```PromQL
创建连接的总次数
hikaricp_connections_creation_seconds_count

所有连接创建操作的总耗时
hikaricp_connections_creation_seconds_sum

连接创建操作的最大耗时（秒）
hikaricp_connections_creation_seconds_max

# 连接创建平均时长（秒）
avg(rate(hikaricp_connections_creation_seconds_sum[5m]) / rate(hikaricp_connections_creation_seconds_count[5m])) by (job)

# 连接创建最大时长（秒）
avg(hikaricp_connections_creation_seconds_max) by (job)
```

连接使用性能指标

```PromQL
# 连接使用操作的总次数
hikaricp_connections_usage_seconds_count

# 所有连接使用操作的总耗时（秒）
hikaricp_connections_usage_seconds_sum

# 连接使用操作的最大耗时（秒）
hikaricp_connections_usage_seconds_max

# 连接使用操作的平均时长（秒）
avg(rate(hikaricp_connections_usage_seconds_sum[5m]) / rate(hikaricp_connections_usage_seconds_count[5m])) by (job)

# 连接使用操作的最大时长（秒）
avg(hikaricp_connections_usage_seconds_max) by (job)
```

连接超时指标

```PromQL
# 获取连接超时的总次数
hikaricp_connections_timeout_total

# 超时情况
avg(rate(hikaricp_connections_timeout_total[5m])) by (job)
```

## 源码分析