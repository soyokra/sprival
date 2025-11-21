#!/usr/bin/env python3
"""
Elasticsearch 初始化脚本。

功能：
1. 等待 Elasticsearch 启动
2. 等待内置用户初始化
3. 创建/更新角色
4. 创建/更新用户
5. 创建/更新索引模板
"""
import os
import sys
from pathlib import Path
from es_setup.client import ESClient
from es_setup.wait import wait_for_elasticsearch, wait_for_builtin_users
from es_setup.roles import ensure_role, load_role_from_file
from es_setup.users import (
    check_user_exists,
    create_user,
    set_user_password,
    USERS_PASSWORDS,
    USERS_ROLES,
)
from es_setup.templates import ensure_index_template, load_template_from_file


def log(message: str) -> None:
    """输出日志消息。"""
    print(f"[+] {message}")


def sublog(message: str) -> None:
    """输出子级日志消息。"""
    print(f"   ⠿ {message}")


def suberr(message: str) -> None:
    """输出错误消息。"""
    print(f"   ⠍ {message}", file=sys.stderr)


def main() -> int:
    """主函数。"""
    # 获取脚本所在目录
    script_dir = Path(__file__).parent
    roles_dir = script_dir / "roles"
    templates_dir = script_dir / "templates"

    # 初始化客户端
    client = ESClient()

    # 等待 Elasticsearch 启动
    log("Waiting for availability of Elasticsearch. This can take several minutes.")
    if not wait_for_elasticsearch(client=client):
        suberr("Could not resolve host. Is Elasticsearch running?")
        return 1

    sublog("Elasticsearch is running")

    # 等待内置用户初始化
    log("Waiting for initialization of built-in users")
    if not wait_for_builtin_users(client=client):
        suberr("Timed out waiting for condition")
        return 1

    sublog("Built-in users were initialized")

    # 角色配置映射
    roles_files = {
        "logstash_writer": "logstash_writer.json",
        "metricbeat_writer": "metricbeat_writer.json",
        "filebeat_writer": "filebeat_writer.json",
        "heartbeat_writer": "heartbeat_writer.json",
    }

    # 创建/更新角色
    for role_name, role_file in roles_files.items():
        log(f"Role '{role_name}'")

        role_path = roles_dir / role_file
        if not role_path.exists():
            sublog(f"No role body found at '{role_path}', skipping")
            continue

        try:
            role_body = load_role_from_file(roles_dir, role_file)
            sublog("Creating/updating")
            if not ensure_role(client, role_name, role_body):
                return 1
        except Exception as e:
            suberr(f"Failed to process role '{role_name}': {e}")
            return 1

    # 创建/更新用户
    for username, password_env in USERS_PASSWORDS.items():
        log(f"User '{username}'")

        password = os.getenv(password_env)
        if not password:
            sublog("No password defined, skipping")
            continue

        try:
            user_exists = check_user_exists(client, username)

            if user_exists:
                sublog("User exists, setting password")
                if not set_user_password(client, username, password):
                    return 1
            else:
                role = USERS_ROLES.get(username)
                if not role:
                    suberr("  No role defined, skipping creation")
                    continue

                sublog("User does not exist, creating")
                if not create_user(client, username, password, role):
                    return 1
        except Exception as e:
            suberr(f"Failed to process user '{username}': {e}")
            return 1

    # 索引模板配置映射
    index_templates_files = {
        "sprival-logs": "sprival-logs-template.json",
    }

    # 创建/更新索引模板
    for template_name, template_file in index_templates_files.items():
        log(f"Index template '{template_name}'")

        template_path = templates_dir / template_file
        if not template_path.exists():
            sublog(f"No template body found at '{template_path}', skipping")
            continue

        try:
            template_body = load_template_from_file(templates_dir, template_file)
            sublog("Creating/updating")
            if not ensure_index_template(client, template_name, template_body):
                return 1
        except Exception as e:
            suberr(f"Failed to process template '{template_name}': {e}")
            return 1

    return 0


if __name__ == "__main__":
    sys.exit(main())

