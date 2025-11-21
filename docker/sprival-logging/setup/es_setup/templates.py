"""
Elasticsearch 索引模板管理。
"""
import json
from pathlib import Path
from typing import Dict, Any
from es_setup.client import ESClient


def ensure_index_template(
    client: ESClient, template_name: str, template_body: Dict[str, Any]
) -> bool:
    """
    确保 Elasticsearch 索引模板存在并更新。

    Args:
        client: Elasticsearch 客户端
        template_name: 模板名称
        template_body: 模板定义（字典格式）

    Returns:
        True 如果成功，否则 False
    """
    try:
        client.get_client().indices.put_index_template(
            name=template_name, body=template_body
        )
        return True
    except Exception as e:
        print(f"\n{str(e)}\n")
        return False


def load_template_from_file(templates_dir: Path, template_file: str) -> Dict[str, Any]:
    """
    从文件加载索引模板定义。

    Args:
        templates_dir: 模板文件目录
        template_file: 模板文件名

    Returns:
        模板定义字典
    """
    template_path = templates_dir / template_file
    with open(template_path, "r", encoding="utf-8") as f:
        return json.load(f)

