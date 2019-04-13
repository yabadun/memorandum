# 读写锁
- 共享锁（读锁）
- 排它锁（写锁）阻塞其他写锁和读锁

# 粒度锁
- 行锁(INNODB)开销大、加锁速度慢、锁定一行数据、发生锁竞争概率较低、并发度最高、性能高；
- 表锁(MYISAM)开销小、加锁速度快、锁定一整张表、发生锁竞争概率较高、并发度最低、性能低 ；
- 页锁介于两者之间，不太常见。
# 事务隔离级别
`SELECT @@tx_isolation`
`set autocommit=off;`
`set session transaction isolation level read uncommitted;`
- 未提交读(read uncommitted)
- 已提交读(read committed)
- 可重复读(REPEATABLE-READ)
- 序列化(serializable)

# 死锁
- tx1begin
`update table1 set col='val1' where id=1;`
`update table1 set col='val1' where id=2;`
- tx2begin
`update table1 set col='val2' where id=2;`
`update table1 set col='val2' where id=1;`

启动两个事务执行完第一条语句后执行下一条语句会出现死锁，mysql会回滚持有行锁最少的事务，如果持有行锁相同会回滚后执行的事务。
在事务未提交时修改操作并未真正写到磁盘数据库文件中，而是会先修改内存拷贝，再修改磁盘事务日志，而内存中的数据会缓慢的刷新到磁盘；
这种方式被称为预写式日志，所以修改数据会进行两次磁盘操作。
如果数据的修改已经记录到事务日志并持久化，但数据本身还没有写回到磁盘，此时系统崩溃，存储引擎会在重启时自动恢复这部分修改的数据。

# MVCC(多版本并发控制)
INNODB有两个隐藏列:行创建时间(当前服务的事务ID作为版本号),行删除时间(事务ID作为版本号)；

# 存储引擎
`show ENGINES`
- INNODB 基于聚簇索引建立，内部做了很多优化，从磁盘读取数据时采用可预测性

# 磁盘读取三步
- 寻道
- 旋转
- 读取

# 慢查询日志
- `show VARIABLES like 'slow_query_log%';`
- `set global slow_query_log=ON;`
- `show VARIABLES like 'log_queries_not_using_indexes%';`
- `show VARIABLES like 'long_query_time%';`

# show profiles
- `set profiling=1;`
- `show profiles;`
- `show profile for query 5;`
- `SHOW PROFILE BLOCK IO,CPU FOR QUERY 1;`
- `SELECT state, SUM(duration) AS Total_R, 
  ROUND(100 * SUM(duration) / (SELECT SUM(duration) FROM information_schema.profiling WHERE query_id = 1), 2) AS Pct_R, 
  COUNT(*) as Calls, SUM(duration) /COUNT(*) AS "R/Call" 
  FROM information_schema.profiling
WHERE query_id = 1 GROUP BY state ORDER BY total_r DESC;
`
# 执行计划explain
- table：对应的表
- type：连接类型
  - all 全表扫描`select * from table`
  - index 全索引扫描`select id from table`
  - range   `<` `>` `in ()` `between`   根据索引范围查找`select * from table where id>2`
  - ref  根据索引查询匹配某个值的行（默认多行）`select * from table where col=2`
  - eq_ref 根据索引查询匹配某个值的行最多一行的情况（主键或唯一索引）`select a.* from a join b where a.id=b.aid`
  - const/system 使用常量对主键唯一扫描 `select * from table where id=2`
- possible_keys：可能使用的索引
- key：实际使用的索引
- key_len：使用索引长度
- rows：预计扫描行数
- Extra：解析查询的额外信息（using index、using where、using temporary、using filesort）
  - Using index：列数据仅仅使用了索引中的信息而没有读取实际的表`Select address_id from address where address_id=1;`
  - Using where：MySQL服务器将在存储引擎检索行后，通过Where子句条件进行过滤`Select * from address where city_id>12;`
  - Using temporary：MYSQL需要创建一个临时表来存储结果，用于排序`Select DISTINCT district from address;`
  - Using filesort：MySQL将对结果进行外部排序`Select * from address  order by district;`
不考虑存储过程、触发器、缓存；部分计算为估算并不精确；只能解释查询语句
