package com.nphase.service;

import com.nphase.entity.Category;
import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ShoppingCartService {

    private final int amountOfSameItemsToGetDiscount;
    private final int amountOfSameCategoryToGetDiscount;

    public ShoppingCartService() {
        int sameItemAmount = 0;
        int sameCategoryAmount = 0;

        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            sameItemAmount = Integer.parseInt(prop.getProperty("same.items.to.get.discount"));
            sameCategoryAmount = Integer.parseInt(prop.getProperty("same.category.items.to.get.discount"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.amountOfSameItemsToGetDiscount = sameItemAmount;
        this.amountOfSameCategoryToGetDiscount = sameCategoryAmount;
    }


    // Task 1
    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(product -> product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    // Task 2
    public BigDecimal calculateTotalPriceBulkReward(ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(product -> product.getQuantity() > amountOfSameItemsToGetDiscount ?
                        product.getPricePerUnit().multiply(BigDecimal.valueOf(0.9)).multiply(BigDecimal.valueOf(product.getQuantity())) :
                        product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()))
                    )
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    // Task 3
    public BigDecimal calculateTotalPriceCategoryReward(ShoppingCart shoppingCart) {

        Map<Category, List<Product>> productsByCategory = shoppingCart.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        BigDecimal total = BigDecimal.valueOf(0);

        for (Category category : productsByCategory.keySet()) {
            int itemsInCategory = productsByCategory.get(category)
                    .stream()
                    .map(Product::getQuantity)
                    .reduce(Integer::sum)
                    .orElse(0);

            total = total.add(productsByCategory.get(category).stream()
                .map(product -> itemsInCategory > amountOfSameCategoryToGetDiscount ?
                        product.getPricePerUnit().multiply(BigDecimal.valueOf(0.9)).multiply(BigDecimal.valueOf(product.getQuantity())) :
                        product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()))
                )
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO));
        }
        return total;
    }
}
