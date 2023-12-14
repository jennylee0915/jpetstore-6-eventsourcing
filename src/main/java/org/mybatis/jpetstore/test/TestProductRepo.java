package org.mybatis.jpetstore.test;

import org.mybatis.jpetstore.domain.Category;
import org.mybatis.jpetstore.domain.Product;

public class TestProductRepo {
    public static void main(String[] args) {
        Product product = new Product();

        product.setProductId("EST-10000");
        product.setName("Fish");
        product.setDescription("Big Fish");

        System.out.println(product);
        product.getEvents().forEach(event -> {
            System.out.println("Event: " + event);
        });
    }
}
