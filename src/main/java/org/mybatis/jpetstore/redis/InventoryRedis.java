/*
 *    Copyright 2010-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.redis;

import redis.clients.jedis.Jedis;

public class InventoryRedis {
  public static void main(String[] args) {

    try (Jedis jedis = RedisPool.getJedis()) {
      System.out.println("Connected to Redis");
      initializeInventory(jedis);
    }
  }

  private static void initializeInventory(Jedis jedis) {

    jedis.set("inventory:EST-1", "10000");
    jedis.set("inventory:EST-2", "10000");
    jedis.set("inventory:EST-3", "10000");
    jedis.set("inventory:EST-4", "10000");
    jedis.set("inventory:EST-5", "10000");
    jedis.set("inventory:EST-6", "10000");
    jedis.set("inventory:EST-7", "10000");
    jedis.set("inventory:EST-8", "10000");
    jedis.set("inventory:EST-9", "10000");
    jedis.set("inventory:EST-10", "10000");
    jedis.set("inventory:EST-11", "10000");
    jedis.set("inventory:EST-12", "10000");
    jedis.set("inventory:EST-13", "10000");
    jedis.set("inventory:EST-14", "10000");
    jedis.set("inventory:EST-15", "10000");
    jedis.set("inventory:EST-16", "10000");

    System.out.println("Inventory initialized");
  }
}
