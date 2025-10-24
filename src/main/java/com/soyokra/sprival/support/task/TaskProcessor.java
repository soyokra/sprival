package com.soyokra.sprival.support.task;

public interface TaskProcessor {

    // 调度
    // task_tbl
    // id
    // idempotent_id: 幂等id
    // parent_task_code: 父任务编码
    // parent_task_name: 父任务名称
    // task_code: 任务编码
    // task_name: 任务名称
    // dispatch_time：调度时间
    // dispatch_count: 调度计数
    // dispatch_limit: 调度限制
    // dispatch_expire_time: 调度过期时间
    // execute_time：执行时间
    // execute_expire_time: 执行过期时间
    // execute_driver(sync, async, rabbitmq/kafka)：执行驱动
    // status_no: 0 => 待调度 1 => 调度中 2 => 调度成功 3 => 调度失败 4 => 调度关闭 5 => 待执行 6 =>  执行中 7 => 执行成功 8 => 执行失败 9 => 执行关闭
    // task_params
    // task_result
    // error_code
    // error_message
    // 动态索引设计
    // task_archive_datetime_tbl 归档表

    TaskDispatchStatus dispatch();

    // 执行
    TaskExecuteStatus execute();

    // 触发
    boolean trigger();
}
