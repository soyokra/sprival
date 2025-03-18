package com.soyokra.sprival.controller.request.rabbit;

import lombok.Data;

@Data
public class PublishRequest {
    private String queueName;

    private Integer delayTime;

    private String content;
}
