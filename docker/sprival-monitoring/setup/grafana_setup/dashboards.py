"""
Grafana Dashboard 管理。
"""
import json
from pathlib import Path
from typing import Optional
from grafana_setup.client import GrafanaClient


def load_dashboard_from_file(dashboards_dir: Path, dashboard_file: str) -> dict:
    """
    从 JSON 文件加载 Dashboard 配置。

    Args:
        dashboards_dir: Dashboard 配置目录
        dashboard_file: Dashboard 文件名

    Returns:
        Dashboard 配置字典

    Raises:
        FileNotFoundError: 如果文件不存在
        json.JSONDecodeError: 如果 JSON 格式错误
    """
    dashboard_path = dashboards_dir / dashboard_file
    if not dashboard_path.exists():
        raise FileNotFoundError(f"Dashboard file not found: {dashboard_path}")

    with open(dashboard_path, "r", encoding="utf-8") as f:
        return json.load(f)


def replace_datasource_variable(dashboard: dict, datasource_uid: str) -> dict:
    """
    递归替换 Dashboard 中的 datasource 变量。

    Args:
        dashboard: Dashboard 配置字典
        datasource_uid: 数据源 UID

    Returns:
        替换后的 Dashboard 配置字典
    """
    if isinstance(dashboard, dict):
        result = {}
        for key, value in dashboard.items():
            if key == "datasource" and isinstance(value, dict):
                # 替换 datasource 配置中的 UID
                new_datasource = value.copy()
                if new_datasource.get("uid") == "${datasource}":
                    new_datasource["uid"] = datasource_uid
                result[key] = new_datasource
            else:
                result[key] = replace_datasource_variable(value, datasource_uid)
        return result
    elif isinstance(dashboard, list):
        return [replace_datasource_variable(item, datasource_uid) for item in dashboard]
    else:
        return dashboard


def import_dashboard(client: GrafanaClient, dashboard_config: dict, overwrite: bool = True) -> tuple[bool, Optional[str]]:
    """
    导入 Dashboard。

    Args:
        client: Grafana 客户端
        dashboard_config: Dashboard 配置字典
        overwrite: 如果 Dashboard 已存在是否覆盖

    Returns:
        (是否成功, Dashboard UID或错误信息)
    """
    try:
        # 准备导入数据
        import_data = {
            "dashboard": dashboard_config,
            "overwrite": overwrite,
        }

        # 清除可能导致冲突的字段
        if "id" in import_data["dashboard"]:
            import_data["dashboard"]["id"] = None
        if "version" in import_data["dashboard"]:
            import_data["dashboard"]["version"] = 0

        response = client.post("/api/dashboards/db", json=import_data)
        if response.status_code == 200:
            result = response.json()
            return True, result.get("uid")
        else:
            error_msg = response.json().get("message", "Unknown error")
            return False, error_msg
    except Exception as e:
        return False, str(e)


def ensure_dashboard(
    client: GrafanaClient,
    dashboards_dir: Path,
    dashboard_file: str,
    datasource_uid: str,
) -> tuple[bool, Optional[str]]:
    """
    确保 Dashboard 已导入，如果不存在则导入，如果存在则更新。

    Args:
        client: Grafana 客户端
        dashboards_dir: Dashboard 配置目录
        dashboard_file: Dashboard 文件名
        datasource_uid: 数据源 UID，用于替换变量

    Returns:
        (是否成功, Dashboard UID或错误信息)
    """
    try:
        # 加载 Dashboard 配置
        dashboard_config = load_dashboard_from_file(dashboards_dir, dashboard_file)

        # 替换 datasource 变量
        dashboard_config = replace_datasource_variable(dashboard_config, datasource_uid)

        # 导入 Dashboard
        return import_dashboard(client, dashboard_config, overwrite=True)
    except FileNotFoundError as e:
        return False, str(e)
    except json.JSONDecodeError as e:
        return False, f"Invalid JSON format: {e}"
    except Exception as e:
        return False, str(e)

