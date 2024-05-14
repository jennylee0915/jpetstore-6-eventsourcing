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

import org.mybatis.jpetstore.service.InventoryService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestInventoryService {
  private InventoryService inventoryService;

  public static void main(String args[]) {
    // test();
    ApplicationContext context = new FileSystemXmlApplicationContext(
        "file:src/main/webapp/WEB-INF/applicationContext.xml");
    InventoryService service = context.getBean(InventoryService.class);
    service.subscribeOrderStream();
    service.subscribeInvnetoryUpdatedStream();
  }
}
