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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.*;
import org.mybatis.jpetstore.domain.Category;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.mapper.CategoryMapper;
import org.mybatis.jpetstore.mapper.ItemMapper;
import org.mybatis.jpetstore.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The Class CatalogService.
 *
 * @author Eduardo Macarron
 */
@Service
public class CatalogService {

  private final CategoryMapper categoryMapper;
  private final ItemMapper itemMapper;
  private final ProductMapper productMapper;
  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
  private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

  public CatalogService(CategoryMapper categoryMapper, ItemMapper itemMapper, ProductMapper productMapper) {
    this.categoryMapper = categoryMapper;
    this.itemMapper = itemMapper;
    this.productMapper = productMapper;
  }

  public List<CategoryCreatedEvent> getCategoryEvents() {
    String streamPrefix = "org.mybatis.jpetstore.domain.Category.";

    List<CategoryCreatedEvent> allEvents = new ArrayList<>();
    for (DomainEvent event : eventStore.getEventsByStreamPrefix(streamPrefix)) {
      if (event instanceof CategoryCreatedEvent) {
        allEvents.add((CategoryCreatedEvent) event);
      }
    }
    allEvents.sort(Comparator.comparing(e -> e.getTimestamp()));
    return allEvents;
  }

  public List<Category> getCategoryList() {
    List<CategoryCreatedEvent> categoryEvents = getCategoryEvents();
    List<Category> categories = new ArrayList<>();
    for (CategoryCreatedEvent event : categoryEvents) {
      String categoryId = event.getCategoryId();
      Category category = new Category(categoryId);
      category.setName(event.getName());
      category.setDescription(event.getDescription());
      categories.add(category);
    }
    logger.info("Fetching category list from EventStore");
    return categories;

    // return categoryMapper.getCategoryList();
  }

  public Category getCategory(String categoryId) {
    List<CategoryCreatedEvent> categoryEvents = getCategoryEvents();
    CategoryCreatedEvent matchedEvent = categoryEvents.stream()
        .filter(event -> event.getCategoryId().equals(categoryId)).findFirst().orElse(null); // 如果沒有找到匹配的事件，返回null

    if (matchedEvent == null) {
      return null;
    }

    Category category = new Category(categoryId);
    category.setName(matchedEvent.getName());
    category.setDescription(matchedEvent.getDescription());

    logger.info("Fetching category from EventStore with ID: {}", categoryId);
    return category;
    // return categoryMapper.getCategory(categoryId);
  }

  public List<ProductCreatedEvent> getProductEvents() {
    String streamPrefix = "org.mybatis.jpetstore.domain.Product.";

    List<ProductCreatedEvent> allEvents = new ArrayList<>();
    for (DomainEvent event : eventStore.getEventsByStreamPrefix(streamPrefix)) {
      if (event instanceof ProductCreatedEvent) {
        allEvents.add((ProductCreatedEvent) event);
      }
    }
    allEvents.sort(Comparator.comparing(e -> e.getTimestamp()));
    return allEvents;
  }

  public Product getProduct(String productId) {
    List<ProductCreatedEvent> productEvents = getProductEvents();
    ProductCreatedEvent matchedEvent = productEvents.stream().filter(event -> event.getProductId().equals(productId))
        .findFirst().orElse(null);

    if (matchedEvent == null) {
      return null;
    }

    Product product = new Product(productId);
    product.setProductId(matchedEvent.getProductId());
    product.setCategoryId(matchedEvent.getCategory());
    product.setName(matchedEvent.getName());
    product.setDescription(matchedEvent.getDescription());

    logger.info("Fetching product from EventStore with ID: {}", productId);
    return product;

    // return productMapper.getProduct(productId);
  }

  public List<Product> getProductListByCategory(String categoryId) {
    List<ProductCreatedEvent> productEvents = getProductEvents();
    List<ProductCreatedEvent> matchedEvent = productEvents.stream()
        .filter(event -> event.getCategory().equals(categoryId)).collect(Collectors.toList());
    if (matchedEvent == null) {
      return null;
    }

    List<Product> productList = new ArrayList<>();
    for (ProductCreatedEvent event : matchedEvent) {
      String productId = event.getProductId();
      Product product = new Product(productId);
      product.setCategoryId(event.getCategory());
      product.setName(event.getName());
      product.setDescription(event.getDescription());
      productList.add(product);
    }

    logger.info("Fetching product list for EventStore with CategoryId: {}", categoryId);
    return productList;

    // return productMapper.getProductListByCategory(categoryId);
  }

