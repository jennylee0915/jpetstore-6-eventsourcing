/*
 *    Copyright 2010-2024 the original author or authors.
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

  // EventStore.class
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

  public void subscribeStream(String streamPrefix, SubscriptionListener listener) {

    SubscriptionFilter filter = SubscriptionFilter.newBuilder().addStreamNamePrefix(streamPrefix).build();

    // 設置訂閱選項，包括過濾器
    SubscribeToAllOptions options = SubscribeToAllOptions.get().filter(filter);

    // 創建訂閱
    client.subscribeToAll(listener, options);
  }

  public void subscribeEventType(String eventTypePrefix, SubscriptionListener listener) {

    SubscriptionFilter filter = SubscriptionFilter.newBuilder().addEventTypePrefix(eventTypePrefix).build();

    // 設置訂閱選項，包括過濾器
    SubscribeToAllOptions options = SubscribeToAllOptions.get().filter(filter);

    // 包括起始位置
    // SubscribeToAllOptions options = SubscribeToAllOptions.get()
    // .filter(filter)
    // .fromPosition(new Position(commitPosition, preparePosition));

    // 創建訂閱
    client.subscribeToAll(listener, options);
  }

  public long getEventTimestamp(ResolvedEvent event) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      DomainEvent domainEvent = deserialize(
          mapper.readValue(event.getOriginalEvent().getEventData(), LinkedHashMap.class));
      return domainEvent.getTimestamp();
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  public List<DomainEvent> getEventsByStreamPrefix(String streamPrefix) {
    List<DomainEvent> results = new ArrayList<>();
    try {
      ReadAllOptions options = ReadAllOptions.get().forwards().fromStart();
      List<ResolvedEvent> events = client.readAll(options).get().getEvents();
      ObjectMapper mapper = new ObjectMapper();

      for (ResolvedEvent event : events) {
        RecordedEvent recordedEvent = event.getOriginalEvent();
        if (recordedEvent.getStreamId().startsWith(streamPrefix)) {
          Map<String, Object> eventData = mapper.readValue(recordedEvent.getEventData(), LinkedHashMap.class);
          results.add(deserialize(eventData));
        }
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
        Account account = mapper.convertValue(map.get("account"), Account.class);
        Cart cart = mapper.convertValue(map.get("cart"), Cart.class);
        long timestamp = (Long) map.get("timestamp");
        String streamId = (String) map.get("streamId");
        String entityType = (String) map.get("entityType");

        result = new OrderInitializedEvent(streamId, entityType, account, cart, timestamp);
        break;

      case "org.mybatis.jpetstore.core.event.LineItemAddedToOrderEvent":
        Map<String, Object> itemMap = (Map<String, Object>) map.get("item");

        String itemJsonString = mapper.writeValueAsString(itemMap);

        Item item = mapper.readValue(itemJsonString, Item.class);

        // 確保 unitPrice是 BigDecimal
        Object unitPriceObject = map.get("unitPrice");
        BigDecimal unitPrice;
        if (unitPriceObject instanceof Number) {
          unitPrice = BigDecimal.valueOf(((Number) unitPriceObject).doubleValue());
        } else if (unitPriceObject instanceof BigDecimal) {
          unitPrice = (BigDecimal) unitPriceObject;
        } else {
          throw new IllegalArgumentException("Unexpected type for unitPrice: " + unitPriceObject.getClass().getName());
        }

        LineItemAddedToOrderEvent lineItemAddedToOrderEvent = new LineItemAddedToOrderEvent(
            (String) map.get("streamId"), (String) map.get("entityType"), item, (String) map.get("itemId"),
            (int) map.get("quantity"), unitPrice, (long) map.get("timestamp"));
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

      case "org.mybatis.jpetstore.core.event.CategoryCreatedEvent":
        CategoryCreatedEvent categoryCreatedEvent = new CategoryCreatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (long) map.get("timestamp"), (String) map.get("categoryId"),
            (String) map.get("name"), (String) map.get("description"));
        result = categoryCreatedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.ProductCreatedEvent":
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (long) map.get("timestamp"), (String) map.get("productId"),
            (String) map.get("category"), (String) map.get("name"), (String) map.get("description"));
        result = productCreatedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.ItemCreatedEvent":
        Object listPriceObject = map.get("listPrice");
        BigDecimal listPrice;
        if (listPriceObject instanceof Number) {
          listPrice = BigDecimal.valueOf(((Number) listPriceObject).doubleValue());
        } else if (listPriceObject instanceof BigDecimal) {
          listPrice = (BigDecimal) listPriceObject;
        } else {
          throw new IllegalArgumentException("Unexpected type for unitPrice: " + listPriceObject.getClass().getName());
        }

        Object unitCostObject = map.get("unitCost");
        BigDecimal unitCost;
        if (unitCostObject instanceof Number) {
          unitCost = BigDecimal.valueOf(((Number) unitCostObject).doubleValue());
        } else if (listPriceObject instanceof BigDecimal) {
          unitCost = (BigDecimal) unitCostObject;
        } else {
          throw new IllegalArgumentException("Unexpected type for unitPrice: " + listPriceObject.getClass().getName());
        }

        ItemCreatedEvent itemCreatedEvent = new ItemCreatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (long) map.get("timestamp"), (String) map.get("itemId"),
            (String) map.get("productId"), listPrice, unitCost, (int) map.get("supplierId"), (String) map.get("status"),
            (String) map.get("attribute1"));
        result = itemCreatedEvent;
        break;

      case "org.mybatis.jpetstore.core.event.AccountCreatedEvent":
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent((String) map.get("streamId"),
            (String) map.get("entityType"), (long) map.get("timestamp"), (String) map.get("accountId"),
            (String) map.get("username"), (String) map.get("password"), (String) map.get("repeatedPassword"),
            (String) map.get("email"), (String) map.get("firstName"), (String) map.get("lastName"),
            (String) map.get("status"), (String) map.get("address1"), (String) map.get("address2"),
            (String) map.get("city"), (String) map.get("state"), (String) map.get("zip"), (String) map.get("country"),
            (String) map.get("phone"), (String) map.get("favouriteCategoryId"), (String) map.get("languagePreference"),
            (boolean) map.get("listOption"), (boolean) map.get("bannerOption"), (String) map.get("bannerName"));
        result = accountCreatedEvent;
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
