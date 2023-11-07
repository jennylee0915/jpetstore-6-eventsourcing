package org.mybatis.jpetstore.core.eventhandler;

import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.event.OrderCreatedEvent;

public class InventoryService implements DomainEventHandler {
    @Override
    public void handle(DomainEvent event) {
        if (event instanceof OrderCreatedEvent) {
            // 減少庫存的邏輯...
        }
    }


}
