"""
测试执行器模块

协调整个测试流程的执行
"""

from typing import Dict, Any, Callable, Optional
from ..config.settings import Settings
from ..api.api_base import APIBase
from ..scenarios.load_patterns import (
    ConstantLoadScenario, RampUpScenario, SpikeScenario, WaveScenario
)
from ..reporters.report_aggregator import ReportAggregator
from ..utils.logger import Logger
from ..mock_data.factory import DataFactory


class TestExecutor:
    """
    测试执行器
    
    协调场景、接口、数据Mock和报告生成
    """
    
    def __init__(self, settings: Settings):
        """
        初始化测试执行器
        
        Args:
            settings: 配置对象
        """
        self.settings = settings
        self.logger = Logger.get_logger("test_executor")
        self.report_aggregator = ReportAggregator(settings.report.report_dir)
        
        # 创建 API 客户端
        self.api_client = self._create_api_client()
        
        # 创建场景
        self.scenario = self._create_scenario()
    
    def _create_api_client(self) -> APIBase:
        """
        创建 API 客户端
        
        Returns:
            API 客户端实例
        """
        return APIBase(
            base_url=self.settings.http.base_url,
            timeout=self.settings.http.timeout,
            max_retries=self.settings.http.max_retries,
            default_headers=self.settings.http.default_headers
        )
    
    def _create_scenario(self):
        """
        根据配置创建场景
        
        Returns:
            场景实例
        """
        scenario_type = self.settings.scenario.type
        scenario_config = self.settings.scenario
        
        if scenario_type == 'constant':
            return ConstantLoadScenario(
                name=self.settings.test_name,
                threads=scenario_config.threads,
                duration=scenario_config.duration
            )
        
        elif scenario_type == 'ramp_up':
            return RampUpScenario(
                name=self.settings.test_name,
                target_threads=scenario_config.target_threads or scenario_config.threads,
                ramp_duration=scenario_config.ramp_duration or 60,
                hold_duration=scenario_config.hold_duration or 60,
                initial_threads=1
            )
        
        elif scenario_type == 'spike':
            return SpikeScenario(
                name=self.settings.test_name,
                spike_threads=scenario_config.spike_threads or scenario_config.threads,
                spike_duration=scenario_config.spike_duration or 10,
                base_threads=scenario_config.base_threads or 10,
                warmup_duration=scenario_config.warmup_duration or 30
            )
        
        elif scenario_type == 'wave':
            return WaveScenario(
                name=self.settings.test_name,
                min_threads=scenario_config.min_threads or 10,
                max_threads=scenario_config.max_threads or scenario_config.threads,
                wave_period=scenario_config.wave_period or 60,
                total_duration=scenario_config.duration
            )
        
        else:
            raise ValueError(f"未知场景类型: {scenario_type}")
    
    def _create_task_function(self) -> Callable:
        """
        创建任务函数
        
        Returns:
            任务函数
        """
        api_config = self.settings.api_config
        
        def task():
            """执行单个请求"""
            # 准备请求参数
            endpoint = api_config.get('endpoint', '/')
            method = api_config.get('method', 'GET').upper()
            headers = api_config.get('headers', {})
            params = api_config.get('params', {})
            
            # 处理请求体（支持模板）
            body = None
            if 'body_template' in api_config:
                body = DataFactory.object(api_config['body_template'])
            elif 'body' in api_config:
                body = api_config['body']
            
            # 发送请求
            if method == 'GET':
                return self.api_client.get(endpoint, params=params, headers=headers)
            elif method == 'POST':
                return self.api_client.post(endpoint, json=body, headers=headers)
            elif method == 'PUT':
                return self.api_client.put(endpoint, json=body, headers=headers)
            elif method == 'DELETE':
                return self.api_client.delete(endpoint, headers=headers)
            elif method == 'PATCH':
                return self.api_client.patch(endpoint, json=body, headers=headers)
            else:
                raise ValueError(f"不支持的 HTTP 方法: {method}")
        
        return task
    
    def execute(self) -> Dict[str, Any]:
        """
        执行测试
        
        Returns:
            测试结果
        """
        self.logger.info(f"开始执行测试: {self.settings.test_name}")
        self.logger.info(f"场景类型: {self.settings.scenario.type}")
        self.logger.info(f"API: {self.settings.http.base_url}{self.settings.api_config.get('endpoint', '')}")
        
        try:
            # 设置任务函数
            task_func = self._create_task_function()
            self.scenario.set_task(task_func)
            
            # 获取控制台报告器用于实时输出
            console_reporter = self.report_aggregator.get_reporter('console')
            
            # 执行场景
            self.logger.info("场景执行中...")
            metrics = self.scenario.execute()
            
            # 生成报告数据
            report_data = {
                'test_name': self.settings.test_name,
                'description': self.settings.description,
                'scenario_type': self.settings.scenario.type,
                'api_endpoint': f"{self.settings.http.base_url}{self.settings.api_config.get('endpoint', '')}",
                'metrics': metrics
            }
            
            # 生成报告
            self.logger.info("生成测试报告...")
            report_files = self.report_aggregator.generate_reports(
                data=report_data,
                formats=self.settings.report.formats
            )
            
            self.logger.info("测试执行完成")
            
            return {
                'success': True,
                'metrics': metrics,
                'reports': report_files
            }
            
        except KeyboardInterrupt:
            self.logger.warning("测试被用户中断")
            self.scenario.stop()
            return {
                'success': False,
                'error': '用户中断'
            }
        
        except Exception as e:
            self.logger.error(f"测试执行失败: {str(e)}", exc_info=True)
            return {
                'success': False,
                'error': str(e)
            }
        
        finally:
            # 清理资源
            self.api_client.close()
    
    def set_custom_task(self, task_func: Callable):
        """
        设置自定义任务函数
        
        Args:
            task_func: 任务函数
        """
        self.scenario.set_task(task_func)

