"""
响应处理器模块

提供响应数据处理、断言、提取等功能
"""

import json
from typing import Any, Dict, List, Optional, Callable
import requests
from ..utils.validator import ResponseValidator, ValidationError
from ..utils.logger import Logger


class ResponseHandler:
    """
    响应处理器
    
    提供响应数据的解析、验证、提取等功能
    """
    
    def __init__(self, response: requests.Response):
        """
        初始化响应处理器
        
        Args:
            response: HTTP 响应对象
        """
        self.response = response
        self.logger = Logger.get_logger("response_handler")
        self._json_data: Optional[Dict] = None
    
    @property
    def status_code(self) -> int:
        """获取状态码"""
        return self.response.status_code
    
    @property
    def headers(self) -> Dict[str, str]:
        """获取响应头"""
        return dict(self.response.headers)
    
    @property
    def text(self) -> str:
        """获取响应文本"""
        return self.response.text
    
    @property
    def json(self) -> Dict[str, Any]:
        """
        获取 JSON 数据
        
        Returns:
            JSON 数据（字典）
            
        Raises:
            ValueError: 如果响应不是有效的 JSON
        """
        if self._json_data is None:
            try:
                self._json_data = self.response.json()
            except json.JSONDecodeError as e:
                self.logger.error(f"JSON 解析失败: {str(e)}")
                raise ValueError(f"响应不是有效的 JSON: {str(e)}")
        return self._json_data
    
    @property
    def elapsed_ms(self) -> float:
        """获取响应时间（毫秒）"""
        return self.response.elapsed.total_seconds() * 1000
    
    def is_success(self) -> bool:
        """
        判断请求是否成功（状态码 2xx）
        
        Returns:
            是否成功
        """
        return 200 <= self.status_code < 300
    
    def assert_status_code(self, expected_code: int) -> 'ResponseHandler':
        """
        断言状态码
        
        Args:
            expected_code: 期望的状态码
            
        Returns:
            自身，支持链式调用
            
        Raises:
            ValidationError: 如果状态码不匹配
        """
        ResponseValidator.assert_status_code(self.response, expected_code)
        return self
    
    def assert_success(self) -> 'ResponseHandler':
        """
        断言请求成功（状态码 2xx）
        
        Returns:
            自身，支持链式调用
            
        Raises:
            ValidationError: 如果请求失败
        """
        if not self.is_success():
            raise ValidationError(f"请求失败，状态码: {self.status_code}")
        return self
    
    def assert_json_structure(self, expected_keys: List[str], strict: bool = False) -> 'ResponseHandler':
        """
        断言 JSON 数据结构
        
        Args:
            expected_keys: 期望的键列表
            strict: 是否严格模式
            
        Returns:
            自身，支持链式调用
            
        Raises:
            ValidationError: 如果数据结构不匹配
        """
        ResponseValidator.assert_json_structure(self.json, expected_keys, strict)
        return self
    
    def assert_field_value(self, field: str, expected_value: Any) -> 'ResponseHandler':
        """
        断言字段值
        
        Args:
            field: 字段名（支持点号访问嵌套字段，如 "data.user.name"）
            expected_value: 期望的值
            
        Returns:
            自身，支持链式调用
            
        Raises:
            ValidationError: 如果字段值不匹配
        """
        value = self.extract_field(field)
        if value != expected_value:
            raise ValidationError(
                f"字段 {field} 的值不匹配，期望 {expected_value}，实际 {value}"
            )
        return self
    
    def assert_field_type(self, field: str, expected_type: type) -> 'ResponseHandler':
        """
        断言字段类型
        
        Args:
            field: 字段名
            expected_type: 期望的类型
            
        Returns:
            自身，支持链式调用
            
        Raises:
            ValidationError: 如果字段类型不匹配
        """
        value = self.extract_field(field)
        if not isinstance(value, expected_type):
            raise ValidationError(
                f"字段 {field} 的类型不匹配，期望 {expected_type.__name__}，"
                f"实际 {type(value).__name__}"
            )
        return self
    
    def assert_custom(self, validator: Callable[['ResponseHandler'], bool], 
                     error_message: str = "自定义验证失败") -> 'ResponseHandler':
        """
        自定义断言
        
        Args:
            validator: 验证函数，接收 ResponseHandler 参数，返回 True 表示验证通过
            error_message: 验证失败的错误消息
            
        Returns:
            自身，支持链式调用
            
        Raises:
            ValidationError: 如果验证失败
        """
        if not validator(self):
            raise ValidationError(error_message)
        return self
    
    def extract_field(self, field: str, default: Any = None) -> Any:
        """
        提取字段值
        
        支持点号访问嵌套字段，如 "data.user.name"
        
        Args:
            field: 字段名
            default: 默认值（字段不存在时返回）
            
        Returns:
            字段值
        """
        try:
            data = self.json
            parts = field.split('.')
            
            for part in parts:
                if isinstance(data, dict):
                    data = data.get(part)
                elif isinstance(data, list) and part.isdigit():
                    data = data[int(part)]
                else:
                    return default
                
                if data is None:
                    return default
            
            return data
        except (KeyError, IndexError, TypeError):
            return default
    
    def extract_fields(self, fields: List[str]) -> Dict[str, Any]:
        """
        批量提取字段
        
        Args:
            fields: 字段名列表
            
        Returns:
            字段名到值的映射
        """
        return {field: self.extract_field(field) for field in fields}
    
    def log_response(self, include_body: bool = False):
        """
        记录响应日志
        
        Args:
            include_body: 是否包含响应体
        """
        self.logger.info(f"状态码: {self.status_code}")
        self.logger.info(f"响应时间: {self.elapsed_ms:.3f}ms")
        
        if include_body:
            try:
                self.logger.debug(f"响应体: {json.dumps(self.json, indent=2, ensure_ascii=False)}")
            except ValueError:
                self.logger.debug(f"响应体（文本）: {self.text[:500]}")
    
    def to_dict(self) -> Dict[str, Any]:
        """
        转换为字典
        
        Returns:
            包含响应信息的字典
        """
        result = {
            "status_code": self.status_code,
            "elapsed_ms": self.elapsed_ms,
            "headers": self.headers,
            "success": self.is_success()
        }
        
        try:
            result["json"] = self.json
        except ValueError:
            result["text"] = self.text[:1000]  # 限制文本长度
        
        return result


def handle_response(response: requests.Response) -> ResponseHandler:
    """
    创建响应处理器
    
    Args:
        response: HTTP 响应对象
        
    Returns:
        响应处理器实例
    """
    return ResponseHandler(response)

