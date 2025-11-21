FROM python:3.11-slim

WORKDIR /app

# 复制依赖文件
COPY requirements.txt .

# 安装 Python 依赖
RUN pip install --no-cache-dir -r requirements.txt

# 复制应用代码
COPY main.py .
COPY grafana_setup/ ./grafana_setup/
COPY dashboard-config/ ./dashboard-config/

# 设置入口点
ENTRYPOINT ["python", "main.py"]

