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
package org.mybatis.jpetstore.service;

import com.eventstore.dbclient.RecordedEvent;
import com.eventstore.dbclient.ResolvedEvent;
import com.eventstore.dbclient.Subscription;
import com.eventstore.dbclient.SubscriptionListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.core.event.InventoryUpdatedEvent;
import org.mybatis.jpetstore.repository.EventSourcedOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
  private final static EventSourcedOrderRepository repository = new EventSourcedOrderRepository(eventStore);
  private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
  @Autowired
  private JdbcTemplate jdbcTemplate;

  //@PostConstruct
  //public void init() {
    //logger.info("Initializing InventoryService...");
    // createInitialSnapshot();
    // subscribeOrderStream();
    //subscribeInvnetoryUpdatedStream();
  //}
  //public static void main(String args[]) {

  // test();
   //ApplicationContext context = new FileSystemXmlApplicationContext(
   //"file:src/main/webapp/WEB-INF/applicationContext.xml");
   //InventoryService service = context.getBean(InventoryService.class);
   //service.createInitialSnapshot();
  // service.subscribeInvnetoryUpdatedStream();
  // service.processHistoricalEvents();
  //}

  public static void test() {
    String streamPrefix = "org.mybatis.jpetstore.domain.Order.";

    List<DomainEvent> allEvents = eventStore.getEventsByStreamPrefix(streamPrefix).stream()
        .filter(e -> e instanceof InventoryUpdatedEvent)
        .sorted(Comparator.comparing(e -> ((InventoryUpdatedEvent) e).getTimestamp())) // 先按時間排序
        .collect(Collectors.toList());

    // 按日期分组，分组内的事件也按時間排
    Map<LocalDateTime, List<DomainEvent>> groupedEvents = allEvents.stream()
        .collect(Collectors.groupingBy(
            e -> timestampToLocalDateTime(((InventoryUpdatedEvent) e).getTimestamp()).toLocalDate().atStartOfDay(),
            TreeMap::new, // 日期順序
            Collectors.toList()));

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      String prettyPrintedGroupedEvents = objectMapper.writeValueAsString(groupedEvents);
      System.out.println(prettyPrintedGroupedEvents);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void processHistoricalEvents() {
    String streamPrefix = "org.mybatis.jpetstore.domain.Order.";

    List<DomainEvent> allEvents = eventStore.getEventsByStreamPrefix(streamPrefix).stream()
        .filter(e -> e instanceof InventoryUpdatedEvent)
        .sorted(Comparator.comparing(e -> ((InventoryUpdatedEvent) e).getTimestamp())) // 時間排序
        .collect(Collectors.toList());

    // 將事件按日期分組並直接使用日期作為快照表名的一部分
    Map<LocalDateTime, List<DomainEvent>> groupedEvents = allEvents.stream()
        .collect(Collectors.groupingBy(
            e -> timestampToLocalDateTime(((InventoryUpdatedEvent) e).getTimestamp()).toLocalDate().atStartOfDay(),
            TreeMap::new,
            Collectors.toList()));

    for (Map.Entry<LocalDateTime, List<DomainEvent>> entry : groupedEvents.entrySet()) {
      createSnapshotForDate(entry.getValue(), entry.getKey());
    }
  }

  // 根據事件和日期創建新的快照
  public void createSnapshotForDate(List<DomainEvent> events, LocalDateTime date) {
    // 根據日期生成新的快照表名
    String newSnapshotTableName = generateSnapshotTableName(date.toLocalDate());

    // 查詢上一個快照的庫存，先取得上一個快照的表名
    String latestSnapshotTableName = getLatestSnapshotTableName();
    Map<String, Integer> currentInventory = queryCurrentInventory(latestSnapshotTableName);

    // 更新庫存
    Map<String, Integer> updatedInventory = applyInventoryChanges(events, currentInventory);

    // 創新的快照表，保存更新後的庫存
    createNewSnapshotTable(newSnapshotTableName);
    saveUpdatedInventoryToNewSnapshot(updatedInventory, newSnapshotTableName, date);
  }

  // 查詢現在的庫存
  private Map<String, Integer> queryCurrentInventory(String snapshotTableName) {
    String sql = "SELECT item_id, quantity FROM " + snapshotTableName;
    return jdbcTemplate
        .query(sql, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(rs.getString("item_id"), rs.getInt("quantity")))
        .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<String, Integer> applyInventoryChanges(List<DomainEvent> events, Map<String, Integer> currentInventory) {
    Map<String, Integer> updatedInventory = new HashMap<>(currentInventory);
    for (DomainEvent event : events) {
      if (event instanceof InventoryUpdatedEvent) {
        InventoryUpdatedEvent inventoryEvent = (InventoryUpdatedEvent) event;
        String itemId = inventoryEvent.getItemId();
        int quantityChange = inventoryEvent.getQuantityChange();
        updatedInventory.merge(itemId, quantityChange, Integer::sum);
      }
    }
    return updatedInventory;
  }

  // 創建新的快照表
  private void createNewSnapshotTable(String tableName) {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableName
        + " (item_id VARCHAR(255), quantity INT, last_updated TIMESTAMP, PRIMARY KEY (item_id))";
    jdbcTemplate.execute(sql);

    // 保存新創建的快照表名
    saveSnapshotTableName(tableName);
  }

  // 保存更新後的庫存到新快照
  private void saveUpdatedInventoryToNewSnapshot(Map<String, Integer> updatedInventory, String tableName,
      LocalDateTime date) {
    String sql = "INSERT INTO " + tableName + " (item_id, quantity, last_updated) VALUES (?, ?, ?)";
    List<Object[]> batchArgs = new ArrayList<>();
    updatedInventory.forEach((itemId, quantity) -> {
      batchArgs.add(new Object[] { itemId, quantity, Timestamp.valueOf(date) });
    });
    jdbcTemplate.batchUpdate(sql, batchArgs);
  }

  private String getLatestSnapshotTableName() {
    try {
      String sql = "SELECT snapshot_name FROM snapshot_metadata ORDER BY created_at DESC LIMIT 1";
      return jdbcTemplate.queryForObject(sql, String.class);
    } catch (EmptyResultDataAccessException e) {
      // 沒有找到任何快照表
      String initialSnapshotTableName = "inventory_snapshot";
      saveSnapshotTableName(initialSnapshotTableName);
      return initialSnapshotTableName;
    }
  }

  public void saveSnapshotTableName(String snapshotTableName) {
    String sql = "INSERT INTO snapshot_metadata (snapshot_name) VALUES (?)";
    jdbcTemplate.update(sql, snapshotTableName);
  }

  private String generateSnapshotTableName(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    return "inventory_snapshot_" + date.format(formatter);
  }

  public void subscribeOrderStream() {
    logger.info("Subscribing to order stream...");
    SubscriptionListener listener = new SubscriptionListener() {
      @Override
      public void onEvent(Subscription subscription, ResolvedEvent event) {
        logger.info(
            "Received event" + event.getOriginalEvent().getRevision() + "@" + event.getOriginalEvent().getStreamId());
      }
    };

    eventStore.subscribeStream("org.mybatis.jpetstore.domain.Order.", listener);

  }

  public void subscribeInvnetoryUpdatedStream() {
    logger.info("Subscribing to inventory updated stream...");
    SubscriptionListener listener = new SubscriptionListener() {
      @Override
      public void onEvent(Subscription subscription, ResolvedEvent event) {
        logger.info(
            "Received event" + event.getOriginalEvent().getRevision() + "@" + event.getOriginalEvent().getStreamId()
                + "@" + event.getOriginalEvent().getPosition() + "@" + event.getEvent());

        handleEvent(event);
      }
    };

    eventStore.subscribeEventType("org.mybatis.jpetstore.core.event.InventoryUpdatedEvent", listener);

  }

  private void handleEvent(ResolvedEvent event) {
    try {
      RecordedEvent recordedEvent = event.getOriginalEvent();
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> eventData = mapper.readValue(recordedEvent.getEventData(), LinkedHashMap.class);

      // 使用 EventStore 中的 deserialize 方法
      DomainEvent domainEvent = EventStore.deserialize(eventData);

      if (domainEvent instanceof InventoryUpdatedEvent) {
        InventoryUpdatedEvent inventoryEvent = (InventoryUpdatedEvent) domainEvent;
        String itemId = inventoryEvent.getItemId();
        int quantityChange = inventoryEvent.getQuantityChange();

        // 更新庫存快照
        updateInventorySnapshot(itemId, quantityChange);
      }
    } catch (Exception e) {
      logger.error("Error processing event", e);
    }

  }

  private void updateInventorySnapshot(String itemId, int quantityChange) {
    try {
      // 查詢當前库存
      Integer currentQuantity = jdbcTemplate.queryForObject("SELECT quantity FROM inventory_snapshot WHERE item_id = ?",
          new Object[] { itemId }, Integer.class);

      if (currentQuantity != null) {
        // 計算新庫存
        int newQuantity = currentQuantity + quantityChange;

        // 更新庫存快照
        jdbcTemplate.update("UPDATE inventory_snapshot SET quantity = ?, last_updated = ? WHERE item_id = ?",
            newQuantity, new Timestamp(System.currentTimeMillis()), itemId);
      }
    } catch (DataAccessException e) {
      e.printStackTrace();
    }
  }

  public Map<String, Integer> getInitialInventoryData() {
    Map<String, Integer> initialInventory = new HashMap<>();

    List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT itemid, qty FROM inventory");
    for (Map<String, Object> row : rows) {
      String itemId = (String) row.get("itemid");
      Integer quantity = (Integer) row.get("qty");
      initialInventory.put(itemId, quantity);
    }
    return initialInventory;
  }

  //public void createInitialSnapshot() {
    //Map<String, Integer> initialInventory = getInitialInventoryData();

    //for (Map.Entry<String, Integer> entry : initialInventory.entrySet()) {
      //jdbcTemplate.update("INSERT INTO inventory_snapshot (item_id, quantity, last_updated) VALUES (?, ?, ?)",
          //entry.getKey(), entry.getValue(), new Timestamp(System.currentTimeMillis()));
    //}
  //}

  public void createInitialSnapshot() {
    if (!isInventorySnapshotTableExists()) {
      // 如果表不存在，创建表并插入初始数据
      createInventorySnapshotTable();
      Map<String, Integer> initialInventory = getInitialInventoryData();

      for (Map.Entry<String, Integer> entry : initialInventory.entrySet()) {
        jdbcTemplate.update("INSERT INTO inventory_snapshot (item_id, quantity, last_updated) VALUES (?, ?, ?)",
                entry.getKey(), entry.getValue(), new Timestamp(System.currentTimeMillis()));
      }
    }
  }

  private boolean isInventorySnapshotTableExists() {
    try {
      jdbcTemplate.queryForObject("SELECT 1 FROM inventory_snapshot LIMIT 1", Integer.class);
      return true;
    } catch (DataAccessException e) {
      return false;
    }
  }

  private void createInventorySnapshotTable() {
    String createTableSql = "CREATE TABLE inventory_snapshot (" +
            "item_id VARCHAR(255) PRIMARY KEY, " +
            "quantity INT, " +
            "last_updated TIMESTAMP" +
            ")";
    jdbcTemplate.execute(createTableSql);
  }


  public static LocalDateTime timestampToLocalDateTime(long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
  }

}
