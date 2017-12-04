/**
 *    Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 包装了一个数据库连接
 * 处理包含的数据库连接的生命周期.它的创建/准备/提交或回滚/关闭
 */
public interface Transaction {

  /**
   * 获取内部数据库连接
   * @return 数据库连接
   * @throws SQLException
   */
  Connection getConnection() throws SQLException;

  /**
   * 提交内部数据库连接
   * @throws SQLException
   */
  void commit() throws SQLException;

  /**
   * 回滚内部数据库连接.
   * @throws SQLException
   */
  void rollback() throws SQLException;

  /**
   * 关闭内部数据库连接.
   * @throws SQLException
   */
  void close() throws SQLException;

  /**
   * 如果设置了,获取数据库超时属性
   * @throws SQLException
   */
  Integer getTimeout() throws SQLException;
  
}
