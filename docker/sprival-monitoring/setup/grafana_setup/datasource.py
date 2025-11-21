"""
Grafana 数据源管理。
"""
from typing import Optional
from grafana_setup.client import GrafanaClient


def get_datasource_by_name(client: GrafanaClient, name: str) -> Optional[dict]:
    """
    根据名称获取数据源。

    Args:
        client: Grafana 客户端
        name: 数据源名称

    Returns:
        数据源信息字典，如果不存在则返回 None
    """
    try:
        # 获取所有数据源，然后根据名称过滤
        response = client.get("/api/datasources")
        if response.status_code == 200:
            datasources = response.json()
            for ds in datasources:
                if ds.get("name") == name:
                    return ds
        return None
    except Exception:
        return None


def get_datasource_by_uid(client: GrafanaClient, uid: str) -> Optional[dict]:
    """
    根据 UID 获取数据源。

    Args:
        client: Grafana 客户端
        uid: 数据源 UID

    Returns:
        数据源信息字典，如果不存在则返回 None
    """
    try:
        response = client.get(f"/api/datasources/uid/{uid}")
        if response.status_code == 200:
            return response.json()
        return None
    except Exception:
        return None


def create_datasource(client: GrafanaClient, datasource_config: dict) -> tuple[bool, Optional[str]]:
    """
    创建数据源。

    Args:
        client: Grafana 客户端
        datasource_config: 数据源配置字典

    Returns:
        (是否成功, 数据源UID或错误信息)
    """
    try:
        response = client.post("/api/datasources", json=datasource_config)
        if response.status_code == 200:
            result = response.json()
            # 处理不同的响应格式
            datasource = result.get("datasource", result)
            uid = datasource.get("uid")
            return True, uid
        else:
            error_msg = response.json().get("message", "Unknown error")
            return False, error_msg
    except Exception as e:
        return False, str(e)


def update_datasource(client: GrafanaClient, datasource_id: int, datasource_config: dict) -> tuple[bool, Optional[str]]:
    """
    更新数据源。

    Args:
        client: Grafana 客户端
        datasource_id: 数据源 ID
        datasource_config: 数据源配置字典

    Returns:
        (是否成功, 数据源UID或错误信息)
    """
    try:
        response = client.put(f"/api/datasources/{datasource_id}", json=datasource_config)
        if response.status_code == 200:
            result = response.json()
            # 处理不同的响应格式
            datasource = result.get("datasource", result)
            uid = datasource.get("uid")
            return True, uid
        else:
            error_msg = response.json().get("message", "Unknown error")
            return False, error_msg
    except Exception as e:
        return False, str(e)


def ensure_datasource(
    client: GrafanaClient,
    name: str,
    datasource_type: str,
    url: str,
    is_default: bool = True,
    access: str = "proxy",
) -> tuple[bool, Optional[str]]:
    """
    确保数据源存在，如果不存在则创建，如果存在则更新。

    Args:
        client: Grafana 客户端
        name: 数据源名称
        datasource_type: 数据源类型（如 'prometheus'）
        url: 数据源 URL
        is_default: 是否设为默认数据源
        access: 访问模式（'proxy' 或 'direct'）

    Returns:
        (是否成功, 数据源UID或错误信息)
    """
    datasource_config = {
        "name": name,
        "type": datasource_type,
        "url": url,
        "access": access,
        "isDefault": is_default,
    }

    # 检查数据源是否已存在
    existing_datasource = get_datasource_by_name(client, name)
    if existing_datasource:
        # 更新现有数据源
        datasource_id = existing_datasource.get("id")
        existing_uid = existing_datasource.get("uid")
        if datasource_id:
            # 保留现有 UID
            datasource_config["uid"] = existing_uid
            success, result = update_datasource(client, datasource_id, datasource_config)
            # 如果更新成功但返回的UID为空，使用现有的UID
            if success and not result:
                return True, existing_uid
            return success, result
    else:
        # 创建新数据源
        return create_datasource(client, datasource_config)

