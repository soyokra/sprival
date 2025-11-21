"""
等待 Grafana 就绪的工具函数。
"""
import time
from typing import Optional
from grafana_setup.client import GrafanaClient


def wait_for_grafana(
    max_attempts: int = 60, interval: int = 5, client: Optional[GrafanaClient] = None
) -> bool:
    """
    等待 Grafana 服务可用。

    Args:
        max_attempts: 最大尝试次数，默认 60 次（5 分钟）
        interval: 每次尝试之间的间隔（秒），默认 5 秒
        client: Grafana 客户端，如果为 None 则创建新客户端

    Returns:
        True 如果 Grafana 可用，否则 False
    """
    if client is None:
        client = GrafanaClient()

    for attempt in range(1, max_attempts + 1):
        if client.ping():
            return True
        if attempt < max_attempts:
            time.sleep(interval)

    return False

