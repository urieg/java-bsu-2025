package com.еnterprise.service;

import com.еnterprise.model.line.ProductionLine;
import com.еnterprise.model.product.Product;

import java.util.*;
import java.util.stream.Collectors;

public class ProdAnalyzer {
    private final List<ProductionLine<? extends Product>> allLines;

    public ProdAnalyzer(List<ProductionLine<? extends Product>> allLines) {
        this.allLines = allLines;
    }

    public List<ProductionLine<? extends Product>> getAllLines() {
        return new ArrayList<>(allLines);
    }

    public List<String> getHighEfficiencyLines(double threshold) {
        return allLines.stream()
                .filter(line -> line.getEfficiency() > threshold)
                .map(ProductionLine::getLineId)
                .collect(Collectors.toList());
    }

    public Map<String, Long> countProductsByCategory() {
        return allLines.stream()
                .flatMap(line -> line.getProducts().stream())
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));
    }

    public Optional<ProductionLine<? extends Product>> findMostLoadedLine() {
        return allLines.stream()
                .max(Comparator.comparingInt(line -> line.getProducts().size()));
    }

    public List getAllProductsFromLines() {
        return allLines.stream()
                .flatMap(line -> line.getProducts().stream())
                .collect(Collectors.toList());
    }


    /**
     * ADVANCED Расчет общего время производства всех изделий на всех линиях.
     */
    public int calculateTotalProductionTime() {
        return allLines.stream()
                .flatMap(line -> line.getProducts().stream())
                .mapToInt(Product::getProductionTime)
                .sum();
    }
}
