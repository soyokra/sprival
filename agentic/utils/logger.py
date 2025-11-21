"""
日志管理模块

提供统一的日志管理功能
"""

import logging
import os
from datetime import datetime
from typing import Optional

try:
    from colorama import init, Fore, Style
    init(autoreset=True)
    COLORAMA_AVAILABLE = True
except ImportError:
    COLORAMA_AVAILABLE = False
    Fore = Style = None


class ColoredFormatter(logging.Formatter):
    """
    彩色日志格式化器
    
    为不同日志级别设置不同的颜色
    """
    
    COLORS = {
        'DEBUG': Fore.CYAN if COLORAMA_AVAILABLE else '',
        'INFO': Fore.GREEN if COLORAMA_AVAILABLE else '',
        'WARNING': Fore.YELLOW if COLORAMA_AVAILABLE else '',
        'ERROR': Fore.RED if COLORAMA_AVAILABLE else '',
        'CRITICAL': Fore.RED + Style.BRIGHT if COLORAMA_AVAILABLE else '',
    } if COLORAMA_AVAILABLE else {}
    
    def format(self, record):
        """格式化日志记录"""
        if COLORAMA_AVAILABLE:
            levelname = record.levelname
            if levelname in self.COLORS:
                record.levelname = f"{self.COLORS[levelname]}{levelname}{Style.RESET_ALL}"
        
        return super().format(record)


class Logger:
    """
    日志管理器
    
    提供分级日志记录、文件输出、控制台输出功能
    """
    
    _loggers = {}
    
    @classmethod
    def get_logger(cls,
                   name: str = "agentic",
                   level: int = logging.INFO,
                   log_to_file: bool = True,
                   log_to_console: bool = True,
                   log_dir: str = "logs") -> logging.Logger:
        """
        获取日志记录器
        
        Args:
            name: 日志记录器名称
            level: 日志级别
            log_to_file: 是否输出到文件
            log_to_console: 是否输出到控制台
            log_dir: 日志文件目录
            
        Returns:
            配置好的日志记录器
        """
        if name in cls._loggers:
            return cls._loggers[name]
        
        logger = logging.getLogger(name)
        logger.setLevel(level)
        logger.propagate = False
        logger.handlers.clear()
        
        file_format = '%(asctime)s - %(name)s - %(levelname)s - %(filename)s:%(lineno)d - %(message)s'
        console_format = '%(asctime)s - %(levelname)s - %(message)s'
        date_format = '%Y-%m-%d %H:%M:%S'
        
        if log_to_file:
            os.makedirs(log_dir, exist_ok=True)
            log_file = os.path.join(log_dir, f"{name}_{datetime.now().strftime('%Y%m%d')}.log")
            
            file_handler = logging.FileHandler(log_file, encoding='utf-8')
            file_handler.setLevel(level)
            file_formatter = logging.Formatter(file_format, date_format)
            file_handler.setFormatter(file_formatter)
            logger.addHandler(file_handler)
        
        if log_to_console:
            console_handler = logging.StreamHandler()
            console_handler.setLevel(level)
            if COLORAMA_AVAILABLE:
                console_formatter = ColoredFormatter(console_format, date_format)
            else:
                console_formatter = logging.Formatter(console_format, date_format)
            console_handler.setFormatter(console_formatter)
            logger.addHandler(console_handler)
        
        cls._loggers[name] = logger
        return logger
    
    @classmethod
    def set_level(cls, name: str, level: int):
        """
        设置日志级别
        
        Args:
            name: 日志记录器名称
            level: 日志级别
        """
        if name in cls._loggers:
            cls._loggers[name].setLevel(level)
            for handler in cls._loggers[name].handlers:
                handler.setLevel(level)

