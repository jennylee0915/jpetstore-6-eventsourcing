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
package org.mybatis.jpetstore.test;

import java.util.List;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.eventhandler.DomainEventPublisher;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.mapper.ItemMapper;
import org.mybatis.jpetstore.mapper.OrderMapper;
import org.mybatis.jpetstore.repository.EventSourcedOrderRepository;

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
    // eventPublisher.registerHandler(OrderCreatedEvent.class, new OrderCreatedEventHandler(orderMapper));
    // eventPublisher.registerHandler(InventoryUpdatedEvent.class, new InventoryUpdatedEventHandler(itemMapper));

    Order order = new Order();
    repository.save(order);

    List<DomainEvent> eventList = order.getEvents();
    eventList.forEach(eventPublisher::publish);
    // order.setUsername("John");
    // order.setOrderDate(java.sql.Timestamp.valueOf(LocalDateTime.of(2023, 12, 31, 23, 59, 59)));

    // System.out.println(repository.save(order));
    // return order.getOrderId();
  }

}
