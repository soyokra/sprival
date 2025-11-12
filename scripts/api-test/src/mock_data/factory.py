"""
数据生成工厂

提供统一的数据生成入口
"""

from typing import Any, Dict, List, Optional, Union
from .string_generator import StringGenerator
from .number_generator import NumberGenerator
from .date_generator import DateGenerator
from .object_generator import ObjectGenerator


class DataFactory:
    """
    数据生成工厂类
    
    提供便捷的静态方法访问各种数据生成器功能
    """
    
    # 生成器实例（单例）
    _string_gen = None
    _number_gen = None
    _date_gen = None
    _object_gen = None
    
    @classmethod
    def _get_string_generator(cls) -> StringGenerator:
        """获取字符串生成器实例"""
        if cls._string_gen is None:
            cls._string_gen = StringGenerator()
        return cls._string_gen
    
    @classmethod
    def _get_number_generator(cls) -> NumberGenerator:
        """获取数值生成器实例"""
        if cls._number_gen is None:
            cls._number_gen = NumberGenerator()
        return cls._number_gen
    
    @classmethod
    def _get_date_generator(cls) -> DateGenerator:
        """获取日期生成器实例"""
        if cls._date_gen is None:
            cls._date_gen = DateGenerator()
        return cls._date_gen
    
    @classmethod
    def _get_object_generator(cls) -> ObjectGenerator:
        """获取对象生成器实例"""
        if cls._object_gen is None:
            cls._object_gen = ObjectGenerator()
        return cls._object_gen
    
    # ==================== 字符串生成 ====================
    
    @classmethod
    def string(cls, length: int = 10, charset: str = "alphanumeric", **kwargs) -> str:
        """生成字符串"""
        return cls._get_string_generator().generate(length=length, charset=charset, **kwargs)
    
    @classmethod
    def email(cls, domain: str = "example.com") -> str:
        """生成邮箱"""
        return cls._get_string_generator().email(domain=domain)
    
    @classmethod
    def url(cls, protocol: str = "https", domain: Optional[str] = None, path: bool = True) -> str:
        """生成 URL"""
        return cls._get_string_generator().url(protocol=protocol, domain=domain, path=path)
    
    @classmethod
    def phone(cls, country_code: str = "86", length: int = 11) -> str:
        """生成手机号"""
        return cls._get_string_generator().phone(country_code=country_code, length=length)
    
    @classmethod
    def uuid(cls) -> str:
        """生成 UUID"""
        return cls._get_string_generator().uuid()
    
    @classmethod
    def username(cls, min_length: int = 4, max_length: int = 16) -> str:
        """生成用户名"""
        return cls._get_string_generator().username(min_length=min_length, max_length=max_length)
    
    # ==================== 数值生成 ====================
    
    @classmethod
    def integer(cls, min_val: int = 0, max_val: int = 100) -> int:
        """生成整数"""
        return cls._get_number_generator().integer(min_val=min_val, max_val=max_val)
    
    @classmethod
    def float(cls, min_val: float = 0.0, max_val: float = 100.0, precision: int = 2) -> float:
        """生成浮点数"""
        return cls._get_number_generator().float(min_val=min_val, max_val=max_val, precision=precision)
    
    @classmethod
    def price(cls, min_val: float = 0.01, max_val: float = 10000.0) -> float:
        """生成价格"""
        return cls._get_number_generator().price(min_val=min_val, max_val=max_val)
    
    @classmethod
    def percentage(cls, precision: int = 2) -> float:
        """生成百分比"""
        return cls._get_number_generator().percentage(precision=precision)
    
    @classmethod
    def probability(cls, precision: int = 2) -> float:
        """生成概率"""
        return cls._get_number_generator().probability(precision=precision)
    
    # ==================== 日期时间生成 ====================
    
    @classmethod
    def timestamp(cls, milliseconds: bool = False) -> int:
        """生成时间戳"""
        return cls._get_date_generator().timestamp(milliseconds=milliseconds)
    
    @classmethod
    def date(cls, date_format: str = "%Y-%m-%d") -> str:
        """生成日期"""
        return cls._get_date_generator().date(date_format=date_format)
    
    @classmethod
    def datetime(cls, date_format: str = "%Y-%m-%d %H:%M:%S") -> str:
        """生成日期时间"""
        return cls._get_date_generator().datetime(date_format=date_format)
    
    @classmethod
    def time(cls, date_format: str = "%H:%M:%S") -> str:
        """生成时间"""
        return cls._get_date_generator().time(date_format=date_format)
    
    @classmethod
    def iso8601(cls) -> str:
        """生成 ISO8601 格式日期时间"""
        return cls._get_date_generator().iso8601()
    
    @classmethod
    def random_date(cls, start_date=None, end_date=None, date_format: str = "%Y-%m-%d") -> str:
        """生成随机日期"""
        return cls._get_date_generator().random_date(
            start_date=start_date, end_date=end_date, date_format=date_format
        )
    
    @classmethod
    def past_date(cls, days: int = 30, date_format: str = "%Y-%m-%d") -> str:
        """生成过去的日期"""
        return cls._get_date_generator().past_date(days=days, date_format=date_format)
    
    @classmethod
    def future_date(cls, days: int = 30, date_format: str = "%Y-%m-%d") -> str:
        """生成未来的日期"""
        return cls._get_date_generator().future_date(days=days, date_format=date_format)
    
    # ==================== 对象生成 ====================
    
    @classmethod
    def object(cls, template: Dict[str, Any]) -> Dict[str, Any]:
        """根据模板生成对象"""
        return cls._get_object_generator().generate(template=template)
    
    @classmethod
    def array(cls, item_template: Any, min_count: int = 1, max_count: int = 10) -> List[Any]:
        """生成数组"""
        return cls._get_object_generator().array(
            item_template=item_template, min_count=min_count, max_count=max_count
        )
    
    @classmethod
    def nested_object(cls, depth: int = 3, key_count: int = 5) -> Dict[str, Any]:
        """生成嵌套对象"""
        return cls._get_object_generator().nested_object(depth=depth, key_count=key_count)
    
    @classmethod
    def paginated_response(cls,
                          data_template: Any,
                          page: int = 1,
                          page_size: int = 20,
                          total: Optional[int] = None) -> Dict[str, Any]:
        """生成分页响应"""
        return cls._get_object_generator().paginated_response(
            data_template=data_template, page=page, page_size=page_size, total=total
        )
    
    # ==================== 批量生成 ====================
    
    @classmethod
    def batch(cls, generator_func, count: int, **kwargs) -> List[Any]:
        """
        批量生成数据
        
        Args:
            generator_func: 生成器函数（DataFactory 的方法）
            count: 生成数量
            **kwargs: 传递给生成器函数的参数
            
        Returns:
            生成的数据列表
        """
        return [generator_func(**kwargs) for _ in range(count)]

