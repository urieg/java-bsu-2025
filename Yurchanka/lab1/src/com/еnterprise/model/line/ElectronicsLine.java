package com.еnterprise.model.line;

import com.еnterprise.model.product.ElectronicProduct;
import com.еnterprise.model.product.Product;

public class ElectronicsLine extends ProductionLine<ElectronicProduct> {

    public ElectronicsLine(String lineId, double efficiency) {
        super(lineId, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ElectronicProduct;
    }
}
