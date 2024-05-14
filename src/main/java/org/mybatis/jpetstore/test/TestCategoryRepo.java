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

import org.mybatis.jpetstore.core.EventStore;
import org.mybatis.jpetstore.domain.Category;
import org.mybatis.jpetstore.repository.EventSourcedCategoryRepository;

public class TestCategoryRepo {
  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
  private final static EventSourcedCategoryRepository repository = new EventSourcedCategoryRepository(eventStore);

  public static void main(String[] args) {

    Category fishCategory = new Category("FISH", "Fish",
        "<image src=\"../images/fish_icon.gif\"><font size=\"5\" color=\"blue\"> Fish</font>");
    System.out.println(repository.save(fishCategory));
    Category dogCategory = new Category("DOGS", "Dogs",
        "<image src=\"../images/dogs_icon.gif\"><font size=\"5\" color=\"blue\"> Dogs</font>");
    System.out.println(repository.save(dogCategory));
    Category reptilesCategory = new Category("REPTILES", "Reptiles",
        "<image src=\"../images/reptiles_icon.gif\"><font size=\"5\" color=\"blue\"> Reptiles</font>");
    System.out.println(repository.save(reptilesCategory));
    Category catCategory = new Category("CATS", "Cats",
        "<image src=\"../images/cats_icon.gif\"><font size=\"5\" color=\"blue\"> Cats</font>");
    System.out.println(repository.save(catCategory));
    Category birdCategory = new Category("BIRDS", "Birds",
        "<image src=\"../images/birds_icon.gif\"><font size=\"5\" color=\"blue\"> Birds</font>");
    System.out.println(repository.save(birdCategory));

    // System.out.println(category);
    // category.getEvents().forEach(event -> {
    // System.out.println("Event: " + event);
    // });

  }
}
