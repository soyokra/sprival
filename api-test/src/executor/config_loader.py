"""
配置加载器模块

加载和解析 JSON 配置文件
"""

import json
import os
from typing import Dict, Any
from ..config.settings import Settings
from ..config.schema import ConfigValidator
from ..utils.logger import Logger


class ConfigLoader:
    """
    配置加载器
    
    从 JSON 文件加载配置并验证
    """
    
    def __init__(self):
        self.logger = Logger.get_logger("config_loader")
        self.validator = ConfigValidator()
    
    def load(self, config_path: str) -> Settings:
        """
        加载配置文件
        
        Args:
            config_path: 配置文件路径
            
        Returns:
            Settings 对象
            
        Raises:
            FileNotFoundError: 配置文件不存在
            ValueError: 配置格式错误或验证失败
        """
        # 检查文件是否存在
        if not os.path.exists(config_path):
            raise FileNotFoundError(f"配置文件不存在: {config_path}")
        
        self.logger.info(f"加载配置文件: {config_path}")
        
        try:
            # 读取 JSON 文件
            with open(config_path, 'r', encoding='utf-8') as f:
                config_data = json.load(f)
            
            # 验证配置
            self.validator.validate(config_data)
            
            # 创建 Settings 对象
            settings = Settings.from_dict(config_data)
            
            self.logger.info(f"配置加载成功: {settings.test_name}")
            return settings
            
        except json.JSONDecodeError as e:
            raise ValueError(f"JSON 解析失败: {str(e)}")
        except Exception as e:
            raise ValueError(f"配置加载失败: {str(e)}")
    
    def load_from_dict(self, config_data: Dict[str, Any]) -> Settings:
        """
        从字典加载配置
        
        Args:
            config_data: 配置字典
            
        Returns:
            Settings 对象
        """
        # 验证配置
        self.validator.validate(config_data)
        
        # 创建 Settings 对象
        return Settings.from_dict(config_data)
    
    def save(self, settings: Settings, config_path: str):
        """
        保存配置到文件
        
        Args:
            settings: Settings 对象
            config_path: 配置文件路径
        """
        # 确保目录存在
        os.makedirs(os.path.dirname(config_path), exist_ok=True)
        
        # 转换为字典
        config_data = settings.to_dict()
        
        # 写入文件
        with open(config_path, 'w', encoding='utf-8') as f:
            json.dump(config_data, f, indent=2, ensure_ascii=False)
        
        self.logger.info(f"配置已保存: {config_path}")