  /**
   * Search product list.
   *
   * @param keywords
   *          the keywords
   *
   * @return the list
   */
  public List<Product> searchProductList(String keywords) {
    List<Product> products = new ArrayList<>();
    for (String keyword : keywords.split("\\s+")) {
      products.addAll(productMapper.searchProductList("%" + keyword.toLowerCase() + "%"));
    }
    return products;
  }

  public List<ItemCreatedEvent> getItemEvents() {
    String streamPrefix = "org.mybatis.jpetstore.domain.Item.";

    List<ItemCreatedEvent> allEvents = new ArrayList<>();
    for (DomainEvent event : eventStore.getEventsByStreamPrefix(streamPrefix)) {
      if (event instanceof ItemCreatedEvent) {
        allEvents.add((ItemCreatedEvent) event);
      }
    }
    allEvents.sort(Comparator.comparing(e -> e.getTimestamp()));
    return allEvents;
  }

  public List<Item> getItemListByProduct(String productId) {
    List<ItemCreatedEvent> itemEvents = getItemEvents();
    List<ItemCreatedEvent> matchedEvent = itemEvents.stream().filter(event -> event.getProductId().equals(productId))
        .collect(Collectors.toList());
    if (matchedEvent == null) {
      return null;
    }

    List<Item> itemList = new ArrayList<>();
    for (ItemCreatedEvent event : matchedEvent) {
      String itemId = event.getItemId();
      Item item = new Item(itemId);
      item.setProductId(event.getProductId());
      item.setListPrice(event.getListPrice());
      item.setUnitCost(event.getUnitCost());
      item.setSupplierId(event.getSupplierId());
      item.setStatus(event.getStatus());
      item.setAttribute1(event.getAttribute1());
      itemList.add(item);
    }

    logger.info("Fetching product list for EventStore with CategoryId: {}", productId);
    return itemList;

    // return itemMapper.getItemListByProduct(productId);
  }

  public Item getItem(String itemId) {
    List<ItemCreatedEvent> itemEvents = getItemEvents();
    ItemCreatedEvent matchedEvent = itemEvents.stream().filter(event -> event.getItemId().equals(itemId)).findFirst()
        .orElse(null);

    if (matchedEvent == null) {
      return null;
    }

    Item item = new Item(itemId);
    item.setProductId(matchedEvent.getProductId());
    item.setListPrice(matchedEvent.getListPrice());
    item.setUnitCost(matchedEvent.getUnitCost());
    item.setSupplierId(matchedEvent.getSupplierId());
    item.setStatus(matchedEvent.getStatus());
    item.setAttribute1(matchedEvent.getAttribute1());

    logger.info("Fetching product from EventStore with ID: {}", itemId);
    return item;
    // return itemMapper.getItem(itemId);
  }

  public boolean isItemInStock(String itemId) {
    String streamPrefix = "org.mybatis.jpetstore.domain.Order.";

    List<DomainEvent> allEvents = eventStore.getEventsByStreamPrefix(streamPrefix).stream()
        .filter(e -> e instanceof InventoryUpdatedEvent)
        .sorted(Comparator.comparing(e -> ((InventoryUpdatedEvent) e).getTimestamp())) // 案時間排序
        .collect(Collectors.toList());

    // 初始化庫存數量
    int stock = 10000;

    for (DomainEvent event : allEvents) {
      if (event instanceof InventoryUpdatedEvent) {
        InventoryUpdatedEvent inventoryEvent = (InventoryUpdatedEvent) event;
        String eventItemId = inventoryEvent.getItemId();
        // 檢查事件中的商品 ID 是否與查詢的商品 ID 匹配
        if (eventItemId.equals(itemId)) {
          stock += inventoryEvent.getQuantityChange();
        }
      }
    }
    return stock > 0;
    // return itemMapper.getInventoryQuantity(itemId) > 0;
  }
}
