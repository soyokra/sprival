"""
配置 Schema 验证模块

定义配置文件的 JSON Schema 并提供验证功能
"""

from typing import Dict, Any
from jsonschema import validate, ValidationError as JsonSchemaValidationError


# JSON Schema 定义
CONFIG_SCHEMA = {
    "type": "object",
    "properties": {
        "test_name": {
            "type": "string",
            "description": "测试名称"
        },
        "description": {
            "type": "string",
            "description": "测试描述"
        },
        "http": {
            "type": "object",
            "properties": {
                "base_url": {"type": "string"},
                "timeout": {"type": "integer", "minimum": 1},
                "max_retries": {"type": "integer", "minimum": 0},
                "verify_ssl": {"type": "boolean"},
                "default_headers": {"type": "object"}
            }
        },
        "scenario": {
            "type": "object",
            "properties": {
                "type": {
                    "type": "string",
                    "enum": ["constant", "ramp_up", "spike", "wave"]
                },
                "threads": {"type": "integer", "minimum": 1},
                "duration": {"type": "integer", "minimum": 0},
                "target_threads": {"type": "integer", "minimum": 1},
                "ramp_duration": {"type": "integer", "minimum": 1},
                "hold_duration": {"type": "integer", "minimum": 0},
                "spike_threads": {"type": "integer", "minimum": 1},
                "spike_duration": {"type": "integer", "minimum": 1},
                "base_threads": {"type": "integer", "minimum": 1},
                "warmup_duration": {"type": "integer", "minimum": 0},
                "min_threads": {"type": "integer", "minimum": 1},
                "max_threads": {"type": "integer", "minimum": 1},
                "wave_period": {"type": "integer", "minimum": 1}
            },
            "required": ["type"]
        },
        "report": {
            "type": "object",
            "properties": {
                "formats": {
                    "type": "array",
                    "items": {
                        "type": "string",
                        "enum": ["console", "json", "html"]
                    }
                },
                "report_dir": {"type": "string"},
                "auto_save": {"type": "boolean"}
            }
        },
        "log": {
            "type": "object",
            "properties": {
                "level": {
                    "type": "string",
                    "enum": ["DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL"]
                },
                "log_to_file": {"type": "boolean"},
                "log_to_console": {"type": "boolean"},
                "log_dir": {"type": "string"}
            }
        },
        "api": {
            "type": "object",
            "properties": {
                "endpoint": {"type": "string"},
                "method": {
                    "type": "string",
                    "enum": ["GET", "POST", "PUT", "DELETE", "PATCH"]
                },
                "headers": {"type": "object"},
                "params": {"type": "object"},
                "body": {"type": "object"},
                "body_template": {"type": "object"}
            }
        }
    }
}


class ConfigValidator:
    """
    配置验证器
    
    验证配置文件是否符合 Schema 定义
    """
    
    @staticmethod
    def validate(config: Dict[str, Any], schema: Dict[str, Any] = None) -> bool:
        """
        验证配置
        
        Args:
            config: 配置字典
            schema: Schema 定义（不指定则使用默认 Schema）
            
        Returns:
            是否验证通过
            
        Raises:
            JsonSchemaValidationError: 验证失败时抛出
        """
        if schema is None:
            schema = CONFIG_SCHEMA
        
        try:
            validate(instance=config, schema=schema)
            return True
        except JsonSchemaValidationError as e:
            raise ValueError(f"配置验证失败: {e.message}")
    
    @staticmethod
    def validate_scenario_params(scenario_type: str, params: Dict[str, Any]) -> bool:
        """
        验证场景参数
        
        Args:
            scenario_type: 场景类型
            params: 场景参数
            
        Returns:
            是否验证通过
            
        Raises:
            ValueError: 参数缺失或不合法
        """
        if scenario_type == "constant":
            required = ["threads", "duration"]
            for param in required:
                if param not in params:
                    raise ValueError(f"constant 场景缺少必需参数: {param}")
        
        elif scenario_type == "ramp_up":
            required = ["target_threads", "ramp_duration", "hold_duration"]
            for param in required:
                if param not in params:
                    raise ValueError(f"ramp_up 场景缺少必需参数: {param}")
        
        elif scenario_type == "spike":
            required = ["spike_threads", "spike_duration"]
            for param in required:
                if param not in params:
                    raise ValueError(f"spike 场景缺少必需参数: {param}")
        
        elif scenario_type == "wave":
            required = ["min_threads", "max_threads", "wave_period", "total_duration"]
            for param in required:
                if param not in params and param != "total_duration":
                    raise ValueError(f"wave 场景缺少必需参数: {param}")
        
        return True

