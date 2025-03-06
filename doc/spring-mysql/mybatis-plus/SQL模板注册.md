SqlSessionFactory + Mapper id => MappedStatement（SQL 模板）
拼接查询条件 Wrapper（参数）
Wrapper + MappedStatement => BoundSql（占位符 SQL + 参数信息）
BoundSql + Wrapper => 完整 SQL