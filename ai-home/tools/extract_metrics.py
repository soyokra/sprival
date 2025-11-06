#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从 Prometheus HTTP 接口获取所有指标名并去重
"""

import re
import sys
import os
import requests
from requests.exceptions import RequestException, Timeout, ConnectionError


def fetch_metrics_from_endpoint(url, timeout=10):
    """
    从 Prometheus HTTP 接口获取指标数据
    
    Args:
        url: Prometheus 接口 URL
        timeout: 请求超时时间（秒）
        
    Returns:
        响应文本内容
        
    Raises:
        RequestException: 网络请求失败
    """
    try:
        print(f"正在从 {url} 获取指标数据...")
        response = requests.get(url, timeout=timeout)
        response.raise_for_status()  # 检查 HTTP 状态码
        print(f"✓ 成功获取数据，大小: {len(response.text)} 字节")
        return response.text
    except Timeout:
        raise RequestException(f"请求超时（超过 {timeout} 秒）")
    except ConnectionError:
        raise RequestException(f"无法连接到 {url}，请确认服务是否启动")
    except requests.HTTPError as e:
        raise RequestException(f"HTTP 错误: {e.response.status_code} - {e.response.reason}")
    except Exception as e:
        raise RequestException(f"请求失败: {str(e)}")


def extract_metric_names(metrics_text):
    """
    从 Prometheus 格式的指标文本中提取所有唯一的指标名
    
    Args:
        metrics_text: Prometheus 格式的指标文本
        
    Returns:
        排序后的唯一指标名列表
    """
    metric_names = set()
    
    for line in metrics_text.split('\n'):
        # 跳过注释行和空行
        line = line.strip()
        if not line or line.startswith('#'):
            continue
        
        # 提取指标名（在 { 或空格之前的部分）
        # 指标名格式：metric_name{labels} value 或 metric_name value
        match = re.match(r'^([a-zA-Z_:][a-zA-Z0-9_:]*)', line)
        if match:
            metric_name = match.group(1)
            metric_names.add(metric_name)
    
    return sorted(metric_names)


def main():
    # Prometheus 接口 URL
    prometheus_url = 'http://127.0.0.1:8338/api/actuator/prometheus'
    
    # 获取脚本所在目录，向上两级到项目根目录
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(os.path.dirname(script_dir))
    
    # 输出文件路径（使用绝对路径）
    output_file = os.path.join(project_root, 'docs', 'reference', 'monitoring', 'metric_names.txt')
    
    try:
        # 从 HTTP 接口获取指标数据
        metrics_text = fetch_metrics_from_endpoint(prometheus_url)
        
        # 提取指标名
        print(f"\n正在提取指标名...")
        metric_names = extract_metric_names(metrics_text)
        
        if not metric_names:
            print("⚠ 警告: 未找到任何指标")
            return
        
        print(f"✓ 共找到 {len(metric_names)} 个唯一的指标名\n")
        print("=" * 80)
        for metric in metric_names:
            print(metric)
        print("=" * 80)
        
        # 保存到文件（覆盖写入）
        try:
            # 确保输出目录存在
            output_dir = os.path.dirname(output_file)
            if not os.path.exists(output_dir):
                os.makedirs(output_dir, exist_ok=True)
                print(f"✓ 创建目录: {output_dir}")
            
            with open(output_file, 'w', encoding='utf-8') as f:
                for metric in metric_names:
                    f.write(metric + '\n')
            print(f"\n✓ 指标名已保存到: {output_file}")
        except IOError as e:
            print(f"\n✗ 文件写入失败: {str(e)}", file=sys.stderr)
            print(f"   输出路径: {output_file}", file=sys.stderr)
            print(f"   当前工作目录: {os.getcwd()}", file=sys.stderr)
            sys.exit(1)
            
    except RequestException as e:
        print(f"\n✗ 获取指标数据失败: {str(e)}", file=sys.stderr)
        print("\n提示:")
        print("  1. 确认应用已启动: http://127.0.0.1:8338/api/actuator/prometheus")
        print("  2. 检查网络连接和防火墙设置")
        print("  3. 确认 Actuator 端点已暴露")
        sys.exit(1)
    except Exception as e:
        print(f"\n✗ 未知错误: {str(e)}", file=sys.stderr)
        sys.exit(1)


if __name__ == '__main__':
    main()
