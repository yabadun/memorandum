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
