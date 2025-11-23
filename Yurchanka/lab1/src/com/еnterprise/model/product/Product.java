package com.еnterprise.model.product;

public  abstract class Product {
    private final String id;
    private final String name;
    private final int productionTime;

    public Product(String id, String name, int productTime) {
        this.id = id;
        this.name = name;
        this.productionTime = productTime;
    }

    public abstract String getCategory();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getProductionTime() {
        return productionTime;
    }
}
