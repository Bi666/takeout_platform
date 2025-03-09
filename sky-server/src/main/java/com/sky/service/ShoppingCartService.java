package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * add shopping cart
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * view shopping cart
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * Clean shopping cart
     */
    void cleanCart();
}
