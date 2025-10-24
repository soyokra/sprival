#!/usr/bin/env python3
"""
测试logback-core源码读取
直接检查JAR文件内容，尝试读取源码
"""

import os
import zipfile
from pathlib import Path


def test_logback_source():
    """测试logback-core源码读取"""
    print("测试logback-core源码读取...")
    
    # 查找logback-core JAR文件
    m2_repo = Path.home() / ".m2" / "repository"
    logback_jar = m2_repo / "ch" / "qos" / "logback" / "logback-core" / "1.2.12" / "logback-core-1.2.12.jar"
    
    if not logback_jar.exists():
        print(f"未找到JAR文件: {logback_jar}")
        return
    
    print(f"找到JAR文件: {logback_jar}")
    
    try:
        # 打开JAR文件
        with zipfile.ZipFile(logback_jar, 'r') as jar:
            # 列出所有文件
            all_files = jar.namelist()
            print(f"JAR文件包含 {len(all_files)} 个文件")
            
            # 查找AppenderBase相关的class文件
            appender_files = [f for f in all_files if 'AppenderBase' in f and f.endswith('.class')]
            print(f"找到 {len(appender_files)} 个AppenderBase相关文件:")
            for f in appender_files:
                print(f"  {f}")
            
            # 查找ch.qos.logback.core包下的所有class文件
            core_files = [f for f in all_files if f.startswith('ch/qos/logback/core/') and f.endswith('.class')]
            print(f"\n找到 {len(core_files)} 个ch.qos.logback.core包下的文件:")
            for f in core_files[:10]:  # 只显示前10个
                print(f"  {f}")
            if len(core_files) > 10:
                print(f"  ... 还有 {len(core_files) - 10} 个文件")
            
            # 尝试读取AppenderBase.class文件
            appender_base_file = 'ch/qos/logback/core/AppenderBase.class'
            if appender_base_file in all_files:
                print(f"\n找到AppenderBase.class文件")
                
                # 读取class文件内容（前100字节）
                with jar.open(appender_base_file) as class_file:
                    content = class_file.read(100)
                    print(f"AppenderBase.class文件大小: {len(content)} 字节")
                    print(f"文件头: {content[:20].hex()}")
                
                # 检查是否有源码文件
                source_files = [f for f in all_files if f.startswith('ch/qos/logback/core/') and f.endswith('.java')]
                if source_files:
                    print(f"\n找到 {len(source_files)} 个源码文件:")
                    for f in source_files:
                        print(f"  {f}")
                else:
                    print("\n未找到源码文件，需要反编译class文件")
                    
                    # 尝试读取一个简单的class文件内容
                    simple_class = 'ch/qos/logback/core/AppenderBase.class'
                    if simple_class in all_files:
                        print(f"\n尝试读取 {simple_class} 文件信息:")
                        info = jar.getinfo(simple_class)
                        print(f"  文件大小: {info.file_size} 字节")
                        print(f"  压缩大小: {info.compress_size} 字节")
                        print(f"  修改时间: {info.date_time}")
            else:
                print(f"\n未找到AppenderBase.class文件")
                
    except Exception as e:
        print(f"读取JAR文件时出错: {e}")


def check_source_directory():
    """检查源码目录是否存在"""
    print("\n检查源码目录...")
    
    m2_repo = Path.home() / ".m2" / "repository"
    source_dir = m2_repo / "ch" / "qos" / "logback" / "logback-core" / "1.2.12" / "logback-core-1.2.12-sources-sources"
    
    if source_dir.exists():
        print(f"源码目录存在: {source_dir}")
        
        # 查找Java文件
        java_files = list(source_dir.rglob("*.java"))
        print(f"找到 {len(java_files)} 个Java文件")
        
        # 查找AppenderBase.java
        appender_base_java = source_dir / "ch" / "qos" / "logback" / "core" / "AppenderBase.java"
        if appender_base_java.exists():
            print(f"找到AppenderBase.java: {appender_base_java}")
            
            # 读取文件内容的前几行
            try:
                with open(appender_base_java, 'r', encoding='utf-8') as f:
                    lines = f.readlines()[:20]  # 读取前20行
                    print("文件内容预览:")
                    for i, line in enumerate(lines, 1):
                        print(f"{i:2d}: {line.rstrip()}")
            except Exception as e:
                print(f"读取文件时出错: {e}")
        else:
            print("未找到AppenderBase.java文件")
            
        # 显示目录结构
        print(f"\n目录结构:")
        for item in sorted(source_dir.rglob("*")):
            if item.is_file():
                rel_path = item.relative_to(source_dir)
                print(f"  {rel_path}")
    else:
        print(f"源码目录不存在: {source_dir}")
        print("需要先运行转换工具创建源码目录")


if __name__ == "__main__":
    test_logback_source()
    check_source_directory()
