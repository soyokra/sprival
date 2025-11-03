"""
日期时间数据生成器

提供各种日期时间格式的生成功能
"""

import random
import time
from datetime import datetime, timedelta
from typing import Optional
from .base_generator import BaseGenerator


class DateGenerator(BaseGenerator):
    """
    日期时间生成器
    
    支持生成时间戳、格式化日期时间、日期范围等
    """
    
    # 常用日期格式
    FORMAT_DATE = "%Y-%m-%d"
    FORMAT_DATETIME = "%Y-%m-%d %H:%M:%S"
    FORMAT_TIME = "%H:%M:%S"
    FORMAT_ISO8601 = "%Y-%m-%dT%H:%M:%SZ"
    
    def generate(self, 
                 format_type: str = "timestamp",
                 date_format: Optional[str] = None,
                 **kwargs) -> str:
        """
        生成日期时间
        
        Args:
            format_type: 格式类型，可选：timestamp, date, datetime, time, iso8601
            date_format: 自定义日期格式（使用 strftime 格式）
            **kwargs: 其他参数
            
        Returns:
            生成的日期时间字符串或时间戳
        """
        if format_type == "timestamp":
            return self.timestamp()
        elif format_type == "date":
            return self.date(date_format or self.FORMAT_DATE)
        elif format_type == "datetime":
            return self.datetime(date_format or self.FORMAT_DATETIME)
        elif format_type == "time":
            return self.time(date_format or self.FORMAT_TIME)
        elif format_type == "iso8601":
            return self.iso8601()
        else:
            return self.timestamp()
    
    def timestamp(self, milliseconds: bool = False) -> int:
        """
        生成当前时间戳
        
        Args:
            milliseconds: 是否返回毫秒级时间戳
            
        Returns:
            时间戳（秒或毫秒）
        """
        ts = int(time.time())
        return ts * 1000 if milliseconds else ts
    
    def date(self, date_format: str = FORMAT_DATE) -> str:
        """
        生成日期字符串
        
        Args:
            date_format: 日期格式
            
        Returns:
            格式化的日期字符串
        """
        return datetime.now().strftime(date_format)
    
    def datetime(self, date_format: str = FORMAT_DATETIME) -> str:
        """
        生成日期时间字符串
        
        Args:
            date_format: 日期时间格式
            
        Returns:
            格式化的日期时间字符串
        """
        return datetime.now().strftime(date_format)
    
    def time(self, date_format: str = FORMAT_TIME) -> str:
        """
        生成时间字符串
        
        Args:
            date_format: 时间格式
            
        Returns:
            格式化的时间字符串
        """
        return datetime.now().strftime(date_format)
    
    def iso8601(self) -> str:
        """
        生成 ISO8601 格式的日期时间字符串
        
        Returns:
            ISO8601 格式字符串
        """
        return datetime.now().strftime(self.FORMAT_ISO8601)
    
    def random_date(self,
                   start_date: Optional[datetime] = None,
                   end_date: Optional[datetime] = None,
                   date_format: str = FORMAT_DATE) -> str:
        """
        生成指定范围内的随机日期
        
        Args:
            start_date: 开始日期（默认为 1 年前）
            end_date: 结束日期（默认为当前日期）
            date_format: 日期格式
            
        Returns:
            随机日期字符串
        """
        if end_date is None:
            end_date = datetime.now()
        if start_date is None:
            start_date = end_date - timedelta(days=365)
        
        time_delta = end_date - start_date
        random_days = random.randint(0, time_delta.days)
        random_date = start_date + timedelta(days=random_days)
        
        return random_date.strftime(date_format)
    
    def random_datetime(self,
                       start_date: Optional[datetime] = None,
                       end_date: Optional[datetime] = None,
                       date_format: str = FORMAT_DATETIME) -> str:
        """
        生成指定范围内的随机日期时间
        
        Args:
            start_date: 开始日期时间（默认为 1 年前）
            end_date: 结束日期时间（默认为当前时间）
            date_format: 日期时间格式
            
        Returns:
            随机日期时间字符串
        """
        if end_date is None:
            end_date = datetime.now()
        if start_date is None:
            start_date = end_date - timedelta(days=365)
        
        time_delta = end_date - start_date
        random_seconds = random.randint(0, int(time_delta.total_seconds()))
        random_dt = start_date + timedelta(seconds=random_seconds)
        
        return random_dt.strftime(date_format)
    
    def past_date(self, days: int = 30, date_format: str = FORMAT_DATE) -> str:
        """
        生成过去的日期
        
        Args:
            days: 过去的天数
            date_format: 日期格式
            
        Returns:
            过去的日期字符串
        """
        past = datetime.now() - timedelta(days=days)
        return past.strftime(date_format)
    
    def future_date(self, days: int = 30, date_format: str = FORMAT_DATE) -> str:
        """
        生成未来的日期
        
        Args:
            days: 未来的天数
            date_format: 日期格式
            
        Returns:
            未来的日期字符串
        """
        future = datetime.now() + timedelta(days=days)
        return future.strftime(date_format)
    
    def age_to_birthdate(self, age: int, date_format: str = FORMAT_DATE) -> str:
        """
        根据年龄生成出生日期
        
        Args:
            age: 年龄
            date_format: 日期格式
            
        Returns:
            出生日期字符串
        """
        birth_year = datetime.now().year - age
        birth_month = random.randint(1, 12)
        birth_day = random.randint(1, 28)  # 使用 28 避免月份天数问题
        
        birthdate = datetime(birth_year, birth_month, birth_day)
        return birthdate.strftime(date_format)

