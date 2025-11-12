"""
HTML 报告模块

生成美观的 HTML 格式测试报告
"""

import os
from datetime import datetime
from typing import Dict, Any
from jinja2 import Template
from .base_reporter import BaseReporter


# HTML 模板
HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>API 测试报告 - {{ test_name }}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
            min-height: 100vh;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            font-size: 32px;
            margin-bottom: 10px;
        }
        .header p {
            opacity: 0.9;
        }
        .content {
            padding: 30px;
        }
        .section {
            margin-bottom: 30px;
        }
        .section-title {
            font-size: 24px;
            color: #333;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #667eea;
        }
        .metrics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }
        .metric-card {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            border-left: 4px solid #667eea;
        }
        .metric-card.success {
            border-left-color: #28a745;
        }
        .metric-card.warning {
            border-left-color: #ffc107;
        }
        .metric-card.danger {
            border-left-color: #dc3545;
        }
        .metric-label {
            font-size: 14px;
            color: #666;
            margin-bottom: 5px;
        }
        .metric-value {
            font-size: 32px;
            font-weight: bold;
            color: #333;
        }
        .metric-unit {
            font-size: 16px;
            color: #999;
            margin-left: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        th {
            background: #667eea;
            color: white;
            padding: 12px;
            text-align: left;
        }
        td {
            padding: 12px;
            border-bottom: 1px solid #eee;
        }
        tr:hover {
            background: #f8f9fa;
        }
        .status-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
        }
        .status-success {
            background: #d4edda;
            color: #155724;
        }
        .status-error {
            background: #f8d7da;
            color: #721c24;
        }
        .footer {
            background: #f8f9fa;
            padding: 20px;
            text-align: center;
            color: #666;
        }
        .chart-placeholder {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 40px;
            text-align: center;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚀 API 测试报告</h1>
            <p>{{ test_name }} - {{ report_time }}</p>
        </div>
        
        <div class="content">
            <!-- 概览 -->
            <div class="section">
                <h2 class="section-title">📊 测试概览</h2>
                <div class="metrics-grid">
                    <div class="metric-card">
                        <div class="metric-label">总请求数</div>
                        <div class="metric-value">{{ metrics.total_requests }}</div>
                    </div>
                    <div class="metric-card success">
                        <div class="metric-label">成功请求</div>
                        <div class="metric-value">{{ metrics.success_requests }}</div>
                    </div>
                    <div class="metric-card {% if metrics.failed_requests > 0 %}danger{% endif %}">
                        <div class="metric-label">失败请求</div>
                        <div class="metric-value">{{ metrics.failed_requests }}</div>
                    </div>
                    <div class="metric-card {% if metrics.success_rate >= 99 %}success{% elif metrics.success_rate >= 95 %}warning{% else %}danger{% endif %}">
                        <div class="metric-label">成功率</div>
                        <div class="metric-value">{{ "%.2f"|format(metrics.success_rate) }}<span class="metric-unit">%</span></div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">运行时间</div>
                        <div class="metric-value">{{ "%.2f"|format(metrics.elapsed_time) }}<span class="metric-unit">秒</span></div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">QPS</div>
                        <div class="metric-value">{{ "%.2f"|format(metrics.qps) }}</div>
                    </div>
                </div>
            </div>
            
            <!-- 响应时间 -->
            <div class="section">
                <h2 class="section-title">⏱️ 响应时间统计（毫秒）</h2>
                <table>
                    <tr>
                        <th>指标</th>
                        <th>最小值</th>
                        <th>平均值</th>
                        <th>中位数</th>
                        <th>P90</th>
                        <th>P95</th>
                        <th>P99</th>
                        <th>最大值</th>
                    </tr>
                    <tr>
                        <td><strong>响应时间</strong></td>
                        <td>{{ "%.2f"|format(metrics.response_time.min) }}</td>
                        <td>{{ "%.2f"|format(metrics.response_time.mean) }}</td>
                        <td>{{ "%.2f"|format(metrics.response_time.median) }}</td>
                        <td>{{ "%.2f"|format(metrics.response_time.p90) }}</td>
                        <td>{{ "%.2f"|format(metrics.response_time.p95) }}</td>
                        <td>{{ "%.2f"|format(metrics.response_time.p99) }}</td>
                        <td>{{ "%.2f"|format(metrics.response_time.max) }}</td>
                    </tr>
                </table>
            </div>
            
            <!-- 状态码分布 -->
            {% if metrics.status_codes %}
            <div class="section">
                <h2 class="section-title">📈 状态码分布</h2>
                <table>
                    <tr>
                        <th>状态码</th>
                        <th>请求数</th>
                        <th>占比</th>
                        <th>状态</th>
                    </tr>
                    {% for code, count in metrics.status_codes.items() %}
                    <tr>
                        <td><strong>{{ code }}</strong></td>
                        <td>{{ count }}</td>
                        <td>{{ "%.2f"|format(count / metrics.total_requests * 100) }}%</td>
                        <td>
                            {% if 200 <= code < 300 %}
                            <span class="status-badge status-success">成功</span>
                            {% else %}
                            <span class="status-badge status-error">错误</span>
                            {% endif %}
                        </td>
                    </tr>
                    {% endfor %}
                </table>
            </div>
            {% endif %}
            
            <!-- 错误统计 -->
            {% if metrics.errors %}
            <div class="section">
                <h2 class="section-title">❌ 错误统计</h2>
                <table>
                    <tr>
                        <th>错误信息</th>
                        <th>出现次数</th>
                        <th>占比</th>
                    </tr>
                    {% for error, count in metrics.errors.items() %}
                    <tr>
                        <td>{{ error }}</td>
                        <td>{{ count }}</td>
                        <td>{{ "%.2f"|format(count / metrics.total_requests * 100) }}%</td>
                    </tr>
                    {% endfor %}
                </table>
            </div>
            {% endif %}
        </div>
        
        <div class="footer">
            <p>生成时间: {{ report_time }} | 测试开始: {{ start_time }}</p>
            <p>Powered by API Test Framework v1.0</p>
        </div>
    </div>
</body>
</html>
"""


class HtmlReporter(BaseReporter):
    """
    HTML 报告生成器
    
    生成美观的 HTML 格式报告
    """
    
    def __init__(self, report_dir: str = "reports"):
        """
        初始化 HTML 报告生成器
        
        Args:
            report_dir: 报告输出目录
        """
        super().__init__(report_dir)
        self.template = Template(HTML_TEMPLATE)
    
    def generate(self) -> str:
        """
        生成 HTML 报告内容
        
        Returns:
            HTML 字符串
        """
        if not self.report_data:
            return "<html><body><h1>无报告数据</h1></body></html>"
        
        # 准备模板数据
        template_data = {
            "test_name": self.report_data.get('test_name', 'API Test'),
            "report_time": self.report_data.get('report_time', ''),
            "start_time": self.report_data.get('start_time', ''),
            "metrics": self.report_data.get('metrics', {})
        }
        
        return self.template.render(**template_data)
    
    def save(self, filename: str = None) -> str:
        """
        保存 HTML 报告到文件
        
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
            filename = f"{test_name}_report_{timestamp}.html"
        
        # 保存文件
        filepath = os.path.join(self.report_dir, filename)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(self.generate())
        
        return filepath

