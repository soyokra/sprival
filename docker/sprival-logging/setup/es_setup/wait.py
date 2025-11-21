"""
等待 Elasticsearch 就绪的工具函数。
"""
import time
from typing import Optional
from es_setup.client import ESClient


def wait_for_elasticsearch(
    max_attempts: int = 60, interval: int = 5, client: Optional[ESClient] = None
) -> bool:
    """
    等待 Elasticsearch 服务可用。

    Args:
        max_attempts: 最大尝试次数，默认 60 次（5 分钟）
        interval: 每次尝试之间的间隔（秒），默认 5 秒
        client: Elasticsearch 客户端，如果为 None 则创建新客户端

    Returns:
        True 如果 Elasticsearch 可用，否则 False
    """
    if client is None:
        client = ESClient()

    for attempt in range(1, max_attempts + 1):
        if client.ping():
            return True
        if attempt < max_attempts:
            time.sleep(interval)

    return False


def wait_for_builtin_users(
    max_attempts: int = 30, interval: int = 1, client: Optional[ESClient] = None
) -> bool:
    """
    等待 Elasticsearch 内置用户初始化完成。

    Args:
        max_attempts: 最大尝试次数，默认 30 次（30 秒）
        interval: 每次尝试之间的间隔（秒），默认 1 秒
        client: Elasticsearch 客户端，如果为 None 则创建新客户端

    Returns:
        True 如果内置用户已初始化，否则 False
    """
    if client is None:
        client = ESClient()

    for attempt in range(1, max_attempts + 1):
        try:
            response = client.get_client().security.get_user()
            # 检查是否有多个内置用户（_reserved: true）
            # Elasticsearch API 返回的用户信息中，_reserved 可能在 metadata 中，也可能在顶层
            reserved_users = []
            for username, user_info in response.items():
                metadata = user_info.get("metadata", {})
                if metadata.get("_reserved", False) or user_info.get("_reserved", False):
                    reserved_users.append(username)
            # 期望有多个内置用户（至少 elastic 和其他用户）
            if len(reserved_users) > 1:
                return True
        except Exception:
            pass

        if attempt < max_attempts:
            time.sleep(interval)

    return False

