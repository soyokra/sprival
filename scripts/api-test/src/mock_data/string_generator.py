"""
字符串数据生成器

提供各种类型的字符串生成功能
"""

import random
import string
from typing import Optional
from .base_generator import BaseGenerator


class StringGenerator(BaseGenerator):
    """
    字符串生成器
    
    支持生成固定/随机长度、不同字符集的字符串，以及特殊格式的字符串
    """
    
    # 字符集定义
    LOWERCASE = string.ascii_lowercase
    UPPERCASE = string.ascii_uppercase
    DIGITS = string.digits
    SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    CHINESE_RANGE = (0x4e00, 0x9fa5)  # 中文 Unicode 范围
    
    def generate(self, 
                 length: int = 10,
                 min_length: Optional[int] = None,
                 max_length: Optional[int] = None,
                 charset: str = "alphanumeric",
                 **kwargs) -> str:
        """
        生成字符串
        
        Args:
            length: 固定长度（当 min_length 和 max_length 未指定时使用）
            min_length: 最小长度（指定后随机生成 min_length 到 max_length 之间的长度）
            max_length: 最大长度
            charset: 字符集类型，可选值：
                    - alphanumeric: 字母数字
                    - alpha: 纯字母
                    - numeric: 纯数字
                    - lowercase: 小写字母
                    - uppercase: 大写字母
                    - special: 包含特殊字符
                    - chinese: 中文字符
            **kwargs: 其他参数
            
        Returns:
            生成的字符串
        """
        # 确定实际长度
        if min_length is not None and max_length is not None:
            actual_length = random.randint(min_length, max_length)
        else:
            actual_length = length
        
        # 根据字符集类型选择字符池
        if charset == "alphanumeric":
            chars = self.LOWERCASE + self.UPPERCASE + self.DIGITS
        elif charset == "alpha":
            chars = self.LOWERCASE + self.UPPERCASE
        elif charset == "numeric":
            chars = self.DIGITS
        elif charset == "lowercase":
            chars = self.LOWERCASE
        elif charset == "uppercase":
            chars = self.UPPERCASE
        elif charset == "special":
            chars = self.LOWERCASE + self.UPPERCASE + self.DIGITS + self.SPECIAL_CHARS
        elif charset == "chinese":
            return self._generate_chinese(actual_length)
        else:
            chars = self.LOWERCASE + self.UPPERCASE + self.DIGITS
        
        return ''.join(random.choice(chars) for _ in range(actual_length))
    
    def _generate_chinese(self, length: int) -> str:
        """
        生成中文字符串
        
        Args:
            length: 字符串长度
            
        Returns:
            中文字符串
        """
        return ''.join(
            chr(random.randint(self.CHINESE_RANGE[0], self.CHINESE_RANGE[1]))
            for _ in range(length)
        )
    
    def email(self, domain: str = "example.com") -> str:
        """
        生成邮箱地址
        
        Args:
            domain: 邮箱域名
            
        Returns:
            邮箱地址
        """
        username_length = random.randint(5, 15)
        username = self.generate(length=username_length, charset="lowercase")
        return f"{username}@{domain}"
    
    def url(self, protocol: str = "https", domain: Optional[str] = None, path: bool = True) -> str:
        """
        生成 URL
        
        Args:
            protocol: 协议，http 或 https
            domain: 域名（不指定则随机生成）
            path: 是否包含路径
            
        Returns:
            URL 字符串
        """
        if domain is None:
            domain = f"{self.generate(length=8, charset='lowercase')}.com"
        
        url = f"{protocol}://{domain}"
        
        if path:
            path_segments = random.randint(1, 3)
            for _ in range(path_segments):
                segment = self.generate(length=random.randint(4, 10), charset="lowercase")
                url += f"/{segment}"
        
        return url
    
    def phone(self, country_code: str = "86", length: int = 11) -> str:
        """
        生成手机号
        
        Args:
            country_code: 国家代码
            length: 号码长度
            
        Returns:
            手机号字符串
        """
        number = self.generate(length=length, charset="numeric")
        return f"+{country_code}{number}"
    
    def uuid(self) -> str:
        """
        生成 UUID 格式的字符串
        
        Returns:
            UUID 字符串
        """
        import uuid
        return str(uuid.uuid4())
    
    def username(self, min_length: int = 4, max_length: int = 16) -> str:
        """
        生成用户名（字母数字组合）
        
        Args:
            min_length: 最小长度
            max_length: 最大长度
            
        Returns:
            用户名字符串
        """
        length = random.randint(min_length, max_length)
        # 用户名以字母开头
        first_char = random.choice(self.LOWERCASE + self.UPPERCASE)
        rest_chars = ''.join(
            random.choice(self.LOWERCASE + self.UPPERCASE + self.DIGITS)
            for _ in range(length - 1)
        )
        return first_char + rest_chars

