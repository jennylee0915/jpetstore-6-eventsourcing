package org.mybatis.jpetstore.test;

import org.mybatis.jpetstore.domain.Category;

public class TestCategoryRepo {
    public static void main(String[] args){
        Category category = new Category();

        category.setName("456");
        category.setDescription("789");

        System.out.println(category);
        category.getEvents().forEach(event -> {
            System.out.println("Event: " + event);
        });
    }
}
