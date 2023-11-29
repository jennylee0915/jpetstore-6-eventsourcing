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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.*;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.domain.Order;

public class EventSourcedOrderRepository {
  private EventStore eventStore;
  private Map<String, List<String>> orderIdCache;

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
      orderIdCache.computeIfAbsent(order.getUsername(), k -> new ArrayList<>()).add(order.getOrderId());
    }
    return streamId;
  }

  public List<Order> findByUsername(String username) {
    List<String> orderIds = orderIdCache.getOrDefault(username, Collections.emptyList());
    List<Order> orders = new ArrayList<>();
    for (String orderId : orderIds) {
      String streamId = Order.class.getName() + "." + orderId;
      List<DomainEvent> events = eventStore.getStream(streamId);
      Order order = new Order(orderId);
      for (DomainEvent event : events) {
        order.mutate(event);
      }
      orders.add(order);
    }
    return orders;

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
