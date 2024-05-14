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
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.repository.EventSourcedProductRepository;

public class TestProductRepo {
  private final static EventStore eventStore = new EventStore(
      "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
  private final static EventSourcedProductRepository repository = new EventSourcedProductRepository(eventStore);

  public static void main(String[] args) {
    // productid,category,name,description
    Product sw1Product = new Product("FI-SW-01", "FISH", "Angelfish",
        "<image src=\"../images/fish1.gif\">Salt Water fish from Australia");
    System.out.println(repository.save(sw1Product));
    Product sw2Product = new Product("FI-SW-02", "FISH", "Tiger Shark",
        "<image src=\"../images/fish4.gif\">Salt Water fish from Australia");
    System.out.println(repository.save(sw2Product));
    Product fw1Product = new Product("FI-FW-01", "FISH", "Koi",
        "<image src=\"../images/fish3.gif\">Fresh Water fish from Japan");
    System.out.println(repository.save(fw1Product));
    Product fw2Product = new Product("FI-FW-02", "FISH", "Goldfish",
        "<image src=\"../images/fish2.gif\">Fresh Water fish from China");
    System.out.println(repository.save(fw2Product));
    Product bd1Product = new Product("K9-BD-01", "DOGS", "Bulldog",
        "<image src=\"../images/dog2.gif\">Friendly dog from England");
    System.out.println(repository.save(bd1Product));
    Product po2Product = new Product("K9-PO-02", "DOGS", "Poodle",
        "<image src=\"../images/dog6.gif\">Cute dog from France");
    System.out.println(repository.save(po2Product));
    Product dl1Product = new Product("K9-DL-01", "DOGS", "Dalmation",
        "<image src=\"../images/dog5.gif\">Great dog for a Fire Station");
    System.out.println(repository.save(dl1Product));
    Product rt1Product = new Product("K9-RT-01", "DOGS", "Golden Retriever",
        "<image src=\"../images/dog1.gif\">Great family dog");
    System.out.println(repository.save(rt1Product));
    Product rt2Product = new Product("K9-RT-02", "DOGS", "Labrador Retriever",
        "<image src=\"../images/dog5.gif\">Great hunting dog");
    System.out.println(repository.save(rt2Product));
    Product cw1Product = new Product("K9-CW-01", "DOGS", "Chihuahua",
        "<image src=\"../images/dog4.gif\">Great companion dog");
    System.out.println(repository.save(cw1Product));
    Product sn1Product = new Product("RP-SN-01", "REPTILES", "Rattlesnake",
        "<image src=\"../images/snake1.gif\">Doubles as a watch dog");
    System.out.println(repository.save(sn1Product));
    Product li2Product = new Product("RP-LI-02", "REPTILES", "Iguana",
        "<image src=\"../images/lizard1.gif\">Friendly green friend");
    System.out.println(repository.save(li2Product));
    Product dsh1Product = new Product("FL-DSH-01", "CATS", "Manx",
        "<image src=\"../images/cat2.gif\">Great for reducing mouse populations");
    System.out.println(repository.save(dsh1Product));
    Product dlh2Product = new Product("FL-DLH-02", "CATS", "Persian",
        "<image src=\"../images/cat1.gif\">Friendly house cat, doubles as a princess");
    System.out.println(repository.save(dlh2Product));
    Product cb1Product = new Product("AV-CB-01", "BIRDS", "Amazon Parrot",
        "<image src=\"../images/bird2.gif\">Great companion for up to 75 years");
    System.out.println(repository.save(cb1Product));
    Product sb2Product = new Product("AV-SB-02", "BIRDS", "Finch",
        "<image src=\"../images/bird1.gif\">Great stress reliever");
    System.out.println(repository.save(sb2Product));

  }
}
