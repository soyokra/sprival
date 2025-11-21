"""
Elasticsearch 用户管理。
"""
import os
from typing import Dict, Optional
from es_setup.client import ESClient


def check_user_exists(client: ESClient, username: str) -> bool:
    """
    检查用户是否存在。

    Args:
        client: Elasticsearch 客户端
        username: 用户名

    Returns:
        True 如果用户存在，否则 False
    """
    try:
        client.get_client().security.get_user(username=username)
        return True
    except Exception:
        return False


def create_user(client: ESClient, username: str, password: str, role: str) -> bool:
    """
    创建 Elasticsearch 用户。

    Args:
        client: Elasticsearch 客户端
        username: 用户名
        password: 密码
        role: 角色名称

    Returns:
        True 如果成功，否则 False
    """
    try:
        client.get_client().security.put_user(
            username=username, body={"password": password, "roles": [role]}
        )
        return True
    except Exception as e:
        print(f"\n{str(e)}\n")
        return False


def set_user_password(client: ESClient, username: str, password: str) -> bool:
    """
    设置用户密码。

    Args:
        client: Elasticsearch 客户端
        username: 用户名
        password: 新密码

    Returns:
        True 如果成功，否则 False
    """
    try:
        client.get_client().security.change_password(
            username=username, body={"password": password}
        )
        return True
    except Exception as e:
        print(f"\n{str(e)}\n")
        return False


def get_user_password_from_env(username: str) -> Optional[str]:
    """
    从环境变量获取用户密码。

    密码环境变量命名规则：{USERNAME}_PASSWORD（大写）

    Args:
        username: 用户名

    Returns:
        密码字符串，如果未设置则返回 None
    """
    env_key = f"{username.upper().replace('-', '_')}_PASSWORD"
    return os.getenv(env_key)


# 用户配置映射
USERS_PASSWORDS = {
    "logstash_internal": "LOGSTASH_INTERNAL_PASSWORD",
    "kibana_system": "KIBANA_SYSTEM_PASSWORD",
    "metricbeat_internal": "METRICBEAT_INTERNAL_PASSWORD",
    "filebeat_internal": "FILEBEAT_INTERNAL_PASSWORD",
    "heartbeat_internal": "HEARTBEAT_INTERNAL_PASSWORD",
    "monitoring_internal": "MONITORING_INTERNAL_PASSWORD",
    "beats_system": "BEATS_SYSTEM_PASSWORD",
}

USERS_ROLES = {
    "logstash_internal": "logstash_writer",
    "metricbeat_internal": "metricbeat_writer",
    "filebeat_internal": "filebeat_writer",
    "heartbeat_internal": "heartbeat_writer",
    "monitoring_internal": "remote_monitoring_collector",
}

