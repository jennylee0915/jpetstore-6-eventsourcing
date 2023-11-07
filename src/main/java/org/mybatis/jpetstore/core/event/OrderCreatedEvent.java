package org.mybatis.jpetstore.core.event;

import org.mybatis.jpetstore.domain.Order;

import java.util.UUID;

public class OrderCreatedEvent extends DomainEvent {

    private Order order;
    private String orderId;

    public OrderCreatedEvent(String streamId, String entityType, long timestamp) {
        super(streamId, entityType, timestamp);
        this.orderId = UUID.randomUUID().toString();
    }

    public String getOrderId() {
        return orderId;
    }

    public Order getOrder() {
        return order;
    }
}

