"""
规划器

理解任务目标，分解为可执行的步骤序列
"""

import uuid
from typing import Dict, Any, List, Optional
from datetime import datetime

from ..models.plan import Plan, Step, StepStatus
from ..memory.long_term import LongTermMemory
from ..memory.knowledge_base import KnowledgeBase
from ..utils.logger import Logger

logger = Logger.get_logger("planner")


class Planner:
    """
    规划器
    
    负责理解任务目标，生成执行计划
    """
    
    def __init__(self, long_term_memory: Optional[LongTermMemory] = None,
                 knowledge_base: Optional[KnowledgeBase] = None):
        """
        初始化规划器
        
        Args:
            long_term_memory: 长期记忆
            knowledge_base: 知识库
        """
        self.long_term_memory = long_term_memory or LongTermMemory()
        self.knowledge_base = knowledge_base or KnowledgeBase()
    
    def plan(self, goal: str, context: Dict[str, Any]) -> Plan:
        """
        生成执行计划
        
        Args:
            goal: 目标描述
            context: 上下文信息
            
        Returns:
            执行计划
        """
        logger.info(f"开始生成计划，目标: {goal}")
        
        # 1. 理解目标
        goal_info = self._understand_goal(goal, context)
        
        # 2. 检索相关经验
        similar_executions = self.long_term_memory.search_similar_executions(goal, limit=5)
        
        # 3. 生成步骤序列
        steps = self._generate_steps(goal_info, context, similar_executions)
        
        # 4. 创建计划
        plan = Plan(
            id=str(uuid.uuid4()),
            goal=goal,
            steps=steps,
            context=context,
        )
        
        logger.info(f"计划生成完成，包含 {len(steps)} 个步骤")
        return plan
    
    def refine_plan(self, plan: Plan, feedback: Dict[str, Any]) -> Plan:
        """
        根据反馈优化计划
        
        Args:
            plan: 原计划
            feedback: 执行反馈
            
        Returns:
            优化后的计划
        """
        logger.info(f"开始优化计划: {plan.id}")
        
        # 根据反馈调整步骤
        failed_steps = feedback.get('failed_steps', [])
        for step_id in failed_steps:
            step = plan.get_step(step_id)
            if step:
                # 可以添加重试逻辑或替换步骤
                if step.retry_count < step.max_retries:
                    step.status = StepStatus.PENDING
                    step.retry_count += 1
                    logger.info(f"步骤 {step_id} 将重试 (第 {step.retry_count} 次)")
        
        plan.updated_at = datetime.now()
        return plan
    
    def _understand_goal(self, goal: str, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        理解目标
        
        Args:
            goal: 目标描述
            context: 上下文信息
            
        Returns:
            目标信息
        """
        # 简单的目标解析（可扩展为 LLM 理解）
        goal_lower = goal.lower()
        
        goal_info = {
            'original': goal,
            'keywords': goal.split(),
            'type': 'general',
        }
        
        # 识别任务类型
        if any(keyword in goal_lower for keyword in ['部署', 'deploy', '发布', 'release']):
            goal_info['type'] = 'deployment'
        elif any(keyword in goal_lower for keyword in ['监控', 'monitor', '检查', 'check']):
            goal_info['type'] = 'monitoring'
        elif any(keyword in goal_lower for keyword in ['诊断', 'diagnose', '故障', 'troubleshoot']):
            goal_info['type'] = 'diagnosis'
        elif any(keyword in goal_lower for keyword in ['优化', 'optimize', '调优', 'tune']):
            goal_info['type'] = 'optimization'
        elif any(keyword in goal_lower for keyword in ['安全', 'security', '扫描', 'scan']):
            goal_info['type'] = 'security'
        
        return goal_info
    
    def _generate_steps(self, goal_info: Dict[str, Any], context: Dict[str, Any],
                       similar_executions: List[Dict[str, Any]]) -> List[Step]:
        """
        生成步骤序列
        
        Args:
            goal_info: 目标信息
            context: 上下文信息
            similar_executions: 相似执行记录
            
        Returns:
            步骤列表
        """
        steps = []
        task_type = goal_info.get('type', 'general')
        
        # 根据任务类型生成步骤
        if task_type == 'deployment':
            steps = self._generate_deployment_steps(goal_info, context)
        elif task_type == 'monitoring':
            steps = self._generate_monitoring_steps(goal_info, context)
        elif task_type == 'diagnosis':
            steps = self._generate_diagnosis_steps(goal_info, context)
        else:
            # 默认步骤
            steps = self._generate_default_steps(goal_info, context)
        
        # 如果有相似执行记录，可以参考优化步骤
        if similar_executions:
            logger.info(f"参考 {len(similar_executions)} 个相似执行记录")
        
        return steps
    
    def _generate_deployment_steps(self, goal_info: Dict[str, Any], 
                                  context: Dict[str, Any]) -> List[Step]:
        """生成部署步骤"""
        steps = []
        step_id = 1
        
        # Step 1: 检查 GitLab 仓库
        steps.append(Step(
            id=f"step_{step_id}",
            name="检查 GitLab 仓库",
            description="检查 GitLab 仓库中是否存在指定版本",
            tool_name="gitlab",
            tool_params={
                'action': 'check_tag',
                'repository': context.get('repository', ''),
                'tag': context.get('version', ''),
            }
        ))
        step_id += 1
        
        # Step 2: 触发 CI/CD Pipeline
        steps.append(Step(
            id=f"step_{step_id}",
            name="触发 CI/CD Pipeline",
            description="触发 GitLab CI/CD Pipeline 构建",
            tool_name="gitlab",
            tool_params={
                'action': 'trigger_pipeline',
                'repository': context.get('repository', ''),
                'ref': context.get('version', 'main'),
            },
            depends_on=[f"step_{step_id - 1}"]
        ))
        step_id += 1
        
        # Step 3: 等待 Pipeline 完成
        steps.append(Step(
            id=f"step_{step_id}",
            name="等待 Pipeline 完成",
            description="等待 CI/CD Pipeline 执行完成",
            tool_name="gitlab",
            tool_params={
                'action': 'wait_pipeline',
                'pipeline_id': '${step_2.pipeline_id}',
            },
            depends_on=[f"step_{step_id - 1}"]
        ))
        step_id += 1
        
        # Step 4: 构建 Docker 镜像
        steps.append(Step(
            id=f"step_{step_id}",
            name="构建 Docker 镜像",
            description="构建应用 Docker 镜像",
            tool_name="docker",
            tool_params={
                'action': 'build',
                'image_name': context.get('image_name', ''),
                'tag': context.get('version', 'latest'),
            },
            depends_on=[f"step_{step_id - 1}"]
        ))
        step_id += 1
        
        # Step 5: 推送镜像
        steps.append(Step(
            id=f"step_{step_id}",
            name="推送 Docker 镜像",
            description="推送镜像到 Registry",
            tool_name="docker",
            tool_params={
                'action': 'push',
                'image_name': context.get('image_name', ''),
                'tag': context.get('version', 'latest'),
            },
            depends_on=[f"step_{step_id - 1}"]
        ))
        step_id += 1
        
        # Step 6: 更新部署
        steps.append(Step(
            id=f"step_{step_id}",
            name="更新 Kubernetes 部署",
            description="更新 Kubernetes Deployment",
            tool_name="kubernetes",
            tool_params={
                'action': 'update_deployment',
                'namespace': context.get('namespace', 'default'),
                'deployment': context.get('deployment', ''),
                'image': f"{context.get('image_name', '')}:{context.get('version', 'latest')}",
            },
            depends_on=[f"step_{step_id - 1}"]
        ))
        step_id += 1
        
        # Step 7: 健康检查
        steps.append(Step(
            id=f"step_{step_id}",
            name="健康检查",
            description="检查应用健康状态",
            tool_name="health_check",
            tool_params={
                'action': 'check',
                'url': context.get('health_check_url', ''),
            },
            depends_on=[f"step_{step_id - 1}"]
        ))
        
        return steps
    
    def _generate_monitoring_steps(self, goal_info: Dict[str, Any],
                                  context: Dict[str, Any]) -> List[Step]:
        """生成监控步骤"""
        steps = []
        step_id = 1
        
        steps.append(Step(
            id=f"step_{step_id}",
            name="查询监控指标",
            description="从 Prometheus 查询应用指标",
            tool_name="prometheus",
            tool_params={
                'action': 'query',
                'query': context.get('query', ''),
            }
        ))
        
        return steps
    
    def _generate_diagnosis_steps(self, goal_info: Dict[str, Any],
                                  context: Dict[str, Any]) -> List[Step]:
        """生成诊断步骤"""
        steps = []
        step_id = 1
        
        steps.append(Step(
            id=f"step_{step_id}",
            name="收集日志",
            description="从 ELK 收集应用日志",
            tool_name="elk",
            tool_params={
                'action': 'search',
                'query': context.get('log_query', ''),
            }
        ))
        step_id += 1
        
        steps.append(Step(
            id=f"step_{step_id}",
            name="分析异常",
            description="分析日志中的异常模式",
            tool_name="log_analyzer",
            tool_params={
                'action': 'analyze',
                'logs': '${step_1.logs}',
            },
            depends_on=[f"step_{step_id - 1}"]
        ))
        
        return steps
    
    def _generate_default_steps(self, goal_info: Dict[str, Any],
                                context: Dict[str, Any]) -> List[Step]:
        """生成默认步骤"""
        return [
            Step(
                id="step_1",
                name="执行任务",
                description=goal_info.get('original', ''),
                tool_name="echo",
                tool_params={
                    'message': goal_info.get('original', ''),
                }
            )
        ]

