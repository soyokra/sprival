package com.soyokra.sprival.controller.request.rabbit;

import lombok.Data;

@Data
public class TestRequest {
    private String queueName;

    private Integer delayTime;
}
