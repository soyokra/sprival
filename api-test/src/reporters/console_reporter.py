"""
控制台报告模块

实时输出测试进度和结果到控制台
"""

import sys
from typing import Dict, Any
from colorama import Fore, Style
from .base_reporter import BaseReporter


class ConsoleReporter(BaseReporter):
    """
    控制台报告生成器
    
    实时输出测试结果到控制台
    """
    
    def __init__(self):
        """初始化控制台报告生成器"""
        super().__init__()
        self.verbose = True
    
    def generate(self) -> str:
        """生成控制台报告内容"""
        if not self.report_data:
            return "无报告数据"
        
        lines = []
        lines.append("\n" + "=" * 80)
        lines.append(f"{Fore.CYAN}测试报告{Style.RESET_ALL}".center(86))
        lines.append("=" * 80)
        
        # 基本信息
        lines.append(f"\n{Fore.YELLOW}▶ 基本信息{Style.RESET_ALL}")
        lines.append(f"  测试名称: {self.report_data.get('test_name', 'N/A')}")
        lines.append(f"  开始时间: {self.report_data.get('start_time', 'N/A')}")
        lines.append(f"  报告时间: {self.report_data.get('report_time', 'N/A')}")
        
        # 性能指标
        metrics = self.report_data.get('metrics', {})
        if metrics:
            lines.append(f"\n{Fore.YELLOW}▶ 性能指标{Style.RESET_ALL}")
            
            total_requests = metrics.get('total_requests', 0)
            success_requests = metrics.get('success_requests', 0)
            failed_requests = metrics.get('failed_requests', 0)
            success_rate = metrics.get('success_rate', 0)
            elapsed_time = metrics.get('elapsed_time', 0)
            qps = metrics.get('qps', 0)
            
            # 请求统计
            lines.append(f"  总请求数: {Fore.GREEN}{total_requests}{Style.RESET_ALL}")
            lines.append(f"  成功请求: {Fore.GREEN}{success_requests}{Style.RESET_ALL}")
            lines.append(f"  失败请求: {Fore.RED}{failed_requests}{Style.RESET_ALL}")
            
            # 成功率着色
            if success_rate >= 99:
                success_color = Fore.GREEN
            elif success_rate >= 95:
                success_color = Fore.YELLOW
            else:
                success_color = Fore.RED
            lines.append(f"  成功率: {success_color}{success_rate:.2f}%{Style.RESET_ALL}")
            
            lines.append(f"  运行时间: {elapsed_time:.2f} 秒")
            lines.append(f"  QPS: {Fore.CYAN}{qps:.2f}{Style.RESET_ALL}")
            
            # 响应时间统计
            response_time = metrics.get('response_time', {})
            if response_time:
                lines.append(f"\n{Fore.YELLOW}▶ 响应时间（毫秒）{Style.RESET_ALL}")
                lines.append(f"  最小值: {response_time.get('min', 0):.2f} ms")
                lines.append(f"  最大值: {response_time.get('max', 0):.2f} ms")
                lines.append(f"  平均值: {response_time.get('mean', 0):.2f} ms")
                lines.append(f"  中位数: {response_time.get('median', 0):.2f} ms")
                lines.append(f"  P90: {response_time.get('p90', 0):.2f} ms")
                lines.append(f"  P95: {response_time.get('p95', 0):.2f} ms")
                lines.append(f"  P99: {response_time.get('p99', 0):.2f} ms")
            
            # 状态码分布
            status_codes = metrics.get('status_codes', {})
            if status_codes:
                lines.append(f"\n{Fore.YELLOW}▶ 状态码分布{Style.RESET_ALL}")
                for code, count in sorted(status_codes.items()):
                    if 200 <= code < 300:
                        color = Fore.GREEN
                    elif 400 <= code < 500:
                        color = Fore.YELLOW
                    else:
                        color = Fore.RED
                    lines.append(f"  {color}{code}{Style.RESET_ALL}: {count}")
            
            # 错误统计
            errors = metrics.get('errors', {})
            if errors:
                lines.append(f"\n{Fore.YELLOW}▶ 错误统计{Style.RESET_ALL}")
                for error, count in sorted(errors.items(), key=lambda x: x[1], reverse=True)[:10]:
                    lines.append(f"  {Fore.RED}{error}{Style.RESET_ALL}: {count}")
        
        lines.append("\n" + "=" * 80 + "\n")
        
        return "\n".join(lines)
    
    def save(self, filename: str = None) -> str:
        """
        输出到控制台
        
        Args:
            filename: 未使用
            
        Returns:
            "console"
        """
        content = self.generate()
        print(content)
        sys.stdout.flush()
        return "console"
    
    def print_progress(self, current: int, total: int, prefix: str = "Progress"):
        """
        打印进度条
        
        Args:
            current: 当前进度
            total: 总进度
            prefix: 前缀文本
        """
        if total <= 0:
            return
        
        bar_length = 50
        filled_length = int(bar_length * current / total)
        bar = '█' * filled_length + '-' * (bar_length - filled_length)
        percent = current / total * 100
        
        print(f'\r{prefix}: |{bar}| {percent:.1f}% ({current}/{total})', end='', flush=True)
        
        if current >= total:
            print()  # 完成后换行
    
    def print_live_metrics(self, metrics: Dict[str, Any]):
        """
        实时打印指标（单行更新）
        
        Args:
            metrics: 指标数据
        """
        qps = metrics.get('qps', 0)
        success_rate = metrics.get('success_rate', 0)
        total_requests = metrics.get('total_requests', 0)
        avg_response_time = metrics.get('response_time', {}).get('mean', 0)
        
        # 成功率着色
        if success_rate >= 99:
            success_color = Fore.GREEN
        elif success_rate >= 95:
            success_color = Fore.YELLOW
        else:
            success_color = Fore.RED
        
        status = (
            f"\r{Fore.CYAN}实时监控{Style.RESET_ALL} | "
            f"请求数: {Fore.GREEN}{total_requests}{Style.RESET_ALL} | "
            f"QPS: {Fore.CYAN}{qps:.2f}{Style.RESET_ALL} | "
            f"成功率: {success_color}{success_rate:.2f}%{Style.RESET_ALL} | "
            f"平均响应: {avg_response_time:.2f}ms"
        )
        
        print(status, end='', flush=True)

