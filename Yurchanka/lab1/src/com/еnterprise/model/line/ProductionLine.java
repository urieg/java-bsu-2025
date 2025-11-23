package com.еnterprise.model.line;

import com.еnterprise.model.product.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * ADVANCED: protected конструктор -- ProductionLine нельзя инстанцировать напрямую
 * @param <T>
 */

public abstract class ProductionLine<T extends Product> {
    private final String lineId;
    private final List<T> products;
    private final double efficiency;

    protected ProductionLine(String lineId, double efficiency) {
        if (efficiency < 0.0 || efficiency > 1.0) {
            throw new IllegalArgumentException("Эффективность должна быть в диапазоне от 0.0 до 1.0.");
        }
        this.lineId = lineId;
        this.efficiency = efficiency;
        this.products = new ArrayList<>();
    }

    public abstract boolean canProduce(Product product);

    /**
     * ADVANCED: Добавляет продукт на линию с предварительной валидацией.
     * @param product Продукт для добавления
     * @throws IllegalArgumentException если линия не может произвести данный продукт.
     */
    public void addProduct(T product) {
        if (!canProduce(product)) {
            throw new IllegalArgumentException(
                    "Линия " + lineId + " не может производить продукт типа " +
                            product.getClass().getSimpleName() + "."
            );
        }
        products.add(product);
    }

    public String getLineId() {
        return lineId;
    }

    public List<T> getProducts() {
        return new ArrayList<>(products);
    }

    public double getEfficiency() {
        return efficiency;
    }
}
