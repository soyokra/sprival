"""
Elasticsearch 角色管理。
"""
import json
from pathlib import Path
from typing import Dict, Any
from es_setup.client import ESClient


def ensure_role(client: ESClient, role_name: str, role_body: Dict[str, Any]) -> bool:
    """
    确保 Elasticsearch 角色存在并更新。

    Args:
        client: Elasticsearch 客户端
        role_name: 角色名称
        role_body: 角色定义（字典格式）

    Returns:
        True 如果成功，否则 False
    """
    try:
        client.get_client().security.put_role(name=role_name, body=role_body)
        return True
    except Exception as e:
        print(f"\n{str(e)}\n")
        return False


def load_role_from_file(roles_dir: Path, role_file: str) -> Dict[str, Any]:
    """
    从文件加载角色定义。

    Args:
        roles_dir: 角色文件目录
        role_file: 角色文件名

    Returns:
        角色定义字典
    """
    role_path = roles_dir / role_file
    with open(role_path, "r", encoding="utf-8") as f:
        return json.load(f)

