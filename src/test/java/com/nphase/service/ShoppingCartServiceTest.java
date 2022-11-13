package com.nphase.service;


import com.nphase.entity.Category;
import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class ShoppingCartServiceTest {
    private final ShoppingCartService service = new ShoppingCartService();

    @Test
    public void calculatesPrice()  {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 2, null),
                new Product("Coffee", BigDecimal.valueOf(6.5), 1, null)
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        Assertions.assertEquals(result, BigDecimal.valueOf(16.5));
    }

    @Test
    public void calculatesPriceWithBulkDiscount()  {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 5, null),
                new Product("Coffee", BigDecimal.valueOf(3.5), 3, null)
        ));

        BigDecimal result = service.calculateTotalPriceBulkReward(cart);
        Assertions.assertEquals(0, BigDecimal.valueOf(33).compareTo(result));
    }

    @Test
    public void calculatesPriceWithCategoryDiscount()  {
        Category drinks = new Category("drinks");
        Category food = new Category("food");

        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.3), 2, drinks),
                new Product("Coffee", BigDecimal.valueOf(3.5), 2, drinks),
                new Product("Cheese", BigDecimal.valueOf(8), 2, food)
        ));

        BigDecimal result = service.calculateTotalPriceCategoryReward(cart);
        Assertions.assertEquals(0, BigDecimal.valueOf(31.84).compareTo(result));
    }

}