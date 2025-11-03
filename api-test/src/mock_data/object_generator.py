"""
复杂对象数据生成器

提供 JSON 对象、数组等复杂数据结构的生成功能
"""

import random
from typing import Any, Dict, List, Optional
from .base_generator import BaseGenerator
from .string_generator import StringGenerator
from .number_generator import NumberGenerator
from .date_generator import DateGenerator


class ObjectGenerator(BaseGenerator):
    """
    对象生成器
    
    支持生成复杂的 JSON 对象、数组、嵌套结构
    """
    
    def __init__(self):
        self.string_gen = StringGenerator()
        self.number_gen = NumberGenerator()
        self.date_gen = DateGenerator()
    
    def generate(self, template: Dict[str, Any], **kwargs) -> Dict[str, Any]:
        """
        根据模板生成对象
        
        Args:
            template: 对象模板，支持嵌套和特殊标记
            **kwargs: 其他参数
            
        Returns:
            生成的对象
        """
        return self._process_template(template)
    
    def _process_template(self, template: Any) -> Any:
        """
        递归处理模板
        
        Args:
            template: 模板（可以是字典、列表或值）
            
        Returns:
            处理后的数据
        """
        if isinstance(template, dict):
            return {key: self._process_template(value) for key, value in template.items()}
        elif isinstance(template, list):
            return [self._process_template(item) for item in template]
        elif isinstance(template, str) and template.startswith("${mock:"):
            # 解析 mock 标记：${mock:type:param1:param2:...}
            return self._parse_mock_tag(template)
        else:
            return template
    
    def _parse_mock_tag(self, tag: str) -> Any:
        """
        解析 mock 标记并生成相应的数据
        
        Args:
            tag: mock 标记字符串，格式：${mock:type:param1:param2:...}
            
        Returns:
            生成的数据
        """
        # 移除 ${mock: 前缀和 } 后缀
        tag = tag[7:-1]
        parts = tag.split(":")
        
        mock_type = parts[0]
        params = parts[1:] if len(parts) > 1 else []
        
        # 字符串类型
        if mock_type == "string":
            length = int(params[0]) if params else 10
            charset = params[1] if len(params) > 1 else "alphanumeric"
            return self.string_gen.generate(length=length, charset=charset)
        
        elif mock_type == "email":
            domain = params[0] if params else "example.com"
            return self.string_gen.email(domain=domain)
        
        elif mock_type == "url":
            return self.string_gen.url()
        
        elif mock_type == "phone":
            return self.string_gen.phone()
        
        elif mock_type == "uuid":
            return self.string_gen.uuid()
        
        elif mock_type == "username":
            return self.string_gen.username()
        
        # 数值类型
        elif mock_type == "int":
            min_val = int(params[0]) if params else 0
            max_val = int(params[1]) if len(params) > 1 else 100
            return self.number_gen.integer(min_val=min_val, max_val=max_val)
        
        elif mock_type == "float":
            min_val = float(params[0]) if params else 0.0
            max_val = float(params[1]) if len(params) > 1 else 100.0
            precision = int(params[2]) if len(params) > 2 else 2
            return self.number_gen.float(min_val=min_val, max_val=max_val, precision=precision)
        
        elif mock_type == "price":
            return self.number_gen.price()
        
        # 日期时间类型
        elif mock_type == "timestamp":
            return self.date_gen.timestamp()
        
        elif mock_type == "date":
            date_format = params[0] if params else DateGenerator.FORMAT_DATE
            return self.date_gen.date(date_format=date_format)
        
        elif mock_type == "datetime":
            date_format = params[0] if params else DateGenerator.FORMAT_DATETIME
            return self.date_gen.datetime(date_format=date_format)
        
        elif mock_type == "iso8601":
            return self.date_gen.iso8601()
        
        # 布尔类型
        elif mock_type == "bool":
            return random.choice([True, False])
        
        # 从列表中随机选择
        elif mock_type == "choice":
            return random.choice(params)
        
        else:
            return f"UNKNOWN_MOCK_TYPE:{mock_type}"
    
    def array(self, 
             item_template: Any, 
             min_count: int = 1, 
             max_count: int = 10) -> List[Any]:
        """
        生成数组
        
        Args:
            item_template: 数组元素模板
            min_count: 最小元素数量
            max_count: 最大元素数量
            
        Returns:
            生成的数组
        """
        count = random.randint(min_count, max_count)
        return [self._process_template(item_template) for _ in range(count)]
    
    def nested_object(self, 
                     depth: int = 3, 
                     key_count: int = 5) -> Dict[str, Any]:
        """
        生成嵌套对象
        
        Args:
            depth: 嵌套深度
            key_count: 每层的键数量
            
        Returns:
            嵌套对象
        """
        if depth <= 0:
            return self._random_value()
        
        obj = {}
        for i in range(key_count):
            key = f"key_{i}"
            if random.random() < 0.3 and depth > 1:  # 30% 概率嵌套
                obj[key] = self.nested_object(depth=depth-1, key_count=key_count)
            else:
                obj[key] = self._random_value()
        
        return obj
    
    def _random_value(self) -> Any:
        """
        生成随机值
        
        Returns:
            随机值（字符串、数字、布尔值之一）
        """
        value_type = random.choice(["string", "int", "float", "bool"])
        
        if value_type == "string":
            return self.string_gen.generate(length=random.randint(5, 15))
        elif value_type == "int":
            return self.number_gen.integer()
        elif value_type == "float":
            return self.number_gen.float()
        elif value_type == "bool":
            return random.choice([True, False])
    
    def paginated_response(self,
                          data_template: Any,
                          page: int = 1,
                          page_size: int = 20,
                          total: Optional[int] = None) -> Dict[str, Any]:
        """
        生成分页响应结构
        
        Args:
            data_template: 数据项模板
            page: 当前页码
            page_size: 每页大小
            total: 总记录数（不指定则随机生成）
            
        Returns:
            分页响应对象
        """
        if total is None:
            total = random.randint(page_size, page_size * 10)
        
        total_pages = (total + page_size - 1) // page_size
        has_next = page < total_pages
        has_prev = page > 1
        
        # 生成当前页的数据
        current_page_size = min(page_size, total - (page - 1) * page_size)
        data = [self._process_template(data_template) for _ in range(current_page_size)]
        
        return {
            "code": 200,
            "message": "success",
            "data": data,
            "pagination": {
                "page": page,
                "page_size": page_size,
                "total": total,
                "total_pages": total_pages,
                "has_next": has_next,
                "has_prev": has_prev
            }
        }

