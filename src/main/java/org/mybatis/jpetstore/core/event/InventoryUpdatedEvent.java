/*
 *    Copyright 2010-2024 the original author or authors.
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
package org.mybatis.jpetstore.core.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryUpdatedEvent extends DomainEvent {
  private String itemId;
  private int quantityChange;

  public InventoryUpdatedEvent(@JsonProperty("streamId") String streamId, @JsonProperty("entityType") String entityType,
      @JsonProperty("itemId") String itemId, @JsonProperty("quantityChange") int quantityChange,
      @JsonProperty("timestamp") long timestamp) {
    super(streamId, entityType, timestamp);
    this.itemId = itemId;
    this.quantityChange = quantityChange;
  }

  public String getItemId() {
    return itemId;
  }

  public int getQuantityChange() {
    return quantityChange;
  }

  @Override
  public String toString() {
    return "InventoryChangeEvent{" + "itemId='" + itemId + '\'' + ", changeInQuantity=" + quantityChange + "} "
        + super.toString();
  }

}
