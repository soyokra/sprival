package com.soyokra.sprival.database;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.soyokra.sprival.app.repository.db.blog.BlogBaseProvider;
import org.apache.ibatis.type.JdbcType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @link https://baomidou.com/pages/981406/#%E6%95%B0%E6%8D%AE%E5%BA%93%E9%85%8D%E7%BD%AE-datasourceconfig
 *
 * 查询全部表，并且用逗号隔开
 * SELECT GROUP_CONCAT(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='blog';
 */
public class SprivalBlogGenerator {
    public static void main(String[] args) {
        String dbname = "blog";
        String url = "jdbc:mysql://127.0.0.1:33306/"+dbname+"?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false";
        String username = "root";
        String password = "workdock";

        String packageParent = "com.soyokra.sprival.app.repository.db";
        String packageModuleName = "blog";
        String prefix = "";

        Path rootPath = Paths.get(System.getProperty("user.dir"));
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .author("soyokra")
                        .outputDir(rootPath + "/src/main/java")
                        .commentDate("yyyy-MM-dd")
                )
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder -> builder
                        .parent(packageParent)
                        .moduleName(packageModuleName)
                        .entity("model")
                        .mapper("mapper")
                        .service("contract")
                        .serviceImpl("provider")
                        .xml("mapper.xml")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, rootPath + "/src/main/resources/mapper/"+packageModuleName))
                )
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔")))
                        .build()
                        .entityBuilder()
                        .enableLombok()
                        .formatFileName(prefix+"%s")
                        .mapperBuilder()
                        .formatMapperFileName(prefix+"%sMapper")
                        .formatXmlFileName(prefix+"%s")
                        .serviceBuilder()
                        .superServiceImplClass(BlogBaseProvider.class)
                        .formatServiceFileName(prefix+"%sContract")
                        .formatServiceImplFileName(prefix+"%sProvider")
                        .controllerBuilder()
                        .disable()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

    }

    protected static List<String> getTables(String tables) {
        return Arrays.asList(tables.split(","));
    }
}
