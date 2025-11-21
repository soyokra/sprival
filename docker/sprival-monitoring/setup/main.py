#!/usr/bin/env python3
"""
Grafana 初始化脚本。

功能：
1. 等待 Grafana 启动
2. 添加 Prometheus 数据源
3. 导入 Dashboard 配置
"""
import os
import sys
from pathlib import Path
from grafana_setup.client import GrafanaClient
from grafana_setup.wait import wait_for_grafana
from grafana_setup.datasource import ensure_datasource
from grafana_setup.dashboards import ensure_dashboard


def log(message: str) -> None:
    """输出日志消息。"""
    print(f"[+] {message}")


def sublog(message: str) -> None:
    """输出子级日志消息。"""
    print(f"   ⠿ {message}")


def suberr(message: str) -> None:
    """输出错误消息。"""
    print(f"   ⠍ {message}", file=sys.stderr)


def main() -> int:
    """主函数。"""
    # 获取脚本所在目录
    script_dir = Path(__file__).parent
    dashboards_dir = script_dir / "dashboard-config"

    # 初始化客户端
    client = GrafanaClient()

    # 等待 Grafana 启动
    log("Waiting for availability of Grafana. This can take several minutes.")
    if not wait_for_grafana(client=client):
        suberr("Could not connect to Grafana. Is Grafana running?")
        return 1

    sublog("Grafana is running")

    # 添加 Prometheus 数据源
    prometheus_url = os.getenv("PROMETHEUS_URL", "http://prometheus:9090")
    log(f"Data source 'Prometheus' ({prometheus_url})")

    try:
        success, result = ensure_datasource(
            client=client,
            name="Prometheus",
            datasource_type="prometheus",
            url=prometheus_url,
            is_default=True,
            access="proxy",
        )

        if not success:
            suberr(f"Failed to create/update data source: {result}")
            return 1

        datasource_uid = result
        sublog(f"Data source created/updated with UID: {datasource_uid}")
    except Exception as e:
        suberr(f"Failed to process data source: {e}")
        return 1

    # 导入 Dashboard 配置
    dashboard_files = [
        "grafana-jvm-dashboard.json",
        "grafana-feign-dashboard.json",
        "grafana-jetty-dashboard.json",
        "grafana-hikaricp-dashboard.json",
        "grafana-lettuce-dashboard.json",
        "grafana-rabbitmq-dashboard.json",
    ]

    for dashboard_file in dashboard_files:
        log(f"Dashboard '{dashboard_file}'")

        dashboard_path = dashboards_dir / dashboard_file
        if not dashboard_path.exists():
            sublog(f"No dashboard file found at '{dashboard_path}', skipping")
            continue

        try:
            success, result = ensure_dashboard(
                client=client,
                dashboards_dir=dashboards_dir,
                dashboard_file=dashboard_file,
                datasource_uid=datasource_uid,
            )

            if not success:
                suberr(f"Failed to import dashboard: {result}")
                return 1

            dashboard_uid = result
            sublog(f"Dashboard imported with UID: {dashboard_uid}")
        except Exception as e:
            suberr(f"Failed to process dashboard '{dashboard_file}': {e}")
            return 1

    return 0


if __name__ == "__main__":
    sys.exit(main())

