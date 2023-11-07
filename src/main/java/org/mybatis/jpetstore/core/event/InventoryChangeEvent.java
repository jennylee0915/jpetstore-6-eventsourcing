package org.mybatis.jpetstore.core.event;

import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Item;

public class InventoryChangeEvent extends DomainEvent {
    private String itemId;
    private int quantityChange;

    public InventoryChangeEvent(String streamId, String entityType, String itemId, int quantityChange, long timestamp) {
        super(streamId, entityType, timestamp);
        this.itemId = itemId;
        this.quantityChange = quantityChange;
    }

    public String getItemId() {
        return itemId;
    }

    public int getChangeInQuantity() {
        return quantityChange;
    }

    @Override
    public String toString() {
        return "InventoryChangeEvent{" +
                "itemId='" + itemId + '\'' +
                ", changeInQuantity=" + quantityChange +
                "} " + super.toString();
    }

}
