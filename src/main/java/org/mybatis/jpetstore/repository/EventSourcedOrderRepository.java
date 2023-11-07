package org.mybatis.jpetstore.repository;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class EventSourcedOrderRepository {
    private EventStore eventStore;
    private Map<String, String> orderIdCache;

    public EventSourcedOrderRepository(EventStore eventStore) {
        this.eventStore = eventStore;
        this.orderIdCache = new HashMap<>();
    }

    public String save(Order order) {
        String streamId = null;
        for (DomainEvent e : order.getEvents()) {
            try {
                streamId = e.getStreamId();
                eventStore.appendToStream(streamId, e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (streamId != null) {
            orderIdCache.put(order.getUsername(), order.getOrderId());
        }
        return streamId;
    }

    public Order findBy(String orderId) {
        String streamId = Order.class.getName() + "." + orderId;
        List<DomainEvent> events = eventStore.getStream(streamId);
        Order order = new Order(orderId);
        for (DomainEvent event : events) {
            order.mutate(event);
        }
        return order;
    }

    public List<Order> findAll() {
        List<Order> orderList = new ArrayList<>();
        List<DomainEvent> events = eventStore.getAllStream();

        Map<String, List<DomainEvent>> eventsGroupByStreamId = events.stream()
                .filter(event -> event.getEntityType().equals(Order.class.getName()))
                .collect(groupingBy(DomainEvent::getStreamId, toList()));
        eventsGroupByStreamId.forEach((streamId, stream) -> {
            String[] streamSplitByDot = streamId.split("\\.");
            String orderId = streamSplitByDot[streamSplitByDot.length - 1];
            Order order = new Order(orderId);
            stream.forEach(order::mutate);
            orderList.add(order);
        });
        return orderList;
    }

}
