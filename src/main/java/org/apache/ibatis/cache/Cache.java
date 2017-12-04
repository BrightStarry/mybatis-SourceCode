/**
 *    Copyright 2009-2015 the original author or authors.
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
package org.apache.ibatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * SPI缓存提供者
 * 
 * 将为每个命名空间创建一个缓存实例
 * 
 * 缓存实现必须有一个构造函数，该构造函数接收缓存id作为字符串参数
 * 
 * 将把名称空间作为id传递给构造函数
 * 
 * <pre>
 * public MyCache(final String id) {
 *  if (id == null) {
 *    throw new IllegalArgumentException("Cache instances require an ID");
 *  }
 *  this.id = id;
 *  initialize();
 * }
 * </pre>
 */

public interface Cache {

  /**
   * @return 这个缓存的标识符,   通常为命名空间(应该如此)
   */
  String getId();

  /**
   * @param key 可以是任何对象,但通常是一个{@link CacheKey}
   * @param value 一个select的结果
   */
  void putObject(Object key, Object value);

  /**
   * 通过key获取值
   * @param key key
   * @return .
   */
  Object getObject(Object key);

  /**
   * As of 3.3.0 this method is only called during a rollback 
   * for any previous value that was missing in the cache.
   * This lets any blocking cache to release the lock that 
   * may have previously put on the key.
   * A blocking cache puts a lock when a value is null 
   * and releases it when the value is back again.
   * This way other threads will wait for the value to be 
   * available instead of hitting the database.
   *
   * 
   * @param key The key
   * @return Not used
   */
  Object removeObject(Object key);

  /**
   * 清空这个缓存实例
   */  
  void clear();

  /**
   * 可选的,这个方法不是由核心来调用的。
   *
   * @return 存储在缓存中的元素的数量 (不是它的容量(可存储的大小)).
   */
  int getSize();
  
  /**
   * 可选的.
   * 3.2.6版本后,该方法不再被核心调用
   * Any locking needed by the cache must be provided internally by the cache provider.
   * 
   * @return A ReadWriteLock 
   */
  ReadWriteLock getReadWriteLock();

}