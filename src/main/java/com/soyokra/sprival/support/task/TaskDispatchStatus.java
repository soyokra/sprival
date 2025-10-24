package com.soyokra.sprival.support.task;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 调度状态
 */
public enum TaskDispatchStatus {
    WAIT(0, "等待"),
    SUCCESS(1, "成功"),
    FAIL(2,  "失败"),
    CLOSE(3, "关闭")
    ;

    @EnumValue
    @JsonValue
    private final Integer value;

    private final String text;

    TaskDispatchStatus(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
