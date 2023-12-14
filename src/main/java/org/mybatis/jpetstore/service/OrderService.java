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
package org.mybatis.jpetstore.service;

import java.util.Date;
import java.util.List;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.event.EntityNotFoundException;
import org.mybatis.jpetstore.core.event.InventoryUpdatedEvent;
import org.mybatis.jpetstore.core.event.OrderInsertedEvent;
import org.mybatis.jpetstore.core.eventhandler.DomainEventPublisher;
import org.mybatis.jpetstore.core.eventhandler.InventoryUpdatedEventHandler;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.domain.OrderDTO;
import org.mybatis.jpetstore.domain.Sequence;
import org.mybatis.jpetstore.mapper.ItemMapper;
import org.mybatis.jpetstore.mapper.LineItemMapper;
import org.mybatis.jpetstore.mapper.OrderMapper;
import org.mybatis.jpetstore.mapper.SequenceMapper;
import org.mybatis.jpetstore.repository.EventSourcedOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class OrderService.
 *
 * @author Eduardo Macarron
 */
@Service
public class OrderService {

  private final ItemMapper itemMapper;
  private final OrderMapper orderMapper;
  private final SequenceMapper sequenceMapper;
  private final LineItemMapper lineItemMapper;
  private DomainEventPublisher eventPublisher;
  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
  private final static EventSourcedOrderRepository repository = new EventSourcedOrderRepository(eventStore);

  public OrderService(ItemMapper itemMapper, OrderMapper orderMapper, SequenceMapper sequenceMapper,
      LineItemMapper lineItemMapper) {
    this.itemMapper = itemMapper;
    this.orderMapper = orderMapper;
    this.sequenceMapper = sequenceMapper;
    this.lineItemMapper = lineItemMapper;
    this.eventPublisher = new DomainEventPublisher();
    this.eventPublisher.registerHandler(InventoryUpdatedEvent.class, new InventoryUpdatedEventHandler());
  }

  /**
   * Insert order.
   *
   * @param order
   *          the order
   */
  @Transactional
  public void insertOrder(Order order) {

    OrderDTO orderDTO = order.toDTO();
    OrderInsertedEvent insertedEvent = new OrderInsertedEvent(order.getStreamId(), Order.class.getName(), orderDTO,
        new Date().getTime());
    order.cause(insertedEvent);

    order.getLineItems().forEach(lineItem -> {
      InventoryUpdatedEvent inventoryEvent = new InventoryUpdatedEvent(order.getStreamId(), Order.class.getName(),
          lineItem.getItemId(), -lineItem.getQuantity(), new Date().getTime());
      order.cause(inventoryEvent);
      eventPublisher.publish(inventoryEvent);
    });
    // order.setOrderId(getNextId("ordernum"));
    // order.getLineItems().forEach(lineItem -> {
    // String itemId = lineItem.getItemId();
    // Integer increment = lineItem.getQuantity();
    // Map<String, Object> param = new HashMap<>(2);
    // param.put("itemId", itemId);
    // param.put("increment", increment);
    // itemMapper.updateInventoryQuantity(param);
    // });

    // orderMapper.insertOrder(order);
    // orderMapper.insertOrderStatus(order);
    // order.getLineItems().forEach(lineItem -> {
    // lineItem.setOrderId(order.getOrderId());
    // lineItemMapper.insertLineItem(lineItem);
    // });
  }

  /**
   * Gets the order.
   *
   * @param orderId
   *          the order id
   *
   * @return the order
   */

  @Transactional
  public Order getOrder(String orderId) {
    String streamId = Order.class.getName() + "." + orderId;
    List<DomainEvent> events = eventStore.getStream(streamId);
    if (events.isEmpty()) {
      throw new EntityNotFoundException("Order not found: " + orderId);
    }

    Order order = new Order(orderId); // 使用带有订单ID的构造函数
    for (DomainEvent event : events) {
      order.mutate(event);
    }
    return order;
    // Order order = orderMapper.getOrder(orderId);
    // order.setLineItems(lineItemMapper.getLineItemsByOrderId(orderId));

    // order.getLineItems().forEach(lineItem -> {
    // Item item = itemMapper.getItem(lineItem.getItemId());
    // item.setQuantity(itemMapper.getInventoryQuantity(lineItem.getItemId()));
    // lineItem.setItem(item);
    // });

    // return order;
  }

  /**
   * Gets the orders by username.
   *
   * @param username
   *          the username
   *
   * @return the orders by username
   */

  public List<Order> getOrdersByUsername(String username) {
    return repository.findByUsername(username);
  }

  /**
   * Gets the next id.
   *
   * @param name
   *          the name
   *
   * @return the next id
   */
  /**
   * public int getNextId(String name) { Sequence sequence = sequenceMapper.getSequence(new Sequence(name, -1)); if
   * (sequence == null) { throw new RuntimeException( "Error: A null sequence was returned from the database (could not
   * get next " + name + " sequence)."); } Sequence parameterObject = new Sequence(name, sequence.getNextId() + 1);
   * sequenceMapper.updateSequence(parameterObject); return sequence.getNextId(); }
   **/
}
