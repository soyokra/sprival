@echo off
REM Sprival项目UTF-8编码启动脚本
REM 解决Windows GBK环境下的编码兼容性问题

echo 启动Sprival应用（UTF-8编码）...

REM 设置环境变量
set JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dconsole.encoding=UTF-8

REM 启动应用
mvn spring-boot:run -Dfile.encoding=UTF-8

pause
