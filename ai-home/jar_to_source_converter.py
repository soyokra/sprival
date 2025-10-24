#!/usr/bin/env python3
"""
JAR包转源码工具
将JAR包反编译为Java源码，便于AI阅读和分析
"""

import os
import sys
import subprocess
import zipfile
import shutil
import argparse
from pathlib import Path
from typing import List, Dict, Optional, Tuple, Any
import json
import tempfile


class JarToSourceConverter:
    """JAR包转源码转换器"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.m2_repo = Path.home() / ".m2" / "repository"
        self.temp_dir = None
        
    def find_jar_files(self, dependency_info: Dict[str, Any]) -> List[Path]:
        """查找JAR文件位置"""
        jar_files = []
        
        # 在Maven本地仓库中查找
        group_path = dependency_info["groupId"].replace(".", "/")
        artifact_path = self.m2_repo / group_path / dependency_info["artifactId"] / dependency_info["version"]
        
        jar_name = f"{dependency_info['artifactId']}-{dependency_info['version']}.jar"
        jar_file = artifact_path / jar_name
        
        if jar_file.exists():
            jar_files.append(jar_file)
            print(f"找到JAR文件: {jar_file}")
        else:
            print(f"未找到JAR文件: {jar_file}")
            
        return jar_files
    
    def extract_jar(self, jar_file: Path) -> Path:
        """解压JAR文件"""
        if self.temp_dir is None:
            self.temp_dir = Path(tempfile.mkdtemp(prefix="jar_extract_"))
        
        extract_dir = self.temp_dir / jar_file.stem
        extract_dir.mkdir(parents=True, exist_ok=True)
        
        try:
            with zipfile.ZipFile(jar_file, 'r') as zip_ref:
                zip_ref.extractall(extract_dir)
            print(f"JAR文件解压完成: {extract_dir}")
            return extract_dir
        except Exception as e:
            print(f"解压JAR文件失败: {e}")
            return None
    
    def decompile_class_files(self, extract_dir: Path, output_dir: Path) -> bool:
        """反编译class文件为Java源码"""
        try:
            # 查找所有class文件
            class_files = list(extract_dir.rglob("*.class"))
            if not class_files:
                print("未找到class文件")
                return False
            
            print(f"找到 {len(class_files)} 个class文件")
            
            # 使用CFR反编译器
            success = self._decompile_with_cfr(class_files, output_dir)
            if success:
                return True
            
            # 如果CFR失败，尝试使用Fernflower
            print("尝试使用Fernflower反编译器...")
            return self._decompile_with_fernflower(class_files, output_dir)
            
        except Exception as e:
            print(f"反编译过程中出错: {e}")
            return False
    
    def _decompile_with_cfr(self, class_files: List[Path], output_dir: Path) -> bool:
        """使用CFR反编译器"""
        try:
            # 检查CFR是否可用
            result = subprocess.run(["java", "-jar", "cfr.jar", "--help"], 
                                  capture_output=True, text=True, timeout=5)
            if result.returncode != 0:
                print("CFR反编译器不可用")
                return False
        except:
            print("CFR反编译器不可用，尝试下载...")
            if not self._download_cfr():
                return False
        
        try:
            # 创建输出目录
            output_dir.mkdir(parents=True, exist_ok=True)
            
            # 反编译所有class文件
            for class_file in class_files:
                relative_path = class_file.relative_to(class_file.parents[len(class_files[0].parents) - len(class_files[0].parents)])
                output_file = output_dir / relative_path.with_suffix('.java')
                output_file.parent.mkdir(parents=True, exist_ok=True)
                
                cmd = [
                    "java", "-jar", "cfr.jar",
                    str(class_file),
                    "--outputdir", str(output_file.parent),
                    "--silent", "true"
                ]
                
                result = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
                if result.returncode == 0:
                    print(f"✅ 反编译成功: {output_file}")
                else:
                    print(f"反编译失败: {class_file}")
            
            return True
            
        except Exception as e:
            print(f"CFR反编译失败: {e}")
            return False
    
    def _decompile_with_fernflower(self, class_files: List[Path], output_dir: Path) -> bool:
        """使用Fernflower反编译器"""
        try:
            # 检查Fernflower是否可用
            result = subprocess.run(["java", "-jar", "fernflower.jar", "--help"], 
                                  capture_output=True, text=True, timeout=5)
            if result.returncode != 0:
                print("Fernflower反编译器不可用")
                return False
        except:
            print("Fernflower反编译器不可用，尝试下载...")
            if not self._download_fernflower():
                return False
        
        try:
            # 创建输出目录
            output_dir.mkdir(parents=True, exist_ok=True)
            
            # 反编译所有class文件
            for class_file in class_files:
                relative_path = class_file.relative_to(class_file.parents[len(class_files[0].parents) - len(class_files[0].parents)])
                output_file = output_dir / relative_path.with_suffix('.java')
                output_file.parent.mkdir(parents=True, exist_ok=True)
                
                cmd = [
                    "java", "-jar", "fernflower.jar",
                    str(class_file),
                    str(output_file.parent)
                ]
                
                result = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
                if result.returncode == 0:
                    print(f"✅ 反编译成功: {output_file}")
                else:
                    print(f"反编译失败: {class_file}")
            
            return True
            
        except Exception as e:
            print(f"Fernflower反编译失败: {e}")
            return False
    
    def _download_cfr(self) -> bool:
        """下载CFR反编译器"""
        try:
            import urllib.request
            url = "https://github.com/leibnitz27/cfr/releases/download/0.152/cfr-0.152.jar"
            print("正在下载CFR反编译器...")
            urllib.request.urlretrieve(url, "cfr.jar")
            print("CFR反编译器下载完成")
            return True
        except Exception as e:
            print(f"下载CFR失败: {e}")
            return False
    
    def _download_fernflower(self) -> bool:
        """下载Fernflower反编译器"""
        try:
            import urllib.request
            url = "https://github.com/fesh0r/fernflower/releases/download/fernflower-2.5/fernflower.jar"
            print("正在下载Fernflower反编译器...")
            urllib.request.urlretrieve(url, "fernflower.jar")
            print("Fernflower反编译器下载完成")
            return True
        except Exception as e:
            print(f"下载Fernflower失败: {e}")
            return False
    
    def convert_jar_to_source(self, dependency_info: Dict[str, Any]) -> Optional[Path]:
        """将JAR包转换为源码"""
        coordinates = f"{dependency_info['groupId']}:{dependency_info['artifactId']}:{dependency_info['version']}"
        print(f"\n开始转换JAR包: {coordinates}")
        
        # 查找JAR文件
        jar_files = self.find_jar_files(dependency_info)
        if not jar_files:
            return None
        
        jar_file = jar_files[0]
        
        # 解压JAR文件
        extract_dir = self.extract_jar(jar_file)
        if not extract_dir:
            return None
        
        # 在JAR文件所在目录创建源码目录
        jar_dir = jar_file.parent
        source_dir = jar_dir / f"{jar_file.stem}-sources"
        source_dir.mkdir(parents=True, exist_ok=True)
        
        # 反编译class文件
        if self.decompile_class_files(extract_dir, source_dir):
            print(f"源码转换完成: {source_dir}")
            return source_dir
        else:
            print(f"源码转换失败")
            return None
    
    def cleanup(self):
        """清理临时文件"""
        if self.temp_dir and self.temp_dir.exists():
            shutil.rmtree(self.temp_dir)
            print("临时文件清理完成")


def main():
    parser = argparse.ArgumentParser(description="将JAR包转换为Java源码")
    parser.add_argument("--project-root", default=".", help="项目根目录路径")
    parser.add_argument("--dependency", help="要转换的依赖坐标 (groupId:artifactId:version)")
    parser.add_argument("--all", action="store_true", help="转换所有依赖")
    
    args = parser.parse_args()
    
    converter = JarToSourceConverter(args.project_root)
    
    try:
        if args.dependency:
            # 解析依赖坐标
            parts = args.dependency.split(":")
            if len(parts) != 3:
                print("❌ 依赖坐标格式错误，应为: groupId:artifactId:version")
                return
            
            dependency_info = {
                "groupId": parts[0],
                "artifactId": parts[1],
                "version": parts[2]
            }
            
            result = converter.convert_jar_to_source(dependency_info)
            if result:
                print(f"\n转换完成! 源码位置: {result}")
            else:
                print("\n转换失败")
                
        elif args.all:
            # 读取包索引文件
            index_file = Path(args.project_root) / "ai-home" / "PACKAGE-INDEX.json"
            if not index_file.exists():
                print("包索引文件不存在，请先运行 package_index_generator.py")
                return
            
            with open(index_file, 'r', encoding='utf-8') as f:
                index = json.load(f)
            
            dependencies = index.get("dependencies", {}).get("all", [])
            print(f"🔄 开始转换 {len(dependencies)} 个依赖...")
            
            success_count = 0
            for dep in dependencies:
                result = converter.convert_jar_to_source(dep)
                if result:
                    success_count += 1
            
            print(f"\n转换完成! 成功转换 {success_count}/{len(dependencies)} 个依赖")
            
        else:
            print("请指定要转换的依赖或使用 --all 转换所有依赖")
            
    finally:
        converter.cleanup()


if __name__ == "__main__":
    main()
