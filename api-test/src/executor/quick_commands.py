"""
快速命令模块

预定义常用测试场景的快速执行命令
"""

from typing import Dict, Any


class QuickCommands:
    """
    快速命令管理器
    
    提供预定义的测试场景
    """
    
    # 预定义场景配置
    SCENARIOS = {
        'smoke_test': {
            'test_name': 'Smoke Test',
            'description': '冒烟测试 - 快速验证基本功能',
            'scenario': {
                'type': 'constant',
                'threads': 5,
                'duration': 30
            },
            'http': {
                'timeout': 10,
                'max_retries': 1
            },
            'report': {
                'formats': ['console']
            }
        },
        
        'stress_test': {
            'test_name': 'Stress Test',
            'description': '压力测试 - 持续高并发',
            'scenario': {
                'type': 'ramp_up',
                'target_threads': 100,
                'ramp_duration': 30,
                'hold_duration': 120,
                'initial_threads': 10,
                'ramp_steps': 5
            },
            'report': {
                'formats': ['console', 'json', 'html']
            }
        },
        
        'stability_test': {
            'test_name': 'Stability Test',
            'description': '稳定性测试 - 长时间运行',
            'scenario': {
                'type': 'constant',
                'threads': 20,
                'duration': 3600  # 1 小时
            },
            'report': {
                'formats': ['console', 'json', 'html']
            }
        },
        
        'spike_test': {
            'test_name': 'Spike Test',
            'description': '峰值测试 - 突发流量冲击',
            'scenario': {
                'type': 'spike',
                'spike_threads': 200,
                'spike_duration': 10,
                'base_threads': 10,
                'warmup_duration': 30
            },
            'report': {
                'formats': ['console', 'json', 'html']
            }
        },
        
        'wave_test': {
            'test_name': 'Wave Test',
            'description': '波浪测试 - 周期性负载变化',
            'scenario': {
                'type': 'wave',
                'min_threads': 10,
                'max_threads': 100,
                'wave_period': 60,
                'total_duration': 300
            },
            'report': {
                'formats': ['console', 'json', 'html']
            }
        }
    }
    
    @classmethod
    def get_scenario(cls, name: str, base_url: str = None, endpoint: str = None) -> Dict[str, Any]:
        """
        获取预定义场景配置
        
        Args:
            name: 场景名称
            base_url: API 基础 URL（可选）
            endpoint: API 端点（可选）
            
        Returns:
            场景配置字典
            
        Raises:
            ValueError: 场景不存在
        """
        if name not in cls.SCENARIOS:
            available = ', '.join(cls.SCENARIOS.keys())
            raise ValueError(f"未知场景: {name}。可用场景: {available}")
        
        # 深拷贝配置
        import copy
        config = copy.deepcopy(cls.SCENARIOS[name])
        
        # 应用自定义参数
        if base_url:
            if 'http' not in config:
                config['http'] = {}
            config['http']['base_url'] = base_url
        
        if endpoint:
            if 'api' not in config:
                config['api'] = {}
            config['api']['endpoint'] = endpoint
            config['api']['method'] = 'GET'
        
        return config
    
    @classmethod
    def list_scenarios(cls) -> Dict[str, str]:
        """
        列出所有可用场景
        
        Returns:
            场景名称到描述的映射
        """
        return {
            name: config['description']
            for name, config in cls.SCENARIOS.items()
        }
    
    @classmethod
    def print_scenarios(cls):
        """打印所有可用场景"""
        print("\n可用的快速场景:\n")
        for name, description in cls.list_scenarios().items():
            print(f"  • {name:<20} - {description}")
        print()

