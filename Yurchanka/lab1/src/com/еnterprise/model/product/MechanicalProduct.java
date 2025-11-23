package com.еnterprise.model.product;

public class MechanicalProduct extends Product{
    private static final String CATEGORY = "Mechanical";

    public MechanicalProduct(String id, String name, int productTime) {
        super(id, name, productTime);
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }
}
