#!/usr/bin/env python3
"""
测试源码存放位置
验证JAR转源码工具是否在正确的位置创建源码目录
"""

import os
import sys
from pathlib import Path
from jar_to_source_converter import JarToSourceConverter


def test_source_location():
    """测试源码存放位置"""
    print("测试源码存放位置...")
    
    # 创建转换器实例
    converter = JarToSourceConverter(".")
    
    # 测试依赖信息
    test_dependency = {
        "groupId": "org.springframework.boot",
        "artifactId": "spring-boot-starter-web",
        "version": "2.7.18",
        "coordinates": "org.springframework.boot:spring-boot-starter-web:2.7.18"
    }
    
    print(f"测试依赖: {test_dependency['coordinates']}")
    
    # 查找JAR文件
    jar_files = converter.find_jar_files(test_dependency)
    if not jar_files:
        print("未找到JAR文件")
        return
    
    jar_file = jar_files[0]
    print(f"JAR文件位置: {jar_file}")
    
    # 计算预期的源码目录位置
    jar_dir = jar_file.parent
    expected_source_dir = jar_dir / f"{jar_file.stem}-sources"
    print(f"预期源码目录: {expected_source_dir}")
    
    # 检查源码目录是否存在
    if expected_source_dir.exists():
        java_files = list(expected_source_dir.rglob("*.java"))
        print(f"源码目录存在，包含 {len(java_files)} 个Java文件")
        
        # 显示一些示例文件
        if java_files:
            print("示例Java文件:")
            for i, java_file in enumerate(java_files[:5]):
                print(f"  {java_file}")
            if len(java_files) > 5:
                print(f"  ... 还有 {len(java_files) - 5} 个文件")
    else:
        print("源码目录不存在")
        print("请先运行转换工具创建源码目录")


if __name__ == "__main__":
    test_source_location()
