package org.mybatis.jpetstore.test;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.event.InventoryChangeEvent;
import org.mybatis.jpetstore.core.event.OrderCreatedEvent;
import org.mybatis.jpetstore.core.eventhandler.DomainEventPublisher;
import org.mybatis.jpetstore.core.eventhandler.InventoryUpdatedEventHandler;
import org.mybatis.jpetstore.core.eventhandler.OrderCreatedEventHandler;
import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.mapper.ItemMapper;
import org.mybatis.jpetstore.mapper.LineItemMapper;
import org.mybatis.jpetstore.mapper.OrderMapper;
import org.mybatis.jpetstore.repository.EventSourcedOrderRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TestOrderRepo {
    private final static EventStore eventStore = new EventStore(
            "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
    private final static EventSourcedOrderRepository repository = new EventSourcedOrderRepository(eventStore);

    private static OrderMapper orderMapper;
    private static ItemMapper itemMapper;



    public static void main(String[] args) {
        testCreate();
    }


    private static void testCreate() {
        DomainEventPublisher eventPublisher = new DomainEventPublisher();
        eventPublisher.registerHandler(OrderCreatedEvent.class, new OrderCreatedEventHandler(orderMapper));
        eventPublisher.registerHandler(InventoryChangeEvent.class, new InventoryUpdatedEventHandler(itemMapper));

        Order order = new Order();
        repository.save(order);

        List<DomainEvent> eventList = order.getEvents();
        eventList.forEach(eventPublisher::publish);
        //order.setUsername("John");
        //order.setOrderDate(java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 12, 31, 23, 59, 59)));

        //System.out.println(repository.save(order));
        //return order.getOrderId();
    }

}
