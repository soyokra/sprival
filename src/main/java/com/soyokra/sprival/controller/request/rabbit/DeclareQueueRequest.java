package com.soyokra.sprival.controller.request.rabbit;

import lombok.Data;

@Data
public class DeclareQueueRequest {
    private String queueName;

    private Integer delayTime;
}
