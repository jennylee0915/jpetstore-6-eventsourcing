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
package org.mybatis.jpetstore.test;

import java.math.BigDecimal;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.repository.EventSourcedItemRepository;

public class TestItemRepo {
  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
  private final static EventSourcedItemRepository repository = new EventSourcedItemRepository(eventStore);

  public static void main(String args[]) {

    Item item1 = new Item("EST-1", "FI-SW-01", BigDecimal.valueOf(16.50), BigDecimal.valueOf(10.00), 1, "P", "Large");
    System.out.println(repository.save(item1));
    Item item2 = new Item("EST-2", "FI-SW-01", BigDecimal.valueOf(16.50), BigDecimal.valueOf(10.00), 1, "P", "Small");
    System.out.println(repository.save(item2));
    Item item3 = new Item("EST-3", "FI-SW-02", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Toothless");
    System.out.println(repository.save(item3));
    Item item4 = new Item("EST-4", "FI-FW-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P", "Spotted");
    System.out.println(repository.save(item4));
    Item item5 = new Item("EST-5", "FI-FW-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Spotless");
    System.out.println(repository.save(item5));
    Item item6 = new Item("EST-6", "K9-BD-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Male Adult");
    System.out.println(repository.save(item6));
    Item item7 = new Item("EST-7", "K9-BD-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Female Puppy");
    System.out.println(repository.save(item7));
    Item item8 = new Item("EST-8", "K9-PO-02", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Male Puppy");
    System.out.println(repository.save(item8));
    Item item9 = new Item("EST-9", "K9-DL-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Spotless Male Puppy");
    System.out.println(repository.save(item9));
    Item item10 = new Item("EST-10", "K9-DL-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Spotted Adult Female");
    System.out.println(repository.save(item10));
    Item item11 = new Item("EST-11", "RP-SN-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Venomless");
    System.out.println(repository.save(item11));
    Item item12 = new Item("EST-12", "RP-SN-01", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Rattleless");
    System.out.println(repository.save(item12));
    Item item13 = new Item("EST-13", "RP-LI-02", BigDecimal.valueOf(18.50), BigDecimal.valueOf(12.00), 1, "P",
        "Green Adult");
    System.out.println(repository.save(item13));
    Item item14 = new Item("EST-14", "FL-DSH-01", BigDecimal.valueOf(58.50), BigDecimal.valueOf(12.00), 1, "P",
        "Tailless");
    System.out.println(repository.save(item14));
    Item item15 = new Item("EST-15", "FL-DSH-01", BigDecimal.valueOf(23.50), BigDecimal.valueOf(12.00), 1, "P",
        "With tail");
    System.out.println(repository.save(item15));
    Item item16 = new Item("EST-16", "FL-DLH-02", BigDecimal.valueOf(93.50), BigDecimal.valueOf(12.00), 1, "P",
        "Adult Female");
    System.out.println(repository.save(item16));
    Item item17 = new Item("EST-17", "FL-DLH-02", BigDecimal.valueOf(93.50), BigDecimal.valueOf(12.00), 1, "P",
        "Adult Male");
    System.out.println(repository.save(item17));
    Item item18 = new Item("EST-18", "AV-CB-01", BigDecimal.valueOf(193.50), BigDecimal.valueOf(92.00), 1, "P",
        "Adult Male");
    System.out.println(repository.save(item18));
    Item item19 = new Item("EST-19", "AV-SB-02", BigDecimal.valueOf(15.50), BigDecimal.valueOf(2.00), 1, "P",
        "Adult Male");
    System.out.println(repository.save(item19));
    Item item20 = new Item("EST-20", "FI-FW-02", BigDecimal.valueOf(5.50), BigDecimal.valueOf(2.00), 1, "P",
        "Adult Male");
    System.out.println(repository.save(item20));
    Item item21 = new Item("EST-21", "FI-FW-02", BigDecimal.valueOf(5.29), BigDecimal.valueOf(1.00), 1, "P",
        "Adult Female");
    System.out.println(repository.save(item21));
    Item item22 = new Item("EST-22", "K9-RT-02", BigDecimal.valueOf(135.50), BigDecimal.valueOf(100.00), 1, "P",
        "Adult Male");
    System.out.println(repository.save(item22));
    Item item23 = new Item("EST-23", "K9-RT-02", BigDecimal.valueOf(145.49), BigDecimal.valueOf(100.00), 1, "P",
        "Adult Female");
    System.out.println(repository.save(item23));
    Item item24 = new Item("EST-24", "K9-RT-02", BigDecimal.valueOf(255.50), BigDecimal.valueOf(92.00), 1, "P",
        "Adult Male");
    System.out.println(repository.save(item24));
    Item item25 = new Item("EST-25", "K9-RT-02", BigDecimal.valueOf(325.29), BigDecimal.valueOf(90.00), 1, "P",
        "Adult Female");
    System.out.println(repository.save(item25));
    Item item26 = new Item("EST-26", "K9-CW-01", BigDecimal.valueOf(125.50), BigDecimal.valueOf(92.00), 1, "P",
        "Adult Male");
    System.out.println(repository.save(item26));
    Item item27 = new Item("EST-27", "K9-CW-01", BigDecimal.valueOf(155.29), BigDecimal.valueOf(90.00), 1, "P",
        "Adult Female");
    System.out.println(repository.save(item27));
    Item item28 = new Item("EST-28", "K9-RT-01", BigDecimal.valueOf(155.29), BigDecimal.valueOf(90.00), 1, "P",
        "Adult Female");
    System.out.println(repository.save(item28));

  }
}
