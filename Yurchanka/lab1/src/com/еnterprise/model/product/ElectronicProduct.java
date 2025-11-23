package com.еnterprise.model.product;

public class ElectronicProduct extends Product{
    private static final String CATEGORY = "Electronics";

    public ElectronicProduct(String id, String name, int productTime) {
        super(id, name, productTime);
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }
}
