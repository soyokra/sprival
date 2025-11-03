"""
数据验证工具模块

提供各种数据验证功能
"""

import re
from typing import Any, Dict, List, Optional, Callable


class ValidationError(Exception):
    """验证错误异常"""
    pass


class Validator:
    """
    数据验证器
    
    提供常用的数据验证方法
    """
    
    # 正则表达式模式
    EMAIL_PATTERN = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
    URL_PATTERN = r'^https?://[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(/.*)?$'
    PHONE_PATTERN = r'^\+?[0-9]{10,15}$'
    UUID_PATTERN = r'^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$'
    
    @staticmethod
    def not_none(value: Any, field_name: str = "Value") -> Any:
        """
        验证值不为 None
        
        Args:
            value: 要验证的值
            field_name: 字段名称
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果值为 None
        """
        if value is None:
            raise ValidationError(f"{field_name} 不能为 None")
        return value
    
    @staticmethod
    def not_empty(value: Any, field_name: str = "Value") -> Any:
        """
        验证值不为空
        
        Args:
            value: 要验证的值（字符串、列表、字典等）
            field_name: 字段名称
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果值为空
        """
        if not value:
            raise ValidationError(f"{field_name} 不能为空")
        return value
    
    @staticmethod
    def in_range(value: int or float, 
                 min_val: Optional[int or float] = None,
                 max_val: Optional[int or float] = None,
                 field_name: str = "Value") -> int or float:
        """
        验证数值在指定范围内
        
        Args:
            value: 要验证的数值
            min_val: 最小值
            max_val: 最大值
            field_name: 字段名称
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果值不在范围内
        """
        if min_val is not None and value < min_val:
            raise ValidationError(f"{field_name} 必须大于等于 {min_val}")
        if max_val is not None and value > max_val:
            raise ValidationError(f"{field_name} 必须小于等于 {max_val}")
        return value
    
    @staticmethod
    def length_in_range(value: str or List, 
                       min_length: Optional[int] = None,
                       max_length: Optional[int] = None,
                       field_name: str = "Value") -> str or List:
        """
        验证长度在指定范围内
        
        Args:
            value: 要验证的值（字符串或列表）
            min_length: 最小长度
            max_length: 最大长度
            field_name: 字段名称
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果长度不在范围内
        """
        length = len(value)
        if min_length is not None and length < min_length:
            raise ValidationError(f"{field_name} 长度必须大于等于 {min_length}")
        if max_length is not None and length > max_length:
            raise ValidationError(f"{field_name} 长度必须小于等于 {max_length}")
        return value
    
    @staticmethod
    def matches_pattern(value: str, pattern: str, field_name: str = "Value") -> str:
        """
        验证值匹配正则表达式模式
        
        Args:
            value: 要验证的字符串
            pattern: 正则表达式模式
            field_name: 字段名称
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果不匹配模式
        """
        if not re.match(pattern, value):
            raise ValidationError(f"{field_name} 格式不正确")
        return value
    
    @classmethod
    def is_email(cls, value: str, field_name: str = "Email") -> str:
        """验证邮箱格式"""
        return cls.matches_pattern(value, cls.EMAIL_PATTERN, field_name)
    
    @classmethod
    def is_url(cls, value: str, field_name: str = "URL") -> str:
        """验证 URL 格式"""
        return cls.matches_pattern(value, cls.URL_PATTERN, field_name)
    
    @classmethod
    def is_phone(cls, value: str, field_name: str = "Phone") -> str:
        """验证手机号格式"""
        return cls.matches_pattern(value, cls.PHONE_PATTERN, field_name)
    
    @classmethod
    def is_uuid(cls, value: str, field_name: str = "UUID") -> str:
        """验证 UUID 格式"""
        return cls.matches_pattern(value, cls.UUID_PATTERN, field_name)
    
    @staticmethod
    def is_type(value: Any, expected_type: type, field_name: str = "Value") -> Any:
        """
        验证值的类型
        
        Args:
            value: 要验证的值
            expected_type: 期望的类型
            field_name: 字段名称
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果类型不匹配
        """
        if not isinstance(value, expected_type):
            raise ValidationError(
                f"{field_name} 必须是 {expected_type.__name__} 类型，实际是 {type(value).__name__}"
            )
        return value
    
    @staticmethod
    def in_choices(value: Any, choices: List[Any], field_name: str = "Value") -> Any:
        """
        验证值在指定选项中
        
        Args:
            value: 要验证的值
            choices: 可选值列表
            field_name: 字段名称
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果值不在选项中
        """
        if value not in choices:
            raise ValidationError(f"{field_name} 必须是以下之一: {choices}")
        return value
    
    @staticmethod
    def custom(value: Any, validator_func: Callable[[Any], bool], 
              error_message: str = "验证失败") -> Any:
        """
        自定义验证
        
        Args:
            value: 要验证的值
            validator_func: 验证函数，返回 True 表示通过
            error_message: 错误消息
            
        Returns:
            原值
            
        Raises:
            ValidationError: 如果验证失败
        """
        if not validator_func(value):
            raise ValidationError(error_message)
        return value


class ResponseValidator:
    """
    HTTP 响应验证器
    
    用于验证 API 响应的状态码、数据结构等
    """
    
    @staticmethod
    def assert_status_code(response, expected_code: int):
        """
        断言响应状态码
        
        Args:
            response: HTTP 响应对象
            expected_code: 期望的状态码
            
        Raises:
            ValidationError: 如果状态码不匹配
        """
        if response.status_code != expected_code:
            raise ValidationError(
                f"期望状态码 {expected_code}，实际 {response.status_code}"
            )
    
    @staticmethod
    def assert_json_structure(data: Dict, expected_keys: List[str], strict: bool = False):
        """
        断言 JSON 数据结构
        
        Args:
            data: JSON 数据（字典）
            expected_keys: 期望的键列表
            strict: 是否严格模式（数据中不能有多余的键）
            
        Raises:
            ValidationError: 如果数据结构不匹配
        """
        if not isinstance(data, dict):
            raise ValidationError("数据必须是字典类型")
        
        # 检查必需的键
        missing_keys = set(expected_keys) - set(data.keys())
        if missing_keys:
            raise ValidationError(f"缺少必需的键: {missing_keys}")
        
        # 严格模式：检查多余的键
        if strict:
            extra_keys = set(data.keys()) - set(expected_keys)
            if extra_keys:
                raise ValidationError(f"存在多余的键: {extra_keys}")
    
    @staticmethod
    def assert_field_value(data: Dict, field: str, expected_value: Any):
        """
        断言字段值
        
        Args:
            data: JSON 数据
            field: 字段名
            expected_value: 期望的值
            
        Raises:
            ValidationError: 如果字段值不匹配
        """
        if field not in data:
            raise ValidationError(f"字段 {field} 不存在")
        
        if data[field] != expected_value:
            raise ValidationError(
                f"字段 {field} 的值不匹配，期望 {expected_value}，实际 {data[field]}"
            )
    
    @staticmethod
    def assert_field_type(data: Dict, field: str, expected_type: type):
        """
        断言字段类型
        
        Args:
            data: JSON 数据
            field: 字段名
            expected_type: 期望的类型
            
        Raises:
            ValidationError: 如果字段类型不匹配
        """
        if field not in data:
            raise ValidationError(f"字段 {field} 不存在")
        
        if not isinstance(data[field], expected_type):
            raise ValidationError(
                f"字段 {field} 的类型不匹配，期望 {expected_type.__name__}，"
                f"实际 {type(data[field]).__name__}"
            )

