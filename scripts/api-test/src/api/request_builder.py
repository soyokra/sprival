"""
请求构建器模块

提供流式的请求构建接口
"""

from typing import Dict, Any, Optional
from ..mock_data.factory import DataFactory


class RequestBuilder:
    """
    请求构建器
    
    提供链式调用接口来构建 HTTP 请求
    """
    
    def __init__(self):
        self._method: str = "GET"
        self._endpoint: str = ""
        self._params: Dict[str, Any] = {}
        self._headers: Dict[str, str] = {}
        self._body: Optional[Dict[str, Any]] = None
        self._body_template: Optional[Dict[str, Any]] = None
    
    def method(self, method: str) -> 'RequestBuilder':
        """
        设置 HTTP 方法
        
        Args:
            method: HTTP 方法（GET, POST, PUT, DELETE 等）
            
        Returns:
            自身，支持链式调用
        """
        self._method = method.upper()
        return self
    
    def endpoint(self, endpoint: str) -> 'RequestBuilder':
        """
        设置端点
        
        Args:
            endpoint: API 端点
            
        Returns:
            自身，支持链式调用
        """
        self._endpoint = endpoint
        return self
    
    def param(self, key: str, value: Any) -> 'RequestBuilder':
        """
        添加 URL 参数
        
        Args:
            key: 参数键
            value: 参数值
            
        Returns:
            自身，支持链式调用
        """
        self._params[key] = value
        return self
    
    def params(self, params: Dict[str, Any]) -> 'RequestBuilder':
        """
        批量添加 URL 参数
        
        Args:
            params: 参数字典
            
        Returns:
            自身，支持链式调用
        """
        self._params.update(params)
        return self
    
    def header(self, key: str, value: str) -> 'RequestBuilder':
        """
        添加请求头
        
        Args:
            key: 请求头键
            value: 请求头值
            
        Returns:
            自身，支持链式调用
        """
        self._headers[key] = value
        return self
    
    def headers(self, headers: Dict[str, str]) -> 'RequestBuilder':
        """
        批量添加请求头
        
        Args:
            headers: 请求头字典
            
        Returns:
            自身，支持链式调用
        """
        self._headers.update(headers)
        return self
    
    def json_body(self, body: Dict[str, Any]) -> 'RequestBuilder':
        """
        设置 JSON 请求体
        
        Args:
            body: 请求体数据
            
        Returns:
            自身，支持链式调用
        """
        self._body = body
        return self
    
    def body_from_template(self, template: Dict[str, Any]) -> 'RequestBuilder':
        """
        使用模板生成请求体
        
        模板支持 mock 标记：${mock:type:param1:param2:...}
        
        Args:
            template: 请求体模板
            
        Returns:
            自身，支持链式调用
        """
        self._body_template = template
        return self
    
    def build(self) -> Dict[str, Any]:
        """
        构建请求配置
        
        Returns:
            请求配置字典
        """
        config = {
            "method": self._method,
            "endpoint": self._endpoint,
        }
        
        if self._params:
            config["params"] = self._params
        
        if self._headers:
            config["headers"] = self._headers
        
        # 处理请求体
        if self._body_template:
            # 从模板生成请求体
            config["json"] = DataFactory.object(self._body_template)
        elif self._body:
            config["json"] = self._body
        
        return config
    
    def reset(self) -> 'RequestBuilder':
        """
        重置构建器
        
        Returns:
            自身，支持链式调用
        """
        self._method = "GET"
        self._endpoint = ""
        self._params = {}
        self._headers = {}
        self._body = None
        self._body_template = None
        return self
    
    @classmethod
    def create(cls) -> 'RequestBuilder':
        """
        创建新的请求构建器实例
        
        Returns:
            请求构建器实例
        """
        return cls()


class QuickRequestBuilder:
    """
    快速请求构建器
    
    提供便捷方法快速构建常见请求
    """
    
    @staticmethod
    def get(endpoint: str, params: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        构建 GET 请求
        
        Args:
            endpoint: API 端点
            params: URL 参数
            
        Returns:
            请求配置
        """
        builder = RequestBuilder().method("GET").endpoint(endpoint)
        if params:
            builder.params(params)
        return builder.build()
    
    @staticmethod
    def post(endpoint: str,
             body: Optional[Dict[str, Any]] = None,
             template: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        构建 POST 请求
        
        Args:
            endpoint: API 端点
            body: 请求体
            template: 请求体模板
            
        Returns:
            请求配置
        """
        builder = RequestBuilder().method("POST").endpoint(endpoint)
        if template:
            builder.body_from_template(template)
        elif body:
            builder.json_body(body)
        return builder.build()
    
    @staticmethod
    def put(endpoint: str,
            body: Optional[Dict[str, Any]] = None,
            template: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        构建 PUT 请求
        
        Args:
            endpoint: API 端点
            body: 请求体
            template: 请求体模板
            
        Returns:
            请求配置
        """
        builder = RequestBuilder().method("PUT").endpoint(endpoint)
        if template:
            builder.body_from_template(template)
        elif body:
            builder.json_body(body)
        return builder.build()
    
    @staticmethod
    def delete(endpoint: str) -> Dict[str, Any]:
        """
        构建 DELETE 请求
        
        Args:
            endpoint: API 端点
            
        Returns:
            请求配置
        """
        return RequestBuilder().method("DELETE").endpoint(endpoint).build()

