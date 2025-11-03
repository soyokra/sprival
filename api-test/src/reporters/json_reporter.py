"""
JSON 报告模块

生成 JSON 格式的测试报告
"""

import json
import os
from datetime import datetime
from typing import Dict, Any
from .base_reporter import BaseReporter


class JsonReporter(BaseReporter):
    """
    JSON 报告生成器
    
    生成机器可读的 JSON 格式报告
    """
    
    def __init__(self, report_dir: str = "reports"):
        """
        初始化 JSON 报告生成器
        
        Args:
            report_dir: 报告输出目录
        """
        super().__init__(report_dir)
    
    def generate(self) -> str:
        """
        生成 JSON 报告内容
        
        Returns:
            JSON 字符串
        """
        if not self.report_data:
            return json.dumps({"error": "无报告数据"}, indent=2, ensure_ascii=False)
        
        return json.dumps(self.report_data, indent=2, ensure_ascii=False)
    
    def save(self, filename: str = None) -> str:
        """
        保存 JSON 报告到文件
        
        Args:
            filename: 文件名（不指定则自动生成）
            
        Returns:
            保存的文件路径
        """
        # 确保报告目录存在
        os.makedirs(self.report_dir, exist_ok=True)
        
        # 生成文件名
        if filename is None:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            test_name = self.report_data.get('test_name', 'test')
            filename = f"{test_name}_report_{timestamp}.json"
        
        # 保存文件
        filepath = os.path.join(self.report_dir, filename)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(self.generate())
        
        return filepath
    
    def load(self, filepath: str) -> Dict[str, Any]:
        """
        从文件加载报告
        
        Args:
            filepath: 文件路径
            
        Returns:
            报告数据
        """
        with open(filepath, 'r', encoding='utf-8') as f:
            self.report_data = json.load(f)
        return self.report_data

