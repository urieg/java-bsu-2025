package com.еnterprise;

import com.еnterprise.model.line.ChemicalLine;
import com.еnterprise.model.line.ElectronicsLine;
import com.еnterprise.model.line.MechanicalLine;
import com.еnterprise.model.line.ProductionLine;
import com.еnterprise.model.product.ChemicalProduct;
import com.еnterprise.model.product.ElectronicProduct;
import com.еnterprise.model.product.MechanicalProduct;
import com.еnterprise.model.product.Product;
import com.еnterprise.service.ProdAnalyzer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ElectronicProduct watch = new ElectronicProduct("EL1", "Watch", 120);
        ElectronicProduct laptop = new ElectronicProduct("EL2", "Laptop", 240);
        MechanicalProduct engine = new MechanicalProduct("ME1", "Engine", 480);
        MechanicalProduct gear = new MechanicalProduct("ME2", "Gearbox", 300);
        ChemicalProduct hno3 = new ChemicalProduct("CH1", "HNO3", 180);

        ElectronicsLine eline = new ElectronicsLine("EL01", 0.93);
        MechanicalLine mline1 = new MechanicalLine("ML01", 0.86);
        MechanicalLine mline2 = new MechanicalLine("ML02", 0.93);
        ChemicalLine cline = new ChemicalLine("CL01", 0.71);

        eline.addProduct(watch);
        eline.addProduct(laptop);
        mline1.addProduct(engine);
        mline2.addProduct(gear);
        cline.addProduct(hno3);

        System.out.println("Продукты успешно добавлены на соответствующие линии.");

        // Попытка добавить несовместимый продукт (вызовет исключение)
        try {
            System.out.println("\nПопытка добавить электронный продукт на механическую линию...");

            if (!mline1.canProduce(watch)) {
                throw new IllegalArgumentException(
                        "Линия " + mline1.getLineId() + " не может производить продукт типа " +
                                watch.getClass().getSimpleName() + "."
                );
            }

        } catch (IllegalArgumentException e) {
            System.out.println("ОШИБКА: " + e.getMessage());
        }


        List<ProductionLine<? extends Product>> allLines = List.of(eline, mline1, mline2, cline);
        ProdAnalyzer analyzer = new ProdAnalyzer(allLines);

        System.out.println("\n--- РЕЗУЛЬТАТЫ АНАЛИЗА ---");

        System.out.println("\nЛинии с эффективностью > 0.91:");
        analyzer.getHighEfficiencyLines(0.91).forEach(System.out::println);

        System.out.println("\nКоличество продуктов по категориям:");
        analyzer.countProductsByCategory().forEach((category, count) ->
                System.out.println(category + ": " + count));

        System.out.println("\nНаиболее загруженная линия:");
        analyzer.findMostLoadedLine().ifPresent(line ->
                System.out.println("ID: " + line.getLineId() + ", Продуктов: " + line.getProducts().size()));

        System.out.println("\nОбщее время производства всех изделий (в минутах):");
        System.out.println(analyzer.calculateTotalProductionTime());

        System.out.println("\nВсего продуктов на всех линиях:");
        System.out.println(analyzer.getAllProductsFromLines().size());
    }
}

