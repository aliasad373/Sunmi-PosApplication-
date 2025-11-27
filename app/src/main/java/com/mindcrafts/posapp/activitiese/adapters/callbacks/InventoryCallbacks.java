package com.mindcrafts.posapp.activitiese.adapters.callbacks;

import com.mindcrafts.posapp.activitiese.inventory.models.CartProduct;
import com.mindcrafts.posapp.activitiese.inventory.models.InventoryResponse;

public interface InventoryCallbacks {
    public void getSingleProduct(CartProduct product);
}
