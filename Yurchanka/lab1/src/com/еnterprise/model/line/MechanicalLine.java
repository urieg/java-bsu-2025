package com.еnterprise.model.line;

import com.еnterprise.model.product.MechanicalProduct;
import com.еnterprise.model.product.Product;

public class MechanicalLine extends ProductionLine<MechanicalProduct> {

    public MechanicalLine(String lineId, double efficiency) {
        super(lineId, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof MechanicalProduct;
    }
}
