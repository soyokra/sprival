#!/usr/bin/env python3
"""
简化的包索引生成器
直接从pom.xml解析依赖，不依赖Maven命令
"""

import os
import json
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Any, Optional
import argparse


class SimplePackageIndexGenerator:
    """简化的包索引生成器"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.pom_file = self.project_root / "pom.xml"
        
    def parse_pom_dependencies(self) -> List[Dict[str, Any]]:
        """解析pom.xml中的依赖"""
        dependencies = []
        
        try:
            tree = ET.parse(self.pom_file)
            root = tree.getroot()
            
            # 解析依赖
            dependencies_section = root.find(".//{http://maven.apache.org/POM/4.0.0}dependencies")
            if dependencies_section is not None:
                for dep in dependencies_section:
                    if dep.tag.endswith("dependency"):
                        dep_info = self._parse_dependency_element(dep)
                        if dep_info:
                            dependencies.append(dep_info)
                            
        except Exception as e:
            print(f"解析pom.xml时出错: {e}")
            
        return dependencies
    
    def _parse_dependency_element(self, dep_element) -> Optional[Dict[str, Any]]:
        """解析单个依赖元素"""
        try:
            group_id = self._get_element_text(dep_element, "groupId")
            artifact_id = self._get_element_text(dep_element, "artifactId")
            version = self._get_element_text(dep_element, "version")
            scope = self._get_element_text(dep_element, "scope") or "compile"
            
            if not group_id or not artifact_id:
                return None
                
            # 处理版本变量
            if version and version.startswith("${"):
                version = self._resolve_property(version)
            
            return {
                "groupId": group_id,
                "artifactId": artifact_id,
                "version": version or "unknown",
                "scope": scope,
                "packagePath": f"{group_id.replace('.', '/')}/{artifact_id}",
                "coordinates": f"{group_id}:{artifact_id}:{version or 'unknown'}",
                "jarFile": f"{artifact_id}-{version or 'unknown'}.jar"
            }
            
        except Exception as e:
            print(f"解析依赖元素时出错: {e}")
            return None
    
    def _get_element_text(self, parent, tag_name: str) -> Optional[str]:
        """获取元素文本内容"""
        element = parent.find(f".//{{http://maven.apache.org/POM/4.0.0}}{tag_name}")
        return element.text if element is not None else None
    
    def _resolve_property(self, property_ref: str) -> str:
        """解析属性引用"""
        # 简单的属性解析，实际项目中可能需要更复杂的逻辑
        if property_ref == "${spring-boot.version}":
            return "2.7.18"
        elif property_ref == "${spring-cloud.version}":
            return "2021.0.8"
        elif property_ref == "${mysql-connector.version}":
            return "8.0.33"
        elif property_ref == "${mybatis-plus.version}":
            return "3.5.7"
        elif property_ref == "${redisson.version}":
            return "3.23.4"
        else:
            return property_ref
    
    def analyze_pom_properties(self) -> Dict[str, Any]:
        """分析pom.xml中的属性"""
        properties = {}
        
        try:
            tree = ET.parse(self.pom_file)
            root = tree.getroot()
            
            # 解析项目信息
            properties["project"] = {
                "groupId": self._get_element_text(root, "groupId") or "",
                "artifactId": self._get_element_text(root, "artifactId") or "",
                "version": self._get_element_text(root, "version") or "",
                "packaging": self._get_element_text(root, "packaging") or "jar"
            }
            
            # 解析Spring Boot版本
            parent = root.find(".//{http://maven.apache.org/POM/4.0.0}parent")
            if parent is not None:
                spring_boot_version = self._get_element_text(parent, "version")
                if spring_boot_version:
                    properties["springBootVersion"] = spring_boot_version
            
            # 解析属性
            properties_section = root.find(".//{http://maven.apache.org/POM/4.0.0}properties")
            if properties_section is not None:
                for prop in properties_section:
                    if prop.tag.endswith("version"):
                        properties[prop.tag.replace("{http://maven.apache.org/POM/4.0.0}", "")] = prop.text
                        
        except Exception as e:
            print(f"解析pom.xml属性时出错: {e}")
            
        return properties
    
    def generate_package_index(self) -> Dict[str, Any]:
        """生成包索引"""
        print("开始分析pom.xml依赖...")
        
        # 解析依赖
        dependencies = self.parse_pom_dependencies()
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
                "totalDependencies": len(dependencies),
                "source": "pom.xml"
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
    parser = argparse.ArgumentParser(description="简化的Maven项目依赖包索引生成器")
    parser.add_argument("--project-root", default=".", help="项目根目录路径")
    parser.add_argument("--output", help="输出文件路径")
    
    args = parser.parse_args()
    
    generator = SimplePackageIndexGenerator(args.project_root)
    index = generator.generate_package_index()
    
    if index:
        output_file = generator.save_index(index, args.output)
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
