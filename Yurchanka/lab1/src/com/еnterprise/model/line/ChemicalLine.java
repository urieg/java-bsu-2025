package com.еnterprise.model.line;

import com.еnterprise.model.product.ChemicalProduct;
import com.еnterprise.model.product.Product;

public class ChemicalLine extends ProductionLine<ChemicalProduct> {

    public ChemicalLine(String lineId, double efficiency) {
        super(lineId, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ChemicalProduct;
    }
}
