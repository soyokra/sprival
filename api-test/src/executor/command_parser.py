"""
命令行参数解析器模块

解析命令行参数
"""

import argparse
from typing import Dict, Any


class CommandParser:
    """
    命令行参数解析器
    
    解析和处理命令行参数
    """
    
    def __init__(self):
        self.parser = self._create_parser()
    
    def _create_parser(self) -> argparse.ArgumentParser:
        """
        创建参数解析器
        
        Returns:
            ArgumentParser 对象
        """
        parser = argparse.ArgumentParser(
            description='API Test Framework - 功能完善的 REST API 接口测试框架',
            formatter_class=argparse.RawDescriptionHelpFormatter,
            epilog="""
示例:
  # 使用配置文件
  python main.py --config configs/default.json
  
  # 命令行指定参数
  python main.py --url https://api.example.com --scenario constant --threads 10 --duration 60
  
  # 快速执行预定义场景
  python main.py quick smoke_test
  python main.py quick stress_test
            """
        )
        
        # 子命令
        subparsers = parser.add_subparsers(dest='command', help='命令')
        
        # quick 子命令
        quick_parser = subparsers.add_parser('quick', help='快速执行预定义场景')
        quick_parser.add_argument('scenario_name', choices=[
            'smoke_test', 'stress_test', 'stability_test', 'spike_test'
        ], help='场景名称')
        quick_parser.add_argument('--url', help='API 基础 URL')
        quick_parser.add_argument('--endpoint', help='API 端点')
        
        # 配置文件参数
        parser.add_argument(
            '--config',
            type=str,
            help='配置文件路径'
        )
        
        # HTTP 参数
        parser.add_argument(
            '--url',
            type=str,
            help='API 基础 URL'
        )
        
        parser.add_argument(
            '--endpoint',
            type=str,
            help='API 端点'
        )
        
        parser.add_argument(
            '--method',
            type=str,
            choices=['GET', 'POST', 'PUT', 'DELETE', 'PATCH'],
            default='GET',
            help='HTTP 方法'
        )
        
        parser.add_argument(
            '--timeout',
            type=int,
            default=30,
            help='请求超时时间（秒）'
        )
        
        # 场景参数
        parser.add_argument(
            '--scenario',
            type=str,
            choices=['constant', 'ramp_up', 'spike', 'wave'],
            default='constant',
            help='场景类型'
        )
        
        parser.add_argument(
            '--threads',
            type=int,
            default=10,
            help='并发线程数'
        )
        
        parser.add_argument(
            '--duration',
            type=int,
            default=60,
            help='测试持续时间（秒）'
        )
        
        # RampUp 参数
        parser.add_argument(
            '--target-threads',
            type=int,
            help='目标线程数（ramp_up 场景）'
        )
        
        parser.add_argument(
            '--ramp-duration',
            type=int,
            help='渐进时长（秒，ramp_up 场景）'
        )
        
        parser.add_argument(
            '--hold-duration',
            type=int,
            help='保持时长（秒，ramp_up 场景）'
        )
        
        # Spike 参数
        parser.add_argument(
            '--spike-threads',
            type=int,
            help='峰值线程数（spike 场景）'
        )
        
        parser.add_argument(
            '--spike-duration',
            type=int,
            help='峰值持续时间（秒，spike 场景）'
        )
        
        # 报告参数
        parser.add_argument(
            '--reports',
            type=str,
            default='console,json,html',
            help='报告格式，逗号分隔（console,json,html）'
        )
        
        parser.add_argument(
            '--report-dir',
            type=str,
            default='reports',
            help='报告输出目录'
        )
        
        # 日志参数
        parser.add_argument(
            '--log-level',
            type=str,
            choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'],
            default='INFO',
            help='日志级别'
        )
        
        parser.add_argument(
            '--test-name',
            type=str,
            help='测试名称'
        )
        
        return parser
    
    def parse(self, args=None) -> argparse.Namespace:
        """
        解析命令行参数
        
        Args:
            args: 参数列表（不指定则从 sys.argv 获取）
            
        Returns:
            解析后的参数对象
        """
        return self.parser.parse_args(args)
    
    def to_config_dict(self, args: argparse.Namespace) -> Dict[str, Any]:
        """
        将命令行参数转换为配置字典
        
        Args:
            args: 解析后的参数对象
            
        Returns:
            配置字典
        """
        config = {
            'test_name': args.test_name or 'API Test',
            'http': {},
            'scenario': {
                'type': args.scenario,
                'threads': args.threads,
                'duration': args.duration
            },
            'report': {
                'formats': args.reports.split(','),
                'report_dir': args.report_dir
            },
            'log': {
                'level': args.log_level
            },
            'api': {}
        }
        
        # HTTP 配置
        if args.url:
            config['http']['base_url'] = args.url
        if args.timeout:
            config['http']['timeout'] = args.timeout
        
        # API 配置
        if args.endpoint:
            config['api']['endpoint'] = args.endpoint
        if args.method:
            config['api']['method'] = args.method
        
        # 场景特定参数
        if args.scenario == 'ramp_up':
            if args.target_threads:
                config['scenario']['target_threads'] = args.target_threads
            if args.ramp_duration:
                config['scenario']['ramp_duration'] = args.ramp_duration
            if args.hold_duration:
                config['scenario']['hold_duration'] = args.hold_duration
        
        elif args.scenario == 'spike':
            if args.spike_threads:
                config['scenario']['spike_threads'] = args.spike_threads
            if args.spike_duration:
                config['scenario']['spike_duration'] = args.spike_duration
        
        return config

