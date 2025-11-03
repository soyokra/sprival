"""
日志管理模块

提供统一的日志管理功能
"""

import logging
import os
from datetime import datetime
from typing import Optional
from colorama import init, Fore, Style

# 初始化 colorama
init(autoreset=True)


class ColoredFormatter(logging.Formatter):
    """
    彩色日志格式化器
    
    为不同日志级别设置不同的颜色
    """
    
    COLORS = {
        'DEBUG': Fore.CYAN,
        'INFO': Fore.GREEN,
        'WARNING': Fore.YELLOW,
        'ERROR': Fore.RED,
        'CRITICAL': Fore.RED + Style.BRIGHT,
    }
    
    def format(self, record):
        """格式化日志记录"""
        # 添加颜色
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
                   name: str = "api_test",
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
        # 如果已经创建过，直接返回
        if name in cls._loggers:
            return cls._loggers[name]
        
        # 创建日志记录器
        logger = logging.getLogger(name)
        logger.setLevel(level)
        logger.propagate = False
        
        # 清除已有的处理器
        logger.handlers.clear()
        
        # 日志格式
        file_format = '%(asctime)s - %(name)s - %(levelname)s - %(filename)s:%(lineno)d - %(message)s'
        console_format = '%(asctime)s - %(levelname)s - %(message)s'
        
        date_format = '%Y-%m-%d %H:%M:%S'
        
        # 添加文件处理器
        if log_to_file:
            os.makedirs(log_dir, exist_ok=True)
            log_file = os.path.join(log_dir, f"{name}_{datetime.now().strftime('%Y%m%d')}.log")
            
            file_handler = logging.FileHandler(log_file, encoding='utf-8')
            file_handler.setLevel(level)
            file_formatter = logging.Formatter(file_format, date_format)
            file_handler.setFormatter(file_formatter)
            logger.addHandler(file_handler)
        
        # 添加控制台处理器
        if log_to_console:
            console_handler = logging.StreamHandler()
            console_handler.setLevel(level)
            console_formatter = ColoredFormatter(console_format, date_format)
            console_handler.setFormatter(console_formatter)
            logger.addHandler(console_handler)
        
        # 缓存日志记录器
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


# 默认日志记录器
default_logger = Logger.get_logger()


def debug(msg: str, *args, **kwargs):
    """记录 DEBUG 级别日志"""
    default_logger.debug(msg, *args, **kwargs)


def info(msg: str, *args, **kwargs):
    """记录 INFO 级别日志"""
    default_logger.info(msg, *args, **kwargs)


def warning(msg: str, *args, **kwargs):
    """记录 WARNING 级别日志"""
    default_logger.warning(msg, *args, **kwargs)


def error(msg: str, *args, **kwargs):
    """记录 ERROR 级别日志"""
    default_logger.error(msg, *args, **kwargs)


def critical(msg: str, *args, **kwargs):
    """记录 CRITICAL 级别日志"""
    default_logger.critical(msg, *args, **kwargs)

