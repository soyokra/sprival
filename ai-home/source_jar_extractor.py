#!/usr/bin/env python3
"""
源码JAR包提取器
使用Maven下载源码JAR包，然后解压源码
"""

import os
import subprocess
import zipfile
import argparse
from pathlib import Path
from typing import Dict, List, Any, Optional
import json
import tempfile


class SourceJarExtractor:
    """源码JAR包提取器"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.m2_repo = Path.home() / ".m2" / "repository"
        
    def download_sources(self) -> bool:
        """使用Maven下载源码JAR包"""
        print("使用Maven下载源码JAR包...")
        
        try:
            # 运行mvn dependency:sources命令
            cmd = ["mvn", "dependency:sources", "-DoutputDirectory=target/sources"]
            result = subprocess.run(cmd, cwd=self.project_root, capture_output=True, text=True)
            
            if result.returncode == 0:
                print("Maven源码下载成功")
                return True
            else:
                print(f"Maven命令执行失败: {result.stderr}")
                return False
                
        except FileNotFoundError:
            print("错误: 未找到Maven命令，请确保Maven已安装并在PATH中")
            return False
        except Exception as e:
            print(f"执行Maven命令时出错: {e}")
            return False
    
    def find_source_jars(self, dependency_info: Dict[str, Any]) -> List[Path]:
        """查找源码JAR文件"""
        source_jars = []
        
        # 在Maven本地仓库中查找源码JAR
        group_path = dependency_info["groupId"].replace(".", "/")
        artifact_path = self.m2_repo / group_path / dependency_info["artifactId"] / dependency_info["version"]
        
        # 查找源码JAR文件
        source_jar_name = f"{dependency_info['artifactId']}-{dependency_info['version']}-sources.jar"
        source_jar_file = artifact_path / source_jar_name
        
        if source_jar_file.exists():
            source_jars.append(source_jar_file)
            print(f"找到源码JAR文件: {source_jar_file}")
        else:
            print(f"未找到源码JAR文件: {source_jar_file}")
            
        return source_jars
    
    def extract_source_jar(self, source_jar: Path) -> Optional[Path]:
        """解压源码JAR文件"""
        print(f"解压源码JAR文件: {source_jar}")
        
        # 在JAR文件所在目录创建源码目录
        jar_dir = source_jar.parent
        source_dir = jar_dir / f"{source_jar.stem}-sources"
        source_dir.mkdir(parents=True, exist_ok=True)
        
        try:
            with zipfile.ZipFile(source_jar, 'r') as zip_ref:
                zip_ref.extractall(source_dir)
            
            # 统计Java文件数量
            java_files = list(source_dir.rglob("*.java"))
            print(f"解压完成，包含 {len(java_files)} 个Java文件")
            
            return source_dir
            
        except Exception as e:
            print(f"解压源码JAR文件失败: {e}")
            return None
    
    def extract_dependency_sources(self, dependency_info: Dict[str, Any]) -> Optional[Path]:
        """提取依赖的源码"""
        coordinates = f"{dependency_info['groupId']}:{dependency_info['artifactId']}:{dependency_info['version']}"
        print(f"\n提取依赖源码: {coordinates}")
        
        # 查找源码JAR文件
        source_jars = self.find_source_jars(dependency_info)
        if not source_jars:
            print("未找到源码JAR文件，尝试下载...")
            if not self.download_sources():
                return None
            
            # 重新查找
            source_jars = self.find_source_jars(dependency_info)
            if not source_jars:
                print("下载后仍未找到源码JAR文件")
                return None
        
        source_jar = source_jars[0]
        
        # 解压源码JAR文件
        source_dir = self.extract_source_jar(source_jar)
        if source_dir:
            print(f"源码提取完成: {source_dir}")
            return source_dir
        else:
            print("源码提取失败")
            return None
    
    def extract_all_sources(self, dependencies: List[Dict[str, Any]], max_deps: int = 10) -> Dict[str, Any]:
        """提取所有依赖的源码"""
        print(f"开始提取 {min(len(dependencies), max_deps)} 个依赖的源码...")
        
        results = {
            "success": [],
            "failed": [],
            "skipped": []
        }
        
        for i, dep in enumerate(dependencies[:max_deps]):
            print(f"\n[{i+1}/{min(len(dependencies), max_deps)}] 处理: {dep['coordinates']}")
            
            try:
                result = self.extract_dependency_sources(dep)
                if result:
                    results["success"].append(dep)
                    print(f"成功提取: {dep['artifactId']}")
                else:
                    results["failed"].append(dep)
                    print(f"提取失败: {dep['artifactId']}")
                    
            except Exception as e:
                results["failed"].append(dep)
                print(f"提取异常: {dep['artifactId']} - {e}")
        
        return results
    
    def check_source_availability(self, dependency_info: Dict[str, Any]) -> bool:
        """检查源码是否可用"""
        source_jars = self.find_source_jars(dependency_info)
        return len(source_jars) > 0


def main():
    parser = argparse.ArgumentParser(description="源码JAR包提取器")
    parser.add_argument("--project-root", default=".", help="项目根目录路径")
    parser.add_argument("--dependency", help="要提取的依赖坐标 (groupId:artifactId:version)")
    parser.add_argument("--all", action="store_true", help="提取所有依赖")
    parser.add_argument("--max-deps", type=int, default=10, help="最大提取依赖数")
    
    args = parser.parse_args()
    
    extractor = SourceJarExtractor(args.project_root)
    
    try:
        if args.dependency:
            # 解析依赖坐标
            parts = args.dependency.split(":")
            if len(parts) != 3:
                print("依赖坐标格式错误，应为: groupId:artifactId:version")
                return
            
            dependency_info = {
                "groupId": parts[0],
                "artifactId": parts[1],
                "version": parts[2],
                "coordinates": args.dependency
            }
            
            result = extractor.extract_dependency_sources(dependency_info)
            if result:
                print(f"\n源码提取完成! 源码位置: {result}")
            else:
                print("\n源码提取失败")
                
        elif args.all:
            # 读取包索引文件
            index_file = Path(args.project_root) / "ai-home" / "PACKAGE-INDEX.json"
            if not index_file.exists():
                print("包索引文件不存在，请先运行 package_index_generator.py")
                return
            
            with open(index_file, 'r', encoding='utf-8') as f:
                index = json.load(f)
            
            dependencies = index.get("dependencies", {}).get("all", [])
            print(f"发现 {len(dependencies)} 个依赖")
            
            # 提取所有依赖的源码
            results = extractor.extract_all_sources(dependencies, args.max_deps)
            
            print(f"\n源码提取完成!")
            print(f"成功: {len(results['success'])} 个")
            print(f"失败: {len(results['failed'])} 个")
            
        else:
            print("请指定要提取的依赖或使用 --all 提取所有依赖")
            
    except Exception as e:
        print(f"执行过程中出错: {e}")


if __name__ == "__main__":
    main()
