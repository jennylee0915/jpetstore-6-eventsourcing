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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.domain.Item;

public class EventSourcedItemRepository {
  private EventStore eventStore;
  private Map<String, String> itemIdCache;

  public EventSourcedItemRepository(EventStore eventStore) {
    this.eventStore = eventStore;
    this.itemIdCache = new HashMap<>();
  }

  public String save(Item item) {
    String streamId = null;
    for (DomainEvent e : item.getEvents()) {
      try {
        streamId = e.getStreamId();
        eventStore.appendToStream(streamId, e);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (streamId != null) {
      itemIdCache.put(item.getItemId(), streamId);
    }
    return streamId;
  }

  public Item findByItemId(String itemId) {
    String streamId = itemIdCache.get(itemId);
    if (streamId == null) {
      streamId = Item.class.getName() + "." + itemId;
      itemIdCache.put(itemId, streamId);
    }
    List<DomainEvent> events = eventStore.getStream(streamId);
    Item item = new Item(itemId);
    for (DomainEvent event : events) {
      item.mutate(event);
    }
    return item;
  }

  public List<Item> findAll() {
    List<Item> itemList = new ArrayList<>();
    List<DomainEvent> events = eventStore.getAllStream();

    Map<String, List<DomainEvent>> eventsGroupByStreamId = events.stream()
        .filter(event -> event.getEntityType().equals(Item.class.getName()))
        .collect(Collectors.groupingBy(DomainEvent::getStreamId, Collectors.toList()));
    eventsGroupByStreamId.forEach((streamId, stream) -> {
      String itemId = streamId.substring(streamId.lastIndexOf('.') + 1);
      Item item = new Item(itemId);
      stream.forEach(item::mutate);
      itemList.add(item);
    });
    return itemList;
  }

  // Other methods such as find by version can also be implemented similarly
}
