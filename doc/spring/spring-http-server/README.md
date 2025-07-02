## 监控指标

```plaintext
http_server_requests_seconds_count
http_server_requests_seconds_max
http_server_requests_seconds_sum
```



#### PromQL
QPS
```PromQL
# top 10 qps
topk(10, sum(rate(http_server_requests_seconds_count[1m])) by (uri))
```