#!/usr/bin/env python3
"""
第三方源码分析器
集成包索引生成和JAR转源码功能，提供完整的第三方源码分析
"""

import os
import sys
import json
import argparse
from pathlib import Path
from typing import List, Dict, Any, Optional
import subprocess


class ThirdPartySourceAnalyzer:
    """第三方源码分析器"""
    
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.ai_home = self.project_root / "ai-home"
        self.package_index_file = self.ai_home / "PACKAGE-INDEX.json"
        self.m2_repo = Path.home() / ".m2" / "repository"
        
    def generate_package_index(self) -> bool:
        """生成包索引"""
        print("📋 正在生成包索引...")
        
        try:
            result = subprocess.run([
                sys.executable, 
                str(self.ai_home / "package_index_generator.py"),
                "--project-root", str(self.project_root)
            ], capture_output=True, text=True)
            
            if result.returncode == 0:
                print("✅ 包索引生成成功")
                return True
            else:
                print(f"❌ 包索引生成失败: {result.stderr}")
                return False
                
        except Exception as e:
            print(f"❌ 生成包索引时出错: {e}")
            return False
    
    def load_package_index(self) -> Optional[Dict[str, Any]]:
        """加载包索引"""
        if not self.package_index_file.exists():
            print("❌ 包索引文件不存在")
            return None
        
        try:
            with open(self.package_index_file, 'r', encoding='utf-8') as f:
                return json.load(f)
        except Exception as e:
            print(f"❌ 加载包索引文件时出错: {e}")
            return None
    
    def convert_dependencies_to_source(self, dependencies: List[Dict[str, Any]], 
                                     max_deps: int = 10) -> Dict[str, Any]:
        """将依赖转换为源码"""
        print(f"🔄 开始转换 {min(len(dependencies), max_deps)} 个依赖为源码...")
        
        results = {
            "success": [],
            "failed": [],
            "skipped": []
        }
        
        for i, dep in enumerate(dependencies[:max_deps]):
            print(f"\n[{i+1}/{min(len(dependencies), max_deps)}] 转换: {dep['coordinates']}")
            
            try:
                result = subprocess.run([
                    sys.executable,
                    str(self.ai_home / "jar_to_source_converter.py"),
                    "--project-root", str(self.project_root),
                    "--dependency", dep["coordinates"]
                ], capture_output=True, text=True, timeout=300)
                
                if result.returncode == 0:
                    results["success"].append(dep)
                    print(f"✅ 转换成功: {dep['artifactId']}")
                else:
                    results["failed"].append(dep)
                    print(f"❌ 转换失败: {dep['artifactId']}")
                    
            except subprocess.TimeoutExpired:
                results["failed"].append(dep)
                print(f"⏰ 转换超时: {dep['artifactId']}")
            except Exception as e:
                results["failed"].append(dep)
                print(f"❌ 转换异常: {dep['artifactId']} - {e}")
        
        return results
    
    def generate_source_summary(self, results: Dict[str, Any]) -> Dict[str, Any]:
        """生成源码摘要"""
        summary = {
            "totalProcessed": len(results["success"]) + len(results["failed"]),
            "successCount": len(results["success"]),
            "failedCount": len(results["failed"]),
            "successRate": 0,
            "sources": []
        }
        
        if summary["totalProcessed"] > 0:
            summary["successRate"] = summary["successCount"] / summary["totalProcessed"] * 100
        
        # 收集成功转换的源码信息
        for dep in results["success"]:
            # 在Maven仓库中查找源码目录
            group_path = dep["groupId"].replace(".", "/")
            artifact_path = self.m2_repo / group_path / dep["artifactId"] / dep["version"]
            jar_name = f"{dep['artifactId']}-{dep['version']}.jar"
            source_dir = artifact_path / f"{dep['artifactId']}-{dep['version']}-sources"
            
            if source_dir.exists():
                java_files = list(source_dir.rglob("*.java"))
                summary["sources"].append({
                    "dependency": dep,
                    "sourceDir": str(source_dir),
                    "javaFileCount": len(java_files),
                    "status": "available"
                })
            else:
                summary["sources"].append({
                    "dependency": dep,
                    "sourceDir": str(source_dir),
                    "javaFileCount": 0,
                    "status": "missing"
                })
        
        return summary
    
    def save_analysis_report(self, index: Dict[str, Any], 
                           conversion_results: Dict[str, Any], 
                           summary: Dict[str, Any]) -> str:
        """保存分析报告"""
        report = {
            "metadata": {
                "generatedAt": index.get("metadata", {}).get("generatedAt", ""),
                "projectRoot": str(self.project_root),
                "analysisType": "third_party_source_analysis"
            },
            "project": index.get("project", {}),
            "dependencies": index.get("dependencies", {}),
            "conversionResults": conversion_results,
            "summary": summary,
            "recommendations": self._generate_recommendations(summary)
        }
        
        report_file = self.ai_home / "THIRD-PARTY-SOURCE-ANALYSIS.json"
        
        try:
            with open(report_file, 'w', encoding='utf-8') as f:
                json.dump(report, f, indent=2, ensure_ascii=False)
            
            print(f"📄 分析报告已保存: {report_file}")
            return str(report_file)
            
        except Exception as e:
            print(f"❌ 保存分析报告时出错: {e}")
            return ""
    
    def _generate_recommendations(self, summary: Dict[str, Any]) -> List[str]:
        """生成分析建议"""
        recommendations = []
        
        if summary["successRate"] < 50:
            recommendations.append("转换成功率较低，建议检查反编译器配置")
        
        if summary["failedCount"] > 0:
            recommendations.append("部分依赖转换失败，可能需要手动处理")
        
        if summary["successCount"] > 0:
            recommendations.append("成功转换的依赖源码可用于AI分析")
        
        return recommendations
    
    def run_full_analysis(self, max_dependencies: int = 10) -> bool:
        """运行完整的第三方源码分析"""
        print("🚀 开始第三方源码分析...")
        
        # 1. 生成包索引
        if not self.generate_package_index():
            return False
        
        # 2. 加载包索引
        index = self.load_package_index()
        if not index:
            return False
        
        # 3. 获取依赖列表
        dependencies = index.get("dependencies", {}).get("all", [])
        if not dependencies:
            print("❌ 未找到依赖信息")
            return False
        
        print(f"📦 发现 {len(dependencies)} 个依赖")
        
        # 4. 转换依赖为源码
        conversion_results = self.convert_dependencies_to_source(dependencies, max_dependencies)
        
        # 5. 生成摘要
        summary = self.generate_source_summary(conversion_results)
        
        # 6. 保存分析报告
        report_file = self.save_analysis_report(index, conversion_results, summary)
        
        # 7. 显示结果
        self._display_results(summary, report_file)
        
        return True
    
    def _display_results(self, summary: Dict[str, Any], report_file: str):
        """显示分析结果"""
        print(f"\n📊 分析结果:")
        print(f"  总处理数: {summary['totalProcessed']}")
        print(f"  成功转换: {summary['successCount']}")
        print(f"  转换失败: {summary['failedCount']}")
        print(f"  成功率: {summary['successRate']:.1f}%")
        
        if summary["sources"]:
            print(f"\n📁 可用源码:")
            for source in summary["sources"]:
                if source["status"] == "available":
                    print(f"  ✅ {source['dependency']['artifactId']}: {source['javaFileCount']} 个Java文件")
                else:
                    print(f"  ❌ {source['dependency']['artifactId']}: 源码不可用")
        
        if report_file:
            print(f"\n📄 详细报告: {report_file}")


def main():
    parser = argparse.ArgumentParser(description="第三方源码分析器")
    parser.add_argument("--project-root", default=".", help="项目根目录路径")
    parser.add_argument("--max-deps", type=int, default=10, help="最大转换依赖数")
    parser.add_argument("--index-only", action="store_true", help="仅生成包索引")
    parser.add_argument("--convert-only", action="store_true", help="仅转换源码")
    
    args = parser.parse_args()
    
    analyzer = ThirdPartySourceAnalyzer(args.project_root)
    
    if args.index_only:
        # 仅生成包索引
        analyzer.generate_package_index()
    elif args.convert_only:
        # 仅转换源码
        index = analyzer.load_package_index()
        if index:
            dependencies = index.get("dependencies", {}).get("all", [])
            analyzer.convert_dependencies_to_source(dependencies, args.max_deps)
    else:
        # 完整分析
        analyzer.run_full_analysis(args.max_deps)


if __name__ == "__main__":
    main()
