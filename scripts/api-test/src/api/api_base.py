"""
API 基类模块

定义通用的 API 接口行为
"""

from typing import Dict, Any, Optional
import requests
from .http_client import HttpClient
from .request_builder import RequestBuilder
from .response_handler import ResponseHandler, handle_response
from ..utils.logger import Logger


class APIBase:
    """
    API 基类
    
    所有具体的 API 类都应继承此类
    提供通用的 HTTP 请求方法和响应处理
    """
    
    def __init__(self,
                 base_url: str = "",
                 timeout: int = 30,
                 max_retries: int = 3,
                 default_headers: Optional[Dict[str, str]] = None):
        """
        初始化 API 基类
        
        Args:
            base_url: 基础 URL
            timeout: 请求超时时间（秒）
            max_retries: 最大重试次数
            default_headers: 默认请求头
        """
        self.client = HttpClient(
            base_url=base_url,
            timeout=timeout,
            max_retries=max_retries,
            default_headers=default_headers
        )
        self.logger = Logger.get_logger(self.__class__.__name__)
    
    def get(self,
            endpoint: str,
            params: Optional[Dict[str, Any]] = None,
            headers: Optional[Dict[str, str]] = None,
            **kwargs) -> ResponseHandler:
        """
        发送 GET 请求
        
        Args:
            endpoint: API 端点
            params: URL 参数
            headers: 请求头
            **kwargs: 其他参数
            
        Returns:
            响应处理器
        """
        self.logger.info(f"GET {endpoint}")
        response = self.client.get(endpoint, params=params, headers=headers, **kwargs)
        return handle_response(response)
    
    def post(self,
             endpoint: str,
             json: Optional[Dict[str, Any]] = None,
             data: Optional[Any] = None,
             headers: Optional[Dict[str, str]] = None,
             **kwargs) -> ResponseHandler:
        """
        发送 POST 请求
        
        Args:
            endpoint: API 端点
            json: JSON 请求体
            data: 表单数据
            headers: 请求头
            **kwargs: 其他参数
            
        Returns:
            响应处理器
        """
        self.logger.info(f"POST {endpoint}")
        response = self.client.post(endpoint, json=json, data=data, headers=headers, **kwargs)
        return handle_response(response)
    
    def put(self,
            endpoint: str,
            json: Optional[Dict[str, Any]] = None,
            data: Optional[Any] = None,
            headers: Optional[Dict[str, str]] = None,
            **kwargs) -> ResponseHandler:
        """
        发送 PUT 请求
        
        Args:
            endpoint: API 端点
            json: JSON 请求体
            data: 表单数据
            headers: 请求头
            **kwargs: 其他参数
            
        Returns:
            响应处理器
        """
        self.logger.info(f"PUT {endpoint}")
        response = self.client.put(endpoint, json=json, data=data, headers=headers, **kwargs)
        return handle_response(response)
    
    def delete(self,
               endpoint: str,
               headers: Optional[Dict[str, str]] = None,
               **kwargs) -> ResponseHandler:
        """
        发送 DELETE 请求
        
        Args:
            endpoint: API 端点
            headers: 请求头
            **kwargs: 其他参数
            
        Returns:
            响应处理器
        """
        self.logger.info(f"DELETE {endpoint}")
        response = self.client.delete(endpoint, headers=headers, **kwargs)
        return handle_response(response)
    
    def patch(self,
              endpoint: str,
              json: Optional[Dict[str, Any]] = None,
              data: Optional[Any] = None,
              headers: Optional[Dict[str, str]] = None,
              **kwargs) -> ResponseHandler:
        """
        发送 PATCH 请求
        
        Args:
            endpoint: API 端点
            json: JSON 请求体
            data: 表单数据
            headers: 请求头
            **kwargs: 其他参数
            
        Returns:
            响应处理器
        """
        self.logger.info(f"PATCH {endpoint}")
        response = self.client.patch(endpoint, json=json, data=data, headers=headers, **kwargs)
        return handle_response(response)
    
    def request_from_builder(self, builder: RequestBuilder) -> ResponseHandler:
        """
        使用请求构建器发送请求
        
        Args:
            builder: 请求构建器
            
        Returns:
            响应处理器
        """
        config = builder.build()
        method = config.pop("method")
        endpoint = config.pop("endpoint")
        
        response = self.client.request(method, endpoint, **config)
        return handle_response(response)
    
    def close(self):
        """关闭客户端"""
        self.client.close()
    
    def __enter__(self):
        """上下文管理器入口"""
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """上下文管理器出口"""
        self.close()

