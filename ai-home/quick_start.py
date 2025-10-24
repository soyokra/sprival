#!/usr/bin/env python3
"""
第三方源码分析工具 - 快速启动脚本
提供简单的命令行界面来运行各种分析功能
"""

import os
import sys
import subprocess
import argparse
from pathlib import Path


def run_command(cmd: list, description: str) -> bool:
    """运行命令并显示结果"""
    print(f"\n{description}...")
    try:
        result = subprocess.run(cmd, capture_output=True, text=True)
        if result.returncode == 0:
            print(f"{description}完成")
            if result.stdout:
                print(result.stdout)
            return True
        else:
            print(f"{description}失败")
            if result.stderr:
                print(result.stderr)
            return False
    except Exception as e:
        print(f"{description}异常: {e}")
        return False


def main():
    parser = argparse.ArgumentParser(description="第三方源码分析工具 - 快速启动")
    parser.add_argument("--project-root", default=".", help="项目根目录路径")
    parser.add_argument("--max-deps", type=int, default=5, help="最大转换依赖数")
    parser.add_argument("--action", choices=["index", "convert", "full", "help"], 
                       default="full", help="执行的操作")
    parser.add_argument("--dependency", help="要转换的特定依赖 (groupId:artifactId:version)")
    
    args = parser.parse_args()
    
    project_root = Path(args.project_root).resolve()
    ai_home = project_root / "ai-home"
    
    # 如果当前在ai-home目录中，调整路径
    if Path.cwd().name == "ai-home":
        ai_home = Path.cwd()
    
    print("第三方源码分析工具")
    print(f"项目根目录: {project_root}")
    print(f"AI工具目录: {ai_home}")
    
    if not ai_home.exists():
        print("AI工具目录不存在")
        return
    
    if args.action == "help":
        show_help()
        return
    
    if args.action == "index":
        # 仅生成包索引
        cmd = [sys.executable, str(ai_home / "package_index_generator.py")]
        run_command(cmd, "生成包索引")
        
    elif args.action == "convert":
        # 转换源码
        if args.dependency:
            cmd = [sys.executable, str(ai_home / "jar_to_source_converter.py"),
                   "--dependency", args.dependency]
            run_command(cmd, f"转换依赖 {args.dependency}")
        else:
            cmd = [sys.executable, str(ai_home / "jar_to_source_converter.py"), "--all"]
            run_command(cmd, "转换所有依赖")
            
    elif args.action == "full":
        # 完整分析
        cmd = [sys.executable, str(ai_home / "third_party_source_analyzer.py"),
               "--max-deps", str(args.max_deps)]
        run_command(cmd, "完整第三方源码分析")
    
    # 显示结果文件
    show_results(ai_home)


def show_help():
    """显示帮助信息"""
    print("""
第三方源码分析工具使用说明

基本用法:
  python ai-home/quick_start.py                    # 完整分析（默认5个依赖）
  python ai-home/quick_start.py --max-deps 10     # 完整分析（10个依赖）
  python ai-home/quick_start.py --action index    # 仅生成包索引
  python ai-home/quick_start.py --action convert   # 仅转换源码

特定依赖转换:
  python ai-home/quick_start.py --action convert --dependency "org.springframework.boot:spring-boot-starter-web:2.7.18"

输出文件:
  - PACKAGE-INDEX.json: 包索引文件
  - THIRD-PARTY-SOURCE-ANALYSIS.json: 分析报告
  - Maven仓库中的-sources目录: 转换后的源码（在JAR包所在位置）

高级用法:
  python ai-home/package_index_generator.py --help
  python ai-home/jar_to_source_converter.py --help
  python ai-home/third_party_source_analyzer.py --help
""")


def show_results(ai_home: Path):
    """显示结果文件"""
    print("\n分析结果:")
    
    # 检查包索引文件
    index_file = ai_home / "PACKAGE-INDEX.json"
    if index_file.exists():
        print(f"包索引文件: {index_file}")
    else:
        print("包索引文件不存在")
    
    # 检查分析报告
    report_file = ai_home / "THIRD-PARTY-SOURCE-ANALYSIS.json"
    if report_file.exists():
        print(f"分析报告: {report_file}")
    else:
        print("分析报告不存在")
    
    # 检查源码目录（在Maven仓库中）
    m2_repo = Path.home() / ".m2" / "repository"
    if m2_repo.exists():
        # 查找所有-sources目录
        source_dirs = list(m2_repo.rglob("*-sources"))
        if source_dirs:
            total_java_files = 0
            for source_dir in source_dirs:
                java_files = list(source_dir.rglob("*.java"))
                total_java_files += len(java_files)
            print(f"源码目录: Maven仓库中的-sources目录 ({len(source_dirs)} 个目录, {total_java_files} 个Java文件)")
        else:
            print("源码目录不存在")
    else:
        print("Maven仓库不存在")


if __name__ == "__main__":
    main()
