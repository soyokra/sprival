package com.soyokra.sprival.generator;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.soyokra.sprival.dao.master.BaseTblProvider;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Scanner;


/**
 * @link https://baomidou.com/pages/981406/#%E6%95%B0%E6%8D%AE%E5%BA%93%E9%85%8D%E7%BD%AE-datasourceconfig
 *
 * 查询全部表，并且用逗号隔开
 * SELECT GROUP_CONCAT(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='oms_db' AND TABLE_NAME like '%tbl';
 */
public class SprivalGenerator {
    public static void main(String[] args) {
        String dbname = "sprival";
        String url = "jdbc:mysql://localhost:3306/"+dbname+"?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false";
        String username = "root";
        String password = "workdock";
        String rootPath = ".\\src\\main";
        String packageParent = "com.soyokra.sprival.dao";
        String packageModuleName = "sprival";

        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url,username,password)
                .build();

        GlobalConfig globalConfig = new GlobalConfig.Builder()
                .fileOverride()
                .outputDir(rootPath + "\\java")
                .author("zhengchenping")
                .dateType(DateType.TIME_PACK)
                .commentDate("yyyy-MM-dd")
                .build();


        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent(packageParent)
                .moduleName(packageModuleName)
                .entity("model")
                .service("contract")
                .serviceImpl("provider")
                .mapper("mapper")
                .xml("mapper.xml")
                .pathInfo(Collections.singletonMap(OutputFile.mapperXml, rootPath + "\\resources\\mapper\\"+packageModuleName))
                .build();

        TemplateConfig templateConfig = new TemplateConfig.Builder()
                .disable(TemplateType.CONTROLLER)
                .entity("/templates/entity.java")
                .service("/templates/service.java")
                .serviceImpl("/templates/serviceImpl.java")
                .mapper("/templates/mapper.java")
                .mapperXml("/templates/mapper.xml")
                .build();

        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .enableCapitalMode()
                .enableSkipView()
                .disableSqlFilter()
                .addInclude(scanner("表名，多个英文逗号分割").split(","))
                .build()
                .entityBuilder()
                .enableLombok()
                .build()
                .serviceBuilder()
                .superServiceImplClass(BaseTblProvider.class)
                .formatServiceFileName("%sContract")
                .formatServiceImplFileName("%sProvider")
                .build();


        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig);
        autoGenerator.global(globalConfig)
                .packageInfo(packageConfig)
                .template(templateConfig)
                .strategy(strategyConfig)
                .execute(new FreemarkerTemplateEngine());

    }

    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            System.out.println(ipt);
            if (StringUtils.hasLength(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }
}
