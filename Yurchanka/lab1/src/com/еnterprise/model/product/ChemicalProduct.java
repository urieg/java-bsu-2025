package com.еnterprise.model.product;

public class ChemicalProduct extends Product{
    private static final String CATEGORY = "Chemical";

    public ChemicalProduct(String id, String name, int productTime) {
        super(id, name, productTime);
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }
}
