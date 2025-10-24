# 源码存放位置更新说明

## 🔄 修改内容

根据用户要求，已将JAR转源码工具的源码存放位置从独立的 `third-party-sources/` 目录改为直接在JAR包所在位置创建源码目录。

## 📁 新的源码存放结构

### 修改前
```
ai-home/
└── third-party-sources/
    ├── spring-boot-starter-web/
    ├── spring-boot-starter-data-jpa/
    └── ...
```

### 修改后
```
~/.m2/repository/org/springframework/boot/spring-boot-starter-web/2.7.18/
├── spring-boot-starter-web-2.7.18.jar
└── spring-boot-starter-web-2.7.18-sources/    # 转换后的源码
    ├── org/springframework/boot/web/servlet/
    └── ...

~/.m2/repository/org/springframework/boot/spring-boot-starter-data-jpa/2.7.18/
├── spring-boot-starter-data-jpa-2.7.18.jar
└── spring-boot-starter-data-jpa-2.7.18-sources/    # 转换后的源码
    ├── org/springframework/boot/autoconfigure/orm/jpa/
    └── ...
```

## 🔧 修改的文件

### 1. jar_to_source_converter.py
- 移除了 `self.output_dir` 属性
- 修改 `convert_jar_to_source()` 方法，在JAR文件所在目录创建源码目录
- 源码目录命名规则：`{jar-file-name}-sources`

### 2. third_party_source_analyzer.py
- 更新源码目录查找逻辑
- 在Maven仓库中查找 `-sources` 目录
- 更新统计和报告生成逻辑

### 3. quick_start.py
- 更新帮助信息中的输出文件说明
- 修改源码目录检查逻辑
- 在Maven仓库中查找所有 `-sources` 目录

### 4. 文档更新
- 更新 `README-THIRD-PARTY-SOURCE-ANALYSIS.md`
- 更新 `IMPLEMENTATION-SUMMARY.md`
- 创建 `SOURCE-LOCATION-UPDATE.md` 说明文档

## 🎯 优势

### 1. 更直观的源码位置
- 源码直接与JAR包放在一起
- 便于查找和管理
- 符合Maven仓库的惯例

### 2. 更好的组织方式
- 每个依赖的源码独立存放
- 避免源码目录混乱
- 便于按依赖查看源码

### 3. 更符合开发习惯
- 与Maven仓库结构一致
- 便于IDE识别和索引
- 支持源码调试

## 🧪 测试验证

创建了 `test_source_location.py` 测试文件来验证新的源码存放位置：

```python
# 测试结果示例
测试源码存放位置...
测试依赖: org.springframework.boot:spring-boot-starter-web:2.7.18
找到JAR文件: C:\Users\soyok\.m2\repository\org\springframework\boot\spring-boot-starter-web\2.7.18\spring-boot-starter-web-2.7.18.jar
JAR文件位置: C:\Users\soyok\.m2\repository\org\springframework\boot\spring-boot-starter-web\2.7.18\spring-boot-starter-web-2.7.18.jar
预期源码目录: C:\Users\soyok\.m2\repository\org\springframework\boot\spring-boot-starter-web\2.7.18\spring-boot-starter-web-2.7.18-sources
源码目录不存在
请先运行转换工具创建源码目录
```

## 🚀 使用方法

### 转换特定依赖
```bash
python jar_to_source_converter.py --dependency "org.springframework.boot:spring-boot-starter-web:2.7.18"
```

### 转换所有依赖
```bash
python jar_to_source_converter.py --all
```

### 使用快速启动工具
```bash
python quick_start.py --action convert --dependency "org.springframework.boot:spring-boot-starter-web:2.7.18"
```

## 📊 预期结果

转换完成后，源码将直接存放在Maven仓库中：

```
~/.m2/repository/org/springframework/boot/spring-boot-starter-web/2.7.18/
├── spring-boot-starter-web-2.7.18.jar
└── spring-boot-starter-web-2.7.18-sources/
    ├── org/springframework/boot/web/servlet/
    ├── org/springframework/boot/web/servlet/support/
    └── ... (所有Java源码文件)
```

这样的组织方式更加直观和便于管理，符合用户的期望。
