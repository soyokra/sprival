"""
HTTP 客户端模块

基于 requests 库封装，提供重试、超时、连接池等功能
"""

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
from typing import Optional, Dict, Any
from ..utils.logger import Logger
from ..utils.timer import Timer


class HttpClient:
    """
    HTTP 客户端
    
    封装了 requests 库，提供统一的 HTTP 请求接口
    支持自动重试、超时设置、连接池管理
    """
    
    def __init__(self,
                 base_url: str = "",
                 timeout: int = 30,
                 max_retries: int = 3,
                 pool_connections: int = 10,
                 pool_maxsize: int = 10,
                 verify_ssl: bool = True,
                 default_headers: Optional[Dict[str, str]] = None):
        """
        初始化 HTTP 客户端
        
        Args:
            base_url: 基础 URL
            timeout: 请求超时时间（秒）
            max_retries: 最大重试次数
            pool_connections: 连接池连接数
            pool_maxsize: 连接池最大大小
            verify_ssl: 是否验证 SSL 证书
            default_headers: 默认请求头
        """
        self.base_url = base_url.rstrip('/')
        self.timeout = timeout
        self.verify_ssl = verify_ssl
        self.default_headers = default_headers or {}
        
        # 创建会话
        self.session = requests.Session()
        
        # 配置重试策略
        retry_strategy = Retry(
            total=max_retries,
            backoff_factor=0.5,  # 重试间隔：0.5, 1.0, 2.0 秒
            status_forcelist=[429, 500, 502, 503, 504],  # 需要重试的状态码
            allowed_methods=["HEAD", "GET", "OPTIONS", "POST", "PUT", "DELETE"]
        )
        
        # 配置连接池
        adapter = HTTPAdapter(
            max_retries=retry_strategy,
            pool_connections=pool_connections,
            pool_maxsize=pool_maxsize
        )
        
        self.session.mount("http://", adapter)
        self.session.mount("https://", adapter)
        
        # 日志记录器
        self.logger = Logger.get_logger("http_client")
    
    def _build_url(self, endpoint: str) -> str:
        """
        构建完整 URL
        
        Args:
            endpoint: API 端点
            
        Returns:
            完整的 URL
        """
        if endpoint.startswith("http://") or endpoint.startswith("https://"):
            return endpoint
        
        endpoint = endpoint.lstrip('/')
        return f"{self.base_url}/{endpoint}" if self.base_url else endpoint
    
    def _merge_headers(self, headers: Optional[Dict[str, str]] = None) -> Dict[str, str]:
        """
        合并请求头
        
        Args:
            headers: 自定义请求头
            
        Returns:
            合并后的请求头
        """
        merged = self.default_headers.copy()
        if headers:
            merged.update(headers)
        return merged
    
    def request(self,
                method: str,
                endpoint: str,
                params: Optional[Dict[str, Any]] = None,
                data: Optional[Any] = None,
                json: Optional[Dict[str, Any]] = None,
                headers: Optional[Dict[str, str]] = None,
                timeout: Optional[int] = None,
                **kwargs) -> requests.Response:
        """
        发送 HTTP 请求
        
        Args:
            method: HTTP 方法（GET, POST, PUT, DELETE 等）
            endpoint: API 端点
            params: URL 参数
            data: 请求体数据（表单格式）
            json: 请求体数据（JSON 格式）
            headers: 请求头
            timeout: 超时时间（秒）
            **kwargs: 其他 requests 参数
            
        Returns:
            HTTP 响应对象
        """
        url = self._build_url(endpoint)
        merged_headers = self._merge_headers(headers)
        request_timeout = timeout if timeout is not None else self.timeout
        
        # 记录请求日志
        self.logger.debug(f"请求: {method} {url}")
        self.logger.debug(f"参数: {params}")
        self.logger.debug(f"请求头: {merged_headers}")
        
        # 计时
        timer = Timer()
        timer.start()
        
        try:
            response = self.session.request(
                method=method,
                url=url,
                params=params,
                data=data,
                json=json,
                headers=merged_headers,
                timeout=request_timeout,
                verify=self.verify_ssl,
                **kwargs
            )
            
            elapsed = timer.stop()
            
            # 记录响应日志
            self.logger.debug(f"响应: {response.status_code} ({elapsed:.3f}s)")
            
            return response
            
        except requests.exceptions.Timeout as e:
            elapsed = timer.stop()
            self.logger.error(f"请求超时: {url} ({elapsed:.3f}s)")
            raise
        except requests.exceptions.RequestException as e:
            elapsed = timer.stop()
            self.logger.error(f"请求失败: {url} ({elapsed:.3f}s) - {str(e)}")
            raise
    
    def get(self, endpoint: str, params: Optional[Dict[str, Any]] = None, **kwargs) -> requests.Response:
        """
        发送 GET 请求
        
        Args:
            endpoint: API 端点
            params: URL 参数
            **kwargs: 其他参数
            
        Returns:
            HTTP 响应对象
        """
        return self.request("GET", endpoint, params=params, **kwargs)
    
    def post(self,
             endpoint: str,
             data: Optional[Any] = None,
             json: Optional[Dict[str, Any]] = None,
             **kwargs) -> requests.Response:
        """
        发送 POST 请求
        
        Args:
            endpoint: API 端点
            data: 请求体数据（表单格式）
            json: 请求体数据（JSON 格式）
            **kwargs: 其他参数
            
        Returns:
            HTTP 响应对象
        """
        return self.request("POST", endpoint, data=data, json=json, **kwargs)
    
    def put(self,
            endpoint: str,
            data: Optional[Any] = None,
            json: Optional[Dict[str, Any]] = None,
            **kwargs) -> requests.Response:
        """
        发送 PUT 请求
        
        Args:
            endpoint: API 端点
            data: 请求体数据（表单格式）
            json: 请求体数据（JSON 格式）
            **kwargs: 其他参数
            
        Returns:
            HTTP 响应对象
        """
        return self.request("PUT", endpoint, data=data, json=json, **kwargs)
    
    def delete(self, endpoint: str, **kwargs) -> requests.Response:
        """
        发送 DELETE 请求
        
        Args:
            endpoint: API 端点
            **kwargs: 其他参数
            
        Returns:
            HTTP 响应对象
        """
        return self.request("DELETE", endpoint, **kwargs)
    
    def patch(self,
              endpoint: str,
              data: Optional[Any] = None,
              json: Optional[Dict[str, Any]] = None,
              **kwargs) -> requests.Response:
        """
        发送 PATCH 请求
        
        Args:
            endpoint: API 端点
            data: 请求体数据（表单格式）
            json: 请求体数据（JSON 格式）
            **kwargs: 其他参数
            
        Returns:
            HTTP 响应对象
        """
        return self.request("PATCH", endpoint, data=data, json=json, **kwargs)
    
    def close(self):
        """关闭会话"""
        self.session.close()
    
    def __enter__(self):
        """上下文管理器入口"""
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """上下文管理器出口"""
        self.close()

