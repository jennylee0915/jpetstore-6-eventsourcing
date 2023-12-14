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
package org.mybatis.jpetstore.repository;

import org.mybatis.jpetstore.readmodel.InventoryReadModel;
import org.mybatis.jpetstore.redis.RedisPool;

import redis.clients.jedis.Jedis;

public class InventoryReadModelRepository {

  public InventoryReadModel findByItemId(String itemId) {
    try (Jedis jedis = RedisPool.getJedis()) {
      String data = jedis.get("inventory:" + itemId);
      if (data != null) {
        int quantity = Integer.parseInt(data);
        return new InventoryReadModel(itemId, quantity);
      }
      return null;
    }
  }

  public void updateInventory(String itemId, int quantity) {
    try (Jedis jedis = RedisPool.getJedis()) {
      jedis.set("inventory:" + itemId, String.valueOf(quantity));
    }
  }
}
