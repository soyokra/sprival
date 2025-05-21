## prometheus

### 数据模型
> metric_name{label_name=label_value,label_name=label_value,...} value timestamp


- 指标（metric）：指标名称和描述当前样本特征的labelsets；
- 时间戳（timestamp）：一个精确到毫秒的时间戳；
- 样本值（value）： 一个 float64 的浮点型数据表示当前样本的值。

### 指标类型
- Counter（计数器）
- Gauge（仪表盘）
- Histogram（直方图）
- Summary（摘要）