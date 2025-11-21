"""
Grafana API 客户端封装。
"""
import os
from typing import Optional
import requests
from requests.auth import HTTPBasicAuth


class GrafanaClient:
    """Grafana API 客户端封装，支持 Basic Auth 认证。"""

    def __init__(
        self,
        host: Optional[str] = None,
        port: Optional[int] = None,
        admin_password: Optional[str] = None,
    ):
        """
        初始化 Grafana 客户端。

        Args:
            host: Grafana 主机地址，默认为环境变量 GRAFANA_HOST 或 'grafana'
            port: Grafana 端口，默认为环境变量 GRAFANA_PORT 或 3000
            admin_password: Grafana admin 密码，默认为环境变量 GRAFANA_ADMIN_PASSWORD
        """
        self.host = host or os.getenv("GRAFANA_HOST", "grafana")
        self.port = port or int(os.getenv("GRAFANA_PORT", "3000"))
        self.admin_password = admin_password or os.getenv("GRAFANA_ADMIN_PASSWORD", "")
        self.base_url = f"http://{self.host}:{self.port}"
        self.auth = HTTPBasicAuth("admin", self.admin_password) if self.admin_password else None

    def ping(self) -> bool:
        """
        检查 Grafana 是否可用。

        Returns:
            True 如果 Grafana 可用，否则 False
        """
        try:
            response = requests.get(
                f"{self.base_url}/api/health",
                auth=self.auth,
                timeout=5,
            )
            return response.status_code == 200
        except Exception:
            return False

    def get(self, endpoint: str, **kwargs) -> requests.Response:
        """
        发送 GET 请求。

        Args:
            endpoint: API 端点路径
            **kwargs: 传递给 requests.get 的其他参数

        Returns:
            Response 对象
        """
        url = f"{self.base_url}{endpoint}"
        return requests.get(url, auth=self.auth, **kwargs)

    def post(self, endpoint: str, json: Optional[dict] = None, **kwargs) -> requests.Response:
        """
        发送 POST 请求。

        Args:
            endpoint: API 端点路径
            json: JSON 数据
            **kwargs: 传递给 requests.post 的其他参数

        Returns:
            Response 对象
        """
        url = f"{self.base_url}{endpoint}"
        return requests.post(url, json=json, auth=self.auth, **kwargs)

    def put(self, endpoint: str, json: Optional[dict] = None, **kwargs) -> requests.Response:
        """
        发送 PUT 请求。

        Args:
            endpoint: API 端点路径
            json: JSON 数据
            **kwargs: 传递给 requests.put 的其他参数

        Returns:
            Response 对象
        """
        url = f"{self.base_url}{endpoint}"
        return requests.put(url, json=json, auth=self.auth, **kwargs)

