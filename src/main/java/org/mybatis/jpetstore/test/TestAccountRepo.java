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

import java.util.List;

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.core.event.DomainEvent;
import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.repository.EventSourcedAccountRepository;

public class TestAccountRepo {
  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
  private final static EventSourcedAccountRepository repository = new EventSourcedAccountRepository(eventStore);

  public static void main(String[] args) {
    List<DomainEvent> allEvents = eventStore.getAllStream();
    for (DomainEvent event : allEvents) {
      System.out.println(event);
    }
    // String accountId = testCreate();
    // testFindBy(accountId);
    // testAppend(accountId);
    // testFindBy(accountId, 4);
  }

  private static void testAppend(String accountId) {
    Account account = repository.findBy(accountId);
    account.setUsername("John-Updated-1");
    System.out.println("======Test Update Account Attribute======");
    System.out.println(repository.save(account));
    System.out.println(account);
  }

  private static void testFindBy(String accountId) {
    Account account = repository.findBy(accountId);
    System.out.println("======Test Find Account by AccountId======");
    System.out.println(account);
  }

  private static void testFindBy(String accountId, long version) {
    Account account = repository.findBy(accountId, version);
    System.out.println("======Test Find Account by AccountId to Version======");
    System.out.println(account);
  }

  private static String testCreate() {
    Account account = new Account();
    account.setUsername("John");
    account.setPassword("password");
    // account.setEmail("john@gmail.com");
    // account.setFirstName("TestF");
    // account.setLastName("TestL");
    System.out.println("======Test Create Account======");
    System.out.println(repository.save(account));
    return account.getAccountId();
  }
}
