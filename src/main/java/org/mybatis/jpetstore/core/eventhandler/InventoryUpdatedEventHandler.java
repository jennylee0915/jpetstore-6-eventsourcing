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
package org.mybatis.jpetstore.core.eventhandler;

import org.mybatis.jpetstore.core.event.InventoryUpdatedEvent;
import org.mybatis.jpetstore.readmodel.InventoryReadModel;
import org.mybatis.jpetstore.repository.InventoryReadModelRepository;

public class InventoryUpdatedEventHandler implements DomainEventHandler<InventoryUpdatedEvent> {
  private InventoryReadModelRepository repository;

  public InventoryUpdatedEventHandler() {
    this.repository = new InventoryReadModelRepository();
  }

  @Override
  public void handle(InventoryUpdatedEvent event) {
    String itemId = event.getItemId();
    int quantityChange = event.getQuantityChange();

    InventoryReadModel inventory = repository.findByItemId(itemId);
    if (inventory != null) {
      int newQuantity = inventory.getQuantity() + quantityChange;
      repository.updateInventory(itemId, newQuantity);
    } else if (quantityChange > 0) {
      repository.updateInventory(itemId, quantityChange);
    }
  }
}
