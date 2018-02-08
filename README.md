* [See the docs](http://mybatis.github.io/mybatis-3)
* [Download Latest](https://github.com/mybatis/mybatis-3/releases)
* [Download Snapshot](https://oss.sonatype.org/content/repositories/snapshots/org/mybatis/mybatis/)

#### 奇淫巧技
* ctrl + H,查看类的子类
* ctrl + alt + u,或者右击选择

#### MyBatis源码解析
- SqlSessionFactoryBuilder(类): 构造SqlSessionFactory实例
    > 使用Reader/InputStream/Properties/Configuration等构造
    - SqlSessionFactory(接口): 从一个连接或数据源中,创建SqlSession
        - DefaultSqlSessionFactory(类):实现SqlSessionFactory,默认sqlSession工厂类
            > 创建SqlSession,TransactionFactory,关闭事务等
            > 可自定义ExecutorType(执行器类型)/事务隔离界别/是否自动提交等
    
- Configuration(类):配置类.

- Environment(类):环境类.存储了一个 id/事务工厂/dataSource;都不能为空

- TransactionFactory(接口):事务工厂,设置自定义事务属性/创建事务(使用connection或dataSource)
    - JdbcTransactionFactory(类):jdbc事务工厂.创建JdbcTransaction实例
    - ManagedTransactionFactory(类):托管事务工厂,创建ManagedTransaction实例
    
- Transaction(接口):事务,包装了一个数据库连接,处理包含的数据库连接的生命周期.它的创建/准备/提交或回滚/关闭
    > 有获取/提交/回滚/关闭内部数据库连接,获取数据库超时时间等若干方法
    - JdbcTransaction(类):直接使用JDBC的提交或回滚功能.它依赖于从数据源中检索到的连接,来管理事务范围.
        > 包含了 数据库连接/数据源/事务隔离级别/是否自动提交等
    - ManagedTransaction(类):简化了的JdbcTransaction,不支持提交回滚操作

- ResultContext<T>(接口):结果上下文.提供 获取结果对象/获取结果总数/判断是否停止/停止 等方法    
    - DefaultResultContext<T>:默认结果上下文.
        > 组合了 结果对象/结果总数/是否停止.实现父类方法.以及一个 nextResultObject方法. 
        
- ResultHandler<T>(接口):结果处理器.一个handleResult()方法,参数为ResultContext<T>     
    > 该接口有很多很多很多多...实现类.
    
- MappedStatement(类): 用于描述一条SQL语句,一个MappedStatement对象对应Mapper配置文件中的一个select/update/insert/delete节点

- RowBounds(类): 有offset(默认0) 和 limit(默认Integer.MAX_VALUE) 两个参数,也就是MySQL中的limit查询的两个参数,偏移量和查询个数

- CacheKey(类): 缓存key,有update()/updateAll()/getUpdateCount()等方法

- BoundSql(类): 

- Executor(接口):执行器