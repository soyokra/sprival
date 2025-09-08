# IntelliJ IDEA 编码配置指南

## 问题描述
在Windows GBK环境下使用IntelliJ IDEA打开项目时，中文注释显示为乱码。

## 解决方案

### 1. 全局编码设置

#### 步骤1: 打开设置
- 菜单: `File` → `Settings` (或按 `Ctrl+Alt+S`)
- 或者: `File` → `Other Settings` → `Settings for New Projects`

#### 步骤2: 配置编辑器编码
```
Editor → File Encodings
├── Global Encoding: UTF-8
├── Project Encoding: UTF-8  
├── Default encoding for properties files: UTF-8
└── Transparent native-to-ascii conversion: ✅ 勾选
```

#### 步骤3: 配置控制台编码
```
Editor → General → Console
└── Default Encoding: UTF-8
```

### 2. 项目级编码设置

#### 方法1: 通过项目设置
1. 右键点击项目根目录
2. 选择 `Properties`
3. 在 `Resource` 标签页中设置:
   - Text file encoding: UTF-8
   - New text file line delimiter: Unix and macOS (\n)

#### 方法2: 通过.idea/encodings.xml文件
项目已包含正确的编码配置文件。

### 3. 文件级编码设置

#### 临时设置单个文件编码
1. 打开有乱码的文件
2. 右下角状态栏显示当前编码
3. 点击编码名称
4. 选择 `UTF-8`
5. 选择 `Reload` 或 `Convert`

#### 永久设置文件编码
1. 右键点击文件
2. 选择 `File Encoding`
3. 选择 `UTF-8`
4. 选择 `Convert` (转换文件内容)

### 4. 验证配置

#### 检查文件编码
```bash
# 在项目根目录执行
Get-Content src/main/resources/application.properties -Encoding UTF8
```

#### 检查IDEA设置
- 确认 `File Encodings` 中所有选项都设置为 UTF-8
- 确认 `Console` 编码设置为 UTF-8

### 5. 常见问题解决

#### 问题1: 文件仍然显示乱码
**解决方案:**
1. 关闭文件
2. 在IDEA中删除文件缓存: `File` → `Invalidate Caches and Restart`
3. 重新打开文件

#### 问题2: 控制台输出乱码
**解决方案:**
1. 设置控制台编码为UTF-8
2. 在运行配置中添加VM参数: `-Dfile.encoding=UTF-8`

#### 问题3: 新创建的文件乱码
**解决方案:**
1. 确保全局编码设置为UTF-8
2. 检查 `Default encoding for properties files` 设置

### 6. 推荐配置

#### 完整的IDEA编码配置
```xml
<!-- .idea/encodings.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="Encoding" defaultCharsetForPropertiesFiles="UTF-8">
    <file url="file://$PROJECT_DIR$" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/src" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/src/main/resources" charset="UTF-8" />
  </component>
</project>
```

#### 运行配置VM参数
```
-Dfile.encoding=UTF-8
-Dsun.jnu.encoding=UTF-8
-Dconsole.encoding=UTF-8
```

### 7. 团队协作建议

1. **统一编码标准**: 所有团队成员使用UTF-8编码
2. **版本控制**: 确保.gitattributes文件正确配置
3. **文档同步**: 将编码配置要求写入项目文档
4. **定期检查**: 使用编码测试脚本验证配置

## 验证步骤

1. 重启IDEA
2. 重新打开项目
3. 检查application.properties文件中文注释是否正常显示
4. 运行应用验证功能正常

---

*如果问题仍然存在，请检查系统环境变量和JVM启动参数。*
