package com.soyokra.sprival.controller.request.kafka;

import lombok.Data;

@Data
public class PublishRequest {
    private String topic;

    private String content;
}
