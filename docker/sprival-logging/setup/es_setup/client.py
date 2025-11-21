"""
Elasticsearch client wrapper.
"""
import os
from typing import Optional
from elasticsearch import Elasticsearch


class ESClient:
    """Elasticsearch client wrapper with authentication support."""

    def __init__(self, host: Optional[str] = None, password: Optional[str] = None):
        """
        初始化 Elasticsearch 客户端。

        Args:
            host: Elasticsearch 主机地址，默认为环境变量 ELASTICSEARCH_HOST 或 'elasticsearch'
            password: Elasticsearch 密码，默认为环境变量 ELASTIC_PASSWORD
        """
        self.host = host or os.getenv("ELASTICSEARCH_HOST", "elasticsearch")
        self.password = password or os.getenv("ELASTIC_PASSWORD", "")
        self.url = f"http://{self.host}:9200"

        # 构建认证信息
        auth = None
        if self.password:
            auth = ("elastic", self.password)

        self.client = Elasticsearch(
            [self.url],
            basic_auth=auth,
            request_timeout=15,
            max_retries=3,
        )

    def ping(self) -> bool:
        """
        检查 Elasticsearch 是否可用。

        Returns:
            True 如果 Elasticsearch 可用，否则 False
        """
        try:
            return self.client.ping()
        except Exception:
            return False

    def get_client(self) -> Elasticsearch:
        """
        获取 Elasticsearch 客户端实例。

        Returns:
            Elasticsearch 客户端实例
        """
        return self.client

