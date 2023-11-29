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
package org.mybatis.jpetstore.core;

import com.eventstore.dbclient.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.mybatis.jpetstore.core.event.*;
import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Cart;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.OrderDTO;

public class EventStore {
  private EventStoreDBClient client;

  public EventStore(String connectionString) {
    EventStoreDBClientSettings settings = null;
    try {
      settings = EventStoreDBConnectionString.parse(connectionString);
      client = EventStoreDBClient.create(settings);
    } catch (ConnectionStringParsingException e) {
      throw new RuntimeException(e);
    }
  }

  public String appendToStream(String streamId, DomainEvent e) throws ExecutionException, InterruptedException {
    EventData eventData = EventData.builderAsJson(e.getClass().getName(), e).build();
    client.appendToStream(streamId, eventData).get();
    return streamId;
  }

  public List<DomainEvent> getStream(String streamId) {
    List<DomainEvent> results = new ArrayList<>();

    try {
      ReadStreamOptions options = ReadStreamOptions.get().forwards().fromStart();
      List<ResolvedEvent> events = client.readStream(streamId, options).get().getEvents();
      for (ResolvedEvent event : events) {
        ObjectMapper mapper = new ObjectMapper();
        results.add(deserialize(mapper.readValue(event.getOriginalEvent().getEventData(), LinkedHashMap.class)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<DomainEvent> getStream(String streamId, long version) {
    List<DomainEvent> results = new ArrayList<>();

    try {
      ReadStreamOptions options = ReadStreamOptions.get().fromRevision(version).backwards();
      List<ResolvedEvent> events = client.readStream(streamId, options).get().getEvents();
      for (ResolvedEvent event : events) {
        ObjectMapper mapper = new ObjectMapper();
        results.add(deserialize(mapper.readValue(event.getOriginalEvent().getEventData(), LinkedHashMap.class)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<DomainEvent> getAllStream() {
    List<DomainEvent> results = new ArrayList<>();
    try {
      ReadAllOptions options = ReadAllOptions.get().forwards().fromStart();
      List<ResolvedEvent> events = client.readAll(options).get().getEvents();
      ObjectMapper mapper = new ObjectMapper();
      for (ResolvedEvent event : events) {
        if (event.getEvent().getEventType().startsWith("$")) {
          continue;
        }
        results.add(deserialize(mapper.readValue(event.getOriginalEvent().getEventData(), LinkedHashMap.class)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return results;
  }

  public static DomainEvent deserialize(Map map) throws JsonProcessingException {
    String eventType = (String) map.get("eventType");
    DomainEvent result = null;

    ObjectMapper mapper = new ObjectMapper();

    switch (eventType) {
      case "org.mybatis.jpetstore.core.event.EntityCreatedEvent":
        EntityCreatedEvent entityCreatedEvent = new EntityCreatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (long) map.get("timestamp"));
        result = entityCreatedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.AttributeUpdatedEvent":
        AttributeUpdatedEvent attributeUpdatedEvent = new AttributeUpdatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (long) map.get("timestamp"));
        attributeUpdatedEvent.setName((String) map.get("name"));
        attributeUpdatedEvent.setValue(map.get("value"));
        result = attributeUpdatedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.OrderCreatedEvent":
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (String) map.get("orderId"), (long) map.get("timestamp"));
        result = orderCreatedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.OrderInitializedEvent":
        Map<String, Object> accountMap = (Map<String, Object>) map.get("account");
        Map<String, Object> cartMap = (Map<String, Object>) map.get("cart");

        String accountJsonString = mapper.writeValueAsString(accountMap);
        String cartJsonString = mapper.writeValueAsString(cartMap);

        Account account = mapper.readValue(accountJsonString, Account.class);
        Cart cart = mapper.readValue(cartJsonString, Cart.class);

        OrderInitializedEvent orderInitializedEvent = new OrderInitializedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), account, cart, (long) map.get("timestamp"));
        result = orderInitializedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.LineItemAddedToOrderEvent":
        Map<String, Object> itemMap = (Map<String, Object>) map.get("item");

        String itemJsonString = mapper.writeValueAsString(itemMap);

        Item item = mapper.readValue(itemJsonString, Item.class);

        LineItemAddedToOrderEvent lineItemAddedToOrderEvent = new LineItemAddedToOrderEvent(
            (String) map.get("streamId"), (String) map.get("entityType"), item, (String) map.get("itemId"),
            (int) map.get("quantity"), (BigDecimal) map.get("unitPrice"), (long) map.get("timestamp"));
        result = lineItemAddedToOrderEvent;
        break;

      case "org.mybatis.jpetstore.core.event.OrderInsertedEvent":
        Map<String, Object> orderMap = (Map<String, Object>) map.get("orderDTO");

        String orderJsonString = mapper.writeValueAsString(orderMap);

        OrderDTO orderDTO = mapper.readValue(orderJsonString, OrderDTO.class);

        OrderInsertedEvent orderInsertedEvent = new OrderInsertedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), orderDTO, (long) map.get("timestamp"));
        result = orderInsertedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.InventoryUpdatedEvent":
        InventoryUpdatedEvent inventoryUpdatedEvent = new InventoryUpdatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (String) map.get("itemId"), (int) map.get("quantityChange"),
            (long) map.get("timestamp"));
        result = inventoryUpdatedEvent;
        break;

    }
    return result;
  }

  /*
   * if ("org.mybatis.jpetstore.core.event.EntityCreatedEvent".equals(eventType)) { result = new
   * EntityCreatedEvent((String) map.get("streamId"), (String) map.get("entityType"), (long) map.get("timestamp")); }
   * else if ("org.mybatis.jpetstore.core.event.AttributeUpdatedEvent".equals(eventType)) { AttributeUpdatedEvent event
   * = new AttributeUpdatedEvent((String) map.get("streamId"), (String) map.get("entityType"), (long)
   * map.get("timestamp")); event.setName((String) map.get("name")); event.setValue(map.get("value")); result = event; }
   * return result;
   */

}
