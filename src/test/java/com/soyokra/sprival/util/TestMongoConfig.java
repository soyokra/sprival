package com.soyokra.sprival.util;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * 测试MongoDB配置 使用嵌入式MongoDB服务器进行测试
 * 
 * @author Sprival Team
 * @version 1.0
 */
@TestConfiguration
public class TestMongoConfig {

    private MongodExecutable mongodExecutable;
    private MongodProcess mongodProcess;

    @PostConstruct
    public void startMongo() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodConfig mongodConfig = MongodConfig.builder().version(Version.Main.V4_4)
                .net(new Net("localhost", 27018, Network.localhostIsIPv6())).build();

        mongodExecutable = starter.prepare(mongodConfig);
        mongodProcess = mongodExecutable.start();
    }

    @PreDestroy
    public void stopMongo() {
        if (mongodProcess != null) {
            mongodProcess.stop();
        }
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Bean
    @Primary
    public MongoTemplate testMongoTemplate() {
        return new MongoTemplate(
                com.mongodb.client.MongoClients.create("mongodb://localhost:27018"),
                "test_sprival");
    }
}
