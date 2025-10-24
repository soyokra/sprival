#!/usr/bin/env python3
"""
Maven依赖包索引生成器
分析Spring Boot项目的依赖，生成详细的包索引文件
"""

import os
import json
import subprocess
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Any, Optional
import argparse


class MavenDependencyAnalyzer:
    """Maven依赖分析器"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.pom_file = self.project_root / "pom.xml"
        self.target_dir = self.project_root / "target"
        self.dependency_tree_file = self.target_dir / "dependency-tree.txt"
        
    def run_maven_dependency_tree(self) -> bool:
        """运行Maven依赖树命令"""
        try:
            print("正在生成Maven依赖树...")
            cmd = ["mvn", "dependency:tree", "-DoutputType=text", "-DoutputFile=dependency-tree.txt"]
            result = subprocess.run(cmd, cwd=self.project_root, capture_output=True, text=True)
            
            if result.returncode != 0:
                print(f"Maven命令执行失败: {result.stderr}")
                return False
                
            print("Maven依赖树生成成功")
            return True
            
        except FileNotFoundError:
            print("错误: 未找到Maven命令，请确保Maven已安装并在PATH中")
            return False
        except Exception as e:
            print(f"执行Maven命令时出错: {e}")
            return False
    
    def parse_dependency_tree(self) -> List[Dict[str, Any]]:
        """解析Maven依赖树文件"""
        dependencies = []
        
        if not self.dependency_tree_file.exists():
            print(f"依赖树文件不存在: {self.dependency_tree_file}")
            return dependencies
            
        try:
            with open(self.dependency_tree_file, 'r', encoding='utf-8') as f:
                lines = f.readlines()
                
            for line in lines:
                line = line.strip()
                if not line or line.startswith('['):
                    continue
                    
                # 解析依赖行
                dep_info = self._parse_dependency_line(line)
                if dep_info:
                    dependencies.append(dep_info)
                    
        except Exception as e:
            print(f"解析依赖树文件时出错: {e}")
            
        return dependencies
    
    def _parse_dependency_line(self, line: str) -> Optional[Dict[str, Any]]:
        """解析单个依赖行"""
        try:
            # 移除前缀符号和空格
            line = line.lstrip('+-|\\ ')
            
            # 解析坐标
            parts = line.split(':')
            if len(parts) < 4:
                return None
                
            group_id = parts[0]
            artifact_id = parts[1]
            version = parts[3] if len(parts) > 3 else "unknown"
            scope = parts[4] if len(parts) > 4 else "compile"
            
            # 提取包路径
            package_path = f"{group_id.replace('.', '/')}/{artifact_id}"
            
            return {
                "groupId": group_id,
                "artifactId": artifact_id,
                "version": version,
                "scope": scope,
                "packagePath": package_path,
                "coordinates": f"{group_id}:{artifact_id}:{version}",
                "jarFile": f"{artifact_id}-{version}.jar"
            }
            
        except Exception as e:
            print(f"解析依赖行时出错: {line} - {e}")
            return None
    
    def analyze_pom_properties(self) -> Dict[str, Any]:
        """分析pom.xml中的属性"""
        properties = {}
        
        try:
            tree = ET.parse(self.pom_file)
            root = tree.getroot()
            
            # 解析项目信息
            properties["project"] = {
                "groupId": root.find(".//{http://maven.apache.org/POM/4.0.0}groupId").text if root.find(".//{http://maven.apache.org/POM/4.0.0}groupId") is not None else "",
                "artifactId": root.find(".//{http://maven.apache.org/POM/4.0.0}artifactId").text if root.find(".//{http://maven.apache.org/POM/4.0.0}artifactId") is not None else "",
                "version": root.find(".//{http://maven.apache.org/POM/4.0.0}version").text if root.find(".//{http://maven.apache.org/POM/4.0.0}version") is not None else "",
                "packaging": root.find(".//{http://maven.apache.org/POM/4.0.0}packaging").text if root.find(".//{http://maven.apache.org/POM/4.0.0}packaging") is not None else "jar"
            }
            
            # 解析Spring Boot版本
            parent = root.find(".//{http://maven.apache.org/POM/4.0.0}parent")
            if parent is not None:
                spring_boot_version = parent.find(".//{http://maven.apache.org/POM/4.0.0}version")
                if spring_boot_version is not None:
                    properties["springBootVersion"] = spring_boot_version.text
            
            # 解析属性
            properties_section = root.find(".//{http://maven.apache.org/POM/4.0.0}properties")
            if properties_section is not None:
                for prop in properties_section:
                    if prop.tag.endswith("version"):
                        properties[prop.tag.replace("{http://maven.apache.org/POM/4.0.0}", "")] = prop.text
                        
        except Exception as e:
            print(f"解析pom.xml时出错: {e}")
            
        return properties
    
    def generate_package_index(self) -> Dict[str, Any]:
        """生成完整的包索引"""
        print("开始分析Maven依赖...")
        
        # 运行Maven依赖树
        if not self.run_maven_dependency_tree():
            return {}
        
        # 解析依赖
        dependencies = self.parse_dependency_tree()
        print(f"发现 {len(dependencies)} 个依赖")
        
        # 分析POM属性
        pom_properties = self.analyze_pom_properties()
        
        # 按作用域分组
        scoped_deps = {}
        for dep in dependencies:
            scope = dep.get("scope", "compile")
            if scope not in scoped_deps:
                scoped_deps[scope] = []
            scoped_deps[scope].append(dep)
        
        # 生成索引
        index = {
            "metadata": {
                "generatedAt": datetime.now().isoformat(),
                "projectRoot": str(self.project_root),
                "totalDependencies": len(dependencies)
            },
            "project": pom_properties.get("project", {}),
            "springBootVersion": pom_properties.get("springBootVersion", ""),
            "properties": pom_properties,
            "dependencies": {
                "all": dependencies,
                "byScope": scoped_deps
            },
            "categories": self._categorize_dependencies(dependencies)
        }
        
        return index
    
    def _categorize_dependencies(self, dependencies: List[Dict[str, Any]]) -> Dict[str, List[Dict[str, Any]]]:
        """按功能分类依赖"""
        categories = {
            "spring": [],
            "database": [],
            "cache": [],
            "messaging": [],
            "monitoring": [],
            "testing": [],
            "utilities": [],
            "other": []
        }
        
        for dep in dependencies:
            group_id = dep.get("groupId", "").lower()
            artifact_id = dep.get("artifactId", "").lower()
            
            if "spring" in group_id or "spring" in artifact_id:
                categories["spring"].append(dep)
            elif any(db in group_id or db in artifact_id for db in ["mysql", "postgresql", "h2", "clickhouse", "mongodb"]):
                categories["database"].append(dep)
            elif any(cache in group_id or cache in artifact_id for cache in ["redis", "redisson", "cache"]):
                categories["cache"].append(dep)
            elif any(msg in group_id or msg in artifact_id for msg in ["kafka", "rabbitmq", "activemq"]):
                categories["messaging"].append(dep)
            elif any(mon in group_id or mon in artifact_id for mon in ["micrometer", "prometheus", "actuator"]):
                categories["monitoring"].append(dep)
            elif any(test in group_id or test in artifact_id for test in ["junit", "mockito", "testcontainers"]):
                categories["testing"].append(dep)
            elif any(util in group_id or util in artifact_id for util in ["guava", "apache", "commons"]):
                categories["utilities"].append(dep)
            else:
                categories["other"].append(dep)
        
        return categories
    
    def save_index(self, index: Dict[str, Any], output_file: str = None) -> str:
        """保存索引到文件"""
        if output_file is None:
            output_file = self.project_root / "ai-home" / "PACKAGE-INDEX.json"
        
        output_path = Path(output_file)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        try:
            with open(output_path, 'w', encoding='utf-8') as f:
                json.dump(index, f, indent=2, ensure_ascii=False)
            
            print(f"包索引已保存到: {output_path}")
            return str(output_path)
            
        except Exception as e:
            print(f"保存索引文件时出错: {e}")
            return ""


def main():
    parser = argparse.ArgumentParser(description="生成Maven项目依赖包索引")
    parser.add_argument("--project-root", default=".", help="项目根目录路径")
    parser.add_argument("--output", help="输出文件路径")
    
    args = parser.parse_args()
    
    analyzer = MavenDependencyAnalyzer(args.project_root)
    index = analyzer.generate_package_index()
    
    if index:
        output_file = analyzer.save_index(index, args.output)
        if output_file:
            print(f"\n包索引生成完成!")
            print(f"输出文件: {output_file}")
            print(f"总依赖数: {index['metadata']['totalDependencies']}")
            
            # 显示分类统计
            categories = index.get("categories", {})
            print(f"\n依赖分类统计:")
            for category, deps in categories.items():
                if deps:
                    print(f"  {category}: {len(deps)} 个")
    else:
        print("包索引生成失败")


if __name__ == "__main__":
    main()
