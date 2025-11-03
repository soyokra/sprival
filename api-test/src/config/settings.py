"""
全局设置模块

管理框架的全局配置
"""

from typing import Dict, Any, Optional
from dataclasses import dataclass, field


@dataclass
class HttpSettings:
    """HTTP 客户端设置"""
    base_url: str = ""
    timeout: int = 30
    max_retries: int = 3
    verify_ssl: bool = True
    default_headers: Dict[str, str] = field(default_factory=dict)


@dataclass
class ScenarioSettings:
    """场景设置"""
    type: str = "constant"  # constant, ramp_up, spike, wave
    threads: int = 10
    duration: int = 60
    
    # RampUp 特定参数
    target_threads: Optional[int] = None
    ramp_duration: Optional[int] = None
    hold_duration: Optional[int] = None
    
    # Spike 特定参数
    spike_threads: Optional[int] = None
    spike_duration: Optional[int] = None
    base_threads: Optional[int] = None
    warmup_duration: Optional[int] = None
    
    # Wave 特定参数
    min_threads: Optional[int] = None
    max_threads: Optional[int] = None
    wave_period: Optional[int] = None


@dataclass
class ReportSettings:
    """报告设置"""
    formats: list = field(default_factory=lambda: ["console", "json", "html"])
    report_dir: str = "reports"
    auto_save: bool = True


@dataclass
class LogSettings:
    """日志设置"""
    level: str = "INFO"
    log_to_file: bool = True
    log_to_console: bool = True
    log_dir: str = "logs"


@dataclass
class Settings:
    """全局设置"""
    # 测试基本信息
    test_name: str = "API Test"
    description: str = ""
    
    # 各模块设置
    http: HttpSettings = field(default_factory=HttpSettings)
    scenario: ScenarioSettings = field(default_factory=ScenarioSettings)
    report: ReportSettings = field(default_factory=ReportSettings)
    log: LogSettings = field(default_factory=LogSettings)
    
    # API 配置
    api_config: Dict[str, Any] = field(default_factory=dict)
    
    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'Settings':
        """
        从字典创建设置对象
        
        Args:
            data: 配置字典
            
        Returns:
            Settings 实例
        """
        settings = cls()
        
        # 基本信息
        settings.test_name = data.get('test_name', settings.test_name)
        settings.description = data.get('description', settings.description)
        
        # HTTP 设置
        if 'http' in data:
            http_data = data['http']
            settings.http = HttpSettings(
                base_url=http_data.get('base_url', ''),
                timeout=http_data.get('timeout', 30),
                max_retries=http_data.get('max_retries', 3),
                verify_ssl=http_data.get('verify_ssl', True),
                default_headers=http_data.get('default_headers', {})
            )
        
        # 场景设置
        if 'scenario' in data:
            scenario_data = data['scenario']
            settings.scenario = ScenarioSettings(**scenario_data)
        
        # 报告设置
        if 'report' in data:
            report_data = data['report']
            settings.report = ReportSettings(
                formats=report_data.get('formats', ['console', 'json', 'html']),
                report_dir=report_data.get('report_dir', 'reports'),
                auto_save=report_data.get('auto_save', True)
            )
        
        # 日志设置
        if 'log' in data:
            log_data = data['log']
            settings.log = LogSettings(
                level=log_data.get('level', 'INFO'),
                log_to_file=log_data.get('log_to_file', True),
                log_to_console=log_data.get('log_to_console', True),
                log_dir=log_data.get('log_dir', 'logs')
            )
        
        # API 配置
        settings.api_config = data.get('api', {})
        
        return settings
    
    def to_dict(self) -> Dict[str, Any]:
        """
        转换为字典
        
        Returns:
            配置字典
        """
        return {
            'test_name': self.test_name,
            'description': self.description,
            'http': {
                'base_url': self.http.base_url,
                'timeout': self.http.timeout,
                'max_retries': self.http.max_retries,
                'verify_ssl': self.http.verify_ssl,
                'default_headers': self.http.default_headers
            },
            'scenario': {
                'type': self.scenario.type,
                'threads': self.scenario.threads,
                'duration': self.scenario.duration,
                'target_threads': self.scenario.target_threads,
                'ramp_duration': self.scenario.ramp_duration,
                'hold_duration': self.scenario.hold_duration,
                'spike_threads': self.scenario.spike_threads,
                'spike_duration': self.scenario.spike_duration,
                'base_threads': self.scenario.base_threads,
                'warmup_duration': self.scenario.warmup_duration,
                'min_threads': self.scenario.min_threads,
                'max_threads': self.scenario.max_threads,
                'wave_period': self.scenario.wave_period
            },
            'report': {
                'formats': self.report.formats,
                'report_dir': self.report.report_dir,
                'auto_save': self.report.auto_save
            },
            'log': {
                'level': self.log.level,
                'log_to_file': self.log.log_to_file,
                'log_to_console': self.log.log_to_console,
                'log_dir': self.log.log_dir
            },
            'api': self.api_config
        }

