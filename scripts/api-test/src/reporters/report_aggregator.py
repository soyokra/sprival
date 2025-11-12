"""
报告聚合器模块

统一管理多种格式的报告生成
"""

from typing import Dict, Any, List
from .console_reporter import ConsoleReporter
from .json_reporter import JsonReporter
from .html_reporter import HtmlReporter
from ..utils.logger import Logger


class ReportAggregator:
    """
    报告聚合器
    
    同时生成多种格式的报告
    """
    
    def __init__(self, report_dir: str = "reports"):
        """
        初始化报告聚合器
        
        Args:
            report_dir: 报告输出目录
        """
        self.report_dir = report_dir
        self.reporters = {
            "console": ConsoleReporter(),
            "json": JsonReporter(report_dir),
            "html": HtmlReporter(report_dir)
        }
        self.logger = Logger.get_logger("report_aggregator")
    
    def generate_reports(self,
                        data: Dict[str, Any],
                        formats: List[str] = None) -> Dict[str, str]:
        """
        生成指定格式的报告
        
        Args:
            data: 报告数据
            formats: 报告格式列表，可选值：console, json, html
                    不指定则生成所有格式
            
        Returns:
            格式到文件路径的映射
        """
        if formats is None:
            formats = ["console", "json", "html"]
        
        results = {}
        
        for format_name in formats:
            if format_name not in self.reporters:
                self.logger.warning(f"不支持的报告格式: {format_name}")
                continue
            
            try:
                reporter = self.reporters[format_name]
                reporter.set_report_data(data)
                filepath = reporter.save()
                results[format_name] = filepath
                
                self.logger.info(f"生成 {format_name.upper()} 报告: {filepath}")
            except Exception as e:
                self.logger.error(f"生成 {format_name} 报告失败: {str(e)}", exc_info=True)
                results[format_name] = f"ERROR: {str(e)}"
        
        return results
    
    def get_reporter(self, format_name: str):
        """
        获取指定格式的报告生成器
        
        Args:
            format_name: 格式名称
            
        Returns:
            报告生成器实例
        """
        return self.reporters.get(format_name)
    
    def add_reporter(self, name: str, reporter):
        """
        添加自定义报告生成器
        
        Args:
            name: 报告格式名称
            reporter: 报告生成器实例
        """
        self.reporters[name] = reporter
        self.logger.info(f"添加自定义报告生成器: {name}")

