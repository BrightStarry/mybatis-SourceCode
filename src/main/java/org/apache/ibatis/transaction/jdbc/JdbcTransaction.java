/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.transaction.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionException;

/**
 * {@link Transaction}直接使用JDBC的提交或回滚功能.
 *  它依赖于从数据源中检索到的连接,来管理事务范围.
 *  在调用getConnection()之前，延迟连接检索.
 *  在自动提交时,忽略 提交或回滚请求
 *
 * @see JdbcTransactionFactory
 */
public class JdbcTransaction implements Transaction {

  //MyBatis自己的log对象 以后再看
  private static final Log log = LogFactory.getLog(JdbcTransaction.class);

  //数据库连接
  protected Connection connection;
  //数据源
  protected DataSource dataSource;
  //事物隔离级别
  protected TransactionIsolationLevel level;

  /**
   * 是否自动提交
   * (官方)备忘录,我们知道打印错误.查看 #941
   * ...我去.这#941是啥不知道.不过搜索941,能找到一个Code类
   */
  protected boolean autoCommmit;

  public JdbcTransaction(DataSource ds, TransactionIsolationLevel desiredLevel, boolean desiredAutoCommit) {
    dataSource = ds;
    level = desiredLevel;
    autoCommmit = desiredAutoCommit;
  }

  public JdbcTransaction(Connection connection) {
    this.connection = connection;
  }

  /**
   * 获取数据库连接,如果为空,调用{@link #openConnection}方法
   */
  @Override
  public Connection getConnection() throws SQLException {
    if (connection == null) {
      //打开连接
      openConnection();
    }
    return connection;
  }

  @Override
  public void commit() throws SQLException {
    //只有在数据库连接不为空,并且未开启自动提交时,才执行
    if (connection != null && !connection.getAutoCommit()) {
      if (log.isDebugEnabled()) {
        log.debug("Committing JDBC Connection [" + connection + "]");
      }
      connection.commit();
    }
  }

  @Override
  public void rollback() throws SQLException {
    if (connection != null && !connection.getAutoCommit()) {
      if (log.isDebugEnabled()) {
        log.debug("Rolling back JDBC Connection [" + connection + "]");
      }
      connection.rollback();
    }
  }

  @Override
  public void close() throws SQLException {
    if (connection != null) {
      //重置自动提交
      resetAutoCommit();
      if (log.isDebugEnabled()) {
        log.debug("Closing JDBC Connection [" + connection + "]");
      }
      connection.close();
    }
  }

  /**
   * 设置预期的自动提交属性
   */
  protected void setDesiredAutoCommit(boolean desiredAutoCommit) {
    try {
      //如果连接的 自动提交配置和预期的不同
      if (connection.getAutoCommit() != desiredAutoCommit) {
        if (log.isDebugEnabled()) {
          log.debug("Setting autocommit to " + desiredAutoCommit + " on JDBC Connection [" + connection + "]");
        }
        //才设置上预期的自动提交属性
        connection.setAutoCommit(desiredAutoCommit);
      }
    } catch (SQLException e) {
      //(官方)只有一个非常糟糕的驱动程序才会失败
      //我们对此无能为力
      //...666
      throw new TransactionException("Error configuring AutoCommit.  "
          + "Your driver may not support getAutoCommit() or setAutoCommit(). "
          + "Requested setting: " + desiredAutoCommit + ".  Cause: " + e, e);
    }
  }

  /**
   * 重置自动提交,为了解决一些数据库的问题
   */
  protected void resetAutoCommit() {
    try {
      //如果未开启自动提交
      if (!connection.getAutoCommit()) {
        /**
         * 如果选择执行,MyBatis不会调用Connection的提交/回滚.
         * 一些数据库使用select语句开启事务,并在关闭前强制提交/回滚.
         * 一个变通方法是在关闭连接前,将autocommit设置为true
         * Sybase(美国数据库公司)在这里抛出了一个异常
         */
        if (log.isDebugEnabled()) {
          log.debug("Resetting autocommit to true on JDBC Connection [" + connection + "]");
        }
        connection.setAutoCommit(true);
      }
    } catch (SQLException e) {
      if (log.isDebugEnabled()) {
        log.debug("Error resetting autocommit to true "
          + "before closing the connection.  Cause: " + e);
      }
    }
  }

  /**
   * 打开连接
   */
  protected void openConnection() throws SQLException {
    if (log.isDebugEnabled()) {
      log.debug("Opening JDBC Connection");
    }
    //从数据源中获取连接
    connection = dataSource.getConnection();
    //如果隔离级别不为空,设置隔离级别到该连接
    if (level != null) {
      connection.setTransactionIsolation(level.getLevel());
    }
    //设置是否自动提交
    setDesiredAutoCommit(autoCommmit);
  }

  //此处没有超时时间设置
  @Override
  public Integer getTimeout() throws SQLException {
    return null;
  }
  
}
