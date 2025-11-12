"""
API Test Framework - 命令行入口

提供命令行接口来执行 API 测试
"""

import sys
import os

# 添加 src 目录到路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src'))

from src.executor.command_parser import CommandParser
from src.executor.config_loader import ConfigLoader
from src.executor.quick_commands import QuickCommands
from src.executor.test_executor import TestExecutor
from src.config.settings import Settings
from src.utils.logger import Logger
from colorama import Fore, Style, init

# 初始化 colorama
init(autoreset=True)


def print_banner():
    """打印欢迎横幅"""
    banner = f"""
{Fore.CYAN}================================================================
                                                              
        {Fore.YELLOW}API Test Framework v1.0{Fore.CYAN}                          
                                                              
        {Fore.GREEN}功能完善的 REST API 接口测试框架{Fore.CYAN}                    
                                                              
================================================================{Style.RESET_ALL}
"""
    print(banner)


def handle_quick_command(args) -> int:
    """
    处理快速命令
    
    Args:
        args: 命令行参数
        
    Returns:
        退出代码
    """
    logger = Logger.get_logger("main")
    
    try:
        # 获取场景配置
        config_dict = QuickCommands.get_scenario(
            name=args.scenario_name,
            base_url=args.url,
            endpoint=args.endpoint
        )
        
        # 创建配置加载器
        loader = ConfigLoader()
        settings = loader.load_from_dict(config_dict)
        
        # 打印场景信息
        print(f"\n{Fore.GREEN}>> 执行快速场景:{Style.RESET_ALL} {args.scenario_name}")
        print(f"{Fore.CYAN}场景描述:{Style.RESET_ALL} {settings.description}\n")
        
        # 创建并执行测试
        executor = TestExecutor(settings)
        result = executor.execute()
        
        if result['success']:
            print(f"\n{Fore.GREEN}[SUCCESS] 测试完成{Style.RESET_ALL}")
            return 0
        else:
            print(f"\n{Fore.RED}[FAILED] 测试失败: {result.get('error', '未知错误')}{Style.RESET_ALL}")
            return 1
    
    except Exception as e:
        logger.error(f"快速命令执行失败: {str(e)}", exc_info=True)
        print(f"\n{Fore.RED}[ERROR] 错误: {str(e)}{Style.RESET_ALL}")
        return 1


def handle_normal_command(args) -> int:
    """
    处理常规命令
    
    Args:
        args: 命令行参数
        
    Returns:
        退出代码
    """
    logger = Logger.get_logger("main")
    
    try:
        # 加载配置
        if args.config:
            # 从配置文件加载
            loader = ConfigLoader()
            settings = loader.load(args.config)
            print(f"\n{Fore.GREEN}>> 使用配置文件:{Style.RESET_ALL} {args.config}\n")
        else:
            # 从命令行参数创建配置
            parser = CommandParser()
            config_dict = parser.to_config_dict(args)
            
            loader = ConfigLoader()
            settings = loader.load_from_dict(config_dict)
            print(f"\n{Fore.GREEN}>> 使用命令行参数{Style.RESET_ALL}\n")
        
        # 创建并执行测试
        executor = TestExecutor(settings)
        result = executor.execute()
        
        if result['success']:
            print(f"\n{Fore.GREEN}[SUCCESS] 测试完成{Style.RESET_ALL}")
            
            # 打印报告文件路径
            if 'reports' in result:
                print(f"\n{Fore.CYAN}报告文件:{Style.RESET_ALL}")
                for format_name, filepath in result['reports'].items():
                    if filepath != 'console':
                        print(f"  - {format_name}: {filepath}")
            
            return 0
        else:
            print(f"\n{Fore.RED}[FAILED] 测试失败: {result.get('error', '未知错误')}{Style.RESET_ALL}")
            return 1
    
    except FileNotFoundError as e:
        logger.error(f"文件不存在: {str(e)}")
        print(f"\n{Fore.RED}[ERROR] 错误: {str(e)}{Style.RESET_ALL}")
        return 1
    
    except ValueError as e:
        logger.error(f"配置错误: {str(e)}")
        print(f"\n{Fore.RED}[ERROR] 配置错误: {str(e)}{Style.RESET_ALL}")
        return 1
    
    except Exception as e:
        logger.error(f"测试执行失败: {str(e)}", exc_info=True)
        print(f"\n{Fore.RED}[ERROR] 错误: {str(e)}{Style.RESET_ALL}")
        return 1


def main():
    """主函数"""
    # 打印横幅
    print_banner()
    
    # 解析命令行参数
    parser = CommandParser()
    args = parser.parse()
    
    # 处理命令
    if args.command == 'quick':
        # 快速命令
        exit_code = handle_quick_command(args)
    else:
        # 检查是否提供了必要的参数
        if not args.config and not args.url:
            print(f"{Fore.RED}错误: 必须指定 --config 或 --url 参数{Style.RESET_ALL}")
            print(f"\n使用 {Fore.CYAN}python main.py --help{Style.RESET_ALL} 查看帮助")
            print(f"\n或使用快速命令：")
            QuickCommands.print_scenarios()
            exit_code = 1
        else:
            # 常规命令
            exit_code = handle_normal_command(args)
    
    sys.exit(exit_code)


if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        print(f"\n\n{Fore.YELLOW}测试被用户中断{Style.RESET_ALL}")
        sys.exit(130)

